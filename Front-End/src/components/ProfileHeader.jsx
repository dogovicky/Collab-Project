import { useState } from "react";
import "../pages/CssSheets/ProfileHeader.css";
import { toggleFollow } from "../api/authA"; // Import the new API function

const ProfileHeader = ({ user }) => {
  const [isFollowing, setIsFollowing] = useState(user?.isFollowing);
  const [message, setMessage] = useState(""); // State for displaying messages

  const handleFollowToggle = async () => {
    try {
      const response = await toggleFollow(user.id, !isFollowing); // Use the new API function
      setIsFollowing(!isFollowing);
      setMessage(response.message || "Follow status updated successfully!"); // Use API response message
    } catch (error) {
      console.error("Failed to update follow status:", error);
      setMessage(error.message || "An error occurred. Please try again."); // Use error message
    }
  };

  return (
    <div className="profile-header">
      <div className="cover-photo"></div>
      <div className="profile-info">
        <img src={user?.avatar} alt="User Avatar" className="profile-avatar" />
        <div className="profile-details">
          <h2 className="profile-name">{user?.name}</h2>
          <p className="profile-username">@{user?.username}</p>
          <p className="profile-bio">{user?.bio}</p>
        </div>
      </div>
      <button
        className={`follow-btn ${isFollowing ? "disconnect" : ""}`}
        onClick={handleFollowToggle}
      >
        {isFollowing ? "disconnect" : "connect"}
      </button>
      {message && <p className="message">{message}</p>} {/* Display message */}
    </div>
  );
};

export default ProfileHeader;
