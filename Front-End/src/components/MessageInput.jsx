import React from 'react';
import './MessageInput.css';

const MessageInput = ({ onSend }) => {
  const handleSubmit = (message) => {
    if (message.trim()) {
      onSend(message.trim());
    }
  };

  return (
    <div className="message-input">
      <input
        type="text"
        className="message-input__field"
        placeholder="Type a message..."
        onKeyPress={(e) => {
          if (e.key === 'Enter') {
            handleSubmit(e.target.value);
            e.target.value = '';
          }
        }}
      />
      <button
        className="message-input__button"
        onClick={() => {
          const input = document.querySelector('.message-input__field');
          handleSubmit(input.value);
          input.value = '';
        }}
      >
        Send
      </button>
    </div>
  );
};

export default MessageInput;
