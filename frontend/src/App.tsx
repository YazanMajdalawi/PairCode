import { Container, CssBaseline             } from '@mui/material'
import { BrowserRouter, Route, Routes       } from "react-router-dom";
import { QueryClient, QueryClientProvider   } from "@tanstack/react-query";
import { ToastContainer                     } from 'react-toastify';
import { ThemeProvider                      } from "@mui/material";
import   defaultTheme                         from './themes/DefaultTheme'

import   EditorPageDataFetcher                from "./pages/EditorPageDataFetcher.tsx";
import   LoginPage                            from "./pages/LoginPage.tsx";
import   HomePage                             from "./pages/HomePage.tsx";
import   PageNotFound                         from "./pages/PageNotFound.tsx";
import   RegisterPage                         from "./pages/RegisterPage.tsx";

const queryClient = new QueryClient();

export default function App() {
    return (
        <QueryClientProvider client={queryClient}>
            <ThemeProvider theme={defaultTheme}>
                <Container maxWidth={false} disableGutters>
                    <CssBaseline />
                    <ToastContainer />
                    <BrowserRouter>
                        <Routes>
                            <Route path="/login"                element={<LoginPage/>} />
                            <Route path="/register"             element={<RegisterPage />} />
                            <Route path="/home"                 element={<HomePage/>} />
                            <Route path="/"                     element={<HomePage/>} />
                            <Route path="/editor/:projectId"    element={<EditorPageDataFetcher/>} />
                            <Route path="*"                     element={<PageNotFound/>} />
                        </Routes>
                    </BrowserRouter>
                </Container>
                </ThemeProvider>
            </QueryClientProvider>
    );
}