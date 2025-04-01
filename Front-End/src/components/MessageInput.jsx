import React from 'react';
import './MessageInput.css';

const MessageInput = ({ userId, users, onSend }) => {
  return (
    <div className="message-input">
      <input
        type="text"
        className="message-input__field"
        placeholder="Type a message..."
        onKeyPress={(e) => {
          if (e.key === 'Enter' && e.target.value.trim()) {
            onSend(e.target.value.trim());
            e.target.value = '';
          }
        }}
      />
      <button
        className="message-input__button"
        onClick={() => {
          const input = document.querySelector('input[type="text"]');
          if (input.value.trim()) {
            onSend(input.value.trim());
            input.value = '';
          }
        }}
      >
        Send
      </button>
    </div>
  );
};

export default MessageInput;
