// 1. Defined joinChannel, connectSocket and joinChannel in socket.js
// 2. Changed channel name to nitifications

import React, { useState, useEffect, useRef } from 'react';
import { toast } from 'react-toastify';
import MessageList from '../components/MessageList';
import MessageInput from '../components/MessageInput';
// import { connectSocket, joinChannel, fetchMessages, pushMessage } from '../utils/socket'; // Import connectSocket and joinChannel
import { connectSocket, joinChannel,fetchMessages, pushMessage } from '../utils/socket'; // Import connectSocket and joinChannel
import './CssSheets/Messages.css';

const Messages = ({ userId, users }) => {
  const [messages, setMessages] = useState([]);
  const [connected, setConnected] = useState(false);
  const channelRef = useRef(null);

  useEffect(() => {
    let currentChannel;
    let reconnectTimer;

    const setupChannel = async () => {
      try {
        const socket = connectSocket();
        if (!socket) {
          throw new Error('Failed to create socket connection');
        }

        // const { channel } = await joinChannel('notifications:notifications', { user_id: userId });
        const { channel } = await joinChannel(userId);
        currentChannel = channel;
        channelRef.current = channel;
        setConnected(true);

        channel.on('new_message', (payload) => {
          //
          console.log(payload)
          const newMessage = {
            id: payload.id,
            content: payload.message,
            sender_id: payload.sender_id,
            sender_name: payload.sender_name,
            sender_avatar: payload.sender_avatar,
            inserted_at: payload.inserted_at
          };
          setMessages(prev => [...prev, newMessage]);
        });

        // const existingMessages = await fetchMessages('notifications:notifications', { user_id: userId });
        const existingMessages = await fetchMessages(userId);

        //
        console.log(existingMessages)

        setMessages(existingMessages.messages || []);
      } catch (error) {
        console.error('Channel setup failed:', error);
        setConnected(false);
        toast.error('Connection lost. Attempting to reconnect...');
        // Attempt to reconnect after 5 seconds
        reconnectTimer = setTimeout(setupChannel, 5000);
      }
    };

    setupChannel();

    return () => {
      if (currentChannel) {
        currentChannel.leave();
      }
      if (reconnectTimer) {
        clearTimeout(reconnectTimer);
      }
    };
  }, [userId]);

  // Handle sending a message
  const handleSendMessage = async (content) => {
    if (!content.trim() || !channelRef.current) return;
    
    try {
      await pushMessage(channelRef.current, 'new_message', {
        message: content,
        sender_id: userId,
        room_id: 'lobby'
      });
    } catch (error) {
      console.error('Failed to send message:', error);
      toast.error('Failed to send message. Please try again.');
    }
  };

  return (
    <div className="messages">
      {!connected && (
        <div className="connection-status">
          Attempting to connect...
        </div>
      )}
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

