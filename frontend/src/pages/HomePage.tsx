import {useEffect,  useState }             from "react";

import Workspaces               from "../components/Workspaces.tsx";
import FormDialog               from "../components/mui/CreateWorkspaceDialog.tsx";
import MainResponsiveAppBar     from "../components/mui/MainResponsiveAppBar.tsx";

import { Container, Divider }   from "@mui/material";
import Box                      from "@mui/material/Box";
import Typography               from "@mui/material/Typography";
import Button                   from "@mui/material/Button";
import { useNavigate } from "react-router-dom";


export default function HomePage() {
    const [open, setOpen] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const token = sessionStorage.getItem("jwt");
        if (!token) {
            navigate("/login");
        }
    }, [navigate]);

    const handleClose = () => {
        setOpen(false);
        // TODO: Make this smoother -> also where deletion happen
        window.location.reload();
    }

    return (
        <>
            <MainResponsiveAppBar />
            <Container sx={{ display: "flex", flexDirection: "column" }}>
                <Box sx={{
                    m:2,
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "flex-end",
                    flexWrap: "wrap"
                }}>
                    <Typography variant={"h5"}>Your Workspaces</Typography>
                    <Button onClick={()=>setOpen(true)} variant="contained">Add a Workspace</Button>
                    {open && <FormDialog open={open} handleClose={handleClose}/>}
                </Box>
                <Divider orientation="horizontal" variant="middle" />
                <Workspaces />
            </Container>
        </>
    )
}


