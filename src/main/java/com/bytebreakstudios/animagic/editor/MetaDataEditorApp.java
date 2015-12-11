package com.bytebreakstudios.animagic.editor;

import com.badlogic.gdx.Game;

public class MetaDataEditorApp extends Game {
    @Override
    public void create() {
        setScreen(new EditorScreen());
    }
}
