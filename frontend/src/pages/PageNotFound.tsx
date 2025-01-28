import { Box, Typography } from "@mui/material";
import GoToHomeButton from "../components/ui/GoToHomeButton.tsx";

export default function PageNotFound() {
    return (
        <Box
            sx={{
                textAlign: "center",
                height: "100vh",
                display: "flex",
                flexDirection: "column",
                justifyContent: "center",
                alignItems: "center",
                bgc: "background.default",
                color: "primary.dark",
                padding: 4,
            }}
        >
            <Typography variant="h1" sx={{ fontSize: "6rem", fontWeight: "bold", color: "error.main" }}>
                404
            </Typography>

            <Typography variant="h5" sx={{ mt: 2 }}>
                The page you're looking for doesn't exist.
            </Typography>

            <Typography variant="body1" sx={{ mt: 1, mb: 4 }}>
                It seems you’ve hit a broken link or entered an invalid URL.
            </Typography>

            <GoToHomeButton />
        </Box>
    );
}
