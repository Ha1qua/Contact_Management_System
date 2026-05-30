import { useEffect, useState } from "react";
import { getMyProfile } from "../services/authService";
import ProfileDropdown from "./ProfileDropdown";
import "../styles/Navbar.css";

const Navbar = () => {

  const [openDropdown, setOpenDropdown] = useState(false);
  const [profile, setProfile] = useState(null);

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    try {
      const res = await getMyProfile();
      setProfile(res.data);
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <nav className="navbar">

      <div className="logo-section">
        <div className="logo-circle">N</div>
        <h2>ContactSphere</h2>
      </div>

      <div
        className="profile-section"
        onClick={() => setOpenDropdown(!openDropdown)}
      >
        <div className="avatar">
          {profile?.initials}
        </div>


      </div>

      {openDropdown && (
        <ProfileDropdown profile={profile} />
      )}

    </nav>
  );
};

export default Navbar;