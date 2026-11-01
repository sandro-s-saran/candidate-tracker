import type { Candidate, Stage } from "./types";

export interface NewCandidate {
  name: string;
  email: string;
  roleAppliedFor: string;
  phone?: string;
  source?: string;
  resumeText?: string;
}

export type SortField = "DATE_ADDED" | "STAGE";

export interface CandidateFilters {
  stage?: Stage;
  role?: string;
  sort?: SortField;
}

export async function listCandidates(filters: CandidateFilters = {}): Promise<Candidate[]> {
  const params = new URLSearchParams();
  if (filters.stage) params.set("stage", filters.stage);
  if (filters.role) params.set("role", filters.role);
  if (filters.sort) params.set("sort", filters.sort);
  const query = params.toString();

  const response = await fetch(`/api/candidates${query ? `?${query}` : ""}`);
  if (!response.ok) {
    throw new Error(`Failed to load candidates (${response.status})`);
  }
  return response.json();
}

export async function createCandidate(newCandidate: NewCandidate): Promise<Candidate> {
  const response = await fetch("/api/candidates", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(newCandidate),
  });
  if (!response.ok) {
    throw new Error(`Failed to add candidate (${response.status})`);
  }
  return response.json();
}