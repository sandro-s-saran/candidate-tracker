import { useEffect, useState } from "react";
import { listCandidates } from "./api";
import type { CandidateFilters } from "./api";
import CandidateForm from "./components/CandidateForm";
import FilterBar from "./components/FilterBar";
import type { Candidate } from "./types";

export default function App() {
  const [candidates, setCandidates] = useState<Candidate[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [filters, setFilters] = useState<CandidateFilters>({ sort: "DATE_ADDED" });
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    let ignore = false;
    listCandidates(filters)
      .then((data) => {
        if (!ignore) {
          setCandidates(data);
          setError(null);
        }
      })
      .catch((e: Error) => {
        if (!ignore) setError(e.message);
      });
    return () => {
      ignore = true;
    };
  }, [filters, reloadKey]);

  return (
    <main>
      <h1>Candidate Tracker</h1>
      <CandidateForm onCreated={() => setReloadKey((k) => k + 1)} />
      <FilterBar filters={filters} onChange={setFilters} />
      {error && <p role="alert">{error}</p>}
      {candidates.length === 0 ? (
        <p>No candidates match.</p>
      ) : (
        <ul>
          {candidates.map((c) => (
            <li key={c.id}>
              {c.name} – {c.roleAppliedFor} ({c.stage}) – added{" "}
              {new Date(c.createdAt).toLocaleDateString()}
            </li>
          ))}
        </ul>
      )}
    </main>
  );
}