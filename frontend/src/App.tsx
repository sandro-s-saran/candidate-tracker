import { useEffect, useState } from "react";
import { listCandidates } from "./api";
import type { Candidate } from "./types";

export default function App() {
  const [candidates, setCandidates] = useState<Candidate[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    listCandidates()
      .then(setCandidates)
      .catch((e: Error) => setError(e.message));
  }, []);

  return (
    <main>
      <h1>Candidate Tracker</h1>
      {error && <p role="alert">{error}</p>}
      <ul>
        {candidates.map((c) => (
          <li key={c.id}>
            {c.name} – {c.roleAppliedFor} ({c.stage})
          </li>
        ))}
      </ul>
    </main>
  );
}