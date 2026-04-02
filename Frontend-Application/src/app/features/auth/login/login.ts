import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Auth } from '../../../core/services/auth';

@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login implements OnInit {
  loginForm: FormGroup;
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  isLoading = signal(false);

  otpStep = signal(false);
  challengeId = signal<string | null>(null);

  constructor(
    private fb: FormBuilder,
    private authService: Auth,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      otp: ['']
    });
  }

  ngOnInit(): void {
    // Check for success message from registration
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras?.state || history.state;
    if (state?.['message']) {
      this.successMessage.set(state['message']);
    }
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const email = (this.loginForm.get('email')?.value ?? '').toString();

    if (!this.otpStep()) {
      const password = (this.loginForm.get('password')?.value ?? '').toString();
      this.authService.login({ email, password }).subscribe({
        next: (resp) => {
          this.isLoading.set(false);
          this.challengeId.set(resp.challengeId);
          this.otpStep.set(true);

          // OTP required now
          this.loginForm.get('otp')?.setValidators([Validators.required, Validators.pattern(/^\d{6}$/)]);
          this.loginForm.get('otp')?.updateValueAndValidity();

          // If devOtp is provided (dev mode), prefill it to make the demo work.
          if (resp.devOtp) {
            this.loginForm.get('otp')?.setValue(resp.devOtp);
          }

          // We don't need password anymore for step 2
          this.loginForm.get('password')?.clearValidators();
          this.loginForm.get('password')?.updateValueAndValidity();

          this.errorMessage.set('We sent a 6-digit code to your email (check spam). Enter it to finish signing in.');
        },
        error: (error) => {
          this.isLoading.set(false);
          const errorCode = error.error?.error;
          if (errorCode === 'INVALID_CREDENTIALS') {
            this.errorMessage.set('Invalid email or password.');
          } else {
            this.errorMessage.set('Login failed. Please try again.');
          }
        }
      });
      return;
    }

    // Step 2: verify OTP
    const otp = (this.loginForm.get('otp')?.value ?? '').toString();
    const challengeId = this.challengeId();
    if (!challengeId) {
      this.isLoading.set(false);
      this.errorMessage.set('Missing OTP challenge. Please try signing in again.');
      this.otpStep.set(false);
      return;
    }

    this.authService.verifyLogin({ email, challengeId, otp }).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (error) => {
        this.isLoading.set(false);
        const errorCode = error.error?.error;
        if (errorCode === 'INVALID_OTP') {
          this.errorMessage.set('Invalid code. Try again.');
        } else {
          this.errorMessage.set('Verification failed. Please try again.');
        }
      }
    });
  }

  get email() {
    return this.loginForm.get('email');
  }

  get password() {
    return this.loginForm.get('password');
  }

  get otp() {
    return this.loginForm.get('otp');
  }
}
