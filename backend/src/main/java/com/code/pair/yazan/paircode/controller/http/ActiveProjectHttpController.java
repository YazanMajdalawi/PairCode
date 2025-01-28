package com.code.pair.yazan.paircode.controller.http;

import com.code.pair.yazan.paircode.domain.ActiveFile;
import com.code.pair.yazan.paircode.domain.ActiveProject;
import com.code.pair.yazan.paircode.repository.ActiveProjectRepository;
import com.code.pair.yazan.paircode.service.DockerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/project")
@AllArgsConstructor
public class ActiveProjectHttpController {
    private final ActiveProjectRepository activeProjectRepository;
    private final DockerService dockerService;

    @GetMapping("/all")
    public List<ActiveProject> getAll(Principal principal) {
        return activeProjectRepository.findAllByOwnerId(principal.getName());
    }

    @GetMapping("/{id}")
    public ActiveProject getById(@PathVariable String id, Principal principal) {
        var optProj = activeProjectRepository.findById(id);
        return optProj.orElse(null);
    }

    @PostMapping
    public ActiveProject create(@RequestBody ActiveProject activeProject, Principal principal) {
        activeProject.setOwnerId(principal.getName());
        return activeProjectRepository.save(activeProject);
    }

    @PutMapping
    public ActiveProject update(@RequestBody ActiveProject activeProject, Principal principal) {
        if (getById(activeProject.getId(), principal) != null) {
            return activeProjectRepository.save(activeProject);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id, Principal principal) {
        var optProj = activeProjectRepository.findById(id);
        if (optProj.isPresent()) {
            var proj = optProj.get();
            if (proj.getOwnerId().equals(principal.getName())) {
                activeProjectRepository.deleteById(id);
            }
        }
    }

    public record RunInfo(String mainFilePath, String input, String language) { }
    @PostMapping("/{id}/run")
    public String runProject(@PathVariable String id, @RequestBody RunInfo runInfo, Principal principal) {
        var project = getById(id, principal);
        if (project == null) {
            return null;
        }
        var files = project.getFiles();
        files.add(ActiveFile.builder()
                .parentPath(".")
                .fileName("input.txt")
                .sourceCode(runInfo.input)
                .build());
        switch (runInfo.language) {
            case "python":
                return dockerService.runPythonCode(files, "./input.txt", runInfo.mainFilePath);
            case "javascript":
                return dockerService.runJSCode(files, "./input.txt", runInfo.mainFilePath);
        }

        return "Invalid Language";
    }
}
