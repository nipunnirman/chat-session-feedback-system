import { useEffect } from 'react';
import './Toast.css';

export default function Toast({ message, type, onClose }) {
  useEffect(() => {
    const t = setTimeout(onClose, 3500);
    return () => clearTimeout(t);
  }, [onClose]);

  return (
    <div className={`toast toast--${type}`}>
      <span className="toast-icon">{type === 'success' ? '✓' : '✕'}</span>
      {message}
    </div>
  );
}
