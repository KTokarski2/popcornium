import { AppBar, Toolbar, Typography, Box, IconButton } from '@mui/material';
import { Chat, Movie } from '@mui/icons-material';
import { useNavigate, useLocation } from 'react-router-dom';
import { styled } from '@mui/material/styles';

const StyledAppBar = styled(AppBar)({
  backgroundColor: '#000000',
  borderBottom: '1px solid #333333',
});

const NavButton = styled(IconButton)(({ active }) => ({
  color: active ? '#ffffff' : '#666666',
  marginLeft: '16px',
  padding: '12px',
  '&:hover': {
    color: '#ffffff',
    backgroundColor: 'rgba(255, 255, 255, 0.1)',
  },
}));

const Navigation = () => {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <StyledAppBar position="fixed">
      <Toolbar>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, flexGrow: 1 }}>
          <Typography variant="h6" component="div" sx={{ fontSize: 28 }}>
            🍿
          </Typography>
          <Typography variant="h6" component="div" sx={{ fontWeight: 700 }}>
            POPCORNIUM
          </Typography>
        </Box>
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <NavButton
            active={location.pathname === '/movies' || location.pathname.startsWith('/movie/') ? 1 : 0}
            onClick={() => navigate('/movies')}
          >
            <Movie />
          </NavButton>
          <NavButton
            active={location.pathname === '/chat' ? 1 : 0}
            onClick={() => navigate('/chat')}
          >
            <Chat />
          </NavButton>
        </Box>
      </Toolbar>
    </StyledAppBar>
  );
};

export default Navigation;
