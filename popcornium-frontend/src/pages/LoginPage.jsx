import { useState } from 'react';
import { Box, Typography, Link, Alert } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { StyledButton, StyledTextField, StyledPaper } from '../components/Styled';
import { styled } from '@mui/material/styles';

const LoginContainer = styled(Box)({
  minHeight: '100vh',
  backgroundColor: '#000000',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  padding: '24px',
});

const LoginBox = styled(StyledPaper)({
  maxWidth: '450px',
  width: '100%',
});

const LoginPage = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    const result = await login(email, password);

    if (result.success) {
      navigate('/movies');
    } else {
      setError(result.error?.message || 'Login failed. Please try again.');
    }

    setLoading(false);
  };

  return (
    <LoginContainer>
      <LoginBox>
        <Typography variant="h4" sx={{ fontWeight: 700, mb: 1, textAlign: 'center' }}>
          POPCORNIUM
        </Typography>
        <Typography variant="body1" sx={{ color: '#b0b0b0', mb: 4, textAlign: 'center' }}>
          Sign in to your account
        </Typography>

        {error && (
          <Alert severity="error" sx={{ mb: 3, backgroundColor: '#4a1a1a', color: '#ffffff' }}>
            {error}
          </Alert>
        )}

        <form onSubmit={handleLogin}>
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

          <StyledButton fullWidth type="submit" disabled={loading} sx={{ mb: 3 }}>
            {loading ? 'Signing in...' : 'Sign In'}
          </StyledButton>

          <Box sx={{ textAlign: 'center' }}>
            <Typography variant="body2" sx={{ color: '#b0b0b0' }}>
              Don't have an account?{' '}
              <Link
                component="button"
                type="button"
                onClick={() => navigate('/register')}
                sx={{
                  color: '#ffffff',
                  fontWeight: 600,
                  textDecoration: 'none',
                  '&:hover': {
                    textDecoration: 'underline',
                  },
                }}
              >
                Sign up
              </Link>
            </Typography>
          </Box>
        </form>
      </LoginBox>
    </LoginContainer>
  );
};

export default LoginPage;
