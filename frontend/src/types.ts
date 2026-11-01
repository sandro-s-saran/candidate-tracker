export type Stage = "APPLIED" | "SCREENING" | "INTERVIEW" | "OFFER" | "REJECTED";

export interface Note {
  text: string;
  createdAt: string;
}

export interface Candidate {
  id: number;
  name: string;
  email: string;
  phone: string | null;
  roleAppliedFor: string;
  source: string | null;
  resumeText: string | null;
  stage: Stage;
  createdAt: string;
  notes: Note[];
}