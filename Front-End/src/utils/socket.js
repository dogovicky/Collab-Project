// import { Socket } from 'phoenix';

// // Initialize the Phoenix socket connection
// const socket = new Socket("ws://localhost:4000/socket", {
//   params: { user_id: "123" } // Replace "123" with the actual user ID
// });

// // Connect to the socket
// socket.connect()

// // Add error handling
// socket.onError(() => {
//   console.error('Socket connection error. Please check the server.');
// });

// socket.onClose(() => {
//   console.warn('Socket connection closed. Attempting to reconnect...');
// });

// socket.onOpen(() => {
//   console.log('Socket connected');
// });

// // Function to fetch messages via the socket
// export const fetchMessages = async (channelName, params = {}) => {
//   return new Promise((resolve, reject) => {
//     const channel = socket.channel(channelName, params);

//     channel
//       .join()
//       .receive('ok', () => {
//         channel
//           .push('fetch_messages', {})
//           .receive('ok', (response) => {

//             console.log(response) //

//             resolve(response.messages);
//             channel.leave();
//           })
//           .receive('error', (error) => {
//             console.log(error) //
//             reject(error);
//             channel.leave();
//           });
//       })
//       .receive('error', (error) => {

//         console.log(error)

//         reject(error);
//       });
//   });
// };

// export default socket;



import { Socket } from 'phoenix';

// Initialize the Phoenix socket connection
const socket = new Socket("ws://localhost:4000/socket", {
  params: { user_id: "123" } // Replace "123" with the actual user ID
});
socket.connect()

export function connectSocket(){
  socket.connect();
  return socket
}

export function pushMessage(){

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
  //

  return new Promise((resolve, reject) => {
    const channel = socket.channel(`notifications:${userId}`,  {});

    channel
      .join()
      .receive('ok', () => {
        channel
          .push('fetch_messages', {})
          .receive('ok', (response) => {

            console.log(response) //

            resolve(response.messages);
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

