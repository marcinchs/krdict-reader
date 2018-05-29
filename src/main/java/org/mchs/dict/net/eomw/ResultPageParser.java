package org.mchs.dict.net.eomw;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mchs.dict.bo.babelnet.BabelNetEntry;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ResultPageParser {

    private static final String TD_SELECTOR = "tr>td";
    private static final String TABLES_SELECTOR = "tr:has(table)";

    public static Elements getEntries(String webPage) throws Exception {
        Document doc = Jsoup.parse(webPage);
        return doc.select(TABLES_SELECTOR);
    }

    public static Set<BabelNetEntry> getEntriesFromTables(Elements rows) {
        Set<BabelNetEntry> list = new HashSet<>();
        rows.stream().map(ResultPageParser::getEntriesFromTable).forEach(list::add);
        return list;
    }

    private static BabelNetEntry getEntriesFromTable(Element row) {
        Elements cells = row.select(TD_SELECTOR);

        BabelNetEntry entry = new BabelNetEntry();
        entry.setPartOfSpeech(getPartOfSpeech(cells.get(0).select("nobr").get(0).text().split("-")[1]));
        entry.setEnglishSynonyms(new HashSet<>(Collections.singletonList(cells.get(2).text().trim())));
        entry.setKoreanTranslations(new HashSet<>(Collections.singletonList(cells.get(3).text())));
        entry.setEnglishEntryDefinition(Collections.singletonList(cells.get(5).text()));
        entry.setKoreanEntryDefinition(Collections.singletonList(null));
        return entry;
    }

    private static String getPartOfSpeech(String abbrev) {
        switch (abbrev) {
            case "v":
                return "verb";
            case "n":
                return "noun";
            case "a":
                return "adjective";
            case "r":
                return "adverb";
            default:
                return abbrev;
        }
    }
}
