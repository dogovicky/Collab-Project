import { useState } from "react";
import "../pages/CssSheets/ProfileHeader.css";
import { createPost } from "../api/authA"; // Updated import for API usage

const ProfileHeader = ({ user }) => {
  const [isFollowing, setIsFollowing] = useState(user?.isFollowing);

  const handleFollowToggle = async () => {
    try {
      setIsFollowing(!isFollowing);
      await axios.post(`/api/follow`, { userId: user.id, follow: !isFollowing });
    } catch (error) {
      console.error("Failed to update follow status:", error);
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
    </div>
  );
};

export default ProfileHeader;
