import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import ru.netology.pyas.savegame.GameProgress;

public class Main {
    
    private static final String SEP = File.separator;

    public static void main(String[] args) {
        String saveDir = "Game" + SEP + "savegames" + SEP;
        String zipPath = saveDir + "saves.zip";
        String saveFile = saveDir + "save1.sav";

        if (!openZip(zipPath, saveDir)) {
            return;
        }

        GameProgress progress = openProgress(saveFile);
        if (progress == null) {
            return;
        }

        System.out.println(progress);
    }

    private static boolean openZip(String path, String saveDir) {
        try (
            FileInputStream fis = new FileInputStream(path);
            ZipInputStream zis = new ZipInputStream(fis)
        ) {
            ZipEntry entry;
            while (null != (entry = zis.getNextEntry())) {
                String saveFile = saveDir + entry.getName();
                try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                    int chr = -1;
                    while( -1 != (chr = zis.read())) {
                        fos.write(chr);
                    }
                }
                zis.closeEntry();
            }
        } catch (IOException ex) {
            System.err.format("Ошибка разархивирования файла %s: %s%n", path, ex.getMessage());
            return false;
        }
        return true;
    }

    private static GameProgress openProgress(String saveFile) {
        GameProgress result = null;
        try (
            FileInputStream fis = new FileInputStream(saveFile);
            ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            result = (GameProgress) ois.readObject();
        } catch (IOException ex) {
            System.err.format("Ошибка чтения файла %s: %s%n", saveFile, ex.getMessage());
        } catch (ClassNotFoundException ex) {
            System.err.format("Ошибка чтения сохранения %s: %s%n", saveFile, ex.getMessage());
        }

        return result;
    }
}
