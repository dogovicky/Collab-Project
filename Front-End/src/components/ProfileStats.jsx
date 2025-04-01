import "../pages/CssSheets/ProfileStats.css";
import PropTypes from 'prop-types';

const ProfileStats = ({ stats }) => {
  return (
    <div className="profile-stats">
      <div className="stat">
        <strong>{stats.posts}</strong>
        <span>Posts</span>
      </div>
      <div className="stat">
        <strong>{stats.followers}</strong>
        <span>Followers</span>
      </div>
      <div className="stat">
        <strong>{stats.following}</strong>
        <span>Following</span>
      </div>
    </div>
  );
};

ProfileStats.propTypes = {
  stats: PropTypes.shape({
    posts: PropTypes.number.isRequired,
    followers: PropTypes.number.isRequired,
    following: PropTypes.number.isRequired,
  }).isRequired,
};

export default ProfileStats;
