import { Socket } from 'phoenix';

// Initialize the Phoenix socket connection
const socket = new Socket('ws://192.168.155.172:4000/socket'); // Replace with your Phoenix server URL
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

// Function to fetch messages via the socket
export const fetchMessages = async (channelName, params = {}) => {
  return new Promise((resolve, reject) => {
    const channel = socket.channel(channelName, params);

    channel
      .join()
      .receive('ok', () => {
        channel
          .push('fetch_messages', {})
          .receive('ok', (response) => {
            resolve(response.messages);
            channel.leave();
          })
          .receive('error', (error) => {
            reject(error);
            channel.leave();
          });
      })
      .receive('error', (error) => {
        reject(error);
      });
  });
};

export default socket;
