import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Auth } from '../../../core/services/auth';

@Component({
  selector: 'app-register',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  registerForm: FormGroup;
  errorMessage = signal<string | null>(null);
  isLoading = signal(false);

  constructor(
    private fb: FormBuilder,
    private authService: Auth,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email, Validators.maxLength(254)]],
      password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(16)]],
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      role: ['CLIENT', [Validators.required]]
    });
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.authService.register(this.registerForm.value).subscribe({
      next: () => {
        // Registration successful - redirect to login
        this.router.navigate(['/login'], {
          state: {
            message: 'Registration successful! Please login with your credentials.'
          }
        });
      },
      error: (error) => {
        this.isLoading.set(false);

        // Handle specific error codes from backend
        const errorCode = error.error?.error;

        if (errorCode === 'DUPLICATE_RESOURCE') {
          this.errorMessage.set('This email is already registered. Please use a different email or login.');
        } else if (errorCode === 'INVALID_CREDENTIALS') {
          this.errorMessage.set('Invalid credentials provided.');
        } else if (error.error?.errors && error.error.errors.length > 0) {
          // Handle validation errors
          this.errorMessage.set('Please check your input: ' + error.error.errors.join(', '));
        } else {
          this.errorMessage.set('Registration failed. Please try again.');
        }
      }
    });
  }

  get email() {
    return this.registerForm.get('email');
  }

  get password() {
    return this.registerForm.get('password');
  }

  get name() {
    return this.registerForm.get('name');
  }
}
