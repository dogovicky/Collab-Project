import { useEffect, useState } from "react";
import ProfileHeader from "../components/ProfileHeader";
import ProfileStats from "../components/ProfileStats";
import EditProfileModal from "../components/EditProfileModal";
import Post from "../components/Post"; // Ensure Post component is imported
import { createPost } from "../api/authA"; // Import API for potential usage
import "../pages/CssSheets/ProfilePage.css";

const mockUser = {
  id: 1,
  name: "John Doe",
  username: "johndoe",
  avatar: "https://via.placeholder.com/80",
  bio: "Software Engineer | Tech Enthusiast",
  isFollowing: true,
  stats: {
    posts: 120,
    followers: 300,
    following: 180,
  },
  posts: [
    {
      id: 1,
      content: "This is my first post!",
      postTime: "2 hours ago",
      comments: [],
    },
    {
      id: 2,
      content: "Loving the new features!",
      postTime: "1 day ago",
      comments: [],
    },
  ],
  reposts: [
    {
      id: 3,
      content: "Check out this amazing article!",
      postTime: "3 days ago",
      comments: [],
    },
  ],
};

const ProfilePage = () => {
  const [user, setUser] = useState(null);
  const [isEditing, setIsEditing] = useState(false);

  useEffect(() => {
    // TODO: Fetch user data from API
    setUser(mockUser);
  }, []);

  const handleSave = (updatedUser) => {
    setUser({ ...user, ...updatedUser });
    setIsEditing(false);
  };

  if (!user) return <div>Loading...</div>;

  return (
    <div className="profile-page">
      <ProfileHeader user={user} />
      <ProfileStats stats={user.stats} />
      <button onClick={() => setIsEditing(true)}>Edit Profile</button>
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
          <Post
            key={post.id}
            avatarUrl={user.avatar}
            username={user.username}
            content={post.content}
            initialComments={post.comments}
            postTime={post.postTime}
          />
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
