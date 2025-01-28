import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import SaveIcon from '@mui/icons-material/Save';
import CircularProgress from '@mui/material/CircularProgress';
import LanguageMenu from "./LanguageMenu.tsx";
import {useWorkspaceContext} from "../../hooks/UseWorkspaceContext.tsx";
import {runProject} from "../../services/api/projectApi.ts";
import {useStompClient, useSubscription} from "react-stomp-hooks";
import {useState} from 'react';
import {toast} from "react-toastify";

export default function RunResponsiveAppBar() {
    const {workspace, setWorkspace} = useWorkspaceContext()
    const stompClient = useStompClient();
    const [isLoading, setIsLoading] = useState(false);
    const [isSaving, setIsSaving] = useState(false);

    const run = () => {
        setIsLoading(true);
        const projectId = workspace.project.id??""
        const input = workspace.input
        const mainFilePath = workspace.activeFile.filePath
        const language = workspace.language.toLowerCase()

        runProject(projectId, input, mainFilePath, language)
            .then(out => setWorkspace({...workspace, output: out}))
            .catch(err => console.error("Error running project:", err))
            .finally(() => setIsLoading(false));
    }


    const encodedProjectId = encodeURIComponent(workspace.project.id);
    const username = sessionStorage.getItem("username") || "";
    const encodedUsername = encodeURIComponent(username);
    useSubscription(`/topic/save/${encodedProjectId}/${encodedUsername}`, msg => setIsSaving(false));

    const save = () => {
        const projectId = workspace.project.id
        if (stompClient) {
            setIsSaving(true);
            stompClient.publish({
                destination: `/app/save/${projectId}`
            })
        } else {
            console.log("Disconnected To Server")
        }
    }

    return (
        <Box sx={{ flexGrow: 1 }}>
            <AppBar
                position="static"
            >
                <Toolbar
                    sx={{ bgcolor: "primary.dark" }}
                    variant="dense"
                >
                    <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                        {workspace.project.name} : {workspace.activeFile.parentPath}/{workspace.activeFile.fileName}
                    </Typography>

                    <LanguageMenu />
                    <IconButton
                        size="small"
                        edge="end"
                        aria-label="run"
                        sx={{ mx: 2, my: 0.5 }}
                        onClick={save}
                        disabled={isSaving}
                    >
                        { isSaving ? (
                                <CircularProgress
                                    size={24}
                                    sx={{color: "primary.contrastText"}}
                                />
                            ) : (
                                <SaveIcon
                                    sx={{color: "primary.contrastText"}}
                                />
                            )
                        }
                    </IconButton>

                    <IconButton
                        size="small"
                        edge="end"
                        aria-label="run"
                        sx={{ mx: 2, my: 0.5 }}
                        onClick={run}
                        disabled={isLoading}
                    >
                        {isLoading ? (
                            <CircularProgress
                                size={24}
                                sx={{color: "primary.contrastText"}}
                            />
                        ) : (
                            <PlayArrowIcon
                                sx={{color: "primary.contrastText"}}
                            />
                        )}
                    </IconButton>
                </Toolbar>
            </AppBar>
        </Box>
    );
}
