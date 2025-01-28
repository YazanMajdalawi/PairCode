import React, { useState }      from "react";
import axios                    from "axios";
import { useNavigate }          from "react-router-dom";

import Box                      from "@mui/material/Box";
import { Stack }                from "@mui/material";
import Typography               from "@mui/material/Typography";
import TextField                from "@mui/material/TextField";
import Button                   from "@mui/material/Button";
import { User }                 from "../types/types.ts"

export default function LoginPage() {
    const [user, setUser] = useState<User>({ username: "", password: "", });
    const [error, setError] = useState<boolean>(false);
    const navigate = useNavigate();

    function handleChange(event: React.ChangeEvent<HTMLInputElement>){
        setUser({...user, [event.target.name] : event.target.value });
    }

    function handleLogin(){
        axios.post(
            `${import.meta.env.VITE_API_URL}/login`,
            user,
            {
                headers: { 'Content-Type': 'application/json' }
            }
        ).then(res => {
            const jwtToken = res.headers.authorization;
            if (jwtToken !== null) {
                sessionStorage.setItem("username", user.username);
                sessionStorage.setItem("jwt", jwtToken);
                navigate('/')
            }
        }).catch(err => {console.error(err); setError(true)});
    }

    return (
        <Box sx={{ml: 0, mr: 0, textAlign: "center"}}>
            <Box sx={{mt: 5}}>
                <img src={"/icons/android-chrome-512x512.png"} alt={"LOGO"} width={"100px"}/>
            </Box>
            <Typography variant={"h3"} sx={{m: 5, mt: 1}}>
                PairCode
            </Typography>

            <Box className="login-form">
                <Stack spacing={2} alignItems="center" mt={2}>
                    <Typography variant="h5">Login</Typography>
                    <TextField
                        name="username"
                        label="Username"
                        error={error}
                        onChange={handleChange} />
                    <TextField
                        type="password"
                        name="password"
                        label="Password"
                        error={error}
                        onChange={handleChange}/>

                    {error && "Incorrect username or password."}

                    <Button
                        sx={{minWidth: 245}}
                        variant="contained"
                        color="primary"
                        onClick={handleLogin}>
                        Login
                    </Button>

                    <Typography>
                        Don't have an account yet?
                        <Button
                            variant={"text"}
                            color={"primary"}
                            onClick={() => navigate("/register")}
                            sx={{textTransform: "none"}}>
                            Create one
                        </Button>
                    </Typography>
                </Stack>
            </Box>
        </Box>
    )
}
