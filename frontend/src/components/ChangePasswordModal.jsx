import { useState } from "react";
import { changePassword } from "../services/authService";
import { useNavigate } from "react-router-dom";
import "../styles/Modal.css";

const ChangePasswordModal = ({ closeModal }) => {

  const navigate = useNavigate();

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

const handleReset = async () => {

  if (passwords.newPassword !== passwords.confirmPassword) {
    alert("New Password and Confirm Password do not match");
    return;
  }

  try {
    await changePassword({
      oldPassword: passwords.currentPassword,
      newPassword: passwords.newPassword,
    });

    localStorage.removeItem("token");

    alert("Password changed successfully");

    closeModal();

    navigate("/login");

  } catch (error) {
    console.log(error.response?.data);

    alert(
      error.response?.data?.message ||
      "Failed to change password"
    );
  }
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
          <button onClick={closeModal}>
            Cancel
          </button>

          <button onClick={handleReset}>
            Reset
          </button>
        </div>
      </div>
    </div>
  );
};

export default ChangePasswordModal;