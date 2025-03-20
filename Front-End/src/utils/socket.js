import { io } from 'socket.io-client';

const SOCKET_URL = 'http://localhost:5000'; // Update with your backend URL

const socket = io(SOCKET_URL, {
  autoConnect: true, // Automatically try to reconnect
  reconnectionAttempts: 5, // Retry up to 5 times
  reconnectionDelay: 1000, // Start with a 1-second delay between reconnect attempts
  transports: ['websocket'], // Use WebSocket for better performance
});

socket.on('connect', () => {
  console.log('✅ Connected to socket server:', socket.id);
});

socket.on('disconnect', (reason) => {
  console.warn('❌ Disconnected from socket server:', reason);
});

socket.on('connect_error', (error) => {
  console.error('⚠️ Connection error:', error.message);
});

export default socket;
