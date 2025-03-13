import React from 'react';
import './SideBar.css';

const SideBar = () => {
    return (
        <div className="sidebar">
            <button className="sidebar-button">Home</button>
            <button className="sidebar-button">Messages</button>
            <button className="sidebar-button">Notifications</button>
            <button className="sidebar-button">Connections</button>
            <button className="sidebar-button">Profile</button>
        </div>
    );
};

export default SideBar;