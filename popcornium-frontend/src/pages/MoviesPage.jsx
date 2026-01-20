import { useState, useEffect } from 'react';
import { Box, Typography, TextField, Pagination, CardMedia, CardContent, CircularProgress, Alert } from '@mui/material';
import { Search } from '@mui/icons-material';
import { PageContainer, ContentContainer, StyledCard } from '../components/Styled';
import Navigation from '../components/Navigation';
import { useNavigate } from 'react-router-dom';
import movieService from '../api/movieService';

const MoviesPage = () => {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');
  const [page, setPage] = useState(0);
  const [movies, setMovies] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchMovies = async () => {
      setLoading(true);
      setError('');
      try {
        const result = await movieService.getMovies({
          query: searchQuery || undefined,
          page: page,
        });
        setMovies(result.movies);
        setTotalPages(result.totalPages);
      } catch (err) {
        setError('Failed to load movies. Please try again.');
        console.error('Error fetching movies:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchMovies();
  }, [searchQuery, page]);

  return (
    <>
      <Navigation />
      <PageContainer>
        <ContentContainer>
          <Box sx={{ mb: 4 }}>
            <Typography variant="h4" sx={{ mb: 3, fontWeight: 700 }}>
              Movie Catalog
            </Typography>
            <TextField
              fullWidth
              placeholder="Search movies..."
              value={searchQuery}
              onChange={(e) => {
                setSearchQuery(e.target.value);
                setPage(0);
              }}
              InputProps={{
                startAdornment: <Search sx={{ color: '#666666', mr: 1 }} />,
              }}
              sx={{
                maxWidth: '500px',
                '& .MuiOutlinedInput-root': {
                  color: '#ffffff',
                  backgroundColor: '#1a1a1a',
                  borderRadius: '12px',
                  '& fieldset': { borderColor: '#444444' },
                  '&:hover fieldset': { borderColor: '#666666' },
                  '&.Mui-focused fieldset': { borderColor: '#ffffff' },
                },
              }}
            />
          </Box>

          {error && (
            <Alert severity="error" sx={{ mb: 3, backgroundColor: '#4a1a1a', color: '#ffffff' }}>
              {error}
            </Alert>
          )}

          {loading ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
              <CircularProgress sx={{ color: '#ffffff' }} />
            </Box>
          ) : (
            <Box
              sx={{
                display: 'flex',
                flexWrap: 'wrap',
                gap: '24px',
              }}
            >
              {movies.map((movie, index) => (
              <Box
                key={movie.id}
                sx={{
                  width: {
                    xs: '100%',
                    sm: 'calc(50% - 12px)',
                    md: 'calc(33.333% - 16px)',
                    lg: 'calc(25% - 18px)',
                    xl: 'calc(20% - 19.2px)',
                  },
                }}
              >
                <StyledCard
                  onClick={() => navigate(`/movie/${movie.id}`)}
                  sx={{ 
                    cursor: 'pointer',
                    height: '100%',
                    display: 'flex',
                    flexDirection: 'column'
                  }}
                >
                  <CardMedia
                    component="img"
                    image={movie.posterUrl || `data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='300' height='450'%3E%3Crect width='300' height='450' fill='%23333'/%3E%3Ctext x='50%25' y='50%25' dominant-baseline='middle' text-anchor='middle' font-family='Arial' font-size='20' fill='%23666'%3ENo Image%3C/text%3E%3C/svg%3E`}
                    alt={movie.polishTitle}
                    sx={{ aspectRatio: '2/3', objectFit: 'cover' }}
                  />
                  <CardContent sx={{ flexGrow: 1 }}>
                    <Typography
                      variant="h6"
                      sx={{ 
                        fontWeight: 700, 
                        mb: 0.5, 
                        lineHeight: 1.2,
                        overflow: 'hidden',
                        textOverflow: 'ellipsis',
                        display: '-webkit-box',
                        WebkitLineClamp: 2,
                        WebkitBoxOrient: 'vertical'
                      }}
                    >
                      {movie.polishTitle}
                    </Typography>
                    <Typography
                      variant="body2"
                      sx={{ 
                        color: '#b0b0b0', 
                        mb: 1, 
                        fontSize: '0.9rem',
                        overflow: 'hidden',
                        textOverflow: 'ellipsis',
                        display: '-webkit-box',
                        WebkitLineClamp: 1,
                        WebkitBoxOrient: 'vertical'
                      }}
                    >
                      {movie.originalTitle}
                    </Typography>
                    <Typography variant="body2" sx={{ color: '#888888' }}>
                      {movie.releaseYear}
                    </Typography>
                  </CardContent>
                </StyledCard>
              </Box>
            ))}
          </Box>
          )}

          {totalPages > 1 && (
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 6 }}>
              <Pagination
                count={totalPages}
                page={page + 1}
                onChange={(e, value) => setPage(value - 1)}
                sx={{
                  '& .MuiPaginationItem-root': {
                    color: '#ffffff',
                    borderColor: '#444444',
                    '&:hover': {
                      backgroundColor: 'rgba(255, 255, 255, 0.1)',
                    },
                  },
                  '& .Mui-selected': {
                    backgroundColor: '#ffffff !important',
                    color: '#000000',
                  },
                }}
              />
            </Box>
          )}
        </ContentContainer>
      </PageContainer>
    </>
  );
};

export default MoviesPage;
