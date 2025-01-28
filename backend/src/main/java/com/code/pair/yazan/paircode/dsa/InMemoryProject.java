package com.code.pair.yazan.paircode.dsa;

import com.code.pair.yazan.paircode.domain.ActiveProject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryProject {
    private final ActiveProject activeProject;
    private final Map<String, InMemoryFile> inMemFiles = new ConcurrentHashMap<>();

    public InMemoryProject(ActiveProject activeProject) {
        this.activeProject = activeProject;
        for (var file : activeProject.getFiles()) {
            inMemFiles.put(file.getFilePath(), new InMemoryFile(file.getSourceCode()));
        }
    }

    public List<TextModification> applyModification(List<TextModification> modification) {
        if (modification == null || modification.isEmpty()) {
            return null;
        }

        InMemoryFile file = inMemFiles.get(modification.getFirst().getFilePath());

        if (file == null) {
            return null;
        }

        file.addModification(modification);
        return file.applyModifications();
    }

    public TextModification getFileContent(String filePath) {
        InMemoryFile file = inMemFiles.get(filePath);
        return TextModification.builder()
                .start(0).end(0)
                .newVal(file.getText())
                .fileVersion(file.getVersion())
                .build();
    }

    public ActiveProject getActiveProject() {
            var tmp = inMemFiles;

            activeProject.getFiles().forEach(
                    file -> file.setSourceCode(
                            tmp.get(file.getFilePath()).getText()));

            return activeProject;
    }
}
