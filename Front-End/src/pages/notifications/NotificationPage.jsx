import React, { useState, useEffect } from "react";
import axios from "axios";
import "./NotificationPage.css";
import socket from "../../utils/socket"; // Import the WebSocket connection

const Notification = ({ notification, onClick }) => (
  <div
    className={`notification ${notification.read ? "read" : "unread"}`}
    onClick={() => onClick(notification.id)}
  >
    <p>{notification.message}</p>
    <span>{notification.time}</span>
  </div>
);

const NotificationPage = () => {
  const [notifications, setNotifications] = useState([]);

  useEffect(() => {
    const fetchNotifications = async () => {
      try {
        const response = await axios.get(
          "http://localhost:5000/api/notifications"
        );
        setNotifications(response.data);
      } catch (error) {
        console.error("Error fetching notifications:", error);
      }
    };

    fetchNotifications();
  }, []);

  useEffect(() => {
    const handleNewNotification = (payload) => {
      console.log("Received notification:", payload);
      setNotifications((prev) => [...prev, payload]);
    };

    // Listen for new notifications
    socket.on("new_notification", handleNewNotification);

    return () => {
      socket.off("new_notification", handleNewNotification); // Cleanup listener
    };
  }, []);

  const markAllAsRead = () => {
    setNotifications(
      notifications.map((notification) => ({ ...notification, read: true }))
    );
  };

  const clearAll = async () => {
    try {
      await axios.delete("http://localhost:5000/api/notifications");
      setNotifications([]);
    } catch (error) {
      console.error("Error clearing notifications:", error);
    }
  };

  const markAsRead = (id) => {
    setNotifications(
      notifications.map((notification) =>
        notification.id === id ? { ...notification, read: true } : notification
      )
    );
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
          notifications.map((notification) => (
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
