package org.mchs.dict.local;

import org.mchs.dict.file.DirectoryAndFileOperations;

import java.io.IOException;
import java.util.Map;

public class StarDictFileCreator {

/*
    this will create old stardict file (word<TAB>translation)
    which may be of some use if you need only list of words
    without examples.
    Use tab2opf (available on the internet) to create dictionary out of it.
*/

    private static final String DICT_FILE = "dict.txt";
    private static final String FILE_FOLDER_PATH = "dict.entries.in";
    private static final String NO_RESULTS = "No search results starting the key word";

    public static void main(String[] args) throws IOException {

        Map<String, String> fileSet = DirectoryAndFileOperations.createFileList(FILE_FOLDER_PATH);
        System.out.println(fileSet.keySet().size() + " entries.");

        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : fileSet.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();

            String firstFileLine = DirectoryAndFileOperations.readFileToString(v).concat("<hr>");
            if (!firstFileLine.contains(NO_RESULTS)) {
                sb.append(k).append("\t").append(firstFileLine).append("\n");
            }
        }

        DirectoryAndFileOperations.printToFile(sb.toString(), DICT_FILE);
    }

}
