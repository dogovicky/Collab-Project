import { useState } from "react";
import "./UserCard.css";

const UserCard = ({ user }) => {
  const [isFollowing, setIsFollowing] = useState(user.isFollowing);

  const handleFollowToggle = () => {
    setIsFollowing(!isFollowing);
    // TODO: Call API to update follow status
  };

  return (
    <div className="user-card">
      <div className="user-info">
        <img
          src={user.profilePicture}
          alt={`${user.name}'s profile`}
          className="profile-picture"
        />
        <div>
          <h3 className="user-name">{user.name}</h3>
          <p className="user-username">@{user.username}</p>
        </div>
      </div>
      <button
        className={`follow-button ${isFollowing ? "disconnect" : "connect"}`}
        onClick={handleFollowToggle}
      >
        {isFollowing ? "disconnect" : "connect"}
      </button>
    </div>
  );
};

export default UserCard;
