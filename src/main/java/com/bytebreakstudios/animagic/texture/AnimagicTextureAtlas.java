package com.bytebreakstudios.animagic.texture;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.bytebreakstudios.animagic.utils.FileUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Monday on 11/12/2015.
 */
public class AnimagicTextureAtlas extends TextureAtlas {
    final static Pattern lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");

    private static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.enableDefaultTyping();
    }

    private FileHandle atlasFile;
    private AnimagicAnimationData metaFile;

    public AnimagicTextureAtlas(TextureAtlasData data, FileHandle file) {
        super(data);
        atlasFile = file;
    }

    public Array<AnimagicTextureRegion> findRegionsWithoutMeta(String name) {
        return loadAnimagicRegions(name, false);
    }

    public Array<AnimagicTextureRegion> findRegionsWithMeta(String name) {
        return loadAnimagicRegions(name, true);
    }

    private Array<AnimagicTextureRegion> loadAnimagicRegions(String name, boolean withMetaData) {
        setMetaFile(name);
        Array<AtlasRegion> regions = super.findRegions(name);
        Array<AnimagicTextureRegion> animagicRegions = new Array<>();
        if (regions.size > 0) {
            for(AtlasRegion region : regions) {
                AnimagicTextureRegion animagicRegion = new AnimagicTextureRegion(region, new Texture(0, 0, Pixmap.Format.RGBA8888));
                if (withMetaData) {
                    animagicRegion.meta = loadMetaForRegion(name);
                }
                animagicRegions.add(animagicRegion);
            }
            return animagicRegions;
        } else {
            int i = 1;
            String regionName;
            while (true) {
                regionName = name + "/" + i;
                AtlasRegion region = super.findRegion(regionName);
                if (region == null) {
                    break;
                } else {
                    AnimagicTextureRegion animagicRegion = new AnimagicTextureRegion(region, new Texture(0, 0, Pixmap.Format.RGBA8888));
                    if (withMetaData) {
                        animagicRegion.meta = loadMetaForRegion(i);
                    }
                    animagicRegions.add(animagicRegion);
                }
                i++;
            }
            return animagicRegions;
        }
    }

    private void setMetaFile(String name) {
        if (name.matches(".*\\d+$")) {
            name = name.substring(0, name.lastIndexOf("/"));
        }

        FileHandle packedMeta = atlasFile.parent().child(getPackedMetaFileName(name));
        if (packedMeta.exists()) {
            // load meta file
            metaFile = FileUtils.loadFileAs(AnimagicAnimationData.class, packedMeta.file());
        } else {
            metaFile = null;
        }
    }

    private AnimagicTextureData loadMetaForRegion(int frame) {
        //make it zero-based
        frame -= 1;
        AnimagicTextureData data = new AnimagicTextureData();
        if (metaFile != null) {
            if (frame >= 0 && metaFile.frameData.size() > frame) {
                data = metaFile.frameData.get(frame);
            }
        }
        return data;
    }

    private AnimagicTextureData loadMetaForRegion(String name) {
        int frame = -1;
        if (metaFile != null) {
            if (name.matches(".*\\d+$")) {
                frame = extractTrailingNumber(name);
            }
        }
        return loadMetaForRegion(frame);
    }

    private int extractTrailingNumber(String text) {
        int number = -1;
        Matcher matcher = lastIntPattern.matcher(text);
        if (matcher.find()) {
            String someNumberStr = matcher.group(1);
            number = Integer.parseInt(someNumberStr);
        }
        return number;
    }

    private String getPackedMetaFileName(String name) {
        String metaFileName = "meta" + "/" + atlasFile.nameWithoutExtension() + "." + name + ".meta";
        return metaFileName;
    }
}
