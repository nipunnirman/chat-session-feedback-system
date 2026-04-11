import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import './ResponsesPage.css';

const BASE = 'http://localhost:8080';

export default function ResponsesPage() {
  const { enterpriseId } = useParams();
  const [responses, setResponses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [newLink, setNewLink] = useState(null);

  useEffect(() => {
    loadResponses();
  }, [enterpriseId]);

  async function loadResponses() {
    setLoading(true);
    const res = await fetch(`${BASE}/api/admin/enterprises/${enterpriseId}/responses`);
    const data = await res.json();
    setResponses(data);
    setLoading(false);
  }

  async function generateLink() {
    setGenerating(true);
    setNewLink(null);
    const res = await fetch(
      `${BASE}/api/admin/enterprises/${enterpriseId}/create-feedback-link?channel=WHATSAPP`,
      { method: 'POST' }
    );
    const data = await res.json();
    const generatedFeedbackId = data.link.split('/').pop();
    setNewLink(`${window.location.origin}/feedback/${generatedFeedbackId}`);
    setGenerating(false);
    loadResponses(); // refresh table
  }

  const total = responses.length;
  const responded = responses.filter(r => r.status === 'RESPONDED').length;
  const avgRating = responded > 0
    ? (responses.filter(r => r.rating).reduce((s, r) => s + r.rating, 0) / responded).toFixed(1)
    : '—';

  return (
    <div className="page-layout">
      <nav className="sidebar">
        <div className="brand">
          <Link to="/" className="back-link">← Dashboard</Link>
        </div>
        <div className="enterprise-info">
          <div className="enterprise-icon">⚙️</div>
          <h2>Form Config</h2>
          <span className="enterprise-id">{enterpriseId}</span>
        </div>
        <ul className="nav-links">
          <li><Link to={`/editor/${enterpriseId}`}>Form Config</Link></li>
          <li className="active"><Link to={`/responses/${enterpriseId}`}>📊 Responses</Link></li>
        </ul>
      </nav>

      <main className="main-content">
        <header className="content-header">
          <div>
            <h1 className="page-title gradient-text">Customer Responses</h1>
            <p className="page-sub">All feedback submitted by customers of <strong>{enterpriseId}</strong>.</p>
          </div>
          <button className="btn-primary" onClick={generateLink} disabled={generating}>
            {generating ? '⏳ Generating…' : '+ New Feedback Link'}
          </button>
        </header>

        {/* New link popup */}
        {newLink && (
          <div className="new-link-banner">
            <div>
              <strong>✅ New unique link created! Send this to your customer:</strong>
              <a href={newLink} target="_blank" rel="noreferrer">{newLink}</a>
            </div>
            <button onClick={() => { navigator.clipboard.writeText(newLink); }} className="copy-btn">📋 Copy</button>
          </div>
        )}

        {/* Stats row */}
        <div className="stats-row">
          <div className="stat-card"><div className="stat-num">{total}</div><div className="stat-label">Total Links</div></div>
          <div className="stat-card"><div className="stat-num">{responded}</div><div className="stat-label">Responded</div></div>
          <div className="stat-card"><div className="stat-num">{avgRating}</div><div className="stat-label">Avg Rating</div></div>
          <div className="stat-card"><div className="stat-num">{total - responded}</div><div className="stat-label">Pending / Expired</div></div>
        </div>

        {/* Table */}
        <div className="responses-list">
          {loading ? (
            <div className="fb-spinner" />
          ) : responses.length === 0 ? (
            <p style={{ color: 'var(--muted)', textAlign: 'center', padding: '2rem' }}>
              No feedback links yet. Click "+ New Feedback Link" above to generate one.
            </p>
          ) : (
            <table className="responses-table">
              <thead>
                <tr>
                  <th>Status</th>
                  <th>Rating</th>
                  <th>Channel</th>
                  <th>Created</th>
                  <th>Responded At</th>
                  <th>Link (send to customer)</th>
                </tr>
              </thead>
              <tbody>
                {responses.map(r => (
                  <tr key={r.id}>
                    <td>
                      <span className={`status-badge status-${r.status.toLowerCase()}`}>
                        {r.status}
                      </span>
                    </td>
                    <td className="rating-col">
                      {r.rating ? (
                        <span title={`${r.rating} stars`}>
                          {'⭐'.repeat(r.rating)} <span className="rating-num">({r.rating}/5)</span>
                        </span>
                      ) : '—'}
                    </td>
                    <td>{r.channel}</td>
                    <td className="date-col">{new Date(r.createdAt).toLocaleString()}</td>
                    <td className="date-col">{r.respondedAt ? new Date(r.respondedAt).toLocaleString() : '—'}</td>
                    <td>
                      <a href={`${window.location.origin}/feedback/${r.feedbackId}`} target="_blank" rel="noreferrer">
                        Open ↗
                      </a>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </main>
    </div>
  );
}
