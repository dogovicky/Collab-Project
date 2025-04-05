import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import './CssSheets/Home.css';
import SideBar from '../components/SideBar';
import SearchBar from '../components/SearchBar';
import Feeds from '../components/Feeds';
import { toast } from 'react-toastify'; // Add this import

const Home = () => {
  const [posts, setPosts] = useState([]);
  const username = localStorage.getItem('username') || 'User';

  const handleSearch = (query, searchType) => {
    console.log(`Searching for ${query} in ${searchType}`);
    // Implement search logic here
  };

  const fetchPosts = async () => {
    try {
      const response = await fetch(`${process.env.REACT_APP_API_URL}/api/posts`);
      if (!response.ok) {
        throw new Error('Failed to fetch posts');
      }
      const data = await response.json();
      setPosts(data);
    } catch (error) {
      console.error('Error fetching posts:', error);
      toast.error('Failed to load posts. Please try again later.');
    }
  };

  useEffect(() => {
    fetchPosts();
  }, []);

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
          {posts.length > 0 ? (
            <Feeds posts={posts} />
          ) : (
            <p>Loading posts...</p>
          )}
        </main>
      </div>
    </div>
  );
};

export default Home;