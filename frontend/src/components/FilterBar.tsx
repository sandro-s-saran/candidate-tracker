import { STAGES } from "../types";
import type { Stage } from "../types";
import type { CandidateFilters, SortField } from "../api";

interface Props {
  filters: CandidateFilters;
  onChange: (filters: CandidateFilters) => void;
}

export default function FilterBar({ filters, onChange }: Props) {
  return (
    <section aria-label="Filters">
      <label>
        Filter by Stage
        <select
          value={filters.stage ?? ""}
          onChange={(e) =>
            onChange({ ...filters, stage: (e.target.value || undefined) as Stage | undefined })
          }
        >
          <option value="">All stages</option>
          {STAGES.map((s) => (
            <option key={s} value={s}>
              {s}
            </option>
          ))}
        </select>
      </label>

      <label>
        Role
        <input
          value={filters.role ?? ""}
          onChange={(e) => onChange({ ...filters, role: e.target.value || undefined })}
          placeholder="e.g. Software Engineer"
        />
      </label>

      <label>
        Sort by
        <select
          value={filters.sort ?? "DATE_ADDED"}
          onChange={(e) => onChange({ ...filters, sort: e.target.value as SortField })}
        >
          <option value="DATE_ADDED">Date added (newest first)</option>
          <option value="STAGE">Stage</option>
        </select>
      </label>
    </section>
  );
}