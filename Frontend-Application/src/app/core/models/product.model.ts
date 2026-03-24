export interface Product {
  id: string;
  userId: string;
  name: string;
  description: string;
  price: number;
  quantity: number;
  imageUrls: string[];
  seller?: {
    id: string;
    name: string;
    avatar?: string;
  };
}

export interface CreateProductRequest {
  name: string;
  description: string;
  price: number;
  quantity: number;
  imageUrls: string[];
}

export interface UpdateProductRequest {
  name?: string;
  description?: string;
  price?: number;
  quantity?: number;
  imageUrls?: string[];
}

