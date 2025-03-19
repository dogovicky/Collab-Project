import React from 'react';
import './SideBar.css';
import { BiLogOut, BiHome, BiMessage, BiBell, BiUser, BiUserPlus } from 'react-icons/bi';

const SideBar = () => {
    const navItems = [
        { label: 'Home', icon: <BiHome /> },
        { label: 'Messages', icon: <BiMessage /> },
        { label: 'Notifications', icon: <BiBell /> },
        { label: 'Connections', icon: <BiUserPlus /> },
        { label: 'Profile', icon: <BiUser /> }
    ];

    return (
        <div className="sidebar">
            <div className="nav-items">
                {navItems.map((item, index) => (
                    <button key={index} className="sidebar-button">
                        <span className="icon">{item.icon}</span>
                        {item.label}
                    </button>
                ))}
            </div>
            <div className="sidebar-footer">
                <button className="sidebar-button logout-button">
                    <span className="icon"><BiLogOut /></span>
                    Logout
                </button>
            </div>
        </div>
    );
};

export default SideBar;