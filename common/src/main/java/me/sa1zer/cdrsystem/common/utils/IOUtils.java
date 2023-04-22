package me.sa1zer.cdrsystem.common.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@UtilityClass
public class IOUtils {

    public static void createFile(Path path) {
        createFile(path, false);
    }

    @SneakyThrows
    public static void createFile(Path path, boolean recreate) {
        if(recreate)
            Files.deleteIfExists(path);
        if(!Files.exists(path)) {
            Path parent = path.getParent();
            if(parent != null && !Files.isDirectory(path))
                Files.createDirectories(parent);
            Files.createFile(path);
        }
    }

    public static void writeToFile(Path file, String str) {
        try(BufferedWriter bufferedWriter = Files.newBufferedWriter(file)) {
            bufferedWriter.write(str);
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
