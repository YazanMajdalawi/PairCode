import React, { useState } from "react";
import Box from "@mui/material/Box";
import { SimpleTreeView } from "@mui/x-tree-view/SimpleTreeView";
import { TreeItem } from "@mui/x-tree-view/TreeItem";
import Typography from "@mui/material/Typography";
import { useWorkspaceContext } from "../../hooks/UseWorkspaceContext.tsx";
import FormDialog from "./CreateFileDialog.tsx";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';

type TreeNode = {
  id: string;
  name: string;
  fullPath: string;
  type: "file" | "directory";
  children?: TreeNode[];
};


const normalizePath = (path: string): string => {
  // Remove leading "./" and all "/./" patterns
  return path.replace(/^\.\//, "").replace(/\/\.\//g, "/");
};

// Build tree function...
const buildTree = (files: { filePath: string }[]): TreeNode => {
  const root: TreeNode = {
    id: "root",
    name: "root",
    type: "directory",
    fullPath: "",
    children: [],
  };

  const pathMap: Record<string, TreeNode> = { "": root };

  files.forEach(({ filePath }) => {
    const normalizedPath = normalizePath(filePath);
    if (normalizedPath === "." || normalizedPath === "..") return;
    const segments = normalizedPath.split("/").filter(Boolean);

    segments.reduce((parent, segment, idx) => {
      const isFile = idx === segments.length - 1;
      const fullPath = segments.slice(0, idx + 1).join("/");
      const id = fullPath;

      if (!pathMap[id]) {
        const node: TreeNode = {
          id,
          name: segment,
          type: isFile ? "file" : "directory",
          fullPath,
          children: isFile ? undefined : [],
        };

        parent.children?.push(node);
        if (!isFile) pathMap[id] = node;
      }

      return pathMap[id];
    }, root);
  });

  return root;
};

export default function FileTree() {
  const { workspace, setWorkspace } = useWorkspaceContext();
  const fileTree = buildTree(workspace.project.files);

  const [open, setOpen] = useState(false);
  const [chosenDirPath, setChosenDirPath] = useState("");

  const handleDelete = (fullPath: string) => { // the path can be to a file or a directory
    alert(fullPath + " was deleted");
  }

  const handleChoose = (option: string) => {
    console.log("test")
    setWorkspace(workspace => {
      return {
        ...workspace,
        activeFile: workspace.project.files.find(file => file.filePath === option || file.filePath === "./"+option) || workspace.activeFile
      }})
  };

  const handleClose = () => {
    setOpen(false);
    // TODO: Make this smoother
    window.location.reload();
  }

  // Render tree items recursively
  const renderTreeItems = (node: TreeNode): React.ReactNode => {
    if (node.type === "directory") {
      return (
          <TreeItem key={node.id} itemId={node.id} label={
            <Box sx={{ display: "flex", alignItems: "center", gap: 1, "&:hover .action-icon": { opacity: 1, visibility: "visible" } }}>
              {node.name}
              <Box sx={{ flexGrow: 1 }} /> {/*spacer*/}
              <IconButton
                  size="small"
                  className="action-icon"
                  sx={{
                    opacity: 0,
                    visibility: "hidden",
                    transition: "opacity 0.2s, visibility 0.2s",
                  }}
                  onClick={() => {
                    setOpen(true);
                    setChosenDirPath(node.fullPath)
                  }}
              >
                <AddIcon fontSize="small" />
              </IconButton>
              <IconButton
                  size="small"
                  className="action-icon"
                  sx={{
                    opacity: 0,
                    visibility: "hidden",
                    transition: "opacity 0.2s, visibility 0.2s",
                  }}
                  onClick={() => handleDelete(node.fullPath)}
              >
                <DeleteIcon fontSize="small" />
              </IconButton>
            </Box>
          }
          >
            {node.children?.map((child) => renderTreeItems(child))}
          </TreeItem>
      );
    }
    return <TreeItem key={node.id} itemId={node.id} label={
      <Box sx={{ display: "flex", alignItems: "center", gap: 1, "&:hover .action-icon": { opacity: 1, visibility: "visible" } }}>
        {node.name}
        <Box sx={{ flexGrow: 1 }} /> {/*spacer*/}
        <IconButton
            size="small"
            className="action-icon"
            sx={{
              opacity: 0,
              visibility: "hidden",
              transition: "opacity 0.2s, visibility 0.2s",
            }}
            onClick={() => handleDelete(node.fullPath)}
        >
          <DeleteIcon fontSize="small" />
        </IconButton>
      </Box>
    } onClick={() => handleChoose(node.fullPath)} />;
  };

  return (
      <Box sx={{ minHeight: 352, minWidth: 250 }}>
        <SimpleTreeView>
          <Typography
              variant="h6"
              component="div"
              sx={{
                bgcolor: "primary.main",
                fontSize: 16,
                color: "white",
                textTransform: "capitalize",
                textAlign: "left",
                padding: 1,
              }}
          >
            Files
          </Typography>
          {fileTree.children?.map((node) => renderTreeItems(node))}
        </SimpleTreeView>

        {open && <FormDialog open={open} handleClose={handleClose} chosenDirPath={chosenDirPath}/>}

      </Box>
  );
}