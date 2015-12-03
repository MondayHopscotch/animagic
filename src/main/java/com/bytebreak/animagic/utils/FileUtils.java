package com.bytebreak.animagic.utils;

import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.io.*;

import javax.swing.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

/**
 * Simple utility class that allows objects to be saved to file / loaded from file via JSON.
 * @author MondayHopscotch
 */
public class FileUtils {

    private static String nextSaveDir = null;
    private static String lastDirectory = null;

    private static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.enableDefaultTyping();
    }

    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static String saveToFile(Object obj) {
        return saveToFile(toJson(obj));
    }

    public static String saveToFile(Object obj, String dir) {
        nextSaveDir = dir;
        return saveToFile(obj);
    }

    public static String saveToFile(String json) {
        if (nextSaveDir == null || nextSaveDir.isEmpty()) {
            JFileChooser fileChooser = new JFileChooser(nextSaveDir) {
                @Override
                protected JDialog createDialog(Component parent) throws HeadlessException {
                    JDialog dialog = super.createDialog(parent);
                    dialog.setAlwaysOnTop(true);
                    dialog.setModalityType(ModalityType.APPLICATION_MODAL);
                    dialog.setModal(true);
                    return dialog;
                }
            };
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setDialogTitle("Save As");
            fileChooser.setApproveButtonText("Save");
            fileChooser.setCurrentDirectory(new File("."));
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                nextSaveDir = fileChooser.getSelectedFile().getParent();
            }
        }
        try {
            File saveTo = new File(nextSaveDir);
            System.out.println(saveTo.getAbsolutePath());
            FileWriter writer = new FileWriter(nextSaveDir);
            writer.write(json);
            writer.close();
            return json;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    public static String selectDirectory() {
        if (lastDirectory == null) {
            lastDirectory = ".";
        }
        JFileChooser fileChooser = new JFileChooser(lastDirectory) {
            @Override
            protected JDialog createDialog(Component parent) throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                dialog.setAlwaysOnTop(true);
                dialog.setModalityType(ModalityType.APPLICATION_MODAL);
                dialog.setModal(true);
                return dialog;
            }
        };
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Select Directory");
        fileChooser.setApproveButtonText("Select");
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            lastDirectory = fileChooser.getSelectedFile().getPath();
            return lastDirectory;
        }
        return null;
    }
}
