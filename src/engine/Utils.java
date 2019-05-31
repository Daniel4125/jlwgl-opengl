package engine;

import java.io.InputStream;
import java.util.Scanner;

public class Utils {

    /**
     * Retrieve the contents of a file from the class path
     * @param fileName
     * @return
     * @throws Exception
     */
    public static String loadResource(String fileName) throws Exception {
        String result;
        try (InputStream in = Class.forName(Utils.class.getName()).getResourceAsStream(fileName);
             Scanner scanner = new Scanner(in, "UTF-8")) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }
}
