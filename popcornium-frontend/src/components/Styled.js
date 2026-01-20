import { styled } from '@mui/material/styles';
import { Button, TextField, Card, Box, Paper } from '@mui/material';

export const theme = {
  palette: {
    primary: {
      main: '#000000',
      light: '#333333',
      dark: '#000000',
    },
    secondary: {
      main: '#ffffff',
      light: '#ffffff',
      dark: '#f5f5f5',
    },
    background: {
      default: '#000000',
      paper: '#1a1a1a',
    },
    text: {
      primary: '#ffffff',
      secondary: '#b0b0b0',
    },
  },
};

export const StyledButton = styled(Button)({
  backgroundColor: '#ffffff',
  color: '#000000',
  padding: '12px 24px',
  fontWeight: 600,
  textTransform: 'none',
  borderRadius: '8px',
  '&:hover': {
    backgroundColor: '#e0e0e0',
  },
  '&:disabled': {
    backgroundColor: '#555555',
    color: '#888888',
  },
});

export const StyledOutlinedButton = styled(Button)({
  backgroundColor: 'transparent',
  color: '#ffffff',
  padding: '12px 24px',
  fontWeight: 600,
  textTransform: 'none',
  borderRadius: '8px',
  border: '2px solid #ffffff',
  '&:hover': {
    backgroundColor: 'rgba(255, 255, 255, 0.1)',
    border: '2px solid #ffffff',
  },
});

export const StyledTextField = styled(TextField)({
  '& .MuiOutlinedInput-root': {
    color: '#ffffff',
    backgroundColor: '#1a1a1a',
    borderRadius: '8px',
    '& fieldset': {
      borderColor: '#444444',
    },
    '&:hover fieldset': {
      borderColor: '#666666',
    },
    '&.Mui-focused fieldset': {
      borderColor: '#ffffff',
    },
  },
  '& .MuiInputLabel-root': {
    color: '#b0b0b0',
  },
  '& .MuiInputLabel-root.Mui-focused': {
    color: '#ffffff',
  },
});

export const StyledCard = styled(Card)({
  backgroundColor: '#1a1a1a',
  color: '#ffffff',
  borderRadius: '12px',
  transition: 'transform 0.2s, box-shadow 0.2s',
  '&:hover': {
    transform: 'translateY(-4px)',
    boxShadow: '0 8px 24px rgba(255, 255, 255, 0.1)',
  },
});

export const StyledPaper = styled(Paper)({
  backgroundColor: '#1a1a1a',
  color: '#ffffff',
  borderRadius: '12px',
  padding: '24px',
});

export const PageContainer = styled(Box)({
  minHeight: '100vh',
  backgroundColor: '#000000',
  color: '#ffffff',
  paddingTop: '80px',
});

export const ContentContainer = styled(Box)({
  maxWidth: '1400px',
  margin: '0 auto',
  padding: '24px',
});
