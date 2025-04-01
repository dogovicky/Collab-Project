import React from 'react';
import { Link } from 'react-router-dom';
import './CssSheets/Home.css';
import SideBar from '../components/SideBar';
import SearchBar from '../components/SearchBar';
import Feeds from '../components/Feeds';
import posts from '../data/posts';

const Home = () => {
  const username = localStorage.getItem('username') || 'User';

  const handleSearch = (query, searchType) => {
    console.log(`Searching for ${query} in ${searchType}`);
    // Implement search logic here
  };

  

  return (
    <div className="home-container">
      <nav className="home-nav">
        <h1 className="logo">Nexus</h1>
        <div className="nav-links">
          <SearchBar onSearch={handleSearch} />
          
        </div>
      </nav>
      
      <div className="home-content">
        <SideBar />
        <main className="home-main">
          <h2>Welcome, {username}!</h2>
          <Feeds posts={posts} />
        </main>
      </div>
    </div>
  );
};

export default Home;