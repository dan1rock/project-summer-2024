package src.utils;

import java.util.HashSet;
import java.util.Set;

public class Input {
    private static final boolean[] keys = new boolean[1024];
    private static final Set<Integer> keyDowns = new HashSet<>();
    private static final Set<Integer> keyUps = new HashSet<>();

    public static boolean getKey(int keyCode) {
        return keys[keyCode];
    }

    public static boolean getKeyDown(int keyCode) {
        return keyDowns.contains(keyCode);
    }

    public static boolean getKeyUp(int keyCode) {
        return keyUps.contains(keyCode);
    }

    public static void onInputFinish() {
        keyDowns.clear();
        keyUps.clear();
    }
    public static void keyDown(int keyCode) {
        keys[keyCode] = true;
        keyDowns.add(keyCode);
    }

    public static void keyUp(int keyCode) {
        keys[keyCode] = false;
        keyUps.add(keyCode);
    }
}
