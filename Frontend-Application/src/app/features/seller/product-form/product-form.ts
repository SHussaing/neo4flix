import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ProductService } from '../../../core/services/product';
import { MediaService } from '../../../core/services/media';

@Component({
  selector: 'app-product-form',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './product-form.html',
  styleUrl: './product-form.css',
})
export class ProductForm implements OnInit {
  productForm: FormGroup;
  isEditMode = signal(false);
  productId = signal<string | null>(null);
  isLoading = signal(false);
  isSaving = signal(false);
  errorMessage = signal<string | null>(null);
  uploadingImages = signal(false);
  imageUrls = signal<string[]>([]);

  private static readonly MAX_PRICE = 1_000_000_000;
  private static readonly MAX_QUANTITY = 1_000_000_000;

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private mediaService: MediaService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.productForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(100)]],
      description: ['', [Validators.required, Validators.maxLength(200)]],
      price: [0, [Validators.required, Validators.min(0.01), Validators.max(ProductForm.MAX_PRICE)]],
      quantity: [0, [Validators.required, Validators.min(0), Validators.max(ProductForm.MAX_QUANTITY)]]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id && id !== 'new') {
      this.isEditMode.set(true);
      this.productId.set(id);
      this.loadProduct(id);
    }
  }

  loadProduct(id: string): void {
    this.isLoading.set(true);
    this.productService.getProductById(id).subscribe({
      next: (product) => {
        this.productForm.patchValue({
          name: product.name,
          description: product.description,
          price: product.price,
          quantity: product.quantity
        });
        this.imageUrls.set(product.imageUrls || []);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to load product');
        this.isLoading.set(false);
      }
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];

    // Validate file type
    if (!file.type.startsWith('image/')) {
      this.errorMessage.set('Please select a valid image file (PNG, JPG, GIF)');
      return;
    }

    // Validate file size (2MB)
    if (file.size > 2 * 1024 * 1024) {
      this.errorMessage.set('Image size must be less than 2MB. Please choose a smaller file.');
      return;
    }

    this.uploadingImages.set(true);
    this.errorMessage.set(null);

    this.mediaService.uploadImage(file).subscribe({
      next: (response) => {
        this.imageUrls.set([...this.imageUrls(), response.id]);
        this.uploadingImages.set(false);
        input.value = ''; // Reset input
      },
      error: (error) => {
        this.uploadingImages.set(false);
        const errorCode = error.error?.error;

        if (errorCode === 'FILE_TOO_LARGE') {
          this.errorMessage.set('File size exceeds 2MB limit. Please choose a smaller image.');
        } else if (errorCode === 'INVALID_FILE_TYPE') {
          this.errorMessage.set('Invalid file type. Please upload an image file (PNG, JPG, GIF).');
        } else {
          this.errorMessage.set('Failed to upload image. Please try again.');
        }
        input.value = ''; // Reset input
      }
    });
  }

  removeImage(index: number): void {
    const urls = [...this.imageUrls()];
    urls.splice(index, 1);
    this.imageUrls.set(urls);
  }

  getImageUrl(imageId: string): string {
    return this.mediaService.getImageUrl(imageId);
  }

  onSubmit(): void {
    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }

    this.isSaving.set(true);
    this.errorMessage.set(null);

    const formData = {
      ...this.productForm.value,
      imageUrls: this.imageUrls()
    };

    const request = this.isEditMode()
      ? this.productService.updateProduct(this.productId()!, formData)
      : this.productService.createProduct(formData);

    request.subscribe({
      next: () => {
        this.router.navigate(['/seller/dashboard']);
      },
      error: (error) => {
        this.isSaving.set(false);

        // Handle specific error codes from backend
        const errorCode = error.error?.error;

        if (errorCode === 'FORBIDDEN') {
          this.errorMessage.set('You do not have permission to perform this action.');
        } else if (errorCode === 'NOT_FOUND') {
          this.errorMessage.set('Product not found.');
        } else if (errorCode === 'UNAUTHORIZED') {
          this.errorMessage.set('Please login to continue.');
        } else if (error.error?.errors && error.error.errors.length > 0) {
          // Handle validation errors
          this.errorMessage.set('Validation error: ' + error.error.errors.join(', '));
        } else {
          this.errorMessage.set('Failed to save product. Please try again.');
        }
      }
    });
  }

  get name() {
    return this.productForm.get('name');
  }

  get description() {
    return this.productForm.get('description');
  }

  get price() {
    return this.productForm.get('price');
  }

  get quantity() {
    return this.productForm.get('quantity');
  }
}
