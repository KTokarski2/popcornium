import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Box, Typography, Grid, Avatar, IconButton, Divider, Chip, CircularProgress, Alert } from '@mui/material';
import { ThumbUp, ThumbDown, Add } from '@mui/icons-material';
import { PageContainer, ContentContainer, StyledButton, StyledPaper } from '../components/Styled';
import Navigation from '../components/Navigation';
import { styled } from '@mui/material/styles';
import { getMovieDetails, likeMovie, dislikeMovie } from '../api/movieService';

const PosterImage = styled('img')({
  width: '100%',
  borderRadius: '12px',
  boxShadow: '0 8px 32px rgba(0, 0, 0, 0.5)',
});

const CastMember = styled(Box)({
  textAlign: 'center',
  cursor: 'pointer',
  transition: 'transform 0.2s',
  '&:hover': {
    transform: 'scale(1.05)',
  },
});

const LikeButton = styled(IconButton)(({ active }) => ({
  color: active ? '#ffffff' : '#666666',
  backgroundColor: active ? '#333333' : 'transparent',
  border: '2px solid',
  borderColor: active ? '#ffffff' : '#444444',
  padding: '12px',
  '&:hover': {
    backgroundColor: '#333333',
    borderColor: '#ffffff',
    color: '#ffffff',
  },
}));

const MovieDetailsPage = () => {
  const { id } = useParams();
  const [movie, setMovie] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [likeStatus, setLikeStatus] = useState(null);

  useEffect(() => {
    const fetchMovieDetails = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await getMovieDetails(id);
        setMovie(data);
        setLikeStatus(data.userRating ? data.userRating.toLowerCase() : null);
      } catch (err) {
        setError(err.message || 'Failed to load movie details');
      } finally {
        setLoading(false);
      }
    };

    fetchMovieDetails();
  }, [id]);

  const handleLike = async (type) => {
    try {
      const newStatus = likeStatus === type ? null : type;
      
      if (newStatus === 'like') {
        await likeMovie(id);
      } else if (newStatus === 'dislike') {
        await dislikeMovie(id);
      }
      
      setLikeStatus(newStatus);
    } catch (error) {
      console.error('Error updating rating:', error);
    }
  };

  if (loading) {
    return (
      <>
        <Navigation />
        <PageContainer>
          <ContentContainer>
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}>
              <CircularProgress sx={{ color: '#ffffff' }} size={60} />
            </Box>
          </ContentContainer>
        </PageContainer>
      </>
    );
  }

  if (error) {
    return (
      <>
        <Navigation />
        <PageContainer>
          <ContentContainer>
            <Alert severity="error" sx={{ backgroundColor: '#333333', color: '#ffffff' }}>
              {error}
            </Alert>
          </ContentContainer>
        </PageContainer>
      </>
    );
  }

  if (!movie) {
    return (
      <>
        <Navigation />
        <PageContainer>
          <ContentContainer>
            <Typography variant="h5">Movie not found</Typography>
          </ContentContainer>
        </PageContainer>
      </>
    );
  }

  return (
    <>
      <Navigation />
      <PageContainer>
        <ContentContainer>
          <Grid container spacing={4}>
            <Grid item xs={12} md={4}>
              <PosterImage src={movie.posterUrl} alt={movie.polishTitle} />
              
              <Box sx={{ mt: 3, display: 'flex', gap: 2, justifyContent: 'center' }}>
                <LikeButton
                  active={likeStatus === 'like' ? 1 : 0}
                  onClick={() => handleLike('like')}
                >
                  <ThumbUp />
                </LikeButton>
                <LikeButton
                  active={likeStatus === 'dislike' ? 1 : 0}
                  onClick={() => handleLike('dislike')}
                >
                  <ThumbDown />
                </LikeButton>
              </Box>

              <StyledButton
                fullWidth
                startIcon={<Add />}
                sx={{ mt: 2 }}
              >
                Add to List
              </StyledButton>
            </Grid>

            <Grid item xs={12} md={8}>
              <Typography variant="h3" sx={{ fontWeight: 700, mb: 1 }}>
                {movie.polishTitle}
              </Typography>
              <Typography variant="h5" sx={{ color: '#b0b0b0', mb: 2 }}>
                {movie.originalTitle}
              </Typography>

              <Box sx={{ display: 'flex', gap: 2, mb: 3, flexWrap: 'wrap' }}>
                <Chip label={movie.productionYear} sx={{ backgroundColor: '#333333', color: '#ffffff' }} />
                <Chip
                  label={`★ ${movie.rating}/10`}
                  sx={{ backgroundColor: '#ffffff', color: '#000000', fontWeight: 700 }}
                />
                <Chip
                  label={`${movie.ratingCount} ratings`}
                  sx={{ backgroundColor: '#333333', color: '#ffffff' }}
                />
              </Box>

              <Typography variant="h6" sx={{ fontWeight: 700, mb: 1 }}>
                Director
              </Typography>
              <Typography variant="body1" sx={{ mb: 3, color: '#b0b0b0' }}>
                {movie.directorName}
              </Typography>

              <Divider sx={{ backgroundColor: '#333333', mb: 3 }} />

              <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
                Cast
              </Typography>
              <Grid container spacing={2} sx={{ mb: 4 }}>
                {movie.actors.map((actor, index) => (
                  <Grid item xs={6} sm={4} md={3} key={index}>
                    <CastMember>
                      <Avatar
                        src={actor.photoUrl || undefined}
                        alt={actor.name}
                        sx={{ width: 100, height: 100, margin: '0 auto', mb: 1 }}
                      >
                        {!actor.photoUrl && actor.name.charAt(0)}
                      </Avatar>
                      <Typography variant="body2" sx={{ fontWeight: 600 }}>
                        {actor.name}
                      </Typography>
                    </CastMember>
                  </Grid>
                ))}
              </Grid>

              <Divider sx={{ backgroundColor: '#333333', mb: 3 }} />

              <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
                About
              </Typography>
              {movie.descriptions.map((desc, index) => (
                <StyledPaper key={index} sx={{ mb: 2 }}>
                  <Typography variant="body1" sx={{ lineHeight: 1.8 }}>
                    {desc}
                  </Typography>
                </StyledPaper>
              ))}
            </Grid>
          </Grid>
        </ContentContainer>
      </PageContainer>
    </>
  );
};

export default MovieDetailsPage;
