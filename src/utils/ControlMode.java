package src.utils;

public enum ControlMode {
    Camera,
    Light,
    Object;

    private static final ControlMode[] values = values();

    public ControlMode next() {
        return values[(this.ordinal() + 1) % values.length];
    }
}
