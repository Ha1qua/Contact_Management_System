import Navbar from "../components/Navbar";
import Contacts from "./Contacts";
import "../styles/Dashboard.css";

const Dashboard = () => {
  return (
    <div className="dashboard-container">
      <Navbar />

      <div className="dashboard-content">
        <div className="hero-section">
          <h1>Your network, neatly organized.</h1>
          <p>
            Search, edit, and manage every contact in one calm,
            focused workspace.
          </p>
        </div>

        <Contacts />
      </div>
    </div>
  );
};

export default Dashboard;