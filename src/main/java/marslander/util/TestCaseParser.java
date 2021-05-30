package marslander.util;

import java.io.InputStream;
import java.util.function.Function;

public class TestCaseParser {
    private TestCaseParser() {
    }

    public static <T> T parseFileContent(String fileName, Function<InputStream, T> fileParser) {
        ClassLoader classLoader = TestCaseParser.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("File " + fileName + " not found!");
        } else {
            return fileParser.apply(inputStream);
        }
    }
}