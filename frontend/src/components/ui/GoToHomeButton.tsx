import Button           from "@mui/material/Button";
import {useNavigate}    from "react-router-dom";

export default function GoToHomeButton() {
    const navigate = useNavigate();

    return <Button
        variant="contained"
        color="primary"
        onClick={() => navigate("/")}
        sx={{
            paddingX: 4,
            paddingY: 1.5,
            fontSize: "1rem",
            textTransform: "none",
        }}
    >
        Go to Home
    </Button>
}
