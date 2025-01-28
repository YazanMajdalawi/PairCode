import {createContext, useContext} from "react";
import {WorkspaceState} from "../types/types.ts";

export const UseWorkspaceContext
    = createContext<WorkspaceState | undefined>(undefined);

export function useWorkspaceContext()  {
    const workspaceState = useContext(UseWorkspaceContext);

    if (workspaceState == undefined) {
        throw new Error("useWorkspaceContext must be used within UseWorkspaceContext");
    }

    return workspaceState
}
