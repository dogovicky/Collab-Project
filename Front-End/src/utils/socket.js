import { Socket } from 'phoenix';
import { standardizeMessage } from './messageFormatter';

// Initialize the Phoenix socket connection
const socket = new Socket("ws://localhost:4000/socket", {
  params: { user_id: "123" } // Replace "123" with the actual user ID
});
socket.connect()

export function connectSocket(){
  socket.connect();
  return socket
}

export function pushMessage(channel, event, payload = {}) {
  return new Promise((resolve, reject) => {
    channel
      .push(event, payload)
      .receive('ok', (response) => {
        resolve(response);
      })
      .receive('error', (error) => {
        console.error('Failed to push message:', error);
        reject(error);
      })
      .receive('timeout', () => {
        console.error('Push message timeout');
        reject(new Error('Push message timeout'));
      });
  });
}

export async function joinChannel(userId){
  return new Promise((resolve, reject) => {
    const channel = socket.channel(`notifications:${userId}`,  {});

    channel
      .join()
      .receive('ok', () => {
        resolve({ channel });
      })
      .receive('error', (error) => {
        console.error('Failed to join channel:', error);
        reject(error);
      });
  });
}

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
export const fetchMessages = async (userId) => {
  return new Promise((resolve, reject) => {
    const channel = socket.channel(`notifications:${userId}`,  {});

    channel
      .join()
      .receive('ok', () => {
        channel
          .push('fetch_messages', {})
          .receive('ok', (response) => {
            const standardizedMessages = response.messages.map(msg => 
              standardizeMessage(msg, userId)
            );
            resolve({ messages: standardizedMessages });
            channel.leave();
          })
          .receive('error', (error) => {
            console.log(error) //
            reject(error);
            channel.leave();
          });
      })
      .receive('error', (error) => {

        console.log(error)

        reject(error);
      });
  });
};

export default socket;

