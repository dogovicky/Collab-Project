import React, { useEffect, useRef, useCallback } from 'react';
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

  const formatMessage = (msg) => ({
    id: msg.id || msg._id,
    content: msg.content || msg.message,
    senderId: msg.sender_id || msg.senderId,
    timestamp: msg.inserted_at || msg.timestamp,
    senderName: msg.sender_name || getUserInfo(msg.senderId)?.name || 'Unknown',
    avatar: msg.sender_avatar || getUserInfo(msg.senderId)?.avatar || '/default-avatar.png'
  });

  return (
    <div className="flex flex-col gap-2 p-4 h-[500px] overflow-y-auto bg-gray-100 rounded-lg shadow-inner">
      {messages.map((msg) => {
        const formattedMsg = formatMessage(msg);
        const isSentByCurrentUser = formattedMsg.senderId === userId;

        return (
          <div
            key={formattedMsg.id}
            className={`flex items-end gap-2 ${
              isSentByCurrentUser ? 'justify-end' : 'justify-start'
            }`}
          >
            {/* Show avatar only for received messages */}
            {!isSentByCurrentUser && (
              <img
                src={formattedMsg.avatar} // Fallback avatar
                alt={formattedMsg.senderName}
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
                  {formattedMsg.senderName}
                </span>
              )}
              <p className="text-sm">{formattedMsg.content}</p>
              <span className="text-xs text-gray-200 block mt-1">
                {new Date(formattedMsg.timestamp).toLocaleTimeString([], {
                  hour: '2-digit',
                  minute: '2-digit',
                })}
              </span>
            </div>
            {/* Show avatar for sent messages */}
            {isSentByCurrentUser && (
              <img
                src={formattedMsg.avatar}
                alt={formattedMsg.senderName}
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