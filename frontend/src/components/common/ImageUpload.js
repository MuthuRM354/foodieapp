import React, { useState, useRef } from 'react';
import imageService from '../../services/imageService';
import { useToast } from '../../hooks/useToast';
import LoadingStates from './LoadingStates';

const ImageUpload = ({ onUploadSuccess, currentImage, aspectRatio = '1:1' }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [preview, setPreview] = useState(currentImage);
  const fileInputRef = useRef(null);
  const { showToast } = useToast();

  const validateFile = (file) => {
    const validTypes = ['image/jpeg', 'image/png', 'image/webp'];
    const maxSize = 5 * 1024 * 1024; // 5MB

    if (!validTypes.includes(file.type)) {
      throw new Error('Invalid file type. Please upload JPEG, PNG, or WebP images.');
    }

    if (file.size > maxSize) {
      throw new Error('File size too large. Maximum size is 5MB.');
    }
  };

  const handleFileChange = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    try {
      validateFile(file);

      // Create preview
      const previewUrl = URL.createObjectURL(file);
      setPreview(previewUrl);
      setLoading(true);
      setError(null);

      const response = await imageService.upload(file);
      onUploadSuccess(response.url);
      showToast('Image uploaded successfully', 'success');
    } catch (err) {
      setError(err.message);
      showToast(err.message, 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    e.currentTarget.classList.add('dragover');
  };

  const handleDragLeave = (e) => {
    e.preventDefault();
    e.currentTarget.classList.remove('dragover');
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.currentTarget.classList.remove('dragover');

    const file = e.dataTransfer.files[0];
    if (file) {
      fileInputRef.current.files = e.dataTransfer.files;
      handleFileChange({ target: { files: [file] } });
    }
  };

  return (
    <div className="image-upload-container">
      <div
        className="upload-area"
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
      >
        <input
          ref={fileInputRef}
          type="file"
          accept="image/jpeg,image/png,image/webp"
          onChange={handleFileChange}
          disabled={loading}
          className="image-upload-input"
        />

        {preview ? (
          <div className="image-preview" style={{ aspectRatio }}>
            <img src={preview} alt="Upload preview" />
            <div className="preview-overlay">
              <button onClick={() => fileInputRef.current.click()}>
                Change Image
              </button>
            </div>
          </div>
        ) : (
          <div className="upload-placeholder">
            <i className="fas fa-cloud-upload-alt"></i>
            <p>Drag & Drop or Click to Upload</p>
            <span>JPEG, PNG, WebP • Max 5MB</span>
          </div>
        )}

        {loading && <LoadingStates type="spinner" />}

        {error && (
          <div className="upload-error">
            <i className="fas fa-exclamation-circle"></i>
            {error}
          </div>
        )}
      </div>
    </div>
  );
};

export default ImageUpload;
