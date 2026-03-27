const CHANNELS = ['WHATSAPP', 'INSTAGRAM', 'MESSENGER', 'WEB'];

export default function ChannelSelector({ value = [], onChange }) {
  const toggle = (ch) => {
    if (value.includes(ch)) {
      onChange(value.filter((c) => c !== ch));
    } else {
      onChange([...value, ch]);
    }
  };

  const icons = { WHATSAPP: '💬', INSTAGRAM: '📸', MESSENGER: '🗨️', WEB: '🌐' };

  return (
    <div className="channel-selector">
      {CHANNELS.map((ch) => (
        <button
          key={ch}
          type="button"
          className={`channel-chip ${value.includes(ch) ? 'channel-chip--active' : ''}`}
          onClick={() => toggle(ch)}
        >
          <span>{icons[ch]}</span>
          {ch}
        </button>
      ))}
    </div>
  );
}
