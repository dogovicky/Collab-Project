import React, { useState, useEffect } from 'react';
import MessageList from '../components/MessageList';
import MessageInput from '../components/MessageInput';
import socket from '../utils/socket';

const Messages = ({ userId, users }) => {
  const [messages, setMessages] = useState([]);

  // Listen for incoming messages
  useEffect(() => {
    // Listen for incoming messages from the server
    socket.on('receiveMessage', (message) => {
      setMessages((prev) => [...prev, message]);
    });

    // Cleanup to prevent memory leaks
    return () => {
      socket.off('receiveMessage');
    };
  }, []);

  // Handle sending a message
  const handleSendMessage = (content) => {
    if (content.trim() && socket.connected) { // Check if socket is connected
      const newMessage = {
        _id: Date.now().toString(), // Temporary ID before saving to DB
        senderId: userId,
        content,
        timestamp: new Date().toISOString(),
      };

      // Emit message to server
      socket.emit('sendMessage', newMessage);

      // Update local state immediately for faster UX
      setMessages((prev) => [...prev, newMessage]);
    }
  };

  return (
    <div className="flex flex-col h-screen bg-gray-50">
      {/* Message list */}
      <div className="flex-1 overflow-y-auto">
        <MessageList messages={messages} userId={userId} users={users} />
      </div>

      {/* Message input */}
      <MessageInput onSend={handleSendMessage} />
    </div>
  );
};

export default Messages;
