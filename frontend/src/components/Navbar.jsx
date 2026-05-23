import { useState } from "react";
import ProfileDropdown from "./ProfileDropdown";
import "../styles/Navbar.css";

const Navbar = () => {
  const [openDropdown, setOpenDropdown] = useState(false);

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
        <div className="avatar">HM</div>
        <span>Haiqua</span>
      </div>

      {openDropdown && <ProfileDropdown />}
    </nav>
  );
};

export default Navbar;