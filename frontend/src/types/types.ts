import React from "react";

export type User = {
    username: string;
    password: string;
}

export type File = {
    parentPath: string;
    filePath:   string;
    fileName:   string;
    sourceCode: string;
}

export type Project = {
    id:         string;
    name:       string;
    ownerId:    string;
    files:      File[];
}

export type Workspace = {
    project:    Project;
    activeFile: File;
    activeUsers: string[];
    language: string;
    input:      string;
    output:     string;
}

export type WorkspaceState = {
    workspace:      Workspace;
    setWorkspace:   React.Dispatch<React.SetStateAction<Workspace>>;
}

export type ReplacementModification = {
    projectId:      string;
    filePath:       string;
    start:          number;
    end:            number;
    newVal:         string;
    fileVersion:    number;
    modifier:       string;
}