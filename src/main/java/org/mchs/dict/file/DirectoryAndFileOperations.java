package org.mchs.dict.file;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
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
        return String.join("", list);
    }

    public static Map<String, String> createFileList(String fileFolderPath, boolean skipFilesWithNoResult) {
        File folder = new File(fileFolderPath);
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        Map<String, String> map;
        if (skipFilesWithNoResult) {
            map = Arrays.stream(listOfFiles)
                    .filter(DirectoryAndFileOperations::entryNotEmpty)
                    .filter(File::isFile)
                    .collect(Collectors.toMap(File::getName, File::toString));
        } else {
            map = Arrays.stream(listOfFiles)
                    .filter(File::isFile)
                    .collect(Collectors.toMap(File::getName, File::toString));
        }
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
        createFileList(ADDITIONAL_FILES_DIRECTORY, true).values().forEach(DirectoryAndFileOperations::copyFiles);
    }

    private static void copyFiles(String s) {
        try {
            String outputFile = s.substring(s.lastIndexOf(File.separator));
            Files.copy(Paths.get(s), Paths.get(DICTIONARY_OUTPUT_DIR + File.separator + outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Set<String>> createMd5Map(Map<String, String> fileMap) throws IOException, NoSuchAlgorithmException {
        Map<String, Set<String>> md5map = new HashMap<>();

        for (String s : fileMap.keySet()) {
            String md5 = calculateFileMd5(fileMap.get(s));
            Set<String> fileSet = new HashSet<>();
            fileSet.add(s);
            if (md5map.containsKey(md5)) {
                Set<String> previous = md5map.get(md5);
                fileSet.addAll(previous);
            }
            md5map.put(md5, fileSet);
        }
        return md5map;
    }

    private static String calculateFileMd5(String path) throws NoSuchAlgorithmException, IOException {
        String string = readFileToString(path);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(string.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);

    }
}
