import React, { useState } from 'react';

const WebsiteForm = ({ onSubmit, onShowHistory }) => {
  const [url, setUrl] = useState('');
  const [name, setName] = useState('');
  const [scanType, setScanType] = useState('random');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!name.trim() || !url.trim()) {
      setError('Please provide both website name and URL.');
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/api/submission/create", {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          name,
          url,
          scanType: scanType === "own" ? "FULL" : "FULL" 
        })
      });

      if (!response.ok) {
        setError("Could not submit website.");
        return;
      }

      const data = await response.json();
      onSubmit(data);  // ← FIXED

      // reset form
      setName('');
      setUrl('');
      setScanType('random');

    } catch (err) {
      console.error(err);
      setError("Could not submit website (network error).");
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="mb-3">
        <label className="form-label">Scan Type</label>
        <select
          className="form-select cyberpunk-input"
          value={scanType}
          onChange={(e) => setScanType(e.target.value)}
        >
          <option value="random">Random website</option>
          <option value="own">Testing my own website</option>
        </select>
      </div>

      <div className="mb-3">
        <label className="form-label">Website Name</label>
        <input
          type="text"
          className="form-control cyberpunk-input"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
      </div>

      <div className="mb-3">
        <label className="form-label">Website URL</label>
        <input
          type="url"
          className="form-control cyberpunk-input"
          value={url}
          onChange={(e) => setUrl(e.target.value)}
        />
      </div>

      {error && <div className="alert alert-danger py-2">{error}</div>}

      <button type="submit" className="btn btn-cyber w-100">Start Scan</button>

      <button
        type="button"
        className="btn btn-cyber w-100 mt-2"
        onClick={onShowHistory}
      >
        Your Websites
      </button>
    </form>
  );
};

export default WebsiteForm;
