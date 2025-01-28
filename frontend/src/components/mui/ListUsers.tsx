import Box from "@mui/material/Box";
import {List, ListItem, ListItemText} from "@mui/material";
import {IMessage, useStompClient, useSubscription} from "react-stomp-hooks";
import {useEffect, useState} from "react";
import {useWorkspaceContext} from "../../hooks/UseWorkspaceContext.tsx";
import Typography from "@mui/material/Typography";
import {nameToColor} from "../NameToColor.ts"

function getContrastTextColor(hexColor) {
    const r = parseInt(hexColor.substring(0, 2), 16);
    const g = parseInt(hexColor.substring(2, 4), 16);
    const b = parseInt(hexColor.substring(4, 6), 16);
    const luminance = 0.2126 * r + 0.7152 * g + 0.0722 * b;
    return luminance > 128 ? '#000000' : '#FFFFFF'; // Black for light backgrounds, white for dark backgrounds
}

export default function ListUsers() {
    const stompClient = useStompClient();

    const {workspace, setWorkspace} = useWorkspaceContext()

    const encodedFilePath = encodeURIComponent(workspace.activeFile.filePath);
    const encodedProjectId = encodeURIComponent(workspace.project.id);
    const encodedUsername = encodeURIComponent(sessionStorage.getItem("username"));

    useSubscription(`/topic/users/${encodedProjectId}/${encodedFilePath}/${encodedUsername}`, (message: IMessage) => {
        setWorkspace({...workspace, activeUsers: JSON.parse(message.body)})
    })


    useEffect(() => {
        if (stompClient) {
            stompClient.publish({
                destination: `/app/users/${encodedProjectId}/${encodedFilePath}/${encodedUsername}`,
            })
        }

    }, [workspace, stompClient, encodedProjectId, encodedFilePath, encodedUsername]);

    useEffect(() => {
        const style = document.createElement('style');
        style.textContent = '';
        workspace.activeUsers.forEach((user) => {
            const bcol = nameToColor(user)
            const tcol = getContrastTextColor(bcol);
            style.textContent += `
            .${user}-marker {
                color: ${tcol};
                background-color: #${bcol};
                transition: background-color 0.5s ease-out;
            }`
        })
        document.head.appendChild(style);
        return () => { document.head.removeChild(style) }
    }, [workspace.activeUsers]);

    return (
        <Box sx={{width: '100%', maxWidth: 360, bgcolor: 'background.paper'}}>
            <Typography
                variant="h6"
                component="div"
                sx={{
                    bgcolor: "primary.main",
                    fontSize: 16,
                    color: "white",
                    textTransform: "capitalize",
                    textAlign: "center",
                    padding: 1
                }}
            >
                Active Users
            </Typography>
            <List
                sx={{
                    width: '100%',
                    maxWidth: 360,
                    bgcolor: 'background.paper',
                }}
            >
                {
                    workspace.activeUsers.map((u, index) => {
                            const col = nameToColor(u);
                            return <ListItem
                                key={index}
                                sx={{
                                    '&:hover': {backgroundColor: 'rgba(0, 0, 0, 0.1)'},
                                    padding: '8px 16px',
                                }}
                            >
                                <ListItemText
                                    primary={u}
                                    sx={{
                                        color: `#${col}`,
                                        fontWeight: 500,
                                    }}
                                />
                            </ListItem>
                        }
                    )
                }
            </List>
        </Box>
    )
}