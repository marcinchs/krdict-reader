package org.mchs.dict.net.krdict;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class KrDictPageParser {

    private static final String MAIN_DIV_SELECTOR = "ul.search_list.w700.printArea > li";
    private static final String ENTRY_TITLE = "li > p";
    private static final String DIV_WITH_EMPTY_RESULTS = "#page_naviArea2 > div.floatL.sub_left > ul.search_list.w700.printArea > div";

    public static List<Elements> getDictionaryEntries(String webPageContent) throws Exception {
        checkForEmptyResults(webPageContent);
        Elements elements = getMainPart(webPageContent);
        return getDictionaryEntries(elements);
    }

    private static void checkForEmptyResults(String webPageContent) throws Exception {
        Document doc = Jsoup.parse(webPageContent);
        Elements emptyResults = doc.select(DIV_WITH_EMPTY_RESULTS);
        if (!emptyResults.isEmpty()) {
            throw new Exception(emptyResults.text());
        }
    }

    public static String createOutputEntryLong(List<Elements> dictEntryList) {
        Map<String, List<Elements>> elementsMapByPartsOfSpeech = parseElementsToPartsOfSpeechMap(dictEntryList);
        Map<String, List<String>> parsedMap = parsePartsOfSpeechMap(elementsMapByPartsOfSpeech);
        return printMapToString(parsedMap);
    }

    private static String printMapToString(Map<String, List<String>> parsedMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("<ul>").append("\n");
        for (String key : parsedMap.keySet()) {
            sb.append("<li>");
            sb.append(key).append(String.join("", parsedMap.get(key)));
            sb.append("</li>").append("\n");
        }
        sb.append("</ul>");
        return sb.toString();
    }

    private static Map<String, List<String>> parsePartsOfSpeechMap(Map<String, List<Elements>> elementsMapByPartsOfSpeech) {
        Map<String, List<String>> parsedEntries = new TreeMap<>();
        for (String key : elementsMapByPartsOfSpeech.keySet()) {
            parsedEntries.put(
                    "<i>" + key + "</i><br>\n", parseEntries(elementsMapByPartsOfSpeech.get(key)));
        }
        return parsedEntries;
    }

    private static List<String> parseEntries(List<Elements> elementList) {
        List<String> parsedEntries = new ArrayList<>();
        String parsedEntry;

        for (Elements elements : elementList) {
            parsedEntry = parseAndBeautifyEntry(elements);
            parsedEntries.add(parsedEntry);
        }

        return parsedEntries;
    }

    private static String parseAndBeautifyEntry(Elements elements) {
        List<String> list = getListOfExamplesFromEntry(elements);
        return htmlizeEntry(elements, list);
    }

    private static String htmlizeEntry(Elements elements, List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<dt>").append(getEntryName(elements.get(0)))
                .append(" ").append(getPronunciation(elements.get(0)))
                .append("</dt><br>").append("\n");

        for (String s : list) {
            stringBuilder.append("<dd>")
                    .append(s)
                    .append("</dd><br>")
                    .append("\n");
        }

        return stringBuilder.toString();
    }

    private static List<String> getListOfExamplesFromEntry(Elements elements) {
        return elements.stream().skip(1).map(Element::text).collect(Collectors.toList());
    }

    private static Map<String, List<Elements>> parseElementsToPartsOfSpeechMap(List<Elements> dictEntryList) {
        Map<String, List<Elements>> map = new HashMap<>();
        String partOfSpeech;
        for (Elements elements : dictEntryList) {
            partOfSpeech = elements.get(0).select("em.manyLang6").text().toLowerCase();
            List<Elements> values = new ArrayList<>();
            values.add(elements);
            if (map.containsKey(partOfSpeech)) {
                List<Elements> previousValues = map.get(partOfSpeech);
                values.addAll(previousValues);
            }
            map.put(partOfSpeech, values);
        }
        return map;
    }

    public static String createOutputEntryShort(List<Elements> dictEntryList) {
        Map<String, List<String>> listMap = getTitleWordsMap(dictEntryList);
        return getTitleWordsPrettyPrint(listMap);
    }

    private static Map<String, List<String>> getTitleWordsMap(List<Elements> dictEntryList) {
        Map<String, List<String>> stringListMap = new HashMap<>();

        for (Elements elements : dictEntryList) {
            Element element = elements.get(0);
            String partOfSpeech = element.select("em.manyLang6").text();
            String title = getEntryName(element);
            String pronunciation = getPronunciation(element);

            List<String> values = new ArrayList<>();
            values.add(title + " " + pronunciation);

            if (stringListMap.containsKey(partOfSpeech)) {
                List<String> previousValues = stringListMap.get(partOfSpeech);
                values.addAll(previousValues);
            }

            stringListMap.put(partOfSpeech, values);
        }

        return stringListMap;
    }

    private static String getEntryName(Element element) {
        return element.select("a[title]").text();
    }

    private static String getTitleWordsPrettyPrint(Map<String, List<String>> map) {
        Map<String, List<String>> sortedMap = new TreeMap<>(map);
        StringBuilder sb = new StringBuilder();
        for (String key : sortedMap.keySet()) {
            sb.append("<i>").append(key.toLowerCase()).append("</i>: ");
            sb.append(String.join(", ", sortedMap.get(key)));
            sb.append(" ");
        }
        return sb.toString();
    }

    private static String getPronunciation(Element element) {
        String elementText = element.text();
        return (elementText.contains("[") && elementText.contains("]")) ?
                elementText.substring(elementText.indexOf("["), elementText.indexOf("]") + 1) : "[]";
    }

    private static List<Elements> getDictionaryEntries(Elements elements) {
        return elements.stream().map(e -> e.select(ENTRY_TITLE)).collect(Collectors.toList());
    }

    private static Elements getMainPart(String webPageContent) throws Exception {
        Document doc = Jsoup.parse(webPageContent);
        return doc.select(MAIN_DIV_SELECTOR);
    }
}
