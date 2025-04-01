import React, { useEffect, useRef, useCallback } from 'react';
import socket from '../utils/socket'; // Ensure this points to the updated socket.js
import './MessageList.css'; 
const MessageList = ({ messages, userId, users }) => {
  const messagesEndRef = useRef(null);

  // Auto-scroll to the latest message
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  // Function to get user info (name, avatar)
  const getUserInfo = useCallback(
    (id) => {
      return users.find((user) => user._id === id) || {};
    },
    [users]
  );

  return (
    <div className="flex flex-col gap-2 p-4 h-[500px] overflow-y-auto bg-gray-100 rounded-lg shadow-inner">
      {messages.map((msg) => {
        const user = getUserInfo(msg.senderId);
        const isSentByCurrentUser = msg.senderId === userId;

        return (
          <div
            key={msg._id}
            className={`flex items-end gap-2 ${
              isSentByCurrentUser ? 'justify-end' : 'justify-start'
            }`}
          >
            {/* Show avatar only for received messages */}
            {!isSentByCurrentUser && (
              <img
                src={user.avatar || '/default-avatar.png'} // Fallback avatar
                alt={user.name}
                className="w-8 h-8 rounded-full"
              />
            )}
            <div
              className={`max-w-[70%] p-2 rounded-xl text-white ${
                isSentByCurrentUser ? 'bg-blue-500' : 'bg-gray-500'
              }`}
            >
              {/* Display username */}
              {!isSentByCurrentUser && (
                <span className="block text-xs text-gray-300">
                  {user.name || 'Unknown'}
                </span>
              )}
              <p className="text-sm">{msg.content}</p>
              <span className="text-xs text-gray-200 block mt-1">
                {new Date(msg.timestamp).toLocaleTimeString([], {
                  hour: '2-digit',
                  minute: '2-digit',
                })}
              </span>
            </div>
            {/* Show avatar for sent messages */}
            {isSentByCurrentUser && (
              <img
                src={user.avatar || '/default-avatar.png'}
                alt={user.name}
                className="w-8 h-8 rounded-full"
              />
            )}
          </div>
        );
      })}
      {/* Invisible element for auto-scrolling */}
      <div ref={messagesEndRef} />
    </div>
  );
};

export default MessageList;