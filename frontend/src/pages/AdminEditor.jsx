import { useState, useEffect } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { getFormConfig, upsertFormConfig } from '../api/adminApi';
import RatingLabelEditor from '../components/RatingLabelEditor';
import ChannelSelector from '../components/ChannelSelector';
import Toast from '../components/Toast';
import './AdminEditor.css';

const DEFAULT_FORM = {
  headerText: '',
  headerDescription: '',
  footerText: '',
  ratingLabels: ['Very Poor', 'Poor', 'Average', 'Good', 'Excellent'],
  thankYouText: '',
  invalidReplyText: '',
  expiredReplyText: '',
  skipForChannels: [],
};

export default function AdminEditor() {
  const { enterpriseId } = useParams();
  const location = useLocation();
  const navigate = useNavigate();

  const [form, setForm] = useState(DEFAULT_FORM);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [toast, setToast] = useState(null);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (location.state?.config) {
      setForm({ ...DEFAULT_FORM, ...location.state.config });
    } else {
      setLoading(true);
      getFormConfig(enterpriseId)
        .then((data) => setForm({ ...DEFAULT_FORM, ...data }))
        .catch(() => setToast({ message: 'Could not load config', type: 'error' }))
        .finally(() => setLoading(false));
    }
  }, [enterpriseId, location.state]);

  const set = (key, value) => {
    setForm((prev) => ({ ...prev, [key]: value }));
    setErrors((prev) => ({ ...prev, [key]: '' }));
  };

  const validate = () => {
    const e = {};
    if (!form.headerText.trim()) e.headerText = 'Required';
    if (!form.thankYouText.trim()) e.thankYouText = 'Required';
    if (!form.invalidReplyText.trim()) e.invalidReplyText = 'Required';
    if (!form.expiredReplyText.trim()) e.expiredReplyText = 'Required';
    if (form.ratingLabels.some((l) => !l.trim())) e.ratingLabels = 'All 5 labels are required';
    return e;
  };

  const handleSave = async (e) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length > 0) { setErrors(errs); return; }
    setSaving(true);
    try {
      const saved = await upsertFormConfig(enterpriseId, form);
      setForm({ ...DEFAULT_FORM, ...saved });
      setToast({ message: 'Form config saved successfully!', type: 'success' });
    } catch (err) {
      setToast({ message: err.message || 'Failed to save', type: 'error' });
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="editor-loading">
        <div className="spinner spinner--lg" />
        <p>Loading configuration…</p>
      </div>
    );
  }

  return (
    <div className="editor-layout">
      {/* Sidebar */}
      <aside className="editor-sidebar">
        <button className="back-btn" onClick={() => navigate('/')}>← Dashboard</button>
        <div className="sidebar-header">
          <div className="sidebar-icon">⚙️</div>
          <h2>Form Config</h2>
          <p className="sidebar-eid">{enterpriseId}</p>
        </div>
        <nav className="sidebar-nav">
          {['Header', 'Rating Labels', 'Messages', 'Channels'].map((s) => (
            <a key={s} href={`#section-${s.replace(' ', '-').toLowerCase()}`} className="sidebar-link">
              {s}
            </a>
          ))}
        </nav>
      </aside>

      {/* Main editor */}
      <main className="editor-main">
        <div className="editor-topbar">
          <div>
            <h1 className="editor-title">Feedback Form Editor</h1>
            <p className="editor-sub">Customise the form shown to end users after their chat session.</p>
          </div>
          <button
            type="button"
            className="btn btn--primary"
            onClick={handleSave}
            disabled={saving}
          >
            {saving ? <><span className="spinner" /> Saving…</> : '💾 Save Changes'}
          </button>
        </div>

        <form onSubmit={handleSave} className="editor-form">

          {/* Header Section */}
          <section className="editor-section" id="section-header">
            <h3 className="section-title"><span>01</span> Header</h3>
            <div className="field-group">
              <div className="input-group">
                <label>Header Text <span className="req">*</span></label>
                <input
                  type="text"
                  className={`input ${errors.headerText ? 'input--error' : ''}`}
                  placeholder="How was your experience?"
                  value={form.headerText}
                  onChange={(e) => set('headerText', e.target.value)}
                  maxLength={200}
                />
                {errors.headerText && <span className="field-error">{errors.headerText}</span>}
                <span className="char-count">{form.headerText.length}/200</span>
              </div>
              <div className="input-group">
                <label>Header Description</label>
                <textarea
                  className="input textarea"
                  placeholder="We'd love to hear your feedback…"
                  value={form.headerDescription || ''}
                  onChange={(e) => set('headerDescription', e.target.value)}
                  maxLength={500}
                  rows={3}
                />
                <span className="char-count">{(form.headerDescription || '').length}/500</span>
              </div>
              <div className="input-group">
                <label>Footer Text</label>
                <input
                  type="text"
                  className="input"
                  placeholder="Thank you for helping us improve!"
                  value={form.footerText || ''}
                  onChange={(e) => set('footerText', e.target.value)}
                  maxLength={300}
                />
              </div>
            </div>
          </section>

          {/* Rating Labels Section */}
          <section className="editor-section" id="section-rating-labels">
            <h3 className="section-title"><span>02</span> Rating Labels</h3>
            <p className="section-desc">Set a label for each star rating (1–5).</p>
            {errors.ratingLabels && <div className="error-banner">{errors.ratingLabels}</div>}
            <RatingLabelEditor
              value={form.ratingLabels}
              onChange={(v) => set('ratingLabels', v)}
            />
          </section>

          {/* Messages Section */}
          <section className="editor-section" id="section-messages">
            <h3 className="section-title"><span>03</span> Messages</h3>
            <div className="field-group">
              <div className="input-group">
                <label>Thank You Text <span className="req">*</span></label>
                <textarea
                  className={`input textarea ${errors.thankYouText ? 'input--error' : ''}`}
                  placeholder="Your feedback has been submitted. Thank you!"
                  value={form.thankYouText}
                  onChange={(e) => set('thankYouText', e.target.value)}
                  maxLength={500}
                  rows={3}
                />
                {errors.thankYouText && <span className="field-error">{errors.thankYouText}</span>}
                <span className="char-count">{form.thankYouText.length}/500</span>
              </div>
              <div className="input-group">
                <label>Invalid Reply Text <span className="req">*</span></label>
                <input
                  type="text"
                  className={`input ${errors.invalidReplyText ? 'input--error' : ''}`}
                  placeholder="Please select a rating between 1 and 5."
                  value={form.invalidReplyText}
                  onChange={(e) => set('invalidReplyText', e.target.value)}
                  maxLength={300}
                />
                {errors.invalidReplyText && <span className="field-error">{errors.invalidReplyText}</span>}
              </div>
              <div className="input-group">
                <label>Expired Reply Text <span className="req">*</span></label>
                <input
                  type="text"
                  className={`input ${errors.expiredReplyText ? 'input--error' : ''}`}
                  placeholder="Sorry, this feedback link has expired."
                  value={form.expiredReplyText}
                  onChange={(e) => set('expiredReplyText', e.target.value)}
                  maxLength={300}
                />
                {errors.expiredReplyText && <span className="field-error">{errors.expiredReplyText}</span>}
              </div>
            </div>
          </section>

          {/* Channels Section */}
          <section className="editor-section" id="section-channels">
            <h3 className="section-title"><span>04</span> Skip Channels</h3>
            <p className="section-desc">Feedback will be skipped for selected channels.</p>
            <ChannelSelector
              value={form.skipForChannels}
              onChange={(v) => set('skipForChannels', v)}
            />
          </section>

          <div className="editor-footer">
            <button type="submit" className="btn btn--primary btn--lg" disabled={saving}>
              {saving ? <><span className="spinner" /> Saving…</> : '💾 Save Changes'}
            </button>
          </div>
        </form>
      </main>

      {toast && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={() => setToast(null)}
        />
      )}
    </div>
  );
}
