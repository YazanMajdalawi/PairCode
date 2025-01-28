package com.code.pair.yazan.paircode.codeExecution.configClasses;

import lombok.Builder;

@Builder
public record ContainerRemovalConfig(
        String containerId,
        boolean force,
        boolean deleteVolumes) {
}
