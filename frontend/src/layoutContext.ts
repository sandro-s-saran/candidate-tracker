import { useOutletContext } from "react-router";

export interface LayoutContext {
  refresh: () => void;
}

export function useLayoutContext() {
  return useOutletContext<LayoutContext>();
}