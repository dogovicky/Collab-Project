import React, { useState } from 'react';
import { createPost } from '../api/authA'; // Adjust the relative path as needed

const CreatePost = ({ updateFeed }) => {
  const [text, setText] = useState('');
  const [file, setFile] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!text.trim() && !file) {
      alert('Please provide some text or upload a file.');
      return;
    }
    const formData = new FormData();
    formData.append('text', text);
    if (file) {
      formData.append('file', file);
    }

    const tempPost = {
      id: Date.now(),
      text,
      file: file ? URL.createObjectURL(file) : null,
      createdAt: new Date().toISOString(),
    };
    updateFeed((prevFeed) => [tempPost, ...prevFeed]);

    try {
      const newPost = await createPost(formData);
      console.log('Post created successfully:', newPost); // Debugging log
      updateFeed((prevFeed) =>
        prevFeed.map((post) => (post.id === tempPost.id ? newPost : post))
      );
    } catch (error) {
      updateFeed((prevFeed) => prevFeed.filter((post) => post.id !== tempPost.id));
    } finally {
      setText('');
      setFile(null);
      setIsModalOpen(false);
    }
  };

  return (
    <>
      <button
        onClick={() => setIsModalOpen(true)}
        style={{
          position: 'fixed',
          bottom: '20px',
          right: '20px',
          borderRadius: '50%',
          width: '60px',
          height: '60px',
          backgroundColor: '#007bff',
          color: 'white',
          fontSize: '24px',
          border: 'none',
          cursor: 'pointer',
        }}
      >
        +
      </button>

      {isModalOpen && (
        <div
          style={{
            position: 'fixed',
            top: 0,
            left: 0,
            width: '100%',
            height: '100%',
            backgroundColor: 'rgba(0, 0, 0, 0.5)',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
          }}
        >
          <div
            style={{
              backgroundColor: 'white',
              padding: '20px',
              borderRadius: '8px',
              width: '400px',
              boxShadow: '0 4px 8px rgba(0, 0, 0, 0.2)',
            }}
          >
            <form onSubmit={handleSubmit}>
              <textarea
                value={text}
                onChange={(e) => setText(e.target.value)}
                placeholder="What's on your mind?"
                style={{ width: '100%', height: '100px', marginBottom: '10px' }}
              />
              <input
                type="file"
                onChange={(e) => setFile(e.target.files[0])}
                accept="image/*,video/*"
                style={{ marginBottom: '10px' }}
              />
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <button type="button" onClick={() => setIsModalOpen(false)}>
                  Cancel
                </button>
                <button type="submit">Post</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </>
  );
};

export default CreatePost;
