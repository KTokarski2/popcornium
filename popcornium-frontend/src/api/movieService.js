import apiClient from './config/apiClient';

const movieService = {
  getMovies: async (params = {}) => {
    const { query, yearFrom, yearTo, ratingFrom, ratingTo, page = 0 } = params;
    
    const queryParams = new URLSearchParams();
    
    if (query) queryParams.append('query', query);
    if (yearFrom) queryParams.append('yearFrom', yearFrom);
    if (yearTo) queryParams.append('yearTo', yearTo);
    if (ratingFrom) queryParams.append('ratingFrom', ratingFrom);
    if (ratingTo) queryParams.append('ratingTo', ratingTo);
    queryParams.append('page', page);
    
    const url = `/movies?${queryParams.toString()}`;
    const data = await apiClient.get(url);
    
    return {
      movies: data.content.map(movie => ({
        ...movie,
        posterUrl: movie.image ? `data:image/jpeg;base64,${movie.image}` : null,
      })),
      totalPages: data.totalPages,
      totalElements: data.totalElements,
      currentPage: data.number,
    };
  },

  getMovieDetails: async (id) => {
    const data = await apiClient.get(`/movies/${id}`);
    
    // Sort actors: first those with photoUrl, then those without
    const sortedActors = data.actors
      .map(actor => ({
        ...actor,
        photoUrl: actor.photoUrl || null,
      }))
      .sort((a, b) => {
        if (a.photoUrl && !b.photoUrl) return -1;
        if (!a.photoUrl && b.photoUrl) return 1;
        return 0;
      });
    
    return {
      ...data,
      posterUrl: data.poster ? `data:image/jpeg;base64,${data.poster}` : null,
      actors: sortedActors,
    };
  },
};

export const { getMovies, getMovieDetails } = movieService;
export default movieService;
