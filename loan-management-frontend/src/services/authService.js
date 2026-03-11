import api from './api';

class AuthService {
  login(username, password) {
    const token = btoa(`${username}:${password}`);  // btoa encodes the string to Base64
    localStorage.setItem('authToken', token);
    return token;
  }

  logout() {
    localStorage.removeItem('authToken');
  }

  isAuthenticated() {
    return !!localStorage.getItem('authToken');
  }

  getToken() {
    return localStorage.getItem('authToken');
  }
}

export default new AuthService();