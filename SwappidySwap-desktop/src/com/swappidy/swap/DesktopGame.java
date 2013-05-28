package com.swappidy.swap;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class DesktopGame {
    public static void main (String[] args) {
        new LwjglApplication(new SwappidySwap(), "Game", 480, 320, false);
    }	
}
