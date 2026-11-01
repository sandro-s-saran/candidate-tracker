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

export type SummaryStatus = "GENERATED" | "NO_RESUME_TEXT" | "UNAVAILABLE";

export interface SummaryResult {
  status: SummaryStatus;
  text: string | null;
}