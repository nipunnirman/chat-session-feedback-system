const BASE = 'http://localhost:8080';

export async function getFormConfig(enterpriseId) {
  const res = await fetch(`${BASE}/api/admin/enterprises/${enterpriseId}/session-feedback-form`);
  if (!res.ok) throw new Error(`Failed to fetch: ${res.status}`);
  return res.json();
}

export async function upsertFormConfig(enterpriseId, data) {
  const res = await fetch(
    `${BASE}/api/admin/enterprises/${enterpriseId}/session-feedback-form`,
    {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    }
  );
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || `Error ${res.status}`);
  }
  return res.json();
}
