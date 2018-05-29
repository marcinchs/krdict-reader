package org.mchs.dict.net.eomw;

import org.jsoup.select.Elements;
import org.mchs.dict.bo.babelnet.BabelNetEntry;
import org.mchs.dict.file.DirectoryAndFileOperations;
import org.mchs.dict.file.LocalFileReader;
import org.mchs.dict.net.babelnet.EntryPrinter;
import org.mchs.dict.net.common.WebPageContentHandler;
import org.mchs.dict.net.eomw.ResultPageParser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ExtendedOpenMultilingualWordnetHandler {

    private static final String DICTIONARY_URL = "http://compling.hss.ntu.edu.sg/omw/cgi-bin/wn-gridx.cgi?" +
            "gridmode=gridx&lang=eng&lang2=kor&lemma=%s";
    private static final String NO_RESULTS = "No search results starting the key word";
    private static final String OUTPUT_FOLDER_PATH = "eomw";

    public static void main(String[] args) throws Exception {
        Set<String> wordList = getWordList();

        for (String searchTerm : wordList) {

            String webPage = WebPageContentHandler.getUrlContent(new URL(String.format(DICTIONARY_URL, searchTerm)));
            Elements entries = ResultPageParser.getEntries(webPage);

            Set<BabelNetEntry> babelNetEntrySet = ResultPageParser.getEntriesFromTables(entries);

            if (babelNetEntrySet.size() > 0) {
                System.out.println(searchTerm + "::::" + babelNetEntrySet.size() + " meanings.");

                String outputEntryShort = EntryPrinter.createOutputEntryShort(babelNetEntrySet);
                String outputEntryLong = EntryPrinter.createOutputEntryLong(babelNetEntrySet, true);
                String footer = "<!-- eomw1.2 -->";
                String dictEntry = outputEntryShort + "<br>\n" + outputEntryLong + footer + "\n";
                Files.write(Paths.get(OUTPUT_FOLDER_PATH + File.separator + searchTerm), dictEntry.getBytes(Charset.forName("UTF-8")));
            } else {
                System.out.println(searchTerm + "'s translations not found");
                Files.write(Paths.get(OUTPUT_FOLDER_PATH + File.separator + searchTerm), NO_RESULTS.getBytes(Charset.forName("UTF-8")));
            }
        }

    }

    private static Set<String> getWordList() throws IOException {
        LocalFileReader fileReader = new LocalFileReader();
        List<String> list = fileReader.readBaseFileToArray("babelnet/in/base_adv.txt");
        List<String> verbs = fileReader.readBaseFileToArray("babelnet/in/base_verb.txt");
        List<String> nouns = fileReader.readBaseFileToArray("babelnet/in/base_nouns.txt");
        Set<String> lemmies = fileReader.readBaseFileToMap("en.words/in/2+2+3lem.txt").keySet();
        list.addAll(verbs);
        list.addAll(nouns);
        list.addAll(lemmies);
        Map<String, String> fileSet = DirectoryAndFileOperations.createFileList(OUTPUT_FOLDER_PATH, false);
        Map<String, String> fileSet2 = DirectoryAndFileOperations.createFileList("dict.entries.in", false);
        Map<String, String> fileSet3 = DirectoryAndFileOperations.createFileList(OUTPUT_FOLDER_PATH, false);
        return list.stream().filter(e -> !fileSet.containsKey(e))
                .filter(e -> !fileSet2.containsKey(e))
                .filter(e -> !fileSet3.containsKey(e))
                .filter(e -> !"con".equals(e))
                .filter(e -> !"prn".equals(e)).collect(Collectors.toCollection(TreeSet::new));
    }
}
