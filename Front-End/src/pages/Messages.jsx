import React, { useState, useEffect, useRef } from 'react';
import MessageList from '../components/MessageList';
import MessageInput from '../components/MessageInput';
import socket, { fetchMessages } from '../utils/socket'; // Import fetchMessages
import './CssSheets/Messages.css';

const Messages = ({ userId, users }) => {
  const [messages, setMessages] = useState([]);
  const channelRef = useRef(null);

  useEffect(() => {
    console.log('Messages component mounted');

    // Fetch messages via the socket
    const loadMessages = async () => {
      try {
        const data = await fetchMessages('room:lobby', { user_id: userId });
        setMessages(data);
      } catch (error) {
        console.error('Error fetching messages:', error);
      }
    };

    loadMessages();

    // Join the Phoenix channel
    const channel = socket.channel('room:lobby', { user_id: userId });
    channelRef.current = channel;
    channel
      .join()
      .receive('ok', () => console.log('Joined channel successfully'))
      .receive('error', (resp) => console.error('Unable to join channel', resp));

    // Listen for incoming messages
    const handleNewMessage = (payload) => {
      console.log('Received message:', payload);
      setMessages((prev) => [...prev, payload]);
    };

    channel.on('new_message', handleNewMessage);

    return () => {
      console.log('Messages component unmounted');
      channel.leave();
    };
  }, [userId]);

  // Handle sending a message
  const handleSendMessage = (content) => {
    if (content.trim()) {
      const newMessage = {
        _id: Date.now().toString(),
        senderId: userId,
        content,
        timestamp: new Date().toISOString(),
      };

      console.log('Sending message:', newMessage);
      channelRef.current.push('send_message', newMessage);

      // Update local state immediately for faster UX
      setMessages((prev) => [...prev, newMessage]);
    }
  };

  return (
    <div className="messages">
      {/* Message list */}
      <div className="messages__list">
        <MessageList messages={messages} userId={userId} users={users} />
      </div>

      {/* Message input */}
      <div className="messages__input">
        <MessageInput onSend={handleSendMessage} userId={userId} users={users} />
      </div>
    </div>
  );
};

export default Messages;

