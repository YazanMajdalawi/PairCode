import { Project, Workspace }   from "../types/types.ts";
import { useQuery           }   from "@tanstack/react-query";
import { getProject         }   from "../services/api/projectApi.ts";
import { useNavigate, useParams          }   from "react-router-dom";
import   GoToHomeButton         from "../components/ui/GoToHomeButton.tsx";
import   EditorPage             from "./EditorPage.tsx";
import { useEffect          }   from "react";
import   Typography             from "@mui/material/Typography";
import   Box                    from "@mui/material/Box";
import { StompSessionProvider } from "react-stomp-hooks";

export default function EditorPageDataFetcher() {
    let { projectId } = useParams();
    const navigate = useNavigate();

    useEffect(() => {
        const token = sessionStorage.getItem("jwt");
        if (!token) {
            navigate("/login");
        }
    }, [navigate]);

    projectId = projectId ? projectId : "";

    const {data, error, isSuccess} = useQuery<Project, Error>({
        queryKey: [`project@${projectId}`],
        queryFn: () => getProject(projectId)
    })


    if (error) {
        return <Box sx={{textAlign: "center"}}>
            <Typography
                variant={"h3"}
                sx={{color: "error.main", m: 5, tf: "bold"}}
            >
                An Error Has Occurred!!
            </Typography>
            <GoToHomeButton />
        </Box>
    }

    if (!isSuccess) {
        return <Typography
            variant={"h3"}
            sx={{color: "primary.dark", textAlign: "center", m: 5, tf: "bold"}}
        >
            Loading...
        </Typography>
    }

    if (data) {
        const workspace: Workspace = {
            "project": data,
            "activeFile": data.files[0],
            "language": "python",
            "activeUsers": [],
            "input": "",
            "output": ""
        }

        return <StompSessionProvider url={import.meta.env.VITE_STOMP_URL}
                                     connectHeaders={{
                                         "Authorization": `${sessionStorage.getItem("jwt")}`,
                                     }}>
            <EditorPage w={workspace}/>
        </StompSessionProvider>
    }
}