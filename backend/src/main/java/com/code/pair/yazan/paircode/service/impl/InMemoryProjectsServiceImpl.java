package com.code.pair.yazan.paircode.service.impl;

import com.code.pair.yazan.paircode.domain.ActiveProject;
import com.code.pair.yazan.paircode.dsa.InMemoryProject;
import com.code.pair.yazan.paircode.repository.ActiveProjectRepository;
import com.code.pair.yazan.paircode.service.InMemoryProjectsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class InMemoryProjectsServiceImpl implements InMemoryProjectsService {
    private final ActiveProjectRepository activeProjectRepository;
    private final Map<String, InMemoryProject> projects = new ConcurrentHashMap<>();

    public InMemoryProject getProject(String projectId) {
        loadProject(projectId);
        return projects.get(projectId);
    }

    public void loadProject(String projectId) {
        if (isLoaded(projectId)) {
            return;
        }

        var project = activeProjectRepository.findById(projectId);

        if (project.isPresent()) {
            var crrProject = project.get();
            projects.put(crrProject.getId(), new InMemoryProject(crrProject));
        }
    }

    @Override
    public ActiveProject saveProject(String projectId) {
        if (isLoaded(projectId)) {
            return activeProjectRepository.save(projects.get(projectId).getActiveProject());
        }

        return null;
    }

    public void closeProject(String projectId) {
        if (!isLoaded(projectId)) {
            return;
        }

        saveProject(projectId);
        projects.remove(projectId);
    }

    public boolean isLoaded(String projectId) {
        return projects.containsKey(projectId);
    }
}
