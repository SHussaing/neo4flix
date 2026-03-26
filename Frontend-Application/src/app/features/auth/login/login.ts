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
  otpRequired = signal(false);

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

    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (error) => {
        this.isLoading.set(false);

        // Handle specific error codes from backend
        const errorCode = error.error?.error;

        if (errorCode === 'OTP_REQUIRED') {
          this.otpRequired.set(true);
          this.loginForm.get('otp')?.setValidators([Validators.required, Validators.pattern(/^\d{6}$/)]);
          this.loginForm.get('otp')?.updateValueAndValidity();
          this.errorMessage.set('Two-factor authentication is enabled. Enter the 6-digit code from your authenticator app.');
          return;
        }

        if (errorCode === 'INVALID_CREDENTIALS') {
          this.errorMessage.set('Invalid email or password. Please try again.');
        } else if (errorCode === 'UNAUTHORIZED') {
          this.errorMessage.set('You are not authorized to login.');
        } else if (error.error?.errors && error.error.errors.length > 0) {
          // Handle validation errors
          this.errorMessage.set('Please check your input: ' + error.error.errors.join(', '));
        } else {
          this.errorMessage.set('Login failed. Please try again.');
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
