package org.mchs.dict.local;

import org.mchs.dict.file.DirectoryAndFileOperations;
import org.mchs.dict.file.LocalFileReader;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

class InflectionMerger {

   /*
       Methods in this class works on files in 1 directory
       they don't compare directories for duplicates
   */
    static void combineWithAdditionalInflections(String pathToDirectory, Map<String, Set<String>> initialInflections, Map<String, String> fileSet) throws Exception {
        Map<String, Set<String>> additionalInflections = createAdditionalInflectionMap(pathToDirectory);
        combineInflectionMaps(initialInflections, additionalInflections, fileSet, pathToDirectory);
    }

    static void cleanUpInflections(Map<String, Set<String>> initialInflections, Map<String, String> fileSet) {

        initialInflections.entrySet().removeIf(k ->
                !fileSet.keySet().contains(k.getKey())
        );

        for ( String s: fileSet.keySet()){
            if( !initialInflections.containsKey(s)){
                initialInflections.put(s, Collections.emptySet());
            }
        }

        initialInflections.keySet().parallelStream().forEach(
                key -> initialInflections.values().forEach(set -> set.remove(key)));
    }

   /*
        check if files in pathToDirectory have the same md5
        (if they're effectively the same entries
    */
    private static Map<String, Set<String>> createAdditionalInflectionMap(String pathToDirectory) throws Exception {
        LocalFileReader fileReader = new LocalFileReader();
        Map<String, String> fileSetInAdditionalDir = DirectoryAndFileOperations.createFileList(pathToDirectory, true);
        checkPossibleEntryDuplicates(fileSetInAdditionalDir, pathToDirectory);
        return fileReader.readBaseFileToMap(getAdditionalInflectionsFilename(pathToDirectory));
    }

    private static String getAdditionalInflectionsFilename(String pathToDirectory) {
        return "additional_inflections_" + pathToDirectory + ".txt";
    }

    private static void combineInflectionMaps(Map<String, Set<String>> initialInflections, Map<String, Set<String>> additionalInflections, Map<String, String> fileSet, String pathToDirectory) {
        for (String eomwKey : additionalInflections.keySet()) {
            Set<String> eomwExistingValues = additionalInflections.get(eomwKey);

            // remove duplicates from paths
            for (String s : eomwExistingValues) {
                if (fileSet.get(s) != null && fileSet.get(s).contains(pathToDirectory)) {
                    System.out.println("Removing " + fileSet.get(s) + " " + eomwKey + " was found");
                    fileSet.remove(s);
                }
            }

            // but keep main entry
            eomwExistingValues.add(eomwKey);
            Set<String> eomwNewValues = new TreeSet<>();

            for (String val : eomwExistingValues) {
                Set<String> values = initialInflections.get(val);
                if (values == null) {
                    values = new TreeSet<>();
                }
                if (!val.equals(eomwKey)) {
                    eomwNewValues.add(val);
                }
                eomwNewValues.addAll(values);
                initialInflections.remove(val);
            }
            initialInflections.put(eomwKey, eomwNewValues);
        }
    }

    private static void checkPossibleEntryDuplicates(Map<String, String> fileSetInAdditionalDir, String pathToDirectory) throws Exception {
        Map<String, Set<String>> filesMd5Map = DirectoryAndFileOperations.createMd5Map(fileSetInAdditionalDir);

        StringBuilder sb = new StringBuilder();

        for (String key : filesMd5Map.keySet()) {
            if (filesMd5Map.get(key).size() > 1) {
                sb.append(String.join(" ", filesMd5Map.get(key)));
                sb.append("\n");
            }
        }
        Files.write(Paths.get("src/main/resources/" + getAdditionalInflectionsFilename(pathToDirectory)), sb.toString().getBytes(Charset.forName("UTF-8")));
        InputDictionaryWordListsOperations.addPath("src/main/resources");
    }
}
