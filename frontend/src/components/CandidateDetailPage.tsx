import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import { useNavigate, useParams } from "react-router";
import { addNote, ApiError, deleteCandidate, getCandidate, moveCandidate } from "../api";
import { useLayoutContext } from "../layoutContext";
import SummaryPanel from "./SummaryPanel";
import { STAGES } from "../types";
import type { Candidate, Stage } from "../types";

export default function CandidateDetailPage() {
  const { id } = useParams();
  const candidateId = Number(id);
  const navigate = useNavigate();
  const { refresh } = useLayoutContext();

  const [candidate, setCandidate] = useState<Candidate | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [notFound, setNotFound] = useState(false);
  const [noteText, setNoteText] = useState("");

  useEffect(() => {
    let ignore = false;
    getCandidate(candidateId)
      .then((data) => {
        if (!ignore) {
          setCandidate(data);
          setError(null);
          setNotFound(false);
        }
      })
      .catch((e: Error) => {
        if (ignore) return;
        if (e instanceof ApiError && e.status === 404) {
          setNotFound(true);
        } else {
          setError(e.message);
        }
      });
    return () => {
      ignore = true;
    };
  }, [candidateId]);

  async function handleStageChange(stage: Stage) {
    try {
      setCandidate(await moveCandidate(candidateId, stage));
      refresh();
      setError(null);
    } catch (e) {
      setError((e as Error).message);
    }
  }

  async function handleAddNote(event: FormEvent) {
    event.preventDefault();
    if (!noteText.trim()) return;
    try {
      setCandidate(await addNote(candidateId, noteText));
      setNoteText("");
      setError(null);
    } catch (e) {
      setError((e as Error).message);
    }
  }

  async function handleDelete() {
    if (!window.confirm(`Delete ${candidate?.name}? This cannot be undone.`)) return;
    try {
      await deleteCandidate(candidateId);
      refresh();
      navigate("/");
    } catch (e) {
      setError((e as Error).message);
    }
  }

  if (notFound) {
    return (
      <article>
        <p>Candidate not found.</p>
      </article>
    );
  }

  if (!candidate) {
    return <article>{error ? <p role="alert">{error}</p> : <p>Loading…</p>}</article>;
  }

  return (
    <article>
      <h2>Candidate Profile</h2>
      {error && <p role="alert">{error}</p>}

      <div className="profile-grid">
        <section className="card profile-card" aria-label="Contact details">
          <dl>
            <dt>Name</dt>
            <dd>{candidate.name}</dd>
            <dt>Email</dt>
            <dd>{candidate.email}</dd>
            <dt>Phone</dt>
            <dd>{candidate.phone ?? "—"}</dd>
            <dt>Role</dt>
            <dd>{candidate.roleAppliedFor}</dd>
            <dt>Source</dt>
            <dd>{candidate.source ?? "—"}</dd>
          </dl>
        </section>

        <section className="card stage-card" aria-label="Stage and applied date">
          <div>
            <label>
              Stage
              <select
                value={candidate.stage}
                onChange={(e) => handleStageChange(e.target.value as Stage)}
              >
                {STAGES.map((s) => (
                  <option key={s} value={s}>
                    {s}
                  </option>
                ))}
              </select>
            </label>
          </div>
          <div className="stage-card-date">
            <span className="field-label">Applied date</span>
            <p className="date-value">
              {new Date(candidate.createdAt).toLocaleDateString(undefined, { dateStyle: "medium" })}
            </p>
          </div>
        </section>

        <section className="card resume-card" aria-labelledby="resume-heading">
          <div className="card-header">
            <h3 id="resume-heading">Resume</h3>
          </div>
          {candidate.resumeText ? (
            <pre className="resume-box" role="region" aria-label="Resume text" tabIndex={0}>{candidate.resumeText}</pre>
          ) : (
            <p>No resume text provided.</p>
          )}
        </section>

        <SummaryPanel
          key={candidate.id}
          candidateId={candidate.id}
          resumeText={candidate.resumeText}
        />

        <section className="card notes-card" aria-labelledby="notes-heading">
          <h3 id="notes-heading">Notes</h3>
          <div className="notes-layout">
            <form onSubmit={handleAddNote}>
              <label>
                Add a note
                <textarea value={noteText} onChange={(e) => setNoteText(e.target.value)} rows={3} />
              </label>
              <button type="submit">Add note</button>
            </form>

            <div className="notes-history">
              <h4 className="field-label">Note history</h4>
              {candidate.notes.length === 0 ? (
                <p>No notes yet.</p>
              ) : (
                <ul>
                  {candidate.notes.map((note, index) => (
                    <li key={index}>
                      <strong>{new Date(note.createdAt).toLocaleString()}</strong>: {note.text}
                    </li>
                  ))}
                </ul>
              )}
            </div>
          </div>
        </section>
      </div>

      <button type="button" onClick={handleDelete}>
        Delete candidate
      </button>
    </article>
  );
}