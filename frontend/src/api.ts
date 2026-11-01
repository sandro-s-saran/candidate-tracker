import type { Candidate, Stage } from "./types";

export type SortField = "DATE_ADDED" | "STAGE";

export interface CandidateFilters {
  stage?: Stage;
  role?: string;
  sort?: SortField;
}

export interface NewCandidate {
  name: string;
  email: string;
  roleAppliedFor: string;
  phone?: string;
  source?: string;
  resumeText?: string;
}

export class ApiError extends Error {
  status: number;

  constructor(status: number, message: string) {
    super(message);
    this.status = status;
  }
}

async function request<T>(url: string, init?: RequestInit): Promise<T> {
  const response = await fetch(url, init);
  if (!response.ok) {
    throw new ApiError(response.status, `Request failed (${response.status})`);
  }
  return response.status === 204 ? (undefined as T) : response.json();
}

function jsonRequest(method: string, body: unknown): RequestInit {
  return {
    method,
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  };
}

export function listCandidates(filters: CandidateFilters = {}): Promise<Candidate[]> {
  const params = new URLSearchParams();
  if (filters.stage) params.set("stage", filters.stage);
  if (filters.role) params.set("role", filters.role);
  if (filters.sort) params.set("sort", filters.sort);
  const query = params.toString();
  return request(`/api/candidates${query ? `?${query}` : ""}`);
}

export function getCandidate(id: number): Promise<Candidate> {
  return request(`/api/candidates/${id}`);
}

export function createCandidate(newCandidate: NewCandidate): Promise<Candidate> {
  return request("/api/candidates", jsonRequest("POST", newCandidate));
}

export function moveCandidate(id: number, stage: Stage): Promise<Candidate> {
  return request(`/api/candidates/${id}/stage`, jsonRequest("PATCH", { stage }));
}

export function addNote(id: number, text: string): Promise<Candidate> {
  return request(`/api/candidates/${id}/notes`, jsonRequest("POST", { text }));
}

export function deleteCandidate(id: number): Promise<void> {
  return request(`/api/candidates/${id}`, { method: "DELETE" });
}