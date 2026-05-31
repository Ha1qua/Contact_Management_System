import { useState } from "react";
import { changePassword } from "../services/authService";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import "../styles/Modal.css";

const ChangePasswordModal = ({ closeModal }) => {

  const navigate = useNavigate();

  const [passwords, setPasswords] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });

  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    setPasswords({
      ...passwords,
      [e.target.name]: e.target.value,
    });
  };

  // ✅ VALIDATION
  const validate = () => {
    let newErrors = {};

    const passwordRegex =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,}$/;

    if (!passwords.currentPassword) {
      newErrors.currentPassword = "Current password is required";
    }

    if (!passwords.newPassword) {
      newErrors.newPassword = "New password is required";
    } else if (passwords.newPassword.length < 6) {
      newErrors.newPassword = "Password must be at least 6 characters";
    }else if (!passwords.newPassword) {
      newErrors.newPassword = "New password is required";
    } else if (!passwordRegex.test(passwords.newPassword)) {
      newErrors.newPassword =
        "Password must include uppercase, lowercase, number & special character";
    }

    if (!passwords.confirmPassword) {
      newErrors.confirmPassword = "Confirm password is required";
    } else if (passwords.newPassword !== passwords.confirmPassword) {
      newErrors.confirmPassword = "Passwords do not match";
    }

    setErrors(newErrors);

    if (Object.keys(newErrors).length > 0) {
      toast.error("Please fix form errors");
      return false;
    }

    return true;
  };

  const handleReset = async () => {

    if (!validate()) return;

    try {
      await changePassword({
        oldPassword: passwords.currentPassword,
        newPassword: passwords.newPassword,
      });

      localStorage.removeItem("token");

      toast.success("Password changed successfully");

      closeModal();
      navigate("/login");

    } catch (error) {
      toast.error(
        error?.response?.data?.message || "Failed to change password"
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
        {errors.currentPassword && (
          <small className="error">{errors.currentPassword}</small>
        )}

        <input
          type="password"
          name="newPassword"
          placeholder="New Password"
          value={passwords.newPassword}
          onChange={handleChange}
        />
        {errors.newPassword && (
          <small className="error">{errors.newPassword}</small>
        )}

        <input
          type="password"
          name="confirmPassword"
          placeholder="Confirm Password"
          value={passwords.confirmPassword}
          onChange={handleChange}
        />
        {errors.confirmPassword && (
          <small className="error">{errors.confirmPassword}</small>
        )}

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