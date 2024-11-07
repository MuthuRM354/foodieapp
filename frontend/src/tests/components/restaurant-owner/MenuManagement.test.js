import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import MenuManagement from '../../../components/restaurant-owner/MenuManagement';

describe('MenuManagement Component', () => {
  test('renders menu management form', () => {
    render(<MenuManagement />);
    expect(screen.getByText(/add new item/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/item name/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/price/i)).toBeInTheDocument();
  });

  test('handles item addition', async () => {
    render(<MenuManagement />);

    fireEvent.change(screen.getByPlaceholderText(/item name/i), {
      target: { value: 'New Dish' }
    });

    fireEvent.change(screen.getByPlaceholderText(/price/i), {
      target: { value: '9.99' }
    });

    fireEvent.click(screen.getByRole('button', { name: /add item/i }));

    await waitFor(() => {
      expect(screen.getByText('New Dish')).toBeInTheDocument();
    });
  });

  test('handles image upload', async () => {
    render(<MenuManagement />);

    const file = new File(['test'], 'test.png', { type: 'image/png' });
    const input = screen.getByLabelText(/upload image/i);

    fireEvent.change(input, { target: { files: [file] } });

    await waitFor(() => {
      expect(screen.getByAltText('Preview')).toBeInTheDocument();
    });
  });
});
