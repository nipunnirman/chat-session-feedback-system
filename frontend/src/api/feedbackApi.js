const BASE = 'http://localhost:8080';

export async function getFeedbackInfo(feedbackId) {
  const res = await fetch(`${BASE}/api/public/feedback/${feedbackId}`);
  if (!res.ok) throw new Error(`Failed to fetch: ${res.status}`);
  return res.json();
}

export async function submitRating(feedbackId, rating) {
  const res = await fetch(`${BASE}/api/public/feedback/${feedbackId}/respond`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ rating }),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || `Error ${res.status}`);
  }
  return res.json();
}
