import { render, screen, fireEvent } from '@testing-library/react';
import { Provider } from 'react-redux';
import Login from '../../../components/auth/Login';
import store from '../../../redux/store';

describe('Login Component', () => {
  test('renders login form', () => {
    render(
      <Provider store={store}>
        <Login />
      </Provider>
    );

    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /sign in/i })).toBeInTheDocument();
  });

  test('handles form submission', async () => {
    render(
      <Provider store={store}>
        <Login />
      </Provider>
    );

    fireEvent.change(screen.getByLabelText(/email/i), {
      target: { value: 'test@example.com' },
    });
    fireEvent.change(screen.getByLabelText(/password/i), {
      target: { value: 'password123' },
    });
    fireEvent.click(screen.getByRole('button', { name: /sign in/i }));
  });
});
