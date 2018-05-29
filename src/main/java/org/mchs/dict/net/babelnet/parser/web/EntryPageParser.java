package org.mchs.dict.net.babelnet.parser.web;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class EntryPageParser {

    public static final String MAIN_DIV_SELECTOR = "div.tabbable[data-lang='KO']";
    public static final String DIV_DEFINITION = "div.word-definition";

    public static Elements getEntries(String webPageContent) {
        Document doc = Jsoup.parse(webPageContent);
        return doc.select(MAIN_DIV_SELECTOR);
    }

    public static String getDefinition(Elements entryElements) {
        return entryElements.select(DIV_DEFINITION).text();
    }
}
