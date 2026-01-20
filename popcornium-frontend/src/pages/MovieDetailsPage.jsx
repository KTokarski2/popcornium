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
    poster: `data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='400' height='600'%3E%3Crect width='400' height='600' fill='%23333'/%3E%3Ctext x='50%25' y='50%25' dominant-baseline='middle' text-anchor='middle' font-family='Arial' font-size='24' fill='%23666'%3EMovie Poster%3C/text%3E%3C/svg%3E`,
    director: 'John Director',
    cast: [
      { id: 1, name: 'Actor One', photo: `data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='150' height='150'%3E%3Crect width='150' height='150' fill='%23444'/%3E%3Ctext x='50%25' y='50%25' dominant-baseline='middle' text-anchor='middle' font-family='Arial' font-size='40' fill='%23888'%3EA1%3C/text%3E%3C/svg%3E` },
      { id: 2, name: 'Actor Two', photo: `data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='150' height='150'%3E%3Crect width='150' height='150' fill='%23444'/%3E%3Ctext x='50%25' y='50%25' dominant-baseline='middle' text-anchor='middle' font-family='Arial' font-size='40' fill='%23888'%3EA2%3C/text%3E%3C/svg%3E` },
      { id: 3, name: 'Actor Three', photo: `data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='150' height='150'%3E%3Crect width='150' height='150' fill='%23444'/%3E%3Ctext x='50%25' y='50%25' dominant-baseline='middle' text-anchor='middle' font-family='Arial' font-size='40' fill='%23888'%3EA3%3C/text%3E%3C/svg%3E` },
      { id: 4, name: 'Actor Four', photo: `data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='150' height='150'%3E%3Crect width='150' height='150' fill='%23444'/%3E%3Ctext x='50%25' y='50%25' dominant-baseline='middle' text-anchor='middle' font-family='Arial' font-size='40' fill='%23888'%3EA4%3C/text%3E%3C/svg%3E` },
      { id: 5, name: 'Actor Five', photo: `data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='150' height='150'%3E%3Crect width='150' height='150' fill='%23444'/%3E%3Ctext x='50%25' y='50%25' dominant-baseline='middle' text-anchor='middle' font-family='Arial' font-size='40' fill='%23888'%3EA5%3C/text%3E%3C/svg%3E` },
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
