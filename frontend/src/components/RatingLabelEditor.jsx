const LABELS = ['1 Star', '2 Stars', '3 Stars', '4 Stars', '5 Stars'];

export default function RatingLabelEditor({ value = [], onChange }) {
  const update = (i, v) => {
    const next = [...value];
    next[i] = v;
    onChange(next);
  };

  return (
    <div className="rating-label-editor">
      {Array.from({ length: 5 }).map((_, i) => (
        <div key={i} className="rating-label-row">
          <span className="rating-star">{'★'.repeat(i + 1)}</span>
          <input
            type="text"
            className="input"
            placeholder={LABELS[i]}
            value={value[i] || ''}
            onChange={(e) => update(i, e.target.value)}
            maxLength={80}
          />
        </div>
      ))}
    </div>
  );
}
