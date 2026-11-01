import { useState } from "react";
import type { ChangeEvent, FormEvent } from "react";
import { createCandidate } from "../api";
import type { Candidate } from "../types";

interface Props {
  onCreated: (candidate: Candidate) => void;
}

export default function CandidateForm({ onCreated }: Props) {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [roleAppliedFor, setRoleAppliedFor] = useState("");
  const [phone, setPhone] = useState("");
  const [source, setSource] = useState("");
  const [resumeText, setResumeText] = useState("");
  const [error, setError] = useState<string | null>(null);

  async function handleFile(event: ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0];
    if (file) {
      setResumeText(await file.text());
    }
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError(null);
    try {
      const created = await createCandidate({
        name,
        email,
        roleAppliedFor,
        phone: phone || undefined,
        source: source || undefined,
        resumeText: resumeText || undefined,
      });
      onCreated(created);
      setName("");
      setEmail("");
      setRoleAppliedFor("");
      setPhone("");
      setSource("");
      setResumeText("");
    } catch (e) {
      setError((e as Error).message);
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <h2>Add a candidate</h2>
      {error && <p role="alert">{error}</p>}

      <label>
        Name
        <input value={name} onChange={(e) => setName(e.target.value)} required />
      </label>
      <label>
        Email
        <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
      </label>
      <label>
        Role applied for
        <input value={roleAppliedFor} onChange={(e) => setRoleAppliedFor(e.target.value)} required />
      </label>
      <label>
        Phone (optional)
        <input value={phone} onChange={(e) => setPhone(e.target.value)} />
      </label>
      <label>
        Source (optional)
        <input value={source} onChange={(e) => setSource(e.target.value)} />
      </label>
      <label>
        Resume or cover letter
        <textarea value={resumeText} onChange={(e) => setResumeText(e.target.value)} rows={6} />
      </label>
      <label>
        Or load from a text file
        <input type="file" accept=".txt,text/plain" onChange={handleFile} />
      </label>

      <button type="submit">Add candidate</button>
    </form>
  );
}