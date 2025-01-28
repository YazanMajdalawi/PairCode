import Editor from "@monaco-editor/react";
import { useWorkspaceContext } from "../hooks/UseWorkspaceContext.tsx";
import { ReplacementModification } from "../types/types.ts";
import { IMessage, useStompClient, useSubscription } from "react-stomp-hooks";
import React, { useEffect, useRef, useState } from "react";
import "./styles.css";

export default function CodeEditor() {
    const stompClient = useStompClient();
    const { workspace } = useWorkspaceContext();
    const editorRef = useRef<any>(null);

    const lastSyncedVersionRef = useRef<number>(0);
    const [hasLoaded, setHasLoaded] = useState(false);

    const dontSendRef = useRef<boolean>(false)

    const encodedFilePath = encodeURIComponent(workspace.activeFile.filePath);
    const encodedProjectId = encodeURIComponent(workspace.project.id);
    const username = sessionStorage.getItem("username") || "";
    const encodedUsername = encodeURIComponent(username);

    function handleEditorDidMount(editor, monaco) {
        setHasLoaded(true);
        editorRef.current = editor;
    }

    function sendModifications(modifications: ReplacementModification[]) {
        if (dontSendRef.current) {
            dontSendRef.current = false;
            return;
        }

        if (!stompClient) {
            return;
        }

        stompClient.publish({
            destination: `/app/modify/${encodedProjectId}/${encodedFilePath}`,
            body: JSON.stringify(modifications),
        });
    }

    const applyModification = (mod: ReplacementModification) => {
        lastSyncedVersionRef.current = mod.fileVersion + 1;

        const editor = editorRef.current;
        if (!editor) return;

        const model = editor.getModel();
        if (!model) return;

        if (mod.modifier !== username) {
            const editRange = new monaco.Range(
                model.getPositionAt(mod.start).lineNumber,
                model.getPositionAt(mod.start).column,
                model.getPositionAt(mod.end).lineNumber,
                model.getPositionAt(mod.end).column
            );

            const edits = [
                {
                    range: editRange,
                    text: mod.newVal,
                    forceMoveMarkers: false,
                },
            ];

            dontSendRef.current = true;
            model.pushEditOperations([], edits, () => null);
        }

        const newRange = new monaco.Range(
            model.getPositionAt(mod.start).lineNumber,
            model.getPositionAt(mod.start).column,
            model.getPositionAt(mod.end).lineNumber,
            model.getPositionAt(mod.end).column
        );

        const overlappingDecorations = model.getDecorationsInRange(newRange) || [];
        const overlappingDecorationIds = overlappingDecorations.map((d) => d.id);

        if (overlappingDecorationIds.length > 0) {
            editor.deltaDecorations(overlappingDecorationIds, []);
        }

        const decorations = [
            {
                range: newRange,
                options: {
                    hoverMessage: { value: `Modified By: **${mod.modifier}**` },
                },
            },
        ];
        const decorationIds = editor.deltaDecorations([], decorations);

        if (mod.modifier !== username) {
            const highlightRange = new monaco.Range(
                model.getPositionAt(mod.start).lineNumber,
                model.getPositionAt(mod.start).column,
                model.getPositionAt(mod.start + mod.newVal.length).lineNumber,
                model.getPositionAt(mod.start + mod.newVal.length).column
            );

            const highlightDecorations = [
                {
                    range: highlightRange,
                    options: {
                        inlineClassName: `${mod.modifier}-marker`,
                    },
                },
            ];

            const highlightDecorationIds = editor.deltaDecorations(
                [],
                highlightDecorations
            );

            setTimeout(() => {
                editor.deltaDecorations(highlightDecorationIds, []);
            }, 250);
        }
    };

    useSubscription(
        `/topic/read/${encodedProjectId}/${encodedFilePath}/${encodedUsername}`,
        (message: IMessage) => {
            const mod: ReplacementModification = JSON.parse(message.body);
            if (mod.newVal === "") return;
            mod.modifier = "Initial File";
            applyModification(mod);
        }
    );

    useSubscription(
        `/topic/modify/${encodedProjectId}/${encodedFilePath}`,
        (message: IMessage) => {
            const mod: ReplacementModification[] = JSON.parse(message.body);
            for (let i = 0; i < mod.length; ++i) {
                applyModification(mod[i]);
            }
        }
    );

    function handleEditorChange(value, event) {
        const modifications: ReplacementModification[] = event.changes.map((c) => ({
            projectId: workspace.project.id,
            filePath: workspace.activeFile.filePath,
            start: c.rangeOffset,
            end: c.rangeOffset + c.rangeLength,
            newVal: c.text,
            fileVersion: lastSyncedVersionRef.current,
            modifier: username,
        }));

        console.log(modifications)

        sendModifications(modifications)
    }

    useEffect(() => {
        if (hasLoaded && stompClient && editorRef.current) {
            const editor = editorRef.current;
            if (editor.getValue() !== "") {
                dontSendRef.current = true
                editor.setValue("");
            }
            stompClient.publish({
                destination: `/app/read/${encodedProjectId}/${encodedFilePath}/${encodedUsername}`,
            });
        }
    }, [hasLoaded, stompClient, encodedProjectId, encodedFilePath, encodedUsername]);

    return (
        <Editor
            height="82vh"
            defaultLanguage="python"
            language={workspace.language.toLowerCase()}
            theme="vs-dark"
            onChange={handleEditorChange}
            onMount={handleEditorDidMount}
            options={{
                hover: { enabled: true },
            }}
        />
    );
}