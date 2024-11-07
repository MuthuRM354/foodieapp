import axiosInstance from './api/axiosConfig';

const imageService = {
  upload: async (file, type = 'general') => {
    const formData = new FormData();
    formData.append('image', file);
    formData.append('type', type);

    const response = await axiosInstance.post('/images/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    return response.data;
  },

  delete: async (imageId) => {
    const response = await axiosInstance.delete(`/images/${imageId}`);
    return response.data;
  },

  optimize: async (imageUrl, options = {}) => {
    const response = await axiosInstance.post('/images/optimize', {
      imageUrl,
      ...options
    });
    return response.data;
  },

  getPresignedUrl: async (fileName, fileType) => {
    const response = await axiosInstance.post('/images/presigned-url', {
      fileName,
      fileType
    });
    return response.data;
  }
};

export default imageService;
