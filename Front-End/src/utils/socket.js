import { Socket } from 'phoenix';

const API_CONFIG = {
  WS_URL: 'ws://localhost:4000',
  API_URL:  'http://localhost:4000',
};

let socket = null;

export const createSocket = () => {
  const token = localStorage.getItem('authToken');
  if (!token) {
    throw new Error('No authentication token found');
  }

  socket = new Socket(`${API_CONFIG.WS_URL}/socket`, {
    params: { token },
    encode: JSON.stringify,
    decode: JSON.parse,
    timeout: 10000,
    heartbeatIntervalMs: 30000,
    reconnectAfterMs: (tries) => {
      return [1000, 2000, 5000, 10000][tries - 1] || 10000;
    },
    logger: (kind, msg, data) => {
      console.log(`Phoenix Socket: ${kind}: ${msg}`, data);
    }
  });

  socket.onOpen(() => console.log('Socket connected'));
  socket.onError(() => {
    console.error('Socket error');
    // Attempt to refresh token and reconnect
    setTimeout(connectSocket, 1000);
  });
  socket.onClose(() => console.log('Socket closed'));

  return socket;
};

export const connectSocket = () => {
  try {
    if (!socket) {
      socket = createSocket();
    }
    if (!socket.isConnected()) {
      socket.connect();
    }
    return socket;
  } catch (error) {
    console.error('Socket connection error:', error);
    throw error;
  }
};

export const joinChannel = (channelName, params = {}) => {
  const channel = socket.channel(channelName, params);
  
  // Add proper Phoenix channel event handlers
  channel.onError((error) => {
    console.error('Phoenix channel error:', error);
    channel.rejoin(); // Auto rejoin on error
  });

  channel.onClose(() => {
    console.log('Phoenix channel closed');
    setTimeout(() => channel.rejoin(), 1000); // Rejoin after 1 second
  });

  return new Promise((resolve, reject) => {
    channel
      .join()
      .receive('ok', (response) => {
        console.log('Successfully joined channel', channelName);
        resolve({ channel, response });
      })
      .receive('error', (error) => {
        console.error('Failed to join channel', error);
        reject(error);
      })
      .receive('timeout', () => {
        console.error('Channel join timeout');
        reject('Join timeout');
      });
  });
};

// Add proper message handling
export const pushMessage = (channel, event, payload) => {
  return new Promise((resolve, reject) => {
    channel
      .push(event, payload)
      .receive('ok', (response) => resolve(response))
      .receive('error', (error) => reject(error))
      .receive('timeout', () => reject('Timeout'));
  });
};

export const fetchMessages = async (roomId, params = {}) => {
  try {
    const response = await fetch(`${API_CONFIG.API_URL}/api/messages/${roomId}`, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('authToken')}`
      }
    });
    if (!response.ok) throw new Error('Failed to fetch messages');
    return await response.json();
  } catch (error) {
    console.error('Error fetching messages:', error);
    throw error;
  }
};

export default socket;
