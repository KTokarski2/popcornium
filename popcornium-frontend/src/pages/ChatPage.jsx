import { useState } from 'react';
import { Box, Typography, TextField, IconButton, Paper } from '@mui/material';
import { Send } from '@mui/icons-material';
import { PageContainer, ContentContainer } from '../components/Styled';
import Navigation from '../components/Navigation';
import { styled } from '@mui/material/styles';

const ChatContainer = styled(Box)({
  display: 'flex',
  height: 'calc(100vh - 80px)',
  gap: '24px',
});

const HistoryPanel = styled(Paper)({
  width: '300px',
  backgroundColor: '#1a1a1a',
  borderRadius: '12px',
  padding: '24px',
  overflowY: 'auto',
});

const HistoryItem = styled(Box)({
  padding: '12px',
  marginBottom: '8px',
  backgroundColor: '#262626',
  borderRadius: '8px',
  cursor: 'pointer',
  transition: 'background-color 0.2s',
  '&:hover': {
    backgroundColor: '#333333',
  },
});

const ChatPanel = styled(Box)({
  flex: 1,
  display: 'flex',
  flexDirection: 'column',
  backgroundColor: '#1a1a1a',
  borderRadius: '12px',
  overflow: 'hidden',
});

const MessagesContainer = styled(Box)({
  flex: 1,
  overflowY: 'auto',
  padding: '24px',
  display: 'flex',
  flexDirection: 'column',
  gap: '16px',
});

const Message = styled(Box)(({ isUser }) => ({
  maxWidth: '70%',
  padding: '12px 16px',
  borderRadius: '12px',
  backgroundColor: isUser ? '#ffffff' : '#262626',
  color: isUser ? '#000000' : '#ffffff',
  alignSelf: isUser ? 'flex-end' : 'flex-start',
}));

const InputContainer = styled(Box)({
  padding: '24px',
  borderTop: '1px solid #333333',
  display: 'flex',
  gap: '12px',
  alignItems: 'center',
});

const ChatPage = () => {
  const [messages, setMessages] = useState([
    { id: 1, text: 'Hello! How can I help you find movies today?', isUser: false },
  ]);
  const [input, setInput] = useState('');
  const [conversations] = useState([
    { id: 1, title: 'Action movies recommendations', date: '2026-01-20' },
    { id: 2, title: 'Best Sci-Fi from 2025', date: '2026-01-19' },
    { id: 3, title: 'Classic films discussion', date: '2026-01-18' },
  ]);

  const handleSend = () => {
    if (input.trim()) {
      const newMessage = {
        id: messages.length + 1,
        text: input,
        isUser: true,
      };
      setMessages([...messages, newMessage]);
      setInput('');

      setTimeout(() => {
        const aiResponse = {
          id: messages.length + 2,
          text: 'This is a placeholder response. API integration will be added later.',
          isUser: false,
        };
        setMessages((prev) => [...prev, aiResponse]);
      }, 500);
    }
  };

  return (
    <>
      <Navigation />
      <PageContainer>
        <ContentContainer>
          <ChatContainer>
            <HistoryPanel>
              <Typography variant="h6" sx={{ mb: 3, fontWeight: 700 }}>
                Conversation History
              </Typography>
              {conversations.map((conv) => (
                <HistoryItem key={conv.id}>
                  <Typography variant="body2" sx={{ fontWeight: 600, mb: 0.5 }}>
                    {conv.title}
                  </Typography>
                  <Typography variant="caption" sx={{ color: '#888888' }}>
                    {conv.date}
                  </Typography>
                </HistoryItem>
              ))}
            </HistoryPanel>

            <ChatPanel>
              <MessagesContainer>
                {messages.map((message) => (
                  <Message key={message.id} isUser={message.isUser}>
                    <Typography variant="body1">{message.text}</Typography>
                  </Message>
                ))}
              </MessagesContainer>

              <InputContainer>
                <TextField
                  fullWidth
                  placeholder="Ask about movies..."
                  value={input}
                  onChange={(e) => setInput(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && handleSend()}
                  sx={{
                    '& .MuiOutlinedInput-root': {
                      color: '#ffffff',
                      backgroundColor: '#262626',
                      borderRadius: '12px',
                      '& fieldset': { borderColor: '#444444' },
                      '&:hover fieldset': { borderColor: '#666666' },
                      '&.Mui-focused fieldset': { borderColor: '#ffffff' },
                    },
                  }}
                />
                <IconButton
                  onClick={handleSend}
                  sx={{
                    backgroundColor: '#ffffff',
                    color: '#000000',
                    '&:hover': { backgroundColor: '#e0e0e0' },
                    '&:disabled': { backgroundColor: '#555555' },
                  }}
                  disabled={!input.trim()}
                >
                  <Send />
                </IconButton>
              </InputContainer>
            </ChatPanel>
          </ChatContainer>
        </ContentContainer>
      </PageContainer>
    </>
  );
};

export default ChatPage;
