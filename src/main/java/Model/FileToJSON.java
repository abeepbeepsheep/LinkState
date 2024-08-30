package Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileToJSON {
    public static String parseFile(String filename){
        String json;
        try {
            json = Files.readString(Path.of(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return json;
    }
}
