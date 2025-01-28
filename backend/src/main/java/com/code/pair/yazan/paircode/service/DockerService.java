package com.code.pair.yazan.paircode.service;

import com.code.pair.yazan.paircode.domain.ActiveFile;

import java.util.List;

public interface DockerService {
    String runPythonCode(List<ActiveFile> files, String inputFile, String mainFile);
    String runJSCode(List<ActiveFile> files, String inputFile, String mainFile);
}
