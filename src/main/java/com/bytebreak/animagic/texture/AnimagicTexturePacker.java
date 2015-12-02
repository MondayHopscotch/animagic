package com.bytebreak.animagic.texture;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import java.io.*;
import java.nio.channels.FileChannel;

/**
 * Texture packer that is designed to pack images and also copy meta files.
 * @author MondayHopscotch
 */
public class AnimagicTexturePacker {

    /**
     * filter to only return directories
     */
    FileFilter directoryFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    };

    /**
     * filter to only return meta files
     */
    FileFilter metaFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isFile() && pathname.getName().equals("meta");
        }
    };

    private File inputDir;
    private File outputDir;
    private File metaDir;

    public AnimagicTexturePacker(File inputDir, File outputDir) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
        metaDir = new File(outputDir, "meta");
    }

    public void pack() {
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
            findAndCopyMetaFiles(subDir);
        }
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Finished packing textures");
        System.out.println("Atlases Packed: " + count);
        System.out.println(String.format("Total time: %.2f seconds", duration / 1000f));
    }

    private void findAndCopyMetaFiles(File subDir) {
        recursiveFindAndCopyMeta(subDir, 0);
    }

    private void recursiveFindAndCopyMeta(File dir, int depth) {
        if (depth > 10) {
            return;
        } else {
            for (File subDir : dir.listFiles(directoryFilter)) {
                recursiveFindAndCopyMeta(subDir, depth + 1);
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
                    copyFile(metaFile, new File(metaDir, copiedFileName));
                } catch (IOException e) {
                    System.err.println("Failed to copy meta file for " + dir.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        }
    }

    public void copyFile(final File sourceFile, final File destFile) throws IOException {
        System.out.println("Copying meta file: " + sourceFile.getPath().substring(inputDir.getPath().length()));
        if (!destFile.exists())
        {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;
        try
        {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally
        {
            source.close();
            destination.close();
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: <topLevelInputDirectory> <topLevelOutputDirectory>");
            System.exit(-1);
        }

        File inputDir = new File(args[0]);
        if (!inputDir.isDirectory()) {
            System.err.println(args[0] + " must be a directory");
        }

        File outputDir = new File(args[1]);
        if (!outputDir.isDirectory()) {
            System.err.println(args[1] + " must be a directory");
        }

        AnimagicTexturePacker packer = new AnimagicTexturePacker(inputDir, outputDir);
        packer.pack();
    }
}
