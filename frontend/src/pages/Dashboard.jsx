import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getFormConfig } from '../api/adminApi';
import './Dashboard.css';

export default function Dashboard() {
  const [enterpriseId, setEnterpriseId] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleLoad = async (e) => {
    e.preventDefault();
    if (!enterpriseId.trim()) return;
    setError('');
    setLoading(true);
    try {
      const data = await getFormConfig(enterpriseId.trim());
      navigate(`/editor/${enterpriseId.trim()}`, { state: { config: data } });
    } catch (err) {
      setError(err.message || 'Enterprise not found. Try a different ID.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="dashboard-bg">
      <div className="orb orb-1" />
      <div className="orb orb-2" />
      <div className="orb orb-3" />

      <div className="dashboard-card">
        <div className="brand">
          <div className="brand-icon">💬</div>
          <div>
            <h1>Feedback Admin</h1>
            <p className="brand-sub">Chat Session Feedback System</p>
          </div>
        </div>

        <p className="dashboard-desc">
          Manage and customise feedback form configuration for any enterprise.
        </p>

        <form onSubmit={handleLoad} className="dashboard-form">
          <div className="input-group">
            <label htmlFor="enterpriseId">Enterprise ID</label>
            <input
              id="enterpriseId"
              type="text"
              className="input input--large"
              placeholder="e.g. enterprise-001"
              value={enterpriseId}
              onChange={(e) => setEnterpriseId(e.target.value)}
              autoComplete="off"
            />
          </div>

          {error && <div className="error-banner">{error}</div>}

          <button type="submit" className="btn btn--primary btn--full" disabled={loading || !enterpriseId.trim()}>
            {loading ? <span className="spinner" /> : '→ Load Form Config'}
          </button>
        </form>

        <div className="quick-demo">
          <p>Quick demo:</p>
          <button
            type="button"
            className="chip"
            onClick={() => setEnterpriseId('enterprise-001')}
          >
            enterprise-001
          </button>
        </div>
      </div>
    </div>
  );
}
