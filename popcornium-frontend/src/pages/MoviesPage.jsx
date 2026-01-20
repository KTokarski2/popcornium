import { useState } from 'react';
import { Box, Typography, TextField, Pagination, CardMedia, CardContent } from '@mui/material';
import { Search } from '@mui/icons-material';
import { PageContainer, ContentContainer, StyledCard } from '../components/Styled';
import Navigation from '../components/Navigation';
import { useNavigate } from 'react-router-dom';

const MoviesPage = () => {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');
  const [page, setPage] = useState(1);

  const mockMovies = Array.from({ length: 150 }, (_, i) => ({
    id: i + 1,
    polishTitle: `Polski Tytuł ${i + 1}`,
    originalTitle: `Original Title ${i + 1}`,
    year: 2020 + (i % 6),
    poster: `data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='300' height='450'%3E%3Crect width='300' height='450' fill='%23333'/%3E%3Ctext x='50%25' y='50%25' dominant-baseline='middle' text-anchor='middle' font-family='Arial' font-size='20' fill='%23666'%3EMovie ${i + 1}%3C/text%3E%3C/svg%3E`,
  }));

  const filteredMovies = mockMovies.filter(
    (movie) =>
      movie.polishTitle.toLowerCase().includes(searchQuery.toLowerCase()) ||
      movie.originalTitle.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const moviesPerPage = 50;
  const totalPages = Math.ceil(filteredMovies.length / moviesPerPage);
  const displayedMovies = filteredMovies.slice(
    (page - 1) * moviesPerPage,
    page * moviesPerPage
  );

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
                setPage(1);
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

          <Box
            sx={{
              display: 'flex',
              flexWrap: 'wrap',
              gap: '24px',
            }}
          >
            {displayedMovies.map((movie) => (
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
                    image={movie.poster}
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
                      {movie.year}
                    </Typography>
                  </CardContent>
                </StyledCard>
              </Box>
            ))}
          </Box>

          {totalPages > 1 && (
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 6 }}>
              <Pagination
                count={totalPages}
                page={page}
                onChange={(e, value) => setPage(value)}
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
