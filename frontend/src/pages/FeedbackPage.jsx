import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { getFeedbackInfo, submitRating } from '../api/feedbackApi';
import './FeedbackPage.css';

export default function FeedbackPage() {
  const { feedbackId } = useParams();

  const [info, setInfo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [hovered, setHovered] = useState(0);
  const [selected, setSelected] = useState(0);
  const [submitting, setSubmitting] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [submitError, setSubmitError] = useState('');

  useEffect(() => {
    getFeedbackInfo(feedbackId)
      .then(setInfo)
      .catch(() => setError('This feedback link is invalid or unavailable.'))
      .finally(() => setLoading(false));
  }, [feedbackId]);

  const handleSubmit = async () => {
    if (!selected) return;
    setSubmitting(true);
    setSubmitError('');
    try {
      await submitRating(feedbackId, selected);
      setSubmitted(true);
    } catch (err) {
      setSubmitError(err.message || 'Failed to submit. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  // ── Loading ──────────────────────────────────────────────────────────────
  if (loading) {
    return (
      <div className="fb-center">
        <div className="fb-spinner" />
        <p className="fb-muted">Loading your feedback form…</p>
      </div>
    );
  }

  // ── Error / not found ────────────────────────────────────────────────────
  if (error || !info) {
    return (
      <div className="fb-center">
        <div className="fb-status-card fb-status-card--error">
          <div className="fb-status-icon">⚠️</div>
          <h2>Link Not Found</h2>
          <p>{error || 'This feedback link does not exist.'}</p>
        </div>
      </div>
    );
  }

  const { status, formConfig } = info;

  // ── Expired ──────────────────────────────────────────────────────────────
  if (status === 'EXPIRED') {
    return (
      <div className="fb-center">
        <div className="fb-status-card fb-status-card--expired">
          <div className="fb-status-icon">⏰</div>
          <h2>Link Expired</h2>
          <p>{formConfig?.expiredReplyText || 'Sorry, this feedback link has expired.'}</p>
        </div>
      </div>
    );
  }

  // ── Already responded ────────────────────────────────────────────────────
  if (status === 'RESPONDED') {
    return (
      <div className="fb-center">
        <div className="fb-status-card fb-status-card--responded">
          <div className="fb-status-icon">✅</div>
          <h2>Already Responded</h2>
          <p>You have already submitted your feedback for this session. Thank you!</p>
        </div>
      </div>
    );
  }

  // ── Thank you screen (after submission) ──────────────────────────────────
  if (submitted) {
    return (
      <div className="fb-center">
        <div className="fb-thankyou-card">
          <div className="fb-confetti">🎉</div>
          <div className="fb-stars-display">
            {'★'.repeat(selected)}{'☆'.repeat(5 - selected)}
          </div>
          <h2>{formConfig?.thankYouText || 'Thank you for your feedback!'}</h2>
          <p className="fb-muted">Your response has been recorded.</p>
        </div>
      </div>
    );
  }

  // ── Active feedback form ─────────────────────────────────────────────────
  const labels = formConfig?.ratingLabels || ['Very Poor', 'Poor', 'Average', 'Good', 'Excellent'];
  const activeLabel = hovered
    ? labels[hovered - 1]
    : selected
    ? labels[selected - 1]
    : '';

  return (
    <div className="fb-bg">
      <div className="fb-orb fb-orb-1" />
      <div className="fb-orb fb-orb-2" />

      <div className="fb-card">
        {/* Header */}
        <div className="fb-header">
          <div className="fb-brand-dot" />
          <div>
            <h1 className="fb-title">
              {formConfig?.headerText || 'How was your experience?'}
            </h1>
            {formConfig?.headerDescription && (
              <p className="fb-desc">{formConfig.headerDescription}</p>
            )}
          </div>
        </div>

        {/* Star rating */}
        <div className="fb-rating-area">
          <div className="fb-stars">
            {[1, 2, 3, 4, 5].map((star) => (
              <button
                key={star}
                type="button"
                className={`fb-star ${star <= (hovered || selected) ? 'fb-star--active' : ''} ${star === selected ? 'fb-star--selected' : ''}`}
                onMouseEnter={() => setHovered(star)}
                onMouseLeave={() => setHovered(0)}
                onClick={() => setSelected(star)}
                aria-label={`Rate ${star} out of 5`}
              >
                ★
              </button>
            ))}
          </div>

          <div className="fb-label-row">
            {activeLabel ? (
              <span className="fb-label fb-label--active">{activeLabel}</span>
            ) : (
              <span className="fb-label">Select your rating</span>
            )}
          </div>

          {/* Rating legend */}
          <div className="fb-legend">
            {labels.map((lbl, i) => (
              <span
                key={i}
                className={`fb-legend-item ${selected === i + 1 ? 'fb-legend-item--active' : ''}`}
              >
                {i + 1}. {lbl}
              </span>
            ))}
          </div>
        </div>

        {/* Submit */}
        {submitError && <div className="fb-error">{submitError}</div>}
        <button
          type="button"
          className="fb-submit-btn"
          disabled={!selected || submitting}
          onClick={handleSubmit}
        >
          {submitting ? <span className="fb-spinner fb-spinner--sm" /> : 'Submit Feedback →'}
        </button>

        {/* Footer */}
        {formConfig?.footerText && (
          <p className="fb-footer">{formConfig.footerText}</p>
        )}
      </div>
    </div>
  );
}
