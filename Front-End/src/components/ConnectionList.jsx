import PropTypes from 'prop-types';
import UserCard from "./UserCard";
import "./ConnectionList.css"; 

const ConnectionList = ({ users }) => {
  return (
    <div>
      {users.map((user) => (
        <UserCard key={user.id} user={user} />
      ))}
    </div>
  );
};

ConnectionList.propTypes = {
  users: PropTypes.arrayOf(PropTypes.shape({
    id: PropTypes.number.isRequired,
    // ...other user properties
  })).isRequired,
};

export default ConnectionList;
