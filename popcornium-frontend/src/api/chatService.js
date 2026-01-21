import apiClient from './config/apiClient';

const chatService = {
  sendMessage: async (message) => {
    const data = await apiClient.post('/completions/chat', { query: message });
    return {
      content: data.content,
      model: data.model,
      finishReason: data.finishReason,
    };
  },
};

export const { sendMessage } = chatService;
export default chatService;
