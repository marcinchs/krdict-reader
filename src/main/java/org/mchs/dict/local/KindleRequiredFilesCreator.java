package org.mchs.dict.local;

import org.mchs.dict.file.DirectoryAndFileOperations;
import org.mchs.dict.file.LocalFileReader;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.mchs.dict.file.LocalFileReader.readExternalFileToArray;
import static org.mchs.dict.local.EomwInflectionMerger.EOMW_FOLDER_PATH;


public class KindleRequiredFilesCreator {

    public static final String NO_RESULTS = "No search results starting the key word";
    private static final String DICT_FILE_NAME_TEMPLATE = "dict%d";
    private static final String FILE_FOLDER_PATH = "dict.entries.in";
    private static final String DICTIONARY_TITLE = "English-Korean Learners' Dictionary";
    private static final String DICTIONARY_METADATA_TEMPLATE_OPF = "kindle/templates/dictionary_metadata_template";
    private static final String RELEASE_NOTES_TEMPLATE = "kindle/templates/rel_notes_template";

    public static void main(String[] args) throws Exception {

        boolean addEomw = true;
        LocalFileReader fileReader = new LocalFileReader();
        List<String> outputFileList = new ArrayList<>();

        Map<String, String> fileSet = new TreeMap<>();
        addEntryFilesMapFromDirectory(fileSet, FILE_FOLDER_PATH);

        if (addEomw) {
            addEntryFilesMapFromDirectory(fileSet, EOMW_FOLDER_PATH);
        }
        Map<String, Set<String>> inflectionsFromFile = fileReader.readBaseFileToMap("combined_inflections.txt");

        Map<String, Set<String>> inflections;
        if (addEomw) {
            Map<String, Set<String>> combinedInflections = EomwInflectionMerger.combineWithEomwInflections(inflectionsFromFile);
            inflections = buildInflectionsMapBasedOnAvailableDictionaryEntries(fileSet.keySet(), combinedInflections);
        } else {
            inflections = buildInflectionsMapBasedOnAvailableDictionaryEntries(fileSet.keySet(), inflectionsFromFile);
        }

        System.out.println(fileSet.keySet().size() + " files to process.");

        StringBuilder sb = new StringBuilder();
        String outputFileNameId;

        int id = 1;
        int fileCounter = 0;
        int eomwCounter = 0;

        for (Map.Entry<String, String> entry : fileSet.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();

            String fileContent = DirectoryAndFileOperations.readFileToString(v).concat("<hr>");

            if (!fileContent.contains(NO_RESULTS)) {
                String idxEntryNoContent = WriteXMLFile.createIdxEntry(k, inflections.get(k), id);
                String idxEntryFull = String.format(idxEntryNoContent, fileContent);
                sb.append(idxEntryFull);
                id++;

                if (id % 1500 == 0) {
                    outputFileNameId = saveEntries(fileReader, sb, fileCounter);
                    outputFileList.add(outputFileNameId);
                    fileCounter++;
                }
                if (fileContent.contains("<!-- eomw1.2 -->")) {
                    eomwCounter++;
                }
            }
        }

        // save remaining entries
        outputFileNameId = saveEntries(fileReader, sb, fileCounter);
        outputFileList.add(outputFileNameId);

        System.out.printf("Dictionary will contain %s entries.", id - 1);

        prepareOpf(fileReader, outputFileList);

        int translatedWordsCount = countTranslatedWords(fileSet);
        prepareReleaseNotes(fileReader, id - 1, eomwCounter, translatedWordsCount);
        DirectoryAndFileOperations.copyAdditionalFiles();

    }

    private static void addEntryFilesMapFromDirectory(Map<String, String> map, String directoryPath) {
        Map<String, String> fileMap = DirectoryAndFileOperations.createFileList(directoryPath, true);
        for (String key : fileMap.keySet()) {
            map.put(key, fileMap.get(key));
        }
    }

    private static int countTranslatedWords(Map<String, String> fileSet) throws IOException {
        int count = 0;
        for (Map.Entry<String, String> entry : fileSet.entrySet()) {
            count += readExternalFileToArray(entry.getValue()).get(0).split(",").length;
        }
        return count;
    }

    private static Map<String, Set<String>> buildInflectionsMapBasedOnAvailableDictionaryEntries(Set<String> dictEntries, Map<String, Set<String>> inputInflections) throws IOException {
        LocalFileReader fileReader = new LocalFileReader();
        Map<String, Set<String>> outputInflections = new HashMap<>();

        dictEntries.stream()
                .filter(entry -> !entry.contains(" "))
                .forEach(
                        entry -> {
                            outputInflections.put(entry, inputInflections.getOrDefault(entry, Collections.emptySet()));
                        });

        Set<String> keys = outputInflections.keySet();

        for (Map.Entry<String, Set<String>> mapEntry : outputInflections.entrySet()) {
            Set<String> values = mapEntry.getValue();
            values.removeAll(keys);
        }

        return outputInflections;
    }

    private static void prepareReleaseNotes(LocalFileReader fileReader, int numOfEntries, int eomwCount, int translationCount) throws IOException {
        String relNotesTemplate = fileReader.readBaseFileToArray(RELEASE_NOTES_TEMPLATE).stream().collect(Collectors.joining("\n"));
        String relNotesContent = String.format(relNotesTemplate, DICTIONARY_TITLE, numOfEntries, eomwCount, translationCount, getTodaysDate());
        DirectoryAndFileOperations.printToFile(relNotesContent, "rel_notes.xhtml");
    }

    private static void prepareOpf(LocalFileReader fileReader, List<String> outputFileList) throws IOException {
        String opfSkeleton = fileReader.readBaseFileToArray(DICTIONARY_METADATA_TEMPLATE_OPF).stream().collect(Collectors.joining("\n"));

        String manifestContent = outputFileList.stream()
                .map(s ->
                        "<item id=\"" + s + "\" href=\"" + s + ".html\" media-type=\"application/xhtml+xml\" />"
                ).collect(Collectors.joining("\n"));

        String spineContent = outputFileList.stream()
                .map(s ->
                        "<itemref idref=\"" + s + "\"/>"
                ).collect(Collectors.joining("\n"));

        String opfFileContent = String.format(opfSkeleton, DICTIONARY_TITLE, DICTIONARY_TITLE, getTodaysDate(), manifestContent, spineContent);
        String opfFileName = DICTIONARY_TITLE.replaceAll("\\W", "").concat(".opf");
        DirectoryAndFileOperations.printToFile(opfFileContent, opfFileName);
    }

    private static String saveEntries(LocalFileReader fileReader, StringBuilder sb, int fileCounter) throws IOException {
        String opfHtmlSkeleton = fileReader.readBaseFileToArray("kindle/templates/opf_html_skeleton").stream().collect(Collectors.joining());
        String opfFileContent = String.format(opfHtmlSkeleton, sb.toString());
        String outputFileNameId = String.format(DICT_FILE_NAME_TEMPLATE, fileCounter);
        DirectoryAndFileOperations.printToFile(opfFileContent, outputFileNameId.concat(".html"));
        sb.setLength(0);
        return outputFileNameId;
    }

    private static String getTodaysDate() {
        Date dNow = new Date();
        SimpleDateFormat ft =
                new SimpleDateFormat("yyyy-MM-dd");
        return ft.format(dNow);
    }

}
