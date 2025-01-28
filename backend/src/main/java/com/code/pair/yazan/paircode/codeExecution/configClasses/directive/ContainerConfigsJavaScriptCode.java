package com.code.pair.yazan.paircode.codeExecution.configClasses.directive;

import com.code.pair.yazan.paircode.codeExecution.configClasses.*;
import com.code.pair.yazan.paircode.domain.ActiveFile;

import java.util.List;

public class ContainerConfigsJavaScriptCode implements ContainerConfigs{
    private final String timeToSleep = "10";

    public ContainerCreationConfig.ContainerCreationConfigBuilder creationConfig() {
        return ContainerCreationConfig.builder()
                .imageName("node")
                .imageTag("23-slim")
                .autoRemove(true)
                .workingDir("/home/node/app")
                .command(List.of("sleep", timeToSleep));
    }

    @Override
    public ContainerStartupConfig.ContainerStartupConfigBuilder startupConfig(String containerId) {
        return ContainerStartupConfig.builder()
                .containerId(containerId);
    }

    @Override
    public ContainerExecCreationConfig.ContainerExecCreationConfigBuilder execCreationConfigCreateFile(
            String containerId, ActiveFile file) {

        String parentDirs = file.getParentPath() == null ? "." : file.getParentPath();

        return execCreationConfig(containerId,
                List.of("bash", "-c",
                        String.format("mkdir -p %s && echo -e \"%s\" > %s",
                                parentDirs, file.getSourceCode(), file.getFilePath())
                )
        );
    }

    @Override
    public ContainerExecCreationConfig.ContainerExecCreationConfigBuilder execCreationConfigRunCode(
            String containerId, String mainFile, String inputFile) {

        return execCreationConfig(containerId,
                List.of("bash", "-c", String.format("node %s < %s", mainFile, inputFile))
        );
    }

    @Override
    public ContainerExecStartupConfig.ContainerExecStartupConfigBuilder execStartupConfig(String execId) {
        return ContainerExecStartupConfig.builder()
                .execId(execId);
    }

    @Override
    public ContainerKillingConfig.ContainerKillingConfigBuilder killingConfig(String containerId) {
        return ContainerKillingConfig.builder()
                .containerId(containerId)
                .signal(ContainerKillingConfig.SIGNAL.SIGKILL);
    }

    @Override
    public ContainerRemovalConfig.ContainerRemovalConfigBuilder removalConfig(String containerId) {
        return ContainerRemovalConfig.builder()
                .containerId(containerId)
                .deleteVolumes(true)
                .force(true);
    }

    private ContainerExecCreationConfig.ContainerExecCreationConfigBuilder execCreationConfig(String containerId, List<String> command) {
        return ContainerExecCreationConfig.builder()
                .containerId(containerId)
                .attachStdout(true)
                .attachStderr(true)
                .command(command);
    }
}
