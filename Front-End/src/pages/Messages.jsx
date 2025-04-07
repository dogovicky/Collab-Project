// 1. Defined joinChannel, connectSocket and joinChannel in socket.js
// 2. Changed channel name to notifications

import React, { useState, useEffect, useRef } from 'react';
import { toast } from 'react-toastify';
import MessageList from '../components/MessageList';
import MessageInput from '../components/MessageInput';
import { connectSocket, joinChannel, fetchMessages, pushMessage } from '../utils/socket'; // Import connectSocket and joinChannel
import { standardizeMessage } from '../utils/messageFormatter';
import './CssSheets/Messages.css';

const Messages = ({ userId, users }) => {
  const [messages, setMessages] = useState([]); //all messages will be shown
  const [connected, setConnected] = useState(false); //are we connected to the socket
  const channelRef = useRef(null); //hold the active chat room

  useEffect(() => {
    let currentChannel;
    let reconnectTimer;
//async function to set up the channel. it will be called when the component mounts
    const setupChannel = async () => {
      try {
        const socket = connectSocket();
        if (!socket) {
          throw new Error('Failed to create socket connection');
        }
//joins the channel for current user
        const { channel } = await joinChannel(userId);
        currentChannel = channel;
        channelRef.current = channel;// Store the channel for reference for later use
        setConnected(true);
//this listens for new incoming messages from the server and adds them to the messages state.We use standardizeMessage to clean it up before using it.
        channel.on('new_message', (payload) => {
          console.log('Raw message payload received:', payload);
          const newMessage = standardizeMessage(payload, userId);
          console.log('Standardized message:', newMessage);
          setMessages(prev => [...prev, newMessage]);
        });
// pulls past messages from the server
        const existingMessages = await fetchMessages(userId);

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
    if (!content.trim() || !channelRef.current) {
      toast.error('Cannot send empty message or no active connection');
      return;
    }
    //sends the message to the server
    try {
      const response = await pushMessage(channelRef.current, 'new_message', {
        message: content,
        sender_id: userId,
        room_id: 'notifications'
      });
      
      if (!response) {
        throw new Error('No response from server');
      }
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

