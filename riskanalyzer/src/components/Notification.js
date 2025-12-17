import React from 'react';

const Notification = ({ message, type = 'info', onClose }) => {
  if (!message) return null;

  const bgColor = type === 'success'
    ? 'rgba(0, 245, 255, 0.2)'
    : type === 'danger'
    ? 'rgba(255, 77, 77, 0.2)'
    : 'rgba(255, 255, 255, 0.1)';

  return (
    <div
      className="alert alert-dismissible fade show"
      role="alert"
      style={{
        background: bgColor,
        border: '1px solid rgba(255,255,255,0.2)',
        color: '#fff',
      }}
    >
      {message}
      {onClose && (
        <button
          type="button"
          className="btn-close"
          aria-label="Close"
          onClick={onClose}
        ></button>
      )}
    </div>
  );
};

export default Notification;
