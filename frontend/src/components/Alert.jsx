import React from 'react';

const Alert = ({ type = 'error', message }) => {
  if (!message) return null;

  const styles = {
    error: 'bg-rose-500/10 border-rose-500/30 text-rose-400',
    success: 'bg-neon-400/10 border-neon-400/30 text-neon-400',
    info: 'bg-electric-500/10 border-electric-500/30 text-electric-400',
  };

  const icons = {
    error: (
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <circle cx="12" cy="12" r="10" />
        <line x1="15" y1="9" x2="9" y2="15" />
        <line x1="9" y1="9" x2="15" y2="15" />
      </svg>
    ),
    success: (
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
        <polyline points="22 4 12 14.01 9 11.01" />
      </svg>
    ),
    info: (
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <circle cx="12" cy="12" r="10" />
        <line x1="12" y1="8" x2="12" y2="12" />
        <line x1="12" y1="16" x2="12.01" y2="16" />
      </svg>
    ),
  };

  return (
    <div className={`flex items-start gap-3 px-4 py-3 rounded-xl border text-sm ${styles[type]}`}>
      <span className="mt-0.5 flex-shrink-0">{icons[type]}</span>
      <span>{message}</span>
    </div>
  );
};

export default Alert;
