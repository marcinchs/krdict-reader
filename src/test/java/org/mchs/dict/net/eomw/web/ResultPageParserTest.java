package org.mchs.dict.net.eomw.web;

import org.jsoup.select.Elements;
import org.junit.Test;
import org.mchs.dict.bo.babelnet.BabelNetEntry;
import org.mchs.dict.net.babelnet.EntryPrinter;
import org.mchs.dict.net.common.WebPageContentHandler;
import org.mchs.dict.net.eomw.ResultPageParser;

import java.io.File;
import java.util.Set;

public class ResultPageParserTest {

    @Test
    public void shouldGetEntries() throws Exception {
        String searchTerm = "abbess";
        String webPage = WebPageContentHandler.getUrlContent(
                new File(getClass().getClassLoader().getResource("eomw.html").getFile())
                        .toURI().toURL());
        Elements entries = ResultPageParser.getEntries(webPage);

        Set<BabelNetEntry> babelNetEntrySet = ResultPageParser.getEntriesFromTables(entries);

        if (babelNetEntrySet.size() > 0) {
            System.out.println(searchTerm + "::::" + babelNetEntrySet.size() + " meanings.");

            String outputEntryShort = EntryPrinter.createOutputEntryShort(babelNetEntrySet);
            String outputEntryLong = EntryPrinter.createOutputEntryLong(babelNetEntrySet, true);
            String footer = "<!-- eomw1.2 -->";
            String dictEntry = outputEntryShort + "<br>\n" + outputEntryLong + footer + "\n";
            System.out.println(dictEntry);
            System.out.println("============");


        }

    }

}