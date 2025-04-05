import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './NotificationPage.css';
import socket, { joinChannel } from '../utils/socket'; // Import both the socket and joinChannel

const Notification = ({ notification, onClick }) => (
  <div
    className={`notification ${notification.read ? 'read' : 'unread'}`}
    onClick={() => onClick(notification.id)}
  >
    <p>{notification.message}</p>
    <span>{notification.time}</span>
  </div>
);

const NotificationPage = () => {
  const [notifications, setNotifications] = useState([]);
  const [notificationChannel, setNotificationChannel] = useState(null);

  useEffect(() => {
    const fetchNotifications = async () => {
      try {
        const response = await axios.get('http://localhost:5000/api/notifications');
        setNotifications(response.data);
      } catch (error) {
        console.error('Error fetching notifications:', error);
      }
    };

    fetchNotifications();

    // Setup Phoenix channel for notifications
    const setupChannel = async () => {
      try {
        const userId = "123"; // Replace with actual user ID
        const { channel } = await joinChannel(userId);
        
        // Listen for new notifications using Phoenix channel's "on" method
        channel.on("new_notification", (payload) => {
          console.log('Received notification:', payload);
          setNotifications((prev) => [...prev, payload]);
        });
        
        setNotificationChannel(channel);
      } catch (error) {
        console.error('Error setting up notification channel:', error);
      }
    };
    
    setupChannel();

    // Cleanup function
    return () => {
      if (notificationChannel) {
        notificationChannel.leave();
      }
    };
  }, []);

  const markAllAsRead = () => {
    setNotifications(notifications.map(notification => ({ ...notification, read: true })));
  };

  const clearAll = async () => {
    try {
      await axios.delete('http://localhost:5000/api/notifications');
      setNotifications([]);
    } catch (error) {
      console.error('Error clearing notifications:', error);
    }
  };

  const markAsRead = (id) => {
    setNotifications(notifications.map(notification =>
      notification.id === id ? { ...notification, read: true } : notification
    ));
  };

  return (
    <div className="notification-page">
      <div className="notification-header">
        <h2>Notifications</h2>
        <button onClick={markAllAsRead}>Mark All as Read</button>
        <button onClick={clearAll}>Clear All</button>
      </div>
      <div className="notification-list">
        {notifications.length === 0 ? (
          <p>No notifications yet!</p>
        ) : (
          notifications.map(notification => (
            <Notification
              key={notification.id}
              notification={notification}
              onClick={markAsRead}
            />
          ))
        )}
      </div>
    </div>
  );
};

export default NotificationPage;