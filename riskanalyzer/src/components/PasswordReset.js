import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import Notification from './Notification';
import AuthParticles from './AuthParticles';

const PasswordReset = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const token = searchParams.get('token');

  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [errors, setErrors] = useState({});
  const [notif, setNotif] = useState({ message: '', type: 'info' });
  const [isValidToken, setIsValidToken] = useState(true);

  useEffect(() => {
    if (!token) {
      setIsValidToken(false);
      setNotif({ message: 'Invalid or missing reset token.', type: 'danger' });
    }
  }, [token]);

  const isStrongPassword = (pwd) =>
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{9,}$/.test(pwd);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors({});
    setNotif({ message: '', type: 'info' });

    const newErrors = {};

    if (!newPassword) {
      newErrors.newPassword = 'Please enter a new password.';
    } else if (!isStrongPassword(newPassword)) {
      newErrors.newPassword =
        'Password must be at least 9 characters and include uppercase, lowercase, number, and special character.';
    }

    if (newPassword !== confirmPassword) {
      newErrors.confirmPassword = 'Passwords do not match.';
    }

    if (Object.keys(newErrors).length) {
      setErrors(newErrors);
      return;
    }

    try {
      setNotif({ message: 'Resetting password...', type: 'info' });

      const res = await fetch('http://localhost:8080/api/auth/reset-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          token,
          newPassword
        })
      });

      if (!res.ok) {
        setIsValidToken(false);
        setNotif({
          message: 'This reset link is invalid or has expired.',
          type: 'danger'
        });
        return;
      }

      setNotif({
        message: 'Password reset successful! Redirecting to login...',
        type: 'success'
      });

      setTimeout(() => navigate('/'), 2000);

    } catch (err) {
      console.error(err);
      setNotif({
        message: 'Server error. Please try again later.',
        type: 'danger'
      });
    }
  };

  return (
    <div className="auth-page">
      <AuthParticles />

      <div className="auth-card">
        <h2 className="auth-title text-center">Reset Password</h2>
        <p className="auth-subtitle text-center">
          Enter your new password below.
        </p>

        <Notification
          message={notif.message}
          type={notif.type}
          onClose={() => setNotif({ message: '', type: 'info' })}
        />

        {!isValidToken ? (
          <div className="text-center">
            <p style={{ color: '#fecaca' }}>
              This password reset link is invalid or has expired.
            </p>
            <button
              className="btn btn-cyber mt-3"
              onClick={() => navigate('/')}
            >
              Back to Login
            </button>
          </div>
        ) : (
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">New Password</label>
              <input
                type="password"
                className="form-control cyberpunk-input"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                placeholder="Enter new password"
              />
              {errors.newPassword && (
                <div className="text-danger small mt-1">
                  {errors.newPassword}
                </div>
              )}
            </div>

            <div className="mb-3">
              <label className="form-label">Confirm Password</label>
              <input
                type="password"
                className="form-control cyberpunk-input"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="Confirm new password"
              />
              {errors.confirmPassword && (
                <div className="text-danger small mt-1">
                  {errors.confirmPassword}
                </div>
              )}
            </div>

            <button type="submit" className="btn btn-cyber w-100 mt-2">
              Reset Password
            </button>
          </form>
        )}

        <div className="text-center mt-3">
          <button
            type="button"
            className="btn btn-link auth-switch"
            onClick={() => navigate('/')}
          >
            Back to Login
          </button>
        </div>
      </div>
    </div>
  );
};

export default PasswordReset;
