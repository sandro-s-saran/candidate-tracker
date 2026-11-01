import { useEffect, useState } from "react";
import { generateSummary } from "../api";
import type { SummaryResult } from "../types";

interface Props {
  candidateId: number;
  resumeText: string | null;
}

type State =
  | { kind: "loading" }
  | { kind: "done"; result: SummaryResult }
  | { kind: "error"; message: string };

export default function SummaryPanel({ candidateId, resumeText }: Props) {
  const hasResume = Boolean(resumeText && resumeText.trim());
  const [state, setState] = useState<State>({ kind: "loading" });
  const [attempt, setAttempt] = useState(0);

  useEffect(() => {
    if (!hasResume) return;
    let ignore = false;
    generateSummary(candidateId)
      .then((result) => {
        if (!ignore) setState({ kind: "done", result });
      })
      .catch((e: Error) => {
        if (!ignore) setState({ kind: "error", message: e.message });
      });
    return () => {
      ignore = true;
    };
  }, [candidateId, hasResume, attempt]);

  function regenerate() {
    setState({ kind: "loading" });
    setAttempt((a) => a + 1);
  }

  let body;
  if (!hasResume) {
    body = <p>No resume text to summarise.</p>;
  } else if (state.kind === "loading") {
    body = <p role="status">Generating summary…</p>;
  } else if (state.kind === "error") {
    body = <p role="alert">Couldn’t load the summary ({state.message}).</p>;
  } else if (state.result.status === "GENERATED") {
    body = <p style={{ whiteSpace: "pre-wrap" }}>{state.result.text}</p>;
  } else if (state.result.status === "NO_RESUME_TEXT") {
    body = <p>No resume text to summarise.</p>;
  } else {
    body = <p role="alert">The summary is unavailable right now. Try again in a moment.</p>;
  }

  return (
    <section className="summary-panel" aria-labelledby="summary-heading">
      <h3 id="summary-heading">AI summary</h3>
      <p>
        <small>AI-generated. Check it against the resume before relying on it.</small>
      </p>
      {body}
      {hasResume && (
        <button type="button" onClick={regenerate} disabled={state.kind === "loading"}>
          Regenerate
        </button>
      )}
    </section>
  );
}