package com.bytebreakstudios.animagic.integration.texture;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.bytebreakstudios.animagic.texture.AnimagicTexturePacker;

import java.io.File;

public class TextureTestLauncher {
    public static void main(String[] arg) {
        if (!new File("src/test/resources/packed/character.png").exists()) {
            AnimagicTexturePacker.pack(new File("src/test/resources/sprites"), new File("src/test/resources/packed"));
        }

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 800;
        config.height = 600;
        new LwjglApplication(new TextureTest(), config);
    }
}
