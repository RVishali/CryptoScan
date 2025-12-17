import React, { useState } from 'react';
import Notification from './Notification';
import AuthParticles from './AuthParticles';
import ForgotPassword from './ForgotPassword';
import { useNavigate } from 'react-router-dom';


const isStrongPassword = (pwd) => {
  const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{9,}$/;
  return regex.test(pwd);
};

const isValidUsername = (username) => {
  if (username.length < 3) return false;
  return /[A-Za-z]/.test(username);
};

const Authentication = ({ onAuth, defaultMode = 'login' }) => {
  const [mode, setMode] = useState(
  () => localStorage.getItem('cryptoScanAuthMode') || defaultMode
);

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [username, setUsername] = useState('');
  const [errors, setErrors] = useState({});
  const [notif, setNotif] = useState({ message: '', type: 'info' });

  React.useEffect(() => {
  localStorage.setItem('cryptoScanAuthMode', mode);
}, [mode]);

const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setNotif({ message: '', type: 'info' });
    setErrors({});

    if (mode === 'login') {
  if (!email || !password) {
    setErrors({ form: 'Please enter email and password.' });
    return;
  }

  try {
    const res = await fetch("http://localhost:8080/api/auth/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      credentials: "include",     // <-- ESSENTIAL: stores JSESSIONID
      body: JSON.stringify({ email, password })
    });

    if (!res.ok) {
      setErrors({ form: "Invalid email or password." });
      return;
    }

    const data = await res.json();

    // Backend returns fullName, username, id
    onAuth({
      name: data.fullName || data.username || email.split("@")[0],
      email: data.email,
      id: data.id
    });
    localStorage.removeItem('cryptoScanAuthMode');


    setNotif({ message: "Login successful!", type: "success" });
    navigate('/dashboard');


  } catch (err) {
    setErrors({ form: "Login failed. Try again." });
  }

  return;
}


    if (mode === 'forgot') {
  if (!email) {
    setErrors({ email: 'Please enter your email address.' });
    return;
  }

  try {
    setNotif({ message: 'Sending password reset link...', type: 'info' });

    const res = await fetch(
      'http://localhost:8080/api/auth/forgot-password',
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email })
      }
    );

    // Always show success (prevents email enumeration)
    setNotif({
      message:
        'If an account exists for this email, a password reset link has been sent.',
      type: 'success'
    });

    setTimeout(() => setMode('login'), 2000);

  } catch (err) {
    console.error(err);
    setNotif({
      message: 'Could not send reset link. Please try again later.',
      type: 'danger'
    });
  }

  return;
}

    // Signup validation
    const newErrors = {};

    if (!name.trim()) newErrors.name = 'Name is required.';
    if (!isValidUsername(username))
      newErrors.username =
        'Username must be at least 3 characters and contain at least one alphabet.';
    if (!email) newErrors.email = 'Email is required.';
    if (!isStrongPassword(password))
      newErrors.password =
        'Password must:\n• Be at least 9 characters long\n• Contain uppercase, lowercase, number, and special char';

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    const res = await fetch("http://localhost:8080/api/auth/register", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  credentials: "include",
  body: JSON.stringify({ email, password, username, fullName: name })
});

if (!res.ok) {
  setErrors({ form: "Could not create account." });
  return;
}

setNotif({ message: "Account created successfully! Please log in.", type: "success" });

// Do NOT auto-login.
// Switch to login mode:
setMode("login");
return;

  };

  return (
    <div className="auth-page">
      {/* Floating particles background - RESTORED */}
      <AuthParticles />

      <div className="auth-card cyberpunk-card">
        <div className="text-center mb-3">
          <h2 className="auth-title">
            {mode === 'login'
              ? 'CryptoScan'
              : mode === 'signup'
              ? 'Create Account'
              : 'Reset Password'}
          </h2>
          <p className="auth-subtitle">
            {mode === 'forgot'
              ? 'Enter your email to receive a password reset link.'
              : 'Secure your websites with our risk analyzer.'}
          </p>
        </div>

        <Notification
          message={notif.message}
          type={notif.type}
          onClose={() => setNotif({ message: '', type: 'info' })}
        />

        <form onSubmit={handleSubmit}>
          {mode === 'signup' && (
            <>
              <div className="mb-3">
                <label className="form-label">Full Name</label>
                <input
                  type="text"
                  className="form-control cyberpunk-input"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="Enter your name"
                />
                {errors.name && (
                  <div className="text-danger small mt-1">{errors.name}</div>
                )}
              </div>

              <div className="mb-3">
                <label className="form-label">Username</label>
                <input
                  type="text"
                  className="form-control cyberpunk-input"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="Choose a unique username"
                />
                {errors.username && (
                  <div className="text-danger small mt-1">
                    {errors.username}
                  </div>
                )}
              </div>
            </>
          )}

          <div className="mb-3">
            <label className="form-label">Email address</label>
            <input
              type="email"
              className="form-control cyberpunk-input"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="you@example.com"
            />
            {errors.email && (
              <div className="text-danger small mt-1">{errors.email}</div>
            )}
          </div>

          {mode !== 'forgot' && (
            <div className="mb-3">
              <label className="form-label">Password</label>
              <input
                type="password"
                className="form-control cyberpunk-input"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter password"
              />
              {errors.password && (
                <div className="text-danger small mt-1">
                  {errors.password.split('\n').map((line, idx) => (
                    <div key={idx}>{line}</div>
                  ))}
                </div>
              )}
            </div>
          )}

          {/* Forgot Password link - only show in login mode */}
          {mode === 'login' && (
            <div className="text-end mb-2">
              <button
                type="button"
                className="btn btn-link auth-switch p-0"
                style={{ fontSize: '0.85rem' }}
                onClick={() => setMode('forgot')}
              >
                Forgot password?
              </button>
            </div>
          )}

          {errors.form && (
            <div className="alert alert-danger py-2">{errors.form}</div>
          )}

          <button type="submit" className="btn btn-cyber w-100 mt-2">
            {mode === 'login'
              ? 'Login'
              : mode === 'signup'
              ? 'Sign Up'
              : 'Send Reset Link'}
          </button>
        </form>

        <div className="text-center mt-3">
          {mode === 'login' ? (
            <span>
              New here?{' '}
              <button
                type="button"
                className="btn btn-link auth-switch"
                onClick={() => setMode('signup')}
              >
                Create an account
              </button>
            </span>
          ) : mode === 'signup' ? (
            <span>
              Already have an account?{' '}
              <button
                type="button"
                className="btn btn-link auth-switch"
                onClick={() => setMode('login')}
              >
                Login
              </button>
            </span>
          ) : (
            <span>
              Remember your password?{' '}
              <button
                type="button"
                className="btn btn-link auth-switch"
                onClick={() => setMode('login')}
              >
                Back to Login
              </button>
            </span>
          )}
        </div>
      </div>
    </div>
  );
};

export default Authentication;
