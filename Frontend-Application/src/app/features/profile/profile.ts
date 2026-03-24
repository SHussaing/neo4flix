import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Auth } from '../../core/services/auth';
import { MediaService } from '../../core/services/media';
import { OrderService } from '../../core/services/order';
import { User } from '../../core/models/user.model';
import { BuyerAnalytics, SellerAnalytics } from '../../core/models/order.model';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit {
  profileForm: FormGroup;
  user = signal<User | null>(null);
  isEditing = signal(false);
  isSaving = signal(false);
  isUploadingAvatar = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  buyerAnalytics = signal<BuyerAnalytics | null>(null);
  sellerAnalytics = signal<SellerAnalytics | null>(null);
  analyticsLoading = signal(false);

  constructor(
    private fb: FormBuilder,
    private authService: Auth,
    private mediaService: MediaService,
    private orderService: OrderService
  ) {
    this.profileForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      email: [{ value: '', disabled: true }]
    });
  }

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.authService.getMe().subscribe({
      next: (user) => {
        this.user.set(user);
        this.profileForm.patchValue({
          name: user.name,
          email: user.email
        });
        this.loadAnalytics();
      },
      error: (error) => {
        const errorCode = error.error?.error;
        if (errorCode === 'UNAUTHORIZED') {
          this.errorMessage.set('Please login to view your profile');
        } else {
          this.errorMessage.set('Failed to load profile');
        }
      }
    });
  }

  loadAnalytics(): void {
    if (!this.user()) return;
    this.analyticsLoading.set(true);
    this.buyerAnalytics.set(null);
    this.sellerAnalytics.set(null);

    this.orderService.buyerAnalytics().subscribe({
      next: (data) => {
        this.buyerAnalytics.set(data);
        this.analyticsLoading.set(false);
      },
      error: () => {
        // keep profile usable even if analytics fails
        this.analyticsLoading.set(false);
      }
    });

    if (this.isSeller()) {
      this.orderService.sellerAnalytics().subscribe({
        next: (data) => this.sellerAnalytics.set(data),
        error: () => {},
      });
    }
  }

  toggleEdit(): void {
    this.isEditing.set(!this.isEditing());
    this.errorMessage.set(null);
    this.successMessage.set(null);

    if (!this.isEditing() && this.user()) {
      // Reset form if canceling edit
      this.profileForm.patchValue({
        name: this.user()!.name
      });
    }
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    this.isSaving.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    const updateData = {
      name: this.profileForm.value.name
    };

    this.authService.updateProfile(updateData).subscribe({
      next: (user) => {
        this.user.set(user);
        this.isSaving.set(false);
        this.isEditing.set(false);
        this.successMessage.set('Profile updated successfully');
      },
      error: (error) => {
        this.isSaving.set(false);
        const errorCode = error.error?.error;

        if (errorCode === 'UNAUTHORIZED') {
          this.errorMessage.set('You are not authorized to update this profile');
        } else if (error.error?.errors && error.error.errors.length > 0) {
          this.errorMessage.set('Validation error: ' + error.error.errors.join(', '));
        } else {
          this.errorMessage.set('Failed to update profile');
        }
      }
    });
  }

  onAvatarSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];

    // Validate file type and size
    if (!file.type.startsWith('image/')) {
      this.errorMessage.set('Please select a valid image file');
      return;
    }

    if (file.size > 2 * 1024 * 1024) {
      this.errorMessage.set('Image size must be less than 2MB');
      return;
    }

    this.isUploadingAvatar.set(true);
    this.errorMessage.set(null);

    this.mediaService.uploadImage(file).subscribe({
      next: (response) => {
        // Update profile with new avatar
        this.authService.updateProfile({ avatar: response.id }).subscribe({
          next: (user) => {
            this.user.set(user);
            this.isUploadingAvatar.set(false);
            this.successMessage.set('Avatar updated successfully');
          },
          error: () => {
            this.isUploadingAvatar.set(false);
            this.errorMessage.set('Failed to update avatar');
          }
        });
      },
      error: (error) => {
        this.isUploadingAvatar.set(false);
        const errorCode = error.error?.error;

        if (errorCode === 'INVALID_FILE_TYPE') {
          this.errorMessage.set('Invalid file type. Please upload an image file (PNG, JPG, GIF).');
        } else if (errorCode === 'FILE_TOO_LARGE') {
          this.errorMessage.set('File size exceeds 2MB limit. Please choose a smaller image.');
        } else {
          this.errorMessage.set('Failed to upload avatar. Please try again.');
        }
      }
    });
  }

  getAvatarUrl(avatarId: string | undefined): string {
    if (!avatarId) {
      return '';
    }
    return this.mediaService.getImageUrl(avatarId);
  }

  isSeller(): boolean {
    return this.user()?.role === 'SELLER';
  }

  get name() {
    return this.profileForm.get('name');
  }
}

