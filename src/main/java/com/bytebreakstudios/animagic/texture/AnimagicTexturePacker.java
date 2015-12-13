package com.bytebreakstudios.animagic.texture;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.utils.Array;
import com.bytebreakstudios.animagic.texture.data.AnimagicAnimationData;
import com.bytebreakstudios.animagic.texture.data.AnimagicAtlasData;
import com.bytebreakstudios.animagic.utils.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * Texture packer that is designed to pack images and also copy meta files.
 */
public final class AnimagicTexturePacker {

    /**
     * filter to only return directories
     */
    private static final FileFilter directoryFilter = File::isDirectory;

    /**
     * filter to only return meta files
     */
    private static final FileFilter metaFilter = pathname -> pathname.isFile() && pathname.toString().endsWith("meta");

    public static void pack(File inputDir, File outputDir) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth *= 4;
        settings.maxHeight *= 4;
        settings.combineSubdirectories = true;
        settings.duplicatePadding = true;
        settings.fast = true;
        settings.filterMin = Texture.TextureFilter.Nearest;
        settings.filterMag = Texture.TextureFilter.Nearest;

        System.out.println("Start packing textures");
        int count = 0;
        long startTime = System.currentTimeMillis();
        for (File subDir : inputDir.listFiles(directoryFilter)) {
            count++;
            System.out.println("\n****Packing atlas '" + subDir.getName() + "'");
            TexturePacker.process(settings, subDir.getAbsolutePath(), outputDir.getAbsolutePath(), subDir.getName());
            findAndCopyMetaFiles(subDir, outputDir);
        }
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Finished packing textures");
        System.out.println("Atlases Packed: " + count);
        System.out.println(String.format("Total time: %.2f seconds", duration / 1000f));
    }

    private static void findAndCopyMetaFiles(File rawFilesDir, File outputDir) {
        AnimagicAtlasData atlasMeta = new AnimagicAtlasData();
        for (File subDir : rawFilesDir.listFiles(directoryFilter)) {
            recursiveFindAndCopyMeta(subDir, 0, new Array<>(), atlasMeta);
        }
        FileUtils.saveToFile(atlasMeta, outputDir.getPath() + "/" + rawFilesDir.getName() + ".meta");
    }

    private static void recursiveFindAndCopyMeta(File dir, int depth, Array<String> path, AnimagicAtlasData data) {
        if (depth > 10) {
            return;
        } else {
            Array<String> newPath = new Array<>(path);
            newPath.add(dir.getName());
            for (File subDir : dir.listFiles(directoryFilter)) {
                recursiveFindAndCopyMeta(subDir, depth + 1, newPath, data);
            }
            File[] metaFiles = dir.listFiles(metaFilter);
            if (metaFiles.length > 1) {
                System.err.println("Multiple meta files found in " + dir.getAbsolutePath() + ". Only one meta file permitted per directory");
                System.exit(2);
            } else if (metaFiles.length == 1) {
                File metaFile = metaFiles[0];
                try {
                    AnimagicAnimationData animData = FileUtils.loadFileAs(AnimagicAnimationData.class, metaFile);
                    data.put(newPath.toString("/"), animData);
                } catch (Exception e) {
                    System.err.println("Failed to copy meta file '" + metaFile.getAbsolutePath() + "'");
                    throw e;
                }
            }
        }
    }



    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: <topLevelInputDirectory> <topLevelOutputDirectory>");
            System.exit(1);
        }

        boolean failure = false;
        File inputDir = new File(args[0]);
        if (!inputDir.exists() || !inputDir.isDirectory()) {
            failure = true;
            System.err.println(inputDir.getAbsoluteFile() + " must exist and be a directory");
        }

        File outputDir = new File(args[1]);
        if (!outputDir.exists()) {
            System.out.println(outputDir.getAbsoluteFile() + " does not exist, attempting to create");
            try {
                outputDir = FileUtils.createDirectoryStructure(outputDir);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (!outputDir.exists() || !outputDir.isDirectory()) {
            System.err.println(outputDir.getAbsoluteFile() + " must exist and be a directory");
            failure = true;
        }
        if (failure) {
            System.exit(1);
        }

        AnimagicTexturePacker.pack(inputDir, outputDir);
    }
}
