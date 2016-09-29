package tech.kilocode.pomodoro.model;

public enum AttemptKind {
    FOCUS(25*60, "Focus Mode"),
    BREAK(5*60, "Break Time");

    private int mTotalSeconds;
    private String mDisplayName;

    AttemptKind(int totalSeconds, String displayName) {
        mDisplayName = displayName;
        mTotalSeconds = totalSeconds;
    }

    public int getTotalSeconds() {
        return mTotalSeconds;
    }

    public String getDisplayName() {
        return mDisplayName;
    }
}
