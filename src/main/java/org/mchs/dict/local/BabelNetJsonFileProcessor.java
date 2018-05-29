package org.mchs.dict.local;

import org.mchs.dict.bo.babelnet.BabelNetEntry;
import org.mchs.dict.bo.babelnet.generated.BabelSynSetFromJson;
import org.mchs.dict.file.DirectoryAndFileOperations;
import org.mchs.dict.net.babelnet.EntryPrinter;
import org.mchs.dict.net.babelnet.parser.json.JsonParser;
import org.mchs.dict.net.common.WebPageContentHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BabelNetJsonFileProcessor {

    public static void main(String[] args) throws MalformedURLException {

        Map<String, String> fileSet = DirectoryAndFileOperations.createFileList("babelnet/json", false);

        for (String word : fileSet.keySet()) {
            String fileName = fileSet.get(word);
            String searchTerm = word.replace(".json", "");
/*
            if ( !fileName.contains("abdication")){
                continue;
            }
*/
//        String word = "aar";
//        String longPath = "I:/IdeaProjects/dicts-sandbox/dictSandbox/babelnet/json/";
//        String fileName = longPath + word + ".json";

            String jsonString = WebPageContentHandler.getUrlContent(new File(fileName).toURI().toURL());
            List<BabelSynSetFromJson> list = JsonParser.readJsonFromString(jsonString);

            Set<BabelNetEntry> babelNetEntrySet = JsonParser.readJsonObjectsToBabelNetEntries(list, searchTerm);

            babelNetEntrySet.removeIf(babelNetEntry -> !"CONCEPT".equals(babelNetEntry.getSynsetType()));

            if (babelNetEntrySet.size() > 0) {
                System.out.println(searchTerm + "::::" + babelNetEntrySet.size() + " meanings.");

                String outputEntryShort = EntryPrinter.createOutputEntryShort(babelNetEntrySet);
                String outputEntryLong = EntryPrinter.createOutputEntryLong(babelNetEntrySet, false);
                String footer = "<!-- bn4.0 -->";
                System.out.println(outputEntryShort + "<br>\n" + outputEntryLong + footer + "\n");
                System.out.println("============");

//                printToDictEntryFile(searchTerm, outputEntryShort + "<br>\n" + outputEntryLong);
            }
        }
    }

    private static void printToDictEntryFile(String term, String fullDictEntry) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter out = new PrintWriter(new File(term), "UTF-8");
        out.print(fullDictEntry);
        out.close();
    }
}
