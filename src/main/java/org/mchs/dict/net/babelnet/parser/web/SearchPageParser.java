package org.mchs.dict.net.babelnet.parser.web;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mchs.dict.bo.babelnet.BabelNetEntry;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchPageParser {

    public static final String MAIN_DIV_SELECTOR = "#results-container";
    public static final String DIV_ENTRIES_SELECTOR = "div.section-title, div.result.vspace-1";

    public static final String SECTION_TITLE_CLASS = "section-title";
    public static final String NAMED_ENTITY_CLASS = "Named-Entity";

    public static Map<String, Set<BabelNetEntry>> getDictionaryEntriesForSearchTerm(String searchTerm, Elements elements) {

        List<Element> list = elements.select(DIV_ENTRIES_SELECTOR);

        Map<String, Set<BabelNetEntry>> babelNetEntryMap = new HashMap<>();
        String key = "";

        for (Element element : list) {
            if (element.hasClass(NAMED_ENTITY_CLASS)) {
                continue;
            }

            Set<BabelNetEntry> babelNetEntrySet = new HashSet<>();
            BabelNetEntry babelNetEntry;

            if (element.hasClass(SECTION_TITLE_CLASS)) {
                key = element.text();
            } else {
                babelNetEntry = parseElementToBabelNetEntry(element, searchTerm);
                if (babelNetEntry != null) {
                    babelNetEntrySet.add(babelNetEntry);
                }
            }

            if (!babelNetEntryMap.containsKey(key)) {
                babelNetEntryMap.put(key, babelNetEntrySet);
            } else {
                Set<BabelNetEntry> previous = babelNetEntryMap.get(key);
                babelNetEntrySet.addAll(previous);
                babelNetEntryMap.put(key, babelNetEntrySet);
            }
        }

        removeEmptyKeys(babelNetEntryMap);

        return babelNetEntryMap;
    }

    private static void removeEmptyKeys(Map<String, Set<BabelNetEntry>> babelNetEntryMap) {
        Iterator<Map.Entry<String, Set<BabelNetEntry>>> it = babelNetEntryMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Set<BabelNetEntry>> e = it.next();
            Set<BabelNetEntry> value = e.getValue();
            if (value.isEmpty()) {
                it.remove();
            }
        }
    }

    private static BabelNetEntry parseElementToBabelNetEntry(Element element, String searchTerm) {
        Elements resultDefinition = element.select("div.result-definition");
        if (resultDefinition.size() != 2) {
            return null;
        }

        Elements englishEntryTitleElements = resultDefinition.get(0).select(".word.vspace-1");
        Elements koreanEntryTitleElements = resultDefinition.get(1).select(".word.vspace-1");

        BabelNetEntry babelNetEntry = new BabelNetEntry();
        babelNetEntry.setEntryName(searchTerm);
        babelNetEntry.setEnglishEntryTitle(new HashSet<>(Arrays.asList(englishEntryTitleElements.text().split(","))));
        babelNetEntry.setLinkToFullEntry(englishEntryTitleElements.select("a[href]").attr("href"));
        babelNetEntry.setEnglishEntryDefinition(Collections.singletonList(resultDefinition.select("p.def").text()));

        String koreanEntryTitle = koreanEntryTitleElements.text().trim();
        babelNetEntry.setKoreanTranslations(koreanEntryTitle.isEmpty() ? null : new HashSet<>(Collections.singletonList(koreanEntryTitle)));

        return babelNetEntry.getKoreanTranslations() == null ? null : babelNetEntry;
    }

    public static Elements getMainEntryList(String webPageContent) throws Exception {
        Document doc = Jsoup.parse(webPageContent);
        return doc.select(MAIN_DIV_SELECTOR);
    }
}
