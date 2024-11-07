import imageService from '../../../services/imageService';
import api from '../../../services/api';

jest.mock('../../../services/api');

describe('ImageService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('uploadImage handles file upload', async () => {
    const mockFile = new File(['test'], 'test.jpg', { type: 'image/jpeg' });
    const mockResponse = { data: { imageUrl: 'https://example.com/image.jpg' } };

    api.post.mockResolvedValueOnce(mockResponse);

    const result = await imageService.uploadImage(mockFile);

    expect(result).toBe(mockResponse.data.imageUrl);
    expect(api.post).toHaveBeenCalledWith(
      '/upload/image',
      expect.any(FormData),
      expect.any(Object)
    );
  });

  test('optimizeImage returns optimized URL', async () => {
    const mockImageUrl = 'https://example.com/image.jpg';
    const mockOptions = { width: 800, quality: 80 };
    const mockResponse = {
      data: { optimizedUrl: 'https://example.com/optimized.jpg' }
    };

    api.post.mockResolvedValueOnce(mockResponse);

    const result = await imageService.optimizeImage(mockImageUrl, mockOptions);
    expect(result).toBe(mockResponse.data.optimizedUrl);
  });

  test('generateThumbnail creates thumbnail', async () => {
    const mockImageUrl = 'https://example.com/image.jpg';
    const mockDimensions = { width: 200, height: 200 };
    const mockResponse = {
      data: { thumbnailUrl: 'https://example.com/thumbnail.jpg' }
    };

    api.post.mockResolvedValueOnce(mockResponse);

    const result = await imageService.generateThumbnail(mockImageUrl, mockDimensions);
    expect(result).toBe(mockResponse.data.thumbnailUrl);
  });
});
