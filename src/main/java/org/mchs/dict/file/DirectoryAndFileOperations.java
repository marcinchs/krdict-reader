package org.mchs.dict.file;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.mchs.dict.file.LocalFileReader.readExternalFileToArray;
import static org.mchs.dict.local.KindleRequiredFilesCreator.NO_RESULTS;

public class DirectoryAndFileOperations {

    private static final String DICTIONARY_OUTPUT_DIR = "dict.out";
    private static final String ADDITIONAL_FILES_DIRECTORY = "src/main/resources/kindle/additionalfiles";

    public static void printToFile(String outString, String dictFile) throws IOException {
        Files.createDirectories(Paths.get(DICTIONARY_OUTPUT_DIR));
        PrintWriter out = new PrintWriter(new File(DICTIONARY_OUTPUT_DIR + File.separator + dictFile), "UTF-8");
        out.print(outString);
        out.close();
    }

    public static String readFileToString(String v) throws IOException {
        List<String> list = readExternalFileToArray(v);
        return list.stream().collect(Collectors.joining());
    }

    public static Map<String, String> createFileList(String fileFolderPath) {
        File folder = new File(fileFolderPath);
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        Map<String, String> map = Arrays.stream(listOfFiles)
                .filter(DirectoryAndFileOperations::entryNotEmpty)
                .filter(File::isFile)
                .collect(Collectors.toMap(File::getName, File::toString));
        return new TreeMap<>(map);
    }

    private static boolean entryNotEmpty(File file) {
        try {
            return !DirectoryAndFileOperations.readFileToString(file.getAbsolutePath()).contains(NO_RESULTS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void copyAdditionalFiles() throws IOException {
        Files.createDirectories(Paths.get(DICTIONARY_OUTPUT_DIR));
        createFileList(ADDITIONAL_FILES_DIRECTORY).values().forEach(DirectoryAndFileOperations::copyFiles);
    }

    private static void copyFiles(String s) {
        try {
            String outputFile = s.substring(s.lastIndexOf(File.separator));
            Files.copy(Paths.get(s), Paths.get(DICTIONARY_OUTPUT_DIR + File.separator + outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
