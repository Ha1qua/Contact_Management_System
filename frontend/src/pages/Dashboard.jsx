import { useNavigate } from 'react-router-dom';
import { useAuth } from '../services/AuthContext';
import '../styles/Dashboard.css';

const stats = [
  { icon: '◈', value: '24', label: 'Sessions' },
  { icon: '◉', value: '99.9%', label: 'Uptime' },
  { icon: '◌', value: '1', label: 'Active Device' },
];

const activity = [
  { text: 'Signed in successfully', time: 'Just now' },
  { text: 'Email verified', time: '2 min ago' },
  { text: 'Account created', time: 'Today' },
];

const Dashboard = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const displayName = user?.name
    ? user.name.charAt(0).toUpperCase() + user.name.slice(1)
    : 'User';

  const avatarLetter = displayName.charAt(0).toUpperCase();

  return (
    <div className="dashboard">
      {/* Navigation */}
      <nav className="dashboard__nav">
        <div className="dashboard__nav-brand">
          <div className="dashboard__nav-logo">✦</div>
          <span className="dashboard__nav-name">Nexus</span>
        </div>
        <div className="dashboard__nav-actions">
          <div className="dashboard__avatar">{avatarLetter}</div>
          <button className="dashboard__logout-btn" onClick={handleLogout}>
            Sign Out
          </button>
        </div>
      </nav>

      {/* Body */}
      <main className="dashboard__body">
        {/* Greeting */}
        <section className="dashboard__greeting">
          <p className="dashboard__greeting-eyebrow">Dashboard</p>
          <h1 className="dashboard__greeting-title">
            Welcome back,{' '}
            <span>{displayName}</span>.
          </h1>
          <p className="dashboard__greeting-subtitle">
            Here's an overview of your account activity.
          </p>
        </section>

        {/* Stats */}
        <section>
          <div className="dashboard__stats">
            {stats.map((stat) => (
              <div className="dashboard__stat-card" key={stat.label}>
                <div className="dashboard__stat-icon">{stat.icon}</div>
                <div className="dashboard__stat-value">{stat.value}</div>
                <div className="dashboard__stat-label">{stat.label}</div>
              </div>
            ))}
          </div>
        </section>

        {/* Activity */}
        <section>
          <h2 className="dashboard__section-title">Recent Activity</h2>
          <div className="dashboard__activity">
            {activity.map((item, i) => (
              <div className="dashboard__activity-item" key={i}>
                <div className="dashboard__activity-dot" />
                <span className="dashboard__activity-text">{item.text}</span>
                <span className="dashboard__activity-time">{item.time}</span>
              </div>
            ))}
          </div>
        </section>
      </main>
    </div>
  );
};

export default Dashboard;