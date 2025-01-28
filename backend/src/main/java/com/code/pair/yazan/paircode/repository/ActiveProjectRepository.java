package com.code.pair.yazan.paircode.repository;

import com.code.pair.yazan.paircode.domain.ActiveProject;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ActiveProjectRepository extends CrudRepository<ActiveProject, String> {
    List<ActiveProject> findAllByOwnerId(String ownerId);
}
