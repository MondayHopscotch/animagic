package com.bytebreakstudios.animagic.texture;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
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
    private static final FileFilter metaFilter = pathname -> pathname.isFile() && pathname.getName().equals("meta");

    public static void pack(File inputDir, File outputDir) {
        pack(inputDir, outputDir, new File(outputDir, "meta"));
    }

    public static void pack(File inputDir, File outputDir, File metaDir) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth *= 4;
        settings.maxHeight *= 4;
        settings.combineSubdirectories = true;
        settings.duplicatePadding = true;
        settings.fast = true;
        settings.filterMin = Texture.TextureFilter.Nearest;
        settings.filterMag = Texture.TextureFilter.Nearest;

        metaDir.mkdirs();

        System.out.println("Start packing textures");
        int count = 0;
        long startTime = System.currentTimeMillis();
        for (File subDir : inputDir.listFiles(directoryFilter)) {
            count++;
            System.out.println("\n****Packing atlas '" + subDir.getName() + "'");
            TexturePacker.process(settings, subDir.getAbsolutePath(), outputDir.getAbsolutePath(), subDir.getName());
            findAndCopyMetaFiles(subDir, inputDir, metaDir);
        }
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Finished packing textures");
        System.out.println("Atlases Packed: " + count);
        System.out.println(String.format("Total time: %.2f seconds", duration / 1000f));
    }

    private static void findAndCopyMetaFiles(File subDir, File inputDir, File metaDir) {
        recursiveFindAndCopyMeta(subDir, 0, inputDir, metaDir);
    }

    private static void recursiveFindAndCopyMeta(File dir, int depth, File inputDir, File metaDir) {
        if (depth > 10) {
            return;
        } else {
            for (File subDir : dir.listFiles(directoryFilter)) {
                recursiveFindAndCopyMeta(subDir, depth + 1, inputDir, metaDir);
            }
            File[] metaFiles = dir.listFiles(metaFilter);
            if (metaFiles.length > 1) {
                System.err.println("Multiple meta files found in " + dir.getAbsolutePath() + ". Only one meta file permitted per directory");
                System.exit(2);
            } else if (metaFiles.length == 1) {
                File metaFile = metaFiles[0];
                String copiedFileName = metaFile.getAbsolutePath();
                copiedFileName = copiedFileName.substring(inputDir.getAbsolutePath().length());
                copiedFileName = copiedFileName.replaceAll("[/\\\\]", "."); // can't save slashes into a filename
                if (copiedFileName.startsWith(".")) {
                    copiedFileName = copiedFileName.substring(1);
                }
                try {
                    FileUtils.copyFile(metaFile, new File(metaDir, copiedFileName));
                } catch (IOException e) {
                    System.err.println("Failed to copy meta file for " + dir.getAbsolutePath());
                    e.printStackTrace();
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
