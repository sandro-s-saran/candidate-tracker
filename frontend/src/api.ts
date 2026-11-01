import type { Candidate } from "./types";

export interface NewCandidate {
  name: string;
  email: string;
  roleAppliedFor: string;
  phone?: string;
  source?: string;
  resumeText?: string;
}

export async function listCandidates(): Promise<Candidate[]> {
  const response = await fetch("/api/candidates");
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