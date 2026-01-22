import apiClient from './config/apiClient';

const authService = {
  login: async (email, password) => {
    const data = await apiClient.post('/auth/login', { email, password });
    const token = data.token || data.accessToken || data.jwt;
    
    if (token) {
      localStorage.setItem('accessToken', token);
      if (data.user) {
        localStorage.setItem('user', JSON.stringify(data.user));
      } else {
        localStorage.setItem('user', JSON.stringify({ email }));
      }
    }
    return data;
  },

  register: async (name, email, password) => {
    const data = await apiClient.post('/auth/register', { name, email, password });
    const token = data.token || data.accessToken || data.jwt;
    
    if (token) {
      localStorage.setItem('accessToken', token);
      if (data.user) {
        localStorage.setItem('user', JSON.stringify(data.user));
      } else {
        localStorage.setItem('user', JSON.stringify({ name, email }));
      }
    }
    return data;
  },

  logout: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('user');
  },

  getCurrentUser: () => {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  },

  getToken: () => {
    return localStorage.getItem('accessToken');
  },

  isAuthenticated: () => {
    return !!localStorage.getItem('accessToken');
  },
};

export default authService;
