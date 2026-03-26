package smahfood.neo4flix.user.security;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.Instant;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

@Service
public class TotpService {

    private static final String HMAC_ALGO = "HmacSHA1";
    private static final int SECRET_BYTES = 20; // 160-bit
    private static final int TIME_STEP_SECONDS = 30;
    private static final int DIGITS = 6;
    private static final int WINDOW = 1; // +/- 1 time step

    private final SecureRandom secureRandom = new SecureRandom();

    public String generateBase32Secret() {
        byte[] bytes = new byte[SECRET_BYTES];
        secureRandom.nextBytes(bytes);
        return base32Encode(bytes);
    }

    public boolean verifyCode(String base32Secret, String code) {
        if (base32Secret == null || base32Secret.isBlank()) return false;
        if (code == null || !code.matches("\\d{6}")) return false;

        long timeStep = Instant.now().getEpochSecond() / TIME_STEP_SECONDS;
        for (long i = -WINDOW; i <= WINDOW; i++) {
            String expected = generateCode(base32Secret, timeStep + i);
            if (expected.equals(code)) return true;
        }
        return false;
    }

    public String generateOtpAuthUri(String issuer, String accountName, String base32Secret) {
        // otpauth://totp/Issuer:email?secret=BASE32&issuer=Issuer
        String label = urlEncode(issuer + ":" + accountName);
        return "otpauth://totp/" + label
                + "?secret=" + base32Secret
                + "&issuer=" + urlEncode(issuer);
    }

    private String generateCode(String base32Secret, long timeStep) {
        byte[] key = base32Decode(base32Secret);
        byte[] data = ByteBuffer.allocate(8).putLong(timeStep).array();

        byte[] hash;
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(key, HMAC_ALGO));
            hash = mac.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("TOTP error", e);
        }

        int offset = hash[hash.length - 1] & 0x0F;
        int binary = ((hash[offset] & 0x7f) << 24)
                | ((hash[offset + 1] & 0xff) << 16)
                | ((hash[offset + 2] & 0xff) << 8)
                | (hash[offset + 3] & 0xff);

        int otp = binary % (int) Math.pow(10, DIGITS);
        return String.format("%0" + DIGITS + "d", otp);
    }

    // Minimal Base32 (RFC 4648) without padding
    private static final char[] BASE32_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray();

    private static String base32Encode(byte[] input) {
        StringBuilder out = new StringBuilder((input.length * 8 + 4) / 5);

        int buffer = 0;
        int bitsLeft = 0;
        for (byte b : input) {
            buffer = (buffer << 8) | (b & 0xFF);
            bitsLeft += 8;
            while (bitsLeft >= 5) {
                int index = (buffer >> (bitsLeft - 5)) & 0x1F;
                bitsLeft -= 5;
                out.append(BASE32_ALPHABET[index]);
            }
        }
        if (bitsLeft > 0) {
            int index = (buffer << (5 - bitsLeft)) & 0x1F;
            out.append(BASE32_ALPHABET[index]);
        }
        return out.toString();
    }

    private static byte[] base32Decode(String base32) {
        String s = base32.trim().replace("=", "").toUpperCase();
        int buffer = 0;
        int bitsLeft = 0;
        byte[] out = new byte[s.length() * 5 / 8];
        int outPos = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int val;
            if ('A' <= c && c <= 'Z') val = c - 'A';
            else if ('2' <= c && c <= '7') val = 26 + (c - '2');
            else throw new IllegalArgumentException("Invalid base32 char");

            buffer = (buffer << 5) | val;
            bitsLeft += 5;

            if (bitsLeft >= 8) {
                out[outPos++] = (byte) ((buffer >> (bitsLeft - 8)) & 0xFF);
                bitsLeft -= 8;
            }
        }

        if (outPos == out.length) return out;
        byte[] trimmed = new byte[outPos];
        System.arraycopy(out, 0, trimmed, 0, outPos);
        return trimmed;
    }

    private static String urlEncode(String s) {
        // Very small encoder for spaces and a few delimiters; enough for issuer/email.
        return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}

