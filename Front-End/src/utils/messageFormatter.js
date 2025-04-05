export const standardizeMessage = (message, userId = null) => {
  return {
    id: message.id || message._id,
    content: message.content || message.message,
    senderId: message.sender_id || message.senderId,
    senderName: message.sender_name || message.senderName,
    senderAvatar: message.sender_avatar || message.senderAvatar || message.avatar || '/default-avatar.png',
    timestamp: message.inserted_at || message.timestamp || new Date().toISOString(),
    isSender: userId ? (message.sender_id === userId || message.senderId === userId) : false,
    // Ensure all avatar references are consistent
    avatar: message.sender_avatar || message.senderAvatar || message.avatar || '/default-avatar.png'
  };
};
