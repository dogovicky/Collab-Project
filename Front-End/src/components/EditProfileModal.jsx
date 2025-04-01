import React, { useState } from "react";
import "./EditProfileModal.css";
import { updatePassword } from "../api/authA"; // Import API for potential usage

const EditProfileModal = ({ user, onSave, onClose }) => {
  const [name, setName] = useState(user.name);
  const [bio, setBio] = useState(user.bio);
  const [avatar, setAvatar] = useState(user.avatar);
  const [preview, setPreview] = useState(user.avatar);

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setAvatar(file);
      setPreview(URL.createObjectURL(file));
    }
  };

  const handleSave = () => {
    onSave({ name, bio, avatar });
  };

  return (
    <div className="edit-profile-modal">
      <div className="modal">
        <div className="modal-content">
          <h2>Edit Profile</h2>
          <label>
            Name:
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </label>
          <label>
            Bio:
            <textarea
              value={bio}
              onChange={(e) => setBio(e.target.value)}
            />
          </label>
          <label>
            Profile Picture:
            <input type="file" onChange={handleFileChange} />
          </label>
          {preview && <img src={preview} alt="Profile Preview" className="preview" />}
          <div className="buttons">
            <button onClick={handleSave}>Save</button>
            <button onClick={onClose}>Cancel</button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EditProfileModal;
