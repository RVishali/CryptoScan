import React from 'react';
import { useNavigate } from 'react-router-dom';
import AuthParticles from './AuthParticles';

const Home = () => {
  const navigate = useNavigate();

  return (
    <div className="home-page">
      {/* Full-screen particles background */}
      <AuthParticles />

      {/* Centered hero content */}
      <main className="home-main">
        <section className="home-hero-card">
          {/* Centered branding */}
          <div className="home-branding">
            <span className="home-brand-name">CryptoScan</span>
            <span className="home-brand-sub">: Risk Analyzer</span>
          </div>

          <h1 className="home-hero-title">Stay ahead of web attacks.</h1>

          <p className="hero-desc-main">
            CryptoScan continuously analyzes your web applications to catch
            security issues before attackers do. Detect vulnerabilities,
            prioritize risks, and strengthen your security posture with
            automated scans.
          </p>

          {/* Feature boxes */}
          <div className="feature-boxes">
            <div className="feature-box-small">
              <span className="feature-icon">🔍</span>
              <p className="feature-text">Automated Scanning</p>
            </div>
            <div className="feature-box-small">
              <span className="feature-icon">⚠️</span>
              <p className="feature-text">Threat Severity</p>
            </div>
            <div className="feature-box-small">
              <span className="feature-icon">📊</span>
              <p className="feature-text">Visual Reports</p>
            </div>
            <div className="feature-box-small">
              <span className="feature-icon">🛡️</span>
              <p className="feature-text">Real-time Detection</p>
            </div>
            <div className="feature-box-small">
              <span className="feature-icon">📈</span>
              <p className="feature-text">Track Progress</p>
            </div>
          </div>

          <button
            className="btn btn-cyber mt-4"
            onClick={() => navigate('/signup')}
          >
            Let&apos;s get started
          </button>
        </section>
      </main>
    </div>
  );
};

export default Home;
