# eCommerce Frontend Application

Modern Angular 21 frontend application for the eCommerce marketplace platform.

## Features

- **Authentication & Authorization**
  - JWT-based authentication
  - Role-based access control (CLIENT/SELLER)
  - Secure login and registration

- **Product Management**
  - Browse products (public)
  - View product details with image gallery
  - Seller dashboard for product management
  - Create, update, and delete products (sellers only)

- **Media Management**
  - Image upload with validation
  - 2MB file size limit enforcement
  - Image preview and removal
  - Secure media handling

- **Modern UI/UX**
  - Tailwind CSS for styling
  - Responsive design
  - Loading states and error handling
  - Form validation with inline errors

## Tech Stack

- **Angular 21** - Latest framework features with signals
- **TypeScript** - Type-safe development
- **Tailwind CSS 4** - Utility-first styling
- **RxJS** - Reactive programming
- **Nginx** - Production web server

## Prerequisites

- Node.js 20 or higher
- npm 10 or higher

## Development

### Install Dependencies

```bash
npm install
```

### Start Development Server

```bash
npm start
```

Navigate to `http://localhost:4200/`. The app will automatically reload if you change any source files.

### Build

```bash
npm run build
```

Build artifacts will be stored in the `dist/` directory.

## Docker

### Build Docker Image

```bash
docker build -t ecommerce-frontend .
```

### Run Docker Container

```bash
docker run -p 80:80 ecommerce-frontend
```

## Project Structure

```
src/
├── app/
│   ├── core/                 # Core functionality
│   │   ├── guards/          # Route guards
│   │   ├── interceptors/    # HTTP interceptors
│   │   ├── models/          # TypeScript interfaces
│   │   └── services/        # API services
│   ├── features/            # Feature modules
│   │   ├── auth/           # Authentication pages
│   │   ├── products/       # Product pages
│   │   └── seller/         # Seller dashboard
│   ├── shared/             # Shared components
│   │   └── components/     # Reusable components
│   ├── app.config.ts       # App configuration
│   ├── app.routes.ts       # Route definitions
│   └── app.ts              # Root component
├── environments/           # Environment configs
└── styles.css             # Global styles
```

## Routes

- `/` - Product listing (public)
- `/products/:id` - Product details (public)
- `/login` - Login page
- `/register` - Registration page
- `/seller/dashboard` - Seller dashboard (requires SELLER role)
- `/seller/products/new` - Create product (requires SELLER role)
- `/seller/products/:id/edit` - Edit product (requires SELLER role)

## API Integration

The app connects to the backend API gateway at `http://localhost:8088` (configurable in `environments/environment.ts`).

### Endpoints Used

- `POST /auth/login` - User login
- `POST /auth/register` - User registration
- `GET /users/me` - Get current user profile
- `PUT /users/me` - Update user profile
- `GET /products` - List all products
- `GET /products/:id` - Get product details
- `POST /products` - Create product (seller only)
- `PUT /products/:id` - Update product (seller only)
- `DELETE /products/:id` - Delete product (seller only)
- `POST /media/images` - Upload image (seller only)
- `GET /media/images/:id` - Get image
- `DELETE /media/images/:id` - Delete image (seller only)

## Security Features

- JWT token authentication with Bearer token
- HTTP interceptor for automatic token attachment
- Route guards for authentication and role-based access
- Automatic redirect on 401/403 responses
- File type and size validation before upload
- CSRF protection via Angular's built-in mechanisms

## Environment Variables

Create `src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8088'
};
```

For production, update `src/environments/environment.prod.ts`:

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://your-api-domain.com'
};
```

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## License

This project is part of the eCommerce marketplace platform.
