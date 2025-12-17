import React from 'react';

const ScanHistory = ({ history, onDeleteOne, onClearAll, onSelectFromHistory }) => {
  if (!history || history.length === 0) {
    return (
      <div className="history-box mt-3">
        <p className="text-muted small mb-0">
          No scans in history yet. Run a scan to start building history.
        </p>
      </div>
    );
  }

  return (
    <div className="history-box mt-3">
      <div className="d-flex justify-content-between align-items-center mb-2">
        <h6 className="mb-0">Scanning History</h6>
        <button
          type="button"
          className="btn btn-sm btn-outline-danger"
          onClick={onClearAll}
        >
          Clear All
        </button>
      </div>
      <ul className="list-group history-list">
        {history.map((item, idx) => (
          <li key={idx} className="list-group-item history-item">
            <div
              className="history-main"
              onClick={() => onSelectFromHistory(item)}
            >
              <div className="fw-semibold">{item.name}</div>
              <div className="small text-truncate">{item.url}</div>
              <div className="small text-muted">
                {item.scanType === 'own' ? 'Own website' : 'Random website'} ·{' '}
                {new Date(item.timestamp).toLocaleString()}
              </div>
              <div className="badge bg-primary mt-1">
                {item.threatCount} threats
              </div>
            </div>
            <button
              type="button"
              className="btn btn-sm btn-outline-secondary ms-2"
              onClick={() => onDeleteOne(item.id)}
            >
              Delete
            </button>
          </li>
        ))}
      </ul>
      <p className="text-muted very-small mt-2 mb-0">
        Note: Deleting history here only clears data on this device. Records
        remain stored safely in the server database.
      </p>
    </div>
  );
};

export default ScanHistory;

