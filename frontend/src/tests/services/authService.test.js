import authService from '../../../services/authService';
import api from '../../../services/api';

jest.mock('../../../services/api');

describe('AuthService', () => {
  beforeEach(() => {
    localStorage.clear();
    jest.clearAllMocks();
  });

  test('login success', async () => {
    const mockResponse = {
      data: {
        token: 'test-token',
        user: { id: 1, name: 'Test User' }
      }
    };
    api.post.mockResolvedValueOnce(mockResponse);

    const result = await authService.login({
      email: 'test@example.com',
      password: 'password'
    });

    expect(result).toEqual(mockResponse.data);
    expect(localStorage.getItem('token')).toBe('test-token');
    expect(localStorage.getItem('user')).toBe(JSON.stringify(mockResponse.data.user));
  });

  test('register success', async () => {
    const mockUser = {
      name: 'Test User',
      email: 'test@example.com',
      password: 'password'
    };

    const mockResponse = {
      data: { message: 'Registration successful' }
    };

    api.post.mockResolvedValueOnce(mockResponse);

    const result = await authService.register(mockUser);
    expect(result).toEqual(mockResponse.data);
  });

  test('logout clears localStorage', () => {
    localStorage.setItem('token', 'test-token');
    localStorage.setItem('user', JSON.stringify({ id: 1 }));

    authService.logout();

    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('user')).toBeNull();
  });
});
