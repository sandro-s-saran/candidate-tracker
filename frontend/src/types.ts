export const STAGES = ["APPLIED", "SCREENING", "INTERVIEW", "OFFER", "REJECTED"] as const;
export type Stage = (typeof STAGES)[number];

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