import React, { useState } from 'react';
import imageService from '../../services/imageService';

const ImageUpload = ({ onUploadSuccess }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [preview, setPreview] = useState(null);

  const handleFileChange = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    // Create preview
    const previewUrl = URL.createObjectURL(file);
    setPreview(previewUrl);
    setLoading(true);

    try {
      const response = await imageService.upload(file);
      onUploadSuccess(response.url);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="image-upload-container">
      <input
        type="file"
        accept="image/*"
        onChange={handleFileChange}
        disabled={loading}
        className="image-upload-input"
      />

      {preview && (
        <div className="image-preview">
          <img src={preview} alt="Upload preview" />
        </div>
      )}

      {loading && (
        <div className="upload-status">
          <span className="loading-spinner"></span>
          Uploading...
        </div>
      )}

      {error && (
        <div className="upload-error">
          Error: {error}
        </div>
      )}
    </div>
  );
};

export default ImageUpload;
