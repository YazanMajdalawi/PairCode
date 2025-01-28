package com.code.pair.yazan.paircode.service;

import com.code.pair.yazan.paircode.domain.ActiveProject;
import com.code.pair.yazan.paircode.dsa.InMemoryProject;

public interface InMemoryProjectsService {
    InMemoryProject getProject(String projectId);
    void loadProject(String projectId);
    boolean isLoaded(String projectId);
    ActiveProject saveProject(String projectId);
    void closeProject(String projectId);
}
