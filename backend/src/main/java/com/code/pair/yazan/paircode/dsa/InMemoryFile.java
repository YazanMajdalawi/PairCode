package com.code.pair.yazan.paircode.dsa;

import com.code.pair.yazan.paircode.dsa.rope.Rope;
import lombok.Data;

import java.util.*;

@Data
public class InMemoryFile {
    private Rope text;
    private long version;

    private final List<TextModification> revisionLog = new LinkedList<>();
    private final Queue<TextModification> pendingChanges = new LinkedList<>();

    public InMemoryFile(Rope text, long version) {
        this.text = text;
        this.version  = version;
    }

    public InMemoryFile(Rope text) {
        this(text, 0L);
    }

    public InMemoryFile(String text) {
        this(Rope.from(text));
    }

    public synchronized String getText() {
        return text.toString();
    }

    public synchronized long getVersion() {
        return version;
    }

    public synchronized void addModification(List<TextModification> modification) {
        for (TextModification mod : modification) {
            addModification(mod);
        }
    }

    public synchronized List<TextModification> applyModifications() {
        List<TextModification> modifications = new ArrayList<>();
        while (!pendingChanges.isEmpty()) {
            modifications.add(applyModification());
        }
        return modifications;
    }

    private void addModification(TextModification modification) {
        for (TextModification loggedModification : revisionLog) {
            if (loggedModification.getFileVersion() < modification.getFileVersion()) {
                break;
            }

            modification.transformBasedOn(loggedModification);
        }

        pendingChanges.add(modification);
    }

    private TextModification applyModification() {
        if (pendingChanges.isEmpty()) {
            return null;
        }

        TextModification modification = pendingChanges.poll();
        modification.setFileVersion(version);

        text = text.replace(modification.getStart(), modification.getEnd(),
                modification.getNewVal());

        for (TextModification pendingChange : pendingChanges) {
            pendingChange.transformBasedOn(modification);
        }

        revisionLog.add(modification);
        version += 1;

        return modification;
    }
}
