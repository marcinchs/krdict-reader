package org.mchs.dict.local;

import org.mchs.dict.file.DirectoryAndFileOperations;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private DirectoryAndFileOperations directoryAndFileOperations = new DirectoryAndFileOperations(getTodaysDateAndTime());

    public static void main(String[] args) throws IOException {
        StarDictFileCreator starDictFileCreator = new StarDictFileCreator();
        starDictFileCreator.createDictionary();
    }

    private void createDictionary() throws IOException {
        Map<String, String> fileSet = DirectoryAndFileOperations.createFileList(FILE_FOLDER_PATH, true);
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

        directoryAndFileOperations.printToFile(sb.toString(), DICT_FILE);
    }

    private static String getTodaysDateAndTime() {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
        return ft.format(dNow);
    }
}
