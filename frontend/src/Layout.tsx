import { useCallback, useEffect, useState } from "react";
import { NavLink, Link, Outlet } from "react-router";
import { listCandidates } from "./api";
import type { CandidateFilters } from "./api";
import FilterBar from "./components/FilterBar";
import type { Candidate } from "./types";

export default function Layout() {
  const [candidates, setCandidates] = useState<Candidate[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [filters, setFilters] = useState<CandidateFilters>({ sort: "DATE_ADDED" });
  const [reloadKey, setReloadKey] = useState(0);

  const refresh = useCallback(() => setReloadKey((k) => k + 1), []);

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
    <div className="app">
      <header className="app-header">
        <h1>Candidate Management System</h1>
      </header>
      <div className="app-body">
        <aside className="sidebar" aria-label="Candidates">
          <FilterBar filters={filters} onChange={setFilters} />
          <Link to="/candidates/new" className="add-button">
            Add candidate
          </Link>
          {error && <p role="alert">{error}</p>}
          {candidates.length === 0 ? (
            <p>No candidates match.</p>
          ) : (
            <ul className="candidate-list">
              {candidates.map((c) => (
                <li key={c.id}>
                  <NavLink
                    to={`/candidates/${c.id}`}
                    className={({ isActive }) => (isActive ? "active" : undefined)}
                  >
                    {c.name}
                    <small>
                      {c.roleAppliedFor} · {c.stage}
                    </small>
                  </NavLink>
                </li>
              ))}
            </ul>
          )}
        </aside>
        <main className="pane">
          <Outlet context={{ refresh }} />
        </main>
      </div>
    </div>
  );
}