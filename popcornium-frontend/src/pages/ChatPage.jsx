import { useState, useEffect } from 'react';
import { Box, Typography, TextField, IconButton, Paper, CircularProgress, Button, Select, MenuItem, FormControl, InputLabel } from '@mui/material';
import { Send, Add } from '@mui/icons-material';
import { PageContainer, ContentContainer } from '../components/Styled';
import Navigation from '../components/Navigation';
import { styled } from '@mui/material/styles';
import { sendMessage, getConversations, getConversationDetail } from '../api/chatService';

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

const Message = styled(Box, {
  shouldForwardProp: (prop) => prop !== 'isUser',
})(({ isUser }) => ({
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
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [conversations, setConversations] = useState([]);
  const [currentConversationId, setCurrentConversationId] = useState(null);
  const [loadingConversations, setLoadingConversations] = useState(true);
  const [ragType, setRagType] = useState('NORMAL');

  useEffect(() => {
    fetchConversations();
  }, []);

  const fetchConversations = async () => {
    try {
      setLoadingConversations(true);
      const data = await getConversations();
      setConversations(data);
    } catch (error) {
      console.error('Error fetching conversations:', error);
    } finally {
      setLoadingConversations(false);
    }
  };

  const loadConversation = async (conversationId) => {
    try {
      setLoading(true);
      const data = await getConversationDetail(conversationId);
      setCurrentConversationId(conversationId);
      setMessages(
        data.messages.map((msg, index) => ({
          id: index + 1,
          text: msg.content,
          isUser: msg.source === 'USER',
        }))
      );
    } catch (error) {
      console.error('Error loading conversation:', error);
    } finally {
      setLoading(false);
    }
  };

  const startNewConversation = () => {
    setCurrentConversationId(null);
    setMessages([
      { id: 1, text: 'Hello! How can I help you find movies today?', isUser: false },
    ]);
  };

  const handleSend = async () => {
    if (input.trim() && !loading) {
      const userMessage = {
        id: messages.length + 1,
        text: input,
        isUser: true,
      };
      setMessages([...messages, userMessage]);
      const messageText = input;
      setInput('');
      setLoading(true);

      try {
        const response = await sendMessage(messageText, currentConversationId, ragType);
        
        const aiResponse = {
          id: messages.length + 2,
          text: response.content,
          isUser: false,
        };
        setMessages((prev) => [...prev, aiResponse]);
        
        // Update conversationId if it's a new conversation
        if (!currentConversationId && response.conversationId) {
          setCurrentConversationId(response.conversationId);
          fetchConversations(); // Refresh conversation list
        }
      } catch (error) {
        console.error('Error sending message:', error);
        const errorMessage = {
          id: messages.length + 2,
          text: 'Sorry, I encountered an error. Please try again.',
          isUser: false,
        };
        setMessages((prev) => [...prev, errorMessage]);
      } finally {
        setLoading(false);
      }
    }
  };

  return (
    <>
      <Navigation />
      <PageContainer>
        <ContentContainer>
          <ChatContainer>
            <HistoryPanel>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Typography variant="h6" sx={{ fontWeight: 700 }}>
                  Conversations
                </Typography>
                <IconButton
                  onClick={startNewConversation}
                  sx={{
                    backgroundColor: '#ffffff',
                    color: '#000000',
                    width: 32,
                    height: 32,
                    '&:hover': { backgroundColor: '#e0e0e0' },
                  }}
                >
                  <Add />
                </IconButton>
              </Box>
              
              {loadingConversations ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', py: 2 }}>
                  <CircularProgress size={24} sx={{ color: '#ffffff' }} />
                </Box>
              ) : conversations.length === 0 ? (
                <Typography variant="body2" sx={{ color: '#888888', textAlign: 'center' }}>
                  No conversations yet
                </Typography>
              ) : (
                conversations.map((conv) => (
                  <HistoryItem 
                    key={conv.id} 
                    onClick={() => loadConversation(conv.id)}
                    sx={{
                      backgroundColor: currentConversationId === conv.id ? '#333333' : '#262626',
                    }}
                  >
                    <Typography variant="body2" sx={{ fontWeight: 600, mb: 0.5 }}>
                      {conv.title}
                    </Typography>
                    <Typography variant="caption" sx={{ color: '#888888' }}>
                      {new Date(conv.createdAt).toLocaleDateString()}
                    </Typography>
                  </HistoryItem>
                ))
              )}
            </HistoryPanel>

            <ChatPanel>
              <Box sx={{ 
                padding: '16px 24px', 
                borderBottom: '1px solid #333333',
                display: 'flex',
                alignItems: 'center',
                gap: 2
              }}>
                <Typography variant="body2" sx={{ color: '#ffffff', fontWeight: 600 }}>
                  RAG Type:
                </Typography>
                <Select
                  value={ragType}
                  onChange={(e) => setRagType(e.target.value)}
                  size="small"
                  sx={{
                    minWidth: 140,
                    color: '#ffffff',
                    backgroundColor: '#262626',
                    borderRadius: '8px',
                    '& .MuiOutlinedInput-notchedOutline': { borderColor: '#444444' },
                    '&:hover .MuiOutlinedInput-notchedOutline': { borderColor: '#666666' },
                    '&.Mui-focused .MuiOutlinedInput-notchedOutline': { borderColor: '#ffffff' },
                    '& .MuiSvgIcon-root': { color: '#ffffff' },
                  }}
                  MenuProps={{
                    PaperProps: {
                      sx: {
                        backgroundColor: '#262626',
                        '& .MuiMenuItem-root': {
                          color: '#ffffff',
                          '&:hover': { backgroundColor: '#333333' },
                          '&.Mui-selected': { backgroundColor: '#444444' },
                          '&.Mui-selected:hover': { backgroundColor: '#555555' },
                        },
                      },
                    },
                  }}
                >
                  <MenuItem value="NORMAL">Normal</MenuItem>
                  <MenuItem value="GRAPH">Graph</MenuItem>
                </Select>
                <Typography variant="caption" sx={{ color: '#888888', ml: 'auto' }}>
                  {ragType === 'GRAPH' ? 'Using graph-based retrieval' : 'Using standard retrieval'}
                </Typography>
              </Box>
              <MessagesContainer>
                {messages.map((message) => (
                  <Message key={message.id} isUser={message.isUser}>
                    <Typography variant="body1">{message.text}</Typography>
                  </Message>
                ))}
                {loading && (
                  <Box sx={{ display: 'flex', justifyContent: 'flex-start', alignItems: 'center', gap: 2 }}>
                    <CircularProgress size={20} sx={{ color: '#ffffff' }} />
                    <Typography variant="body2" sx={{ color: '#888888' }}>
                      Thinking...
                    </Typography>
                  </Box>
                )}
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
                  disabled={!input.trim() || loading}
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
