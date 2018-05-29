package org.mchs.dict.net.babelnet.web;

import org.jsoup.select.Elements;
import org.mchs.dict.bo.babelnet.BabelNetEntry;
import org.mchs.dict.net.babelnet.parser.web.EntryPageParser;
import org.mchs.dict.net.babelnet.parser.web.SearchPageParser;
import org.mchs.dict.net.common.WebPageContentHandler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public class WebPagesHandler {

    private static final String DICTIONARY_URL = "https://babelnet.org/search?lang=EN&langTrans=KO&word=%s";
    private static final String BABELNET_URL = "https://babelnet.org/";

    public static void main(String[] args) throws Exception {
        String searchTerm = checkArgs(args);
//        String searchTerm = "hussy";
        String webPage = WebPageContentHandler.getUrlContent(new URL(String.format(DICTIONARY_URL, searchTerm)));

        Elements mainEntriesList = SearchPageParser.getMainEntryList(webPage);
        System.out.println(mainEntriesList.size());

        Map<String, Set<BabelNetEntry>> map = SearchPageParser.getDictionaryEntriesForSearchTerm(searchTerm, mainEntriesList);
        System.out.println(map.keySet().size());

        for (String partOfSpeech : map.keySet()) {
            map.get(partOfSpeech).stream().forEach(WebPagesHandler::processEntry);
        }

    }

    private static void processEntry(BabelNetEntry babelNetEntry) {
        try {
            String entryPage = WebPageContentHandler.getUrlContent(new URL(BABELNET_URL + babelNetEntry.getLinkToFullEntry()));
            Elements entryElements = EntryPageParser.getEntries(entryPage);
            String definition = EntryPageParser.getDefinition(entryElements);
            System.out.println(definition);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private static String checkArgs(String[] args) {
        if (args.length != 1) {
            System.out.println("Need a search term");
            System.exit(0);
        }
        return args[0];
    }
}
