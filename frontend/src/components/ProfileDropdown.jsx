import { useNavigate } from "react-router-dom";
import ChangePasswordModal from "./ChangePasswordModal";
import { useState } from "react";

const ProfileDropdown = ({ profile }) => {

  const navigate = useNavigate();
  const [openPasswordModal, setOpenPasswordModal] = useState(false);

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/");
  };

  return (
    <div className="profile-dropdown">

      <div className="profile-header">

        <div className="large-avatar">
          {profile?.initials}
        </div>

        <div>
          <p>{profile?.email}</p>
        </div>

      </div>

      <button onClick={() => setOpenPasswordModal(true)}>
        Change Password
      </button>

      <button className="logout-btn" onClick={handleLogout}>
        Logout
      </button>

      {openPasswordModal && (
        <ChangePasswordModal
          closeModal={() => setOpenPasswordModal(false)}
        />
      )}

    </div>
  );
};

export default ProfileDropdown;