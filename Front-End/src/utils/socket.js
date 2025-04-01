import { Socket } from 'phoenix';

// Initialize the Phoenix socket connection
const socket = new Socket('ws://localhost:4000/socket'); // Replace with your Phoenix server URL
socket.connect();

// Add error handling
socket.onError(() => {
  console.error('Socket connection error. Please check the server.');
});

socket.onClose(() => {
  console.warn('Socket connection closed. Attempting to reconnect...');
});

socket.onOpen(() => {
  console.log('Socket connected');
});

export default socket;
