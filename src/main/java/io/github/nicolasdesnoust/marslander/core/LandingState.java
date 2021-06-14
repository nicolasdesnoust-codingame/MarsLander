package io.github.nicolasdesnoust.marslander.core;

public enum LandingState {
    CRASHED(true),
    LANDED(true),
    OUT_OF_MAP(true),
    STILL_FLYING(false);

    private final boolean terminalState;

    LandingState(boolean terminalState) {
        this.terminalState = terminalState;
    }

    public boolean isTerminalState() {
        return terminalState;
    }
}
