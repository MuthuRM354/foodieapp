import locationService from '../../../services/locationService';
import api from '../../../services/api';

jest.mock('../../../services/api');

describe('LocationService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('getCurrentLocation returns coordinates', async () => {
    const mockGeolocation = {
      getCurrentPosition: jest.fn()
        .mockImplementationOnce((success) => success({
          coords: {
            latitude: 40.7128,
            longitude: -74.0060
          }
        }))
    };
    global.navigator.geolocation = mockGeolocation;

    const result = await locationService.getCurrentLocation();
    expect(result).toEqual({
      latitude: 40.7128,
      longitude: -74.0060
    });
  });

  test('searchNearbyRestaurants returns restaurants', async () => {
    const mockRestaurants = [
      { id: 1, name: 'Restaurant 1' },
      { id: 2, name: 'Restaurant 2' }
    ];

    api.get.mockResolvedValueOnce({ data: mockRestaurants });

    const coordinates = { latitude: 40.7128, longitude: -74.0060 };
    const result = await locationService.searchNearbyRestaurants(coordinates);

    expect(result).toEqual(mockRestaurants);
    expect(api.get).toHaveBeenCalledWith('/restaurants/nearby', {
      params: coordinates
    });
  });

  test('validateDeliveryAddress validates address', async () => {
    const mockAddress = {
      street: '123 Main St',
      city: 'New York',
      zipCode: '10001'
    };

    const mockResponse = { data: { valid: true } };
    api.post.mockResolvedValueOnce(mockResponse);

    const result = await locationService.validateDeliveryAddress(mockAddress);
    expect(result).toEqual(mockResponse.data);
  });
});
