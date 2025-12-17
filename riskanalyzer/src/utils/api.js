// Centralized API helper for backend communication

const API_BASE = "http://localhost:8080";

export async function apiGet(path) {
  const res = await fetch(API_BASE + path, {
    method: "GET",
    credentials: "include",
  });
  return res.json();
}

export async function apiPost(path, body) {
  const res = await fetch(API_BASE + path, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify(body),
  });

  return res.json();
}

export async function apiPostForm(path) {
  const res = await fetch(API_BASE + path, {
    method: "POST",
    credentials: "include",
  });
  return res.json();
}

// For downloading PDF
export async function downloadPdf(submissionId) {
  const res = await fetch(`${API_BASE}/api/report/${submissionId}`, {
    method: "GET",
    credentials: "include",
  });

  if (!res.ok) {
    throw new Error("PDF not ready");
  }

  const blob = await res.blob();
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = `security-report-${submissionId}.pdf`;
  link.click();
}
