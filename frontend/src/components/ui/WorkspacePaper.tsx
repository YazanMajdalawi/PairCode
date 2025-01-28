import {Paper} from "@mui/material";
import Typography from "@mui/material/Typography";
import DeleteIcon from '@mui/icons-material/Delete';
import ShareIcon from '@mui/icons-material/Share';
import Box from "@mui/material/Box";
import IconButton from "@mui/material/IconButton";
import {deleteProject} from "../../services/api/projectApi.ts";
import { toast } from "react-toastify";

import {useNavigate} from "react-router-dom";

export default function WorkspacePaper({ projectId, name } : {projectId: string, name: string}) {
    const deleteWorkspace = () => {
        deleteProject(projectId)
            .then(() => {window.location.reload()})
            .catch((error: Error) => console.log(error));
    };

    const handleShare = () => {
        navigator.clipboard.writeText(`http://localhost:5173/editor/${projectId}`).then(() =>
            toast.info(`Link to "${name}" project copied!`, {
                position: "bottom-center",
                autoClose: 1200,
                hideProgressBar: true,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: true,
                progress: undefined,
                theme: "colored",
            })
        )
    }

    const navigate = useNavigate();

    const handleRowClick = () => {
        navigate(`/editor/${projectId}`);
    };

    return (
        <Paper
            variant="outlined"
            sx={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                mt: 1.5,
                px: 3,
                width: "95%",
                bgcolor: "primary.light",
                cursor: "pointer",
            }}
            onClick={handleRowClick}
        >
            <Typography
                sx={{
                    ml:2,
                    fontSize: "1.5rem",
                    fontFamily: "bold",
                    textDecoration: "none",
                    color: "primary.contrastText",
                }}
            >
                {name}
            </Typography>

            <Box>
                <IconButton
                    onClick={(e) => {e.stopPropagation(); handleShare()}}
                >
                    <ShareIcon
                        sx={{ m: 0.7, color: "primary.contrastText" }}
                    />
                </IconButton>
                <IconButton
                    onClick={(e) => {
                        e.stopPropagation();
                        deleteWorkspace();
                    }}
                >
                    <DeleteIcon
                        sx={{ m: 0.7, color: "primary.contrastText" }}
                    />
                </IconButton>
            </Box>
        </Paper>
    );
}
