import { useState } from "react";
import "../styles/Modal.css";

const ChangePasswordModal = ({ closeModal }) => {
  const [passwords, setPasswords] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });

  const handleChange = (e) => {
    setPasswords({
      ...passwords,
      [e.target.name]: e.target.value,
    });
  };

  return (
    <div className="modal-overlay">
      <div className="modal-box">


        <input
          type="password"
          name="currentPassword"
          placeholder="Current Password"
          value={passwords.currentPassword}
          onChange={handleChange}
        />

        <input
          type="password"
          name="newPassword"
          placeholder="New Password"
          value={passwords.newPassword}
          onChange={handleChange}
        />

        <input
          type="password"
          name="confirmPassword"
          placeholder="Confirm Password"
          value={passwords.confirmPassword}
          onChange={handleChange}
        />

        <div className="modal-buttons">
          <button onClick={closeModal}>Cancel</button>
          <button>Reset</button>
        </div>
      </div>
    </div>
  );
};

export default ChangePasswordModal;