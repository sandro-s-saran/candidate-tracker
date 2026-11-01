import type { Candidate } from "./types";

export async function listCandidates(): Promise<Candidate[]> {
  const response = await fetch("/api/candidates");
  if (!response.ok) {
    throw new Error(`Failed to load candidates (${response.status})`);
  }
  return response.json();
}