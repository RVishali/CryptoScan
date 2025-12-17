import React, { useState, useEffect } from 'react';
import WebsiteForm from './WebsiteForm';
import Notification from './Notification';
import AuthParticles from './AuthParticles';

const Dashboard = ({ user, onLogout }) => {
  const [websites, setWebsites] = useState([]);
  const [selectedWebsite, setSelectedWebsite] = useState(null);
  const [results, setResults] = useState([]);
  const [notif, setNotif] = useState({ message: '', type: 'info' });
  const [poller, setPoller] = useState(null);
  const [selectedThreat, setSelectedThreat] = useState(null);

  // -----------------------------
  // STEP 1: Submission Received
  // -----------------------------
  const handleBackendSubmission = async (submission) => {
    // Normalize backend field names
    const normalized = {
      ...submission,
      url: submission.url || submission.targetUrl,
    };

    setWebsites((prev) => [...prev, normalized]);
    setSelectedWebsite(normalized);
    setNotif({ message: `Scan started for ${normalized.name}`, type: "info" });

    await startScan(normalized.id);

    const interval = setInterval(() => pollScan(normalized.id), 3000);
    setPoller(interval);
  };

  // -----------------------------
  // STEP 2: Trigger ZAP Scan
  // -----------------------------
  const startScan = async (id) => {
    try {
      await fetch(`http://localhost:8080/api/zap/start?submissionId=${id}`, {
        method: "POST",
        credentials: "include"
      });
    } catch (err) {
      console.error("Cannot start scan:", err);
      setNotif({ message: "Scan could not start.", type: "danger" });
    }
  };

  // -----------------------------
  // STEP 3: Poll scan + results
  // -----------------------------
  const pollScan = async (id) => {
    try {
      // 1. Get submission status
      const subResp = await fetch("http://localhost:8080/api/submission/me", {
        credentials: "include"
      });
      const allSubs = await subResp.json();
      const current = allSubs.find((s) => s.id === id);

      if (!current) return;

      setSelectedWebsite(current);

      if (current.status === "COMPLETED") {
        clearInterval(poller);
        setPoller(null);
        setNotif({ message: "Scan completed!", type: "success" });

        // Fetch results
        await fetchResults(id);
      }

      if (current.status === "FAILED") {
        clearInterval(poller);
        setPoller(null);
        setNotif({ message: "Scan over.", type: "danger" });
      }

    } catch (err) {
      console.error("Polling error:", err);
    }
  };

  // -----------------------------
  // STEP 4: Fetch and Group Results
  // -----------------------------
  const fetchResults = async (id) => {
    try {
      const response = await fetch(`http://localhost:8080/api/results/${id}`, {
        credentials: "include"
      });

      const data = await response.json();

      // Group by vulnerability type to avoid duplicates
      const grouped = {};
      
      data.forEach((r) => {
        const type = r.vulnerabilityType;
        
        if (!grouped[type]) {
          grouped[type] = {
            type: type,
            severity: r.severity,
            description: r.description,
            fix: r.fix,
            priority: r.severity === "High" ? 1 : r.severity === "Medium" ? 2 : 3
          };
        } else {
          // Keep the highest severity if duplicate
          if (r.severity === "High") {
            grouped[type].severity = "High";
            grouped[type].priority = 1;
            grouped[type].description = r.description;
            grouped[type].fix = r.fix;
          } else if (r.severity === "Medium" && grouped[type].severity !== "High") {
            grouped[type].severity = "Medium";
            grouped[type].priority = 2;
            grouped[type].description = r.description;
            grouped[type].fix = r.fix;
          }
        }
      });

      // Convert to array and sort by priority (High first)
      const groupedArray = Object.values(grouped).sort((a, b) => a.priority - b.priority);
      
      setResults(groupedArray);
    } catch (err) {
      console.error("Failed to load results", err);
    }
  };

  // -----------------------------
  // UI BELOW
  // -----------------------------

  if (!user) {
    return null;
  }

  const showHistory = () => {
    const newWindow = window.open('', '_blank');
    const recentWebsites = [...websites].slice(-5).reverse();

    newWindow.document.write(`
      <html>
      <body style="background:#0f172a;color:white;font-family:monospace;padding:20px;">
        <h2>Recent Scan History</h2>
        <ul>
          ${recentWebsites.map(s => `<li>${s.name} — ${s.url}</li>`).join("")}
        </ul>
      </body>
      </html>
    `);
  };

  return (
    <div className="dashboard-page">
      <AuthParticles />

      <header className="dash-header shadow-sm">
        <div>
          <h1 className="dash-title">CryptoScan</h1>
          <p className="dash-subtitle">A Risk Analyzer to monitor vulnerabilities in one place.</p>
        </div>

        <div className="dash-user">
          <span className="dash-user-name">Hi, {user.name}</span>
          <button className="btn btn-sm btn-outline-light ms-2" onClick={onLogout}>
            Logout
          </button>
        </div>
      </header>

      <main className="container dash-main">
        <Notification
          message={notif.message}
          type={notif.type}
          onClose={() => setNotif({ message: '', type: 'info' })}
        />

        <div className="row g-4 mt-1">
          <div className="col-lg-4">
            <div className="no-bootstrap-card card-glass h-100">
              <div className="card-body">
                <h5 className="card-title">Submit Website</h5>
                <p className="small mb-3" style={{ color: '#9ca3af' }}>
                  Add a site to start a scan.
                </p>
                <WebsiteForm
                  onSubmit={handleBackendSubmission}
                  websites={websites}
                  onShowHistory={showHistory}
                />
              </div>
            </div>
          </div>

          <div className="col-lg-8">
            <div className="no-bootstrap-card card-results h-100">
              <div className="card-body">
                <h5>
                  {selectedWebsite
                    ? `Scan Results: ${selectedWebsite.name}`
                    : "Scan Results"}
                </h5>

                {results.length === 0 ? (
                  <p className="small" style={{ color: '#9ca3af' }}>
                    Run a scan to see detected vulnerabilities.
                  </p>
                ) : (
                  <div>
                    <h6>Threat Priority & Severity</h6>

                    <div className="threat-chart-wrapper">
                      {results.map((v, idx) => (
                        <div key={idx} className="threat-bar-wrapper">
                          <div
                            className={`threat-bar priority-${v.priority} ${
                              v.severity === "High"
                                ? "threat-high"
                                : v.severity === "Medium"
                                ? "threat-medium"
                                : "threat-low"
                            }`}
                            onClick={() => setSelectedThreat(v)}
                          ></div>
                          <span className="threat-bar-label">{v.type}</span>
                        </div>
                      ))}
                    </div>

                    {selectedThreat && (
                      <div className="threat-details">
                        <h6>{selectedThreat.type}</h6>
                        <p>
                          Severity: {selectedThreat.severity} | Priority: {selectedThreat.priority}
                        </p>
                        <p>{selectedThreat.description}</p>
                        <p><strong>Fix:</strong> {selectedThreat.fix}</p>
                      </div>
                    )}
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default Dashboard;