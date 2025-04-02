import React, { useState, useEffect } from 'react';
import { fetchMessages } from '../utils/socket'; // Import fetchMessages from socket utility
import './MessageInput.css';

const MessageInput = ({ userId, users, onSend }) => {
  const [messages, setMessages] = useState([]);

  useEffect(() => {
    // Fetch messages using the socket
    fetchMessages('room:lobby')
      .then((data) => setMessages(data))
      .catch((error) => console.error('Error fetching messages:', error));
  }, []);

  return (
    <div className="message-input">
      <div className="message-list">
        {messages.map((message, index) => (
          <div key={index} className="message-item">
            <strong>{message.senderName}:</strong> {message.text}
          </div>
        ))}
      </div>
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
