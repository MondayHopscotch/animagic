package com.bytebreak.animagic.texture;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Monday on 11/12/2015.
 */
public class BitTextureAtlas extends TextureAtlas {

    private static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.enableDefaultTyping();
    }

    private FileHandle atlastFile;
    private AnimagicAnimationData metaFile;

    public BitTextureAtlas(TextureAtlasData data, FileHandle file) {
        super(data);
        atlastFile = file;
    }

    public Array<AtlasRegion> findRegions(String name) {
        Array<AtlasRegion> regions = super.findRegions(name);
        if (regions.size > 0) {
            return regions;
        } else {
            setMetaFile(name);
            int i = 1;
            String regionName;
            while (true) {
                regionName = name + "/" + i;
                AtlasRegion region = super.findRegion(regionName);
                if (region == null) {
                    break;
                } else {
                    AnimagicTextureRegion animagicRegion = new AnimagicTextureRegion(region, new Texture(0, 0, Pixmap.Format.RGBA8888));
                    animagicRegion.meta = loadMetaForRegion(i);
                    regions.add(region);
                }
                i++;
            }
            return regions;
        }
    }

    private AnimagicTextureData loadMetaForRegion(int frame) {
        AnimagicTextureData data = new AnimagicTextureData();
        if (metaFile != null) {
            if (metaFile.frameData.size() >= frame) {
                data = metaFile.frameData.get(frame);
            }
        }
        return data;
    }

    private void setMetaFile(String name) {
        FileHandle packedMeta = atlastFile.parent().child("meta").child(name);
        if (packedMeta.exists()) {
            // load meta file
            metaFile = loadFileAs(AnimagicAnimationData.class, packedMeta.file());
        } else {
            metaFile = null;
        }
    }

    public static <T> T loadFileAs(Class<T> clazz, File file) {
        return loadFileAs(clazz, loadFile(file));
    }

    public static <T> T loadFileAs(Class<T> clazz, String json) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String loadFile(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            StringBuffer json = new StringBuffer();
            String line = reader.readLine();
            while (line != null) {
                json.append(line);
                line = reader.readLine();
            }
            if (json.length() > 0) {
                return json.toString();
            } else {
                System.out.println("File was empty. Could not load.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}
