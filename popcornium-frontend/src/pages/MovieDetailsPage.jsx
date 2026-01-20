import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { Box, Typography, Grid, Avatar, IconButton, Divider, Chip } from '@mui/material';
import { ThumbUp, ThumbDown, Add } from '@mui/icons-material';
import { PageContainer, ContentContainer, StyledButton, StyledPaper } from '../components/Styled';
import Navigation from '../components/Navigation';
import { styled } from '@mui/material/styles';

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
  const [likeStatus, setLikeStatus] = useState(null);

  const mockMovie = {
    id,
    polishTitle: 'Wspaniały Film',
    originalTitle: 'The Amazing Movie',
    year: 2024,
    rating: 8.5,
    ratingCount: 12543,
    poster: 'https://via.placeholder.com/400x600/333333/ffffff?text=Movie+Poster',
    director: 'John Director',
    cast: [
      { id: 1, name: 'Actor One', photo: 'https://via.placeholder.com/150/444444/ffffff?text=A1' },
      { id: 2, name: 'Actor Two', photo: 'https://via.placeholder.com/150/444444/ffffff?text=A2' },
      { id: 3, name: 'Actor Three', photo: 'https://via.placeholder.com/150/444444/ffffff?text=A3' },
      { id: 4, name: 'Actor Four', photo: 'https://via.placeholder.com/150/444444/ffffff?text=A4' },
      { id: 5, name: 'Actor Five', photo: 'https://via.placeholder.com/150/444444/ffffff?text=A5' },
    ],
    descriptions: [
      'This is the main plot description of the movie. It tells the story of an amazing adventure that captivates audiences worldwide.',
      'Critical acclaim: This film has been praised by critics for its outstanding cinematography and compelling narrative.',
      'Behind the scenes: The production took place over 6 months in various locations around the world.',
    ],
  };

  const handleLike = (type) => {
    setLikeStatus(likeStatus === type ? null : type);
  };

  return (
    <>
      <Navigation />
      <PageContainer>
        <ContentContainer>
          <Grid container spacing={4}>
            <Grid item xs={12} md={4}>
              <PosterImage src={mockMovie.poster} alt={mockMovie.polishTitle} />
              
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
                {mockMovie.polishTitle}
              </Typography>
              <Typography variant="h5" sx={{ color: '#b0b0b0', mb: 2 }}>
                {mockMovie.originalTitle}
              </Typography>

              <Box sx={{ display: 'flex', gap: 2, mb: 3, flexWrap: 'wrap' }}>
                <Chip label={mockMovie.year} sx={{ backgroundColor: '#333333', color: '#ffffff' }} />
                <Chip
                  label={`★ ${mockMovie.rating}/10`}
                  sx={{ backgroundColor: '#ffffff', color: '#000000', fontWeight: 700 }}
                />
                <Chip
                  label={`${mockMovie.ratingCount.toLocaleString()} ratings`}
                  sx={{ backgroundColor: '#333333', color: '#ffffff' }}
                />
              </Box>

              <Typography variant="h6" sx={{ fontWeight: 700, mb: 1 }}>
                Director
              </Typography>
              <Typography variant="body1" sx={{ mb: 3, color: '#b0b0b0' }}>
                {mockMovie.director}
              </Typography>

              <Divider sx={{ backgroundColor: '#333333', mb: 3 }} />

              <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
                Cast
              </Typography>
              <Grid container spacing={2} sx={{ mb: 4 }}>
                {mockMovie.cast.map((actor) => (
                  <Grid item xs={6} sm={4} md={3} key={actor.id}>
                    <CastMember>
                      <Avatar
                        src={actor.photo}
                        alt={actor.name}
                        sx={{ width: 100, height: 100, margin: '0 auto', mb: 1 }}
                      />
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
              {mockMovie.descriptions.map((desc, index) => (
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
