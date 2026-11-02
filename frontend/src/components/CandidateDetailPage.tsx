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
      <h2>{candidate.name}</h2>
      {error && <p role="alert">{error}</p>}

      <div className="detail-grid">
        <div>
          <dl>
            <dt>Email</dt>
            <dd>{candidate.email}</dd>
            <dt>Phone</dt>
            <dd>{candidate.phone ?? "—"}</dd>
            <dt>Role</dt>
            <dd>{candidate.roleAppliedFor}</dd>
            <dt>Source</dt>
            <dd>{candidate.source ?? "—"}</dd>
            <dt>Added</dt>
            <dd>{new Date(candidate.createdAt).toLocaleString()}</dd>
          </dl>

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

          <h3>Resume or cover letter</h3>
          {candidate.resumeText ? (
            <pre className="resume-box" role="region" aria-label="Resume text" tabIndex={0}>{candidate.resumeText}</pre>
          ) : (
            <p>No resume text provided.</p>
          )}

          <h3>Notes</h3>
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
          <form onSubmit={handleAddNote}>
            <label>
              Add a note
              <textarea value={noteText} onChange={(e) => setNoteText(e.target.value)} rows={3} />
            </label>
            <button type="submit">Add note</button>
          </form>

          <button type="button" onClick={handleDelete}>
            Delete candidate
          </button>
        </div>

        <SummaryPanel
          key={candidate.id}
          candidateId={candidate.id}
          resumeText={candidate.resumeText}
        />
      </div>
    </article>
  );
}