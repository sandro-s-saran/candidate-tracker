import { useNavigate } from "react-router";
import CandidateForm from "./CandidateForm";
import { useLayoutContext } from "../layoutContext";

export default function NewCandidatePage() {
  const navigate = useNavigate();
  const { refresh } = useLayoutContext();

  return (
    <CandidateForm
      onCreated={(created) => {
        refresh();
        navigate(`/candidates/${created.id}`);
      }}
    />
  );
}