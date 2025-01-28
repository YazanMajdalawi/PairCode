package com.code.pair.yazan.paircode.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class ActiveProject {
    @Id
    private String id;
    private String name;
    @Indexed
    private String ownerId;
    @Builder.Default
    private List<ActiveFile> files = new ArrayList<>();
}
