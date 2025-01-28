import * as React from 'react';
import IconButton from '@mui/material/IconButton';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import {useWorkspaceContext} from "../../hooks/UseWorkspaceContext.tsx";
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";

const ITEM_HEIGHT = 48;

export default function LanguageMenu() {
    const {workspace, setWorkspace} = useWorkspaceContext()
    const options = ["Python", "JavaScript"]

    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
    const open = Boolean(anchorEl);

    const handleClick = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget)
    };

    const handleChoose = (option: string) => {
        setWorkspace(workspace => {
            return {
                ...workspace,
                language: option,
            }})
    };


    const handleClose = () => {
        setAnchorEl(null)
    };

    return (
        <Box display="flex" alignItems="center" gap={1}>
            <Typography
            >
                {workspace.language}
            </Typography>
            <IconButton
                aria-label="more"
                id="long-button"
                aria-controls={open ? 'long-menu' : undefined}
                aria-expanded={open ? 'true' : undefined}
                aria-haspopup="true"
                onClick={handleClick}
                sx={{color: "primary.contrastText" }}
            >
                <KeyboardArrowDownIcon />
            </IconButton>


            <Menu
                id="long-menu"
                MenuListProps={{
                    'aria-labelledby': 'long-button',
                }}
                anchorEl={anchorEl}
                open={open}
                onClose={handleClose}
                slotProps={{
                    paper: {
                        style: {
                            maxHeight: ITEM_HEIGHT * 4.5,
                            width: '20ch',
                        },
                    },
                }}
            >
                {options.map((option) => (
                    <MenuItem key={option} onClick={() => handleChoose(option)}>
                        {option}
                    </MenuItem>
                ))}
            </Menu>
        </Box>
    );
}
