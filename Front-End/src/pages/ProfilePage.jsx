import { useEffect, useState } from "react";
import ProfileHeader from "../components/ProfileHeader";
import ProfileStats from "../components/ProfileStats";
import EditProfileModal from "../components/EditProfileModal";
import Post from "../components/Post"; // Ensure Post component is imported
import { createPost, fetchUserData, updateUserData } from "../api/authA"; // Import API functions
import "../pages/CssSheets/ProfilePage.css";

const ProfilePage = () => {
  const [user, setUser] = useState(null);
  const [isEditing, setIsEditing] = useState(false);

  useEffect(() => {
    const getUserData = async () => {
      try {
        const userData = await fetchUserData(); // Fetch user data from backend
        setUser(userData);
      } catch (error) {
        alert(error.message); // Display error message
      }
    };
    getUserData();
  }, []);

  const handleSave = async (updatedUser) => {
    try {
      const savedUser = await updateUserData(updatedUser); // Update user data in backend
      setUser({ ...user, ...savedUser });
      setIsEditing(false);
      alert("Profile updated successfully!"); // Display success message
    } catch (error) {
      alert(error.message); // Display error message
    }
  };

  if (!user) return <div>Loading...</div>;

  return (
    <div className="profile-page">
      <ProfileHeader user={user} />
      <ProfileStats stats={user.stats} />
      <button className="edit-profile-btn" onClick={() => setIsEditing(true)}>
        Edit Profile
      </button>
      {isEditing && (
        <EditProfileModal
          user={user}
          onSave={handleSave}
          onClose={() => setIsEditing(false)}
        />
      )}
      <div className="profile-posts">
        <h2>Posts</h2>
        {user.posts.map((post) => (
          <div key={post.id} className="post">
            <div className="post-header">
              <img src={user.avatar} alt={user.username} className="avatar" />
              <div className="post-info">
                <h3 className="username">{user.username}</h3>
                <span className="post-time">{post.postTime}</span>
              </div>
            </div>
            <p className="post-content">{post.content}</p>
            {post.comments && (
              <div className="comments">
                {post.comments.map((comment, index) => (
                  <div key={index} className="comment">
                    <div className="comment-user">{comment.username}</div>
                    <div className="comment-content">{comment.content}</div>
                  </div>
                ))}
              </div>
            )}
          </div>
        ))}
      </div>
      <div className="profile-reposts">
        <h2>Reposts</h2>
        {user.reposts.map((repost) => (
          <Post
            key={repost.id}
            avatarUrl={user.avatar}
            username={user.username}
            content={repost.content}
            initialComments={repost.comments}
            postTime={repost.postTime}
          />
        ))}
      </div>
    </div>
  );
};

export default ProfilePage;
