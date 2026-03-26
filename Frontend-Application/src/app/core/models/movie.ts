export type Movie = {
  id: string;
  title: string;
  overview?: string;
  releaseYear?: number;
  genre?: string;
  releaseDate?: string;

  // Aggregates
  averageRating?: number;
  ratingCount?: number;
};
