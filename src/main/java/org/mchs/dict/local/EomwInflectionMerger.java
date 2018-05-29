package org.mchs.dict.local;

import org.mchs.dict.file.DirectoryAndFileOperations;
import org.mchs.dict.file.LocalFileReader;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class EomwInflectionMerger {

    static final String EOMW_FOLDER_PATH = "eomw";

    static Map<String, Set<String>> combineWithEomwInflections(Map<String, Set<String>> combinedInflectionsMap) throws Exception {
        LocalFileReader fileReader = new LocalFileReader();
        Map<String, Set<String>> eomwInflections = createEomwIflectionMap(fileReader);
        return combineInflectionMaps(combinedInflectionsMap, eomwInflections);
    }

    private static Map<String, Set<String>> createEomwIflectionMap(LocalFileReader fileReader) throws Exception {
        Map<String, String> fileSetFromEomw = DirectoryAndFileOperations.createFileList(EOMW_FOLDER_PATH, true);
        checkPossibleEomwDuplicates(fileSetFromEomw);
        return fileReader.readBaseFileToMap("eomw_inflections.txt");
    }

    private static Map<String, Set<String>> combineInflectionMaps(Map<String, Set<String>> combinedInflectionsMap, Map<String, Set<String>> eomwMap) {
        for (String eomwKey : eomwMap.keySet()) {
            Set<String> eomwExistingValues = eomwMap.get(eomwKey);
            Set<String> eomwNewValues = new TreeSet<>();

            for (String val : eomwExistingValues) {
                eomwNewValues.addAll(combinedInflectionsMap.get(val));
                combinedInflectionsMap.remove(val);
            }
            combinedInflectionsMap.put(eomwKey, eomwNewValues);
        }
        return combinedInflectionsMap;
    }

    public static void checkPossibleEomwDuplicates(Map<String, String> fileSetFromEomw) throws Exception {
        Map<String, Set<String>> filesMd5Map = DirectoryAndFileOperations.createMd5Map(fileSetFromEomw);

        StringBuilder sb = new StringBuilder();

        for (String key : filesMd5Map.keySet()) {
            if (filesMd5Map.get(key).size() > 1) {
                sb.append(String.join(" ", filesMd5Map.get(key)));
                sb.append("\n");
            }
        }
        Files.write(Paths.get("src/main/resources/eomw_inflections.txt"), sb.toString().getBytes(Charset.forName("UTF-8")));
        InputDictionaryWordListsOperations.addPath("src/main/resources");
    }
}
