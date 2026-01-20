import { useState } from 'react';
import { Box, Typography, Link } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { StyledButton, StyledTextField, StyledPaper } from '../components/Styled';
import { styled } from '@mui/material/styles';

const RegisterContainer = styled(Box)({
  minHeight: '100vh',
  backgroundColor: '#000000',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  padding: '24px',
});

const RegisterBox = styled(StyledPaper)({
  maxWidth: '450px',
  width: '100%',
});

const RegisterPage = () => {
  const navigate = useNavigate();
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleRegister = (e) => {
    e.preventDefault();
    console.log('Register attempt:', { name, email, password });
    navigate('/movies');
  };

  return (
    <RegisterContainer>
      <RegisterBox>
        <Typography variant="h4" sx={{ fontWeight: 700, mb: 1, textAlign: 'center' }}>
          POPCORNIUM
        </Typography>
        <Typography variant="body1" sx={{ color: '#b0b0b0', mb: 4, textAlign: 'center' }}>
          Create your account
        </Typography>

        <form onSubmit={handleRegister}>
          <StyledTextField
            fullWidth
            label="Name"
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            sx={{ mb: 3 }}
            required
          />

          <StyledTextField
            fullWidth
            label="Email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            sx={{ mb: 3 }}
            required
          />

          <StyledTextField
            fullWidth
            label="Password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            sx={{ mb: 4 }}
            required
          />

          <StyledButton fullWidth type="submit" sx={{ mb: 3 }}>
            Sign Up
          </StyledButton>

          <Box sx={{ textAlign: 'center' }}>
            <Typography variant="body2" sx={{ color: '#b0b0b0' }}>
              Already have an account?{' '}
              <Link
                component="button"
                type="button"
                onClick={() => navigate('/login')}
                sx={{
                  color: '#ffffff',
                  fontWeight: 600,
                  textDecoration: 'none',
                  '&:hover': {
                    textDecoration: 'underline',
                  },
                }}
              >
                Sign in
              </Link>
            </Typography>
          </Box>
        </form>
      </RegisterBox>
    </RegisterContainer>
  );
};

export default RegisterPage;
