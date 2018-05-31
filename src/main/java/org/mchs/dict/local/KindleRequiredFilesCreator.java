package org.mchs.dict.local;

import org.mchs.dict.file.DirectoryAndFileOperations;
import org.mchs.dict.file.LocalFileReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.mchs.dict.file.LocalFileReader.readExternalFileToArray;


public class KindleRequiredFilesCreator {

    public static final String NO_RESULTS = "No search results starting the key word";
    private static final String DICT_FILE_NAME_TEMPLATE = "dict%d";
    private static final String FILE_FOLDER_PATH = "dict.entries.in";
    private static final String DICTIONARY_TITLE = "English-Korean Learners' Dictionary";
    private static final String DICTIONARY_METADATA_TEMPLATE_OPF = "kindle/templates/dictionary_metadata_template";
    private static final String RELEASE_NOTES_TEMPLATE = "kindle/templates/rel_notes_template";

    private DirectoryAndFileOperations directoryAndFileOperations = new DirectoryAndFileOperations(getTodaysDateAndTime());

    public static void main(String[] args) throws Exception {
        KindleRequiredFilesCreator kindleRequiredFilesCreator = new KindleRequiredFilesCreator();
        InputDictionaryWordListsOperations.createRequiredWordFiles();
        kindleRequiredFilesCreator.createDictionary();
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

    private static String getTodaysDate() {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        return ft.format(dNow);
    }

    private static String getTodaysDateAndTime() {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
        return ft.format(dNow);
    }

    private void createDictionary() throws Exception {
        boolean addEomw = true;
        LocalFileReader fileReader = new LocalFileReader();
        List<String> outputFileList = new ArrayList<>();

        Map<String, String> fileSet = new TreeMap<>();
        if (addEomw) {
            addEntryFilesMapFromDirectory(fileSet, "eomw");
        }
        // overwrite eomw paths (if there are any) with these from Krdict
        addEntryFilesMapFromDirectory(fileSet, FILE_FOLDER_PATH);

        Map<String, Set<String>> inflectionsMap = fileReader.readBaseFileToMap("combined_inflections.txt");

        if (addEomw) {
            InflectionMerger.combineWithAdditionalInflections("eomw", inflectionsMap, fileSet);
        }
        InflectionMerger.combineWithAdditionalInflections(FILE_FOLDER_PATH, inflectionsMap, fileSet);

        InflectionMerger.cleanUpInflections(inflectionsMap, fileSet);
        System.out.println(fileSet.keySet().size() + " files to process.");

        StringBuilder sb = new StringBuilder();
        String outputFileNameId;

        int id = 1;
        int fileCounter = 0;
        int eomwCounter = 0;

        List<String> outputListOfInflections = new ArrayList<>();

        for (Map.Entry<String, String> entry : fileSet.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();

            String fileContent = DirectoryAndFileOperations.readFileToString(v).concat("<hr>");

            if (!fileContent.contains(NO_RESULTS)) {

                String vv = inflectionsMap.get(k) == null ? "" : String.join(" ", inflectionsMap.get(k));
                outputListOfInflections.add(v + "::" + vv);

                String idxEntryNoContent = WriteXMLFile.createIdxEntry(k, inflectionsMap.get(k), id);
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

        Files.write(Paths.get("src/main/resources/all_inflections.txt"), outputListOfInflections);

        // save remaining entries
        outputFileNameId = saveEntries(fileReader, sb, fileCounter);
        outputFileList.add(outputFileNameId);

        System.out.printf("Dictionary will contain %s entries.", id - 1);

        prepareOpf(fileReader, outputFileList);

        int translatedWordsCount = countTranslatedWords(fileSet);
        prepareReleaseNotes(fileReader, id - 1, eomwCounter, translatedWordsCount);
        directoryAndFileOperations.copyAdditionalFiles();
    }

    private void prepareReleaseNotes(LocalFileReader fileReader, int numOfEntries, int eomwCount, int translationCount) throws IOException {
        String relNotesTemplate = fileReader.readBaseFileToArray(RELEASE_NOTES_TEMPLATE).stream().collect(Collectors.joining("\n"));
        String relNotesContent = String.format(relNotesTemplate, DICTIONARY_TITLE, numOfEntries, eomwCount, translationCount, getTodaysDate());
        directoryAndFileOperations.printToFile(relNotesContent, "rel_notes.xhtml");
    }

    private void prepareOpf(LocalFileReader fileReader, List<String> outputFileList) throws IOException {
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
        directoryAndFileOperations.printToFile(opfFileContent, opfFileName);
    }

    private String saveEntries(LocalFileReader fileReader, StringBuilder sb, int fileCounter) throws IOException {
        String opfHtmlSkeleton = fileReader.readBaseFileToArray("kindle/templates/opf_html_skeleton").stream().collect(Collectors.joining());
        String opfFileContent = String.format(opfHtmlSkeleton, sb.toString());
        String outputFileNameId = String.format(DICT_FILE_NAME_TEMPLATE, fileCounter);
        directoryAndFileOperations.printToFile(opfFileContent, outputFileNameId.concat(".html"));
        sb.setLength(0);
        return outputFileNameId;
    }
}
