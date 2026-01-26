import apiClient from './config/apiClient';

const chatService = {
  sendMessage: async (message, conversationId = null, ragType = 'NORMAL') => {
    const data = await apiClient.post('/completions/chat', { 
      query: message,
      conversationId: conversationId,
      ragType: ragType
    });
    return {
      content: data.content,
      model: data.model,
      finishReason: data.finishReason,
      conversationId: data.conversationId,
    };
  },

  getConversations: async () => {
    const data = await apiClient.get('/conversations');
    return data;
  },

  getConversationDetail: async (id) => {
    const data = await apiClient.get(`/conversations/${id}`);
    return data;
  },
};

export const { sendMessage, getConversations, getConversationDetail } = chatService;
export default chatService;
