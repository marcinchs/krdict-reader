package org.mchs.dict.net.babelnet;


import org.jsoup.select.Elements;
import org.junit.Test;
import org.mchs.dict.bo.babelnet.BabelNetEntry;
import org.mchs.dict.bo.babelnet.generated.BabelSynSetFromJson;
import org.mchs.dict.bo.babelnet.generated.Category;
import org.mchs.dict.file.DirectoryAndFileOperations;
import org.mchs.dict.net.babelnet.parser.json.JsonParser;
import org.mchs.dict.net.babelnet.parser.web.EntryPageParser;
import org.mchs.dict.net.babelnet.parser.web.SearchPageParser;
import org.mchs.dict.net.common.WebPageContentHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchPageParserTest {

    @Test
    public void shouldReadJsonFile() throws IOException {

        Map<String, String> fileSet = DirectoryAndFileOperations.createFileList("babelnet/json", false);

        for (String word : fileSet.keySet()) {

            String fileName = fileSet.get(word);
/*
            if ( !fileName.contains("arbiter")){
                continue;
            }*/

//        String word = "aar";
//        String longPath = "I:/IdeaProjects/dicts-sandbox/dictSandbox/babelnet/json/";
//        String fileName = longPath + word + ".json";

            String jsonString = WebPageContentHandler.getUrlContent(new File(fileName).toURI().toURL());
            List<BabelSynSetFromJson> list = JsonParser.readJsonFromString(jsonString);

            Set<BabelNetEntry> babelNetEntrySet = JsonParser.readJsonObjectsToBabelNetEntries(list, word.replace(".json", ""));

            babelNetEntrySet.removeIf(babelNetEntry -> !"CONCEPT".equals(babelNetEntry.getSynsetType()));

            if ( babelNetEntrySet.size() > 0) {
                System.out.println(word.replace(".json", "") + "::::" + babelNetEntrySet.size() + " meanings.");

                String outputEntryShort = EntryPrinter.createOutputEntryShort(babelNetEntrySet);
                String outputEntryLong = EntryPrinter.createOutputEntryLong(babelNetEntrySet, false);

                String footer = "<!-- bn4.0 -->";
                System.out.println(outputEntryShort + "<br>\n" + outputEntryLong + footer + "\n");
                System.out.println("============");
            }
        }
    }

    @Test
    public void shouldParseWebPage() throws Exception {

        System.exit(1);
        String searchTerm = "go";
        String mainSearchResultsPage = WebPageContentHandler.getUrlContent(
                new File(getClass().getClassLoader().getResource("babelnet/html/" + searchTerm + "_main.html").getFile())
                        .toURI().toURL());

        String entryPage = WebPageContentHandler.getUrlContent(
                new File(getClass().getClassLoader().getResource("babelnet/html/" + searchTerm + "_entry_01.html").getFile())
                        .toURI().toURL());

        Elements mainEntriesList = SearchPageParser.getMainEntryList(mainSearchResultsPage);
        System.out.println(mainEntriesList.size());

        Map<String, Set<BabelNetEntry>> map = SearchPageParser.getDictionaryEntriesForSearchTerm(searchTerm, mainEntriesList);
        System.out.println(map.keySet().size());

        Elements entryElements = EntryPageParser.getEntries(entryPage);
        String definition = EntryPageParser.getDefinition(entryElements);
        System.out.println(definition);

    }
}