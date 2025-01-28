import * as React from 'react';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import {useState} from "react";
import {createProject} from "../../services/api/projectApi.ts";
import {File} from "../../types/types.ts";

const defaultFiles : File[] = [
    { parentPath: ".",    fileName: "main", filePath: "./main",    sourceCode: "" },
    { parentPath: "utils", fileName: "util.py", filePath: "utils/util.py", sourceCode: "" },
    { parentPath: "utils", fileName: "util.js", filePath: "utils/util.js", sourceCode: "" },
    { parentPath: "lib",  fileName: "lib.py", filePath: "lib/lib.py",  sourceCode: "" },
    { parentPath: "lib",  fileName: "lib.js", filePath: "lib/lib.js",  sourceCode: "" }
];

export default function FormDialog({ open, handleClose } : {open: boolean, handleClose: () => void}) {
    const [projectName, setProjectName] = useState<string>();

    return (
            <Dialog
                open={open}
                onClose={handleClose}
                PaperProps={{
                    component: 'form',
                    onSubmit: (event: React.FormEvent<HTMLFormElement>) => {
                        event.preventDefault();

                        createProject({
                            name: projectName ?? "untitled",
                            files: defaultFiles,
                        }).catch(err => console.log(err))

                        handleClose();
                    },
                }}
            >
                <DialogTitle>New Workspace</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Enter your new workspace name
                    </DialogContentText>
                    <TextField
                        autoFocus
                        required
                        margin="dense"
                        id="name"
                        name="name"
                        label="Workspace Name"
                        type="text"
                        fullWidth
                        variant="standard"
                        value={projectName}
                        onChange={(e) => setProjectName(e.target.value)}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose}>Cancel</Button>
                    <Button type="submit">Submit</Button>
                </DialogActions>
            </Dialog>
    );
}
