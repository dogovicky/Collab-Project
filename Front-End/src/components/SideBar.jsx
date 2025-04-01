import React from 'react';
import './SideBar.css';
import { BiLogOut, BiHome, BiMessage, BiBell, BiUser, BiUserPlus } from 'react-icons/bi';
import { Link } from 'react-router-dom';

const logout = () => {
    localStorage.removeItem('authToken');
    window.location.href = '/signin';
};

const SideBar = () => {
    const navItems = [
        { label: 'Home', icon: <BiHome />, link: '/' },
        { label: 'Messages', icon: <BiMessage />, link: '/messages' },
        { label: 'Notifications', icon: <BiBell />, link: '/notifications' },
        { label: 'Connections', icon: <BiUserPlus />, link: '/connections' },
        { label: 'Profile', icon: <BiUser />, link: '/profile' }
    ];

    return (
        <div className="sidebar">
            <div className="nav-items">
                {navItems.map((item, index) => (
                    <Link to={item.link} key={index}>
                        <button className="sidebar-button">
                            <span className="icon">{item.icon}</span>
                            {item.label}
                        </button>
                    </Link>
                ))}
            </div>
            <div className="sidebar-footer">
                <button className="sidebar-button logout-button" onClick={logout}>
                    <span className="icon"><BiLogOut /></span>
                    Logout
                </button>
            </div>
        </div>
    );
};

export default SideBar;