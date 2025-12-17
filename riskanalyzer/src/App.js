import React, { useState, useEffect } from 'react';
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate
} from 'react-router-dom';

import Home from './components/Home';
import Authentication from './components/Authentication';
import Dashboard from './components/Dashboard';
import PasswordReset from './components/PasswordReset';
import './App.css';

function App() {
  const [user, setUser] = useState(null);
  const [checkingSession, setCheckingSession] = useState(true);

  /**
   * 🔐 Validate backend session on app load
   * Block rendering until complete (no flashes)
   */
  useEffect(() => {
    const checkSession = async () => {
      try {
        const res = await fetch('http://localhost:8080/api/submission/me', {
          credentials: 'include',
        });

        if (!res.ok) throw new Error('Not authenticated');

        const storedUser = localStorage.getItem('cryptoScanUser');
        if (storedUser) {
          setUser(JSON.parse(storedUser));
        }
      } catch {
        setUser(null);
        localStorage.removeItem('cryptoScanUser');
      } finally {
        setCheckingSession(false);
      }
    };

    checkSession();
  }, []);

  const handleAuth = (userData) => {
    setUser(userData);
    localStorage.setItem('cryptoScanUser', JSON.stringify(userData));
  };

  const handleLogout = async () => {
    try {
      await fetch('http://localhost:8080/api/auth/logout', {
        method: 'POST',
        credentials: 'include',
      });
    } catch {}

    setUser(null);
    localStorage.removeItem('cryptoScanUser');
  };

  /**
   * 🚫 Render NOTHING until auth state is known
   * This prevents Home / particles / text flashes
   */
  if (checkingSession) {
    return null;
  }

  return (
    <Router>
      <div className="app-root">
        <Routes>
          {/* Public routes */}
          <Route path="/" element={<Home />} />
          <Route
            path="/login"
            element={<Authentication onAuth={handleAuth} />}
          />
          <Route
            path="/signup"
            element={
              <Authentication
                onAuth={handleAuth}
                defaultMode="signup"
              />
            }
          />
          <Route path="/reset-password" element={<PasswordReset />} />

          {/* Protected route */}
          <Route
            path="/dashboard/*"
            element={
              user ? (
                <Dashboard user={user} onLogout={handleLogout} />
              ) : (
                <Navigate to="/" replace />
              )
            }
          />

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
