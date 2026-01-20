import { useState } from 'react';
import { Box, Typography, Link, Alert } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
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
  const { register } = useAuth();
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleRegister = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    const result = await register(name, email, password);

    if (result.success) {
      navigate('/movies');
    } else {
      setError(result.error?.message || 'Registration failed. Please try again.');
    }

    setLoading(false);
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

        {error && (
          <Alert severity="error" sx={{ mb: 3, backgroundColor: '#4a1a1a', color: '#ffffff' }}>
            {error}
          </Alert>
        )}

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

          <StyledButton fullWidth type="submit" disabled={loading} sx={{ mb: 3 }}>
            {loading ? 'Creating account...' : 'Sign Up'}
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
