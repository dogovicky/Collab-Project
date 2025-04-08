import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import "./CssSheets/Home.css";
import SideBar from "../components/SideBar";
import SearchBar from "../components/SearchBar";
import Feeds from "../components/Feeds";
import { toast } from "react-toastify"; // Add this import
import axios from "axios";
import CreatePost from "../components/CreatePost";

const Home = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(false);
  const username = localStorage.getItem("username");

  const handleSearch = (query, searchType) => {
    console.log(`Searching for ${query} in ${searchType}`);
    // Implement search logic here
  };

  const fetchPosts = async () => {
    try {
      const API_URL = `http://localhost:8080/?username=${username}`;
      console.log(username);

      const response = await axios.get(API_URL, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("authToken")}`,
        },
      });
      setLoading(true);
      if (response.data.status != 200) {
        throw new Error("Failed to fetch posts");
      }
      // const data = await response.json();
      console.log(response.data);
      setPosts(response.data.data);
    } catch (error) {
      console.error("Error fetching posts:", error);
      toast.error("Failed to load posts. Please try again later.");
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
            <p>No events match your interests.</p>
          )}
        </main>
      </div>
      <CreatePost />
    </div>
  );
};

export default Home;
