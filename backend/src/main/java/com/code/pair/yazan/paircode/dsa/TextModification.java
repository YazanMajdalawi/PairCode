package com.code.pair.yazan.paircode.dsa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TextModification {
    private String projectId;
    private String filePath;
    private long fileVersion;

    private int start;
    private int end;
    private String newVal;

    private String modifier;

    public void transformBasedOn(TextModification prev) {
        if (this.modifier.equals(prev.modifier)) {
            return;
        }

        int ps = prev.getStart();
        int pe = prev.getEnd();
        int prevLength = prev.getNewVal() == null ? 0 : prev.getNewVal().length();
        int diff = prevLength - (pe - ps);

        // CASE 1: current is completely left of previous => no impact
        //         ( ... ) [-----]
        //         end() <= start[]
        if (this.end <= ps) {
            return;
        }

        // CASE 2: current is completely right of previous => shift
        //         [-----] ( ... )
        //         end[] <= start()
        if (this.start >= pe) {
            this.start += diff;
            this.end += diff;
            return;
        }

        // CASE 3: previous fully covers current => collapse this edit
        //         [ ( ... ) ]
        //         ps <= start < end <= pe
        if (ps <= this.start && this.end <= pe) {
            int newStart = pe + diff;
            this.start = newStart;
            this.end   = newStart;
            return;
        }

        // CASE 4: current fully covers previous => adjust end only
        //         ( [----] )
        //         start <= ps < pe <= end
        if (this.start <= ps && pe <= this.end) {
            this.end += diff;
            return;
        }

        // CASE 5: partial overlap from the left
        //         ( [ ) ]
        //         start < ps < end <= pe
        if (this.start < ps && this.end <= pe) {
            this.end = ps;
            return;
        }

        // CASE 6: partial overlap from the right
        //         [ ( ] )
        //         ps <= start < pe < end
        if (ps <= this.start && pe < this.end) {
            int newStart = pe + diff;
            int lengthRemaining = this.end - pe;
            this.start = newStart;
            this.end   = newStart + lengthRemaining;
            return;
        }

        throw new RuntimeException("Unhandled transformation scenario!");
    }
}

