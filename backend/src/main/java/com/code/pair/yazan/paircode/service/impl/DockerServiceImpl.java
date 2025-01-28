package com.code.pair.yazan.paircode.service.impl;

import com.code.pair.yazan.paircode.codeExecution.client.DockerClient;
import com.code.pair.yazan.paircode.codeExecution.configClasses.directive.ContainerConfigs;
import com.code.pair.yazan.paircode.codeExecution.configClasses.directive.ContainerConfigsJavaScriptCode;
import com.code.pair.yazan.paircode.codeExecution.configClasses.directive.ContainerConfigsPythonCode;
import com.code.pair.yazan.paircode.domain.ActiveFile;
import com.code.pair.yazan.paircode.service.DockerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DockerServiceImpl implements DockerService {
    private DockerClient dockerClient;

    @Override
    public String runPythonCode(List<ActiveFile> files, String inputFile, String mainFile) {
        return runCode(files, inputFile, mainFile, new ContainerConfigsPythonCode());
    }

    @Override
    public String runJSCode(List<ActiveFile> files, String inputFile, String mainFile) {
        return runCode(files, inputFile, mainFile, new ContainerConfigsJavaScriptCode());
    }

    private String runCode(List<ActiveFile> files, String inputFile, String mainFile, ContainerConfigs configs) {
        String containerId = dockerClient.createContainer(configs.creationConfig().build());
        dockerClient.startContainer(configs.startupConfig(containerId).build());

        files.forEach(file -> {
            String escapedSourceCode = file.getSourceCode().replaceAll("\"", "\\\\\"");
            file.setSourceCode(escapedSourceCode);

            String execId = dockerClient.createExec(
                    configs.execCreationConfigCreateFile(containerId, file).build());

            dockerClient.startExec(
                    configs.execStartupConfig(execId).build());
        });

        String execId = dockerClient.createExec(
                configs.execCreationConfigRunCode(containerId, mainFile, inputFile).build());
        String res = dockerClient.startExec(configs.execStartupConfig(execId).build());
        dockerClient.killContainer(configs.killingConfig(containerId).build());

        return res;
    }
}
