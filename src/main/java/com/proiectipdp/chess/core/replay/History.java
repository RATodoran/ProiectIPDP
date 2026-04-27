package com.proiectipdp.chess.core.replay;

import java.util.ArrayList;
import java.util.List;

public class History {
    private final List<Snapshot> snaps = new ArrayList<>();
    private int index = -1; // snapshot curent (view)

    public void clear() {
        snaps.clear();
        index = -1;
    }

    public void record(Snapshot s) {
        // dacă ești în trecut și faci mutare nouă -> taie viitorul
        if (index < snaps.size() - 1) {
            snaps.subList(index + 1, snaps.size()).clear();
        }
        snaps.add(s);
        index = snaps.size() - 1;
    }

    public boolean canPrev() { return index > 0; }
    public boolean canNext() { return index < snaps.size() - 1; }
    public boolean isAtEnd() { return index == snaps.size() - 1; }
    public int size() { return snaps.size(); }
    public int index() { return index; }

    public Snapshot current() {
        if (index < 0) return null;
        return snaps.get(index);
    }

    public Snapshot prev() {
        if (!canPrev()) return current();
        index--;
        return snaps.get(index);
    }

    public Snapshot next() {
        if (!canNext()) return current();
        index++;
        return snaps.get(index);
    }
    public Snapshot get(int index) {
        if (index < 0 || index >= snaps.size()) {
            return null;
        }
        return snaps.get(index);
    }
}
