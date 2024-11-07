import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import LocationSearch from '../../../components/customer/LocationSearch';
import { searchLocation } from '../../../services/zomatoApi';

jest.mock('../../../services/zomatoApi');

describe('LocationSearch Component', () => {
  test('renders search input', () => {
    render(<LocationSearch />);
    expect(screen.getByPlaceholderText(/enter your location/i)).toBeInTheDocument();
  });

  test('performs location search', async () => {
    const mockLocations = [
      { id: 1, name: 'Location 1', address: 'Address 1' },
      { id: 2, name: 'Location 2', address: 'Address 2' }
    ];

    searchLocation.mockResolvedValueOnce(mockLocations);

    render(<LocationSearch />);

    fireEvent.change(screen.getByPlaceholderText(/enter your location/i), {
      target: { value: 'test location' }
    });

    fireEvent.click(screen.getByRole('button', { name: /search/i }));

    await waitFor(() => {
      expect(screen.getByText('Location 1')).toBeInTheDocument();
      expect(screen.getByText('Location 2')).toBeInTheDocument();
    });
  });
});
