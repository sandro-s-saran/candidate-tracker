import { Route, Routes } from "react-router";
import CandidateDetailPage from "./components/CandidateDetailPage";
import Layout from "./Layout";
import NewCandidatePage from "./components/NewCandidatePage";

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<p>Select a candidate, or add a new one.</p>} />
        <Route path="candidates/new" element={<NewCandidatePage />} />
        <Route path="candidates/:id" element={<CandidateDetailPage />} />
        <Route path="*" element={<p>Page not found.</p>} />
      </Route>
    </Routes>
  );
}