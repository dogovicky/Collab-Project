import React, { useState } from 'react';
import MessageList from '../components/MessageList';
import socket from '../utils/socket';

const MessageInput = ({ userId, users }) => {
  const [messages, setMessages] = useState([]);

  // Handle message sending
  const handleSendMessage = (content) => {
    const newMessage = {
      _id: Date.now().toString(),
      senderId: userId,
      content,
      timestamp: new Date().toISOString(),
    };

    // Emit message to the server
    socket.emit('sendMessage', newMessage);

    // Update state
    setMessages((prev) => [...prev, newMessage]);
  };

  return (
    <div className="flex flex-col h-screen bg-gray-50">
      <MessageList messages={messages} userId={userId} users={users} />
      <MessageInput onSend={handleSendMessage} />
    </div>
  );
};

export default MessageInput;
