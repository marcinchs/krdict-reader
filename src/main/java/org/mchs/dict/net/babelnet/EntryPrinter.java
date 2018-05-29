package org.mchs.dict.net.babelnet;

import org.mchs.dict.bo.babelnet.BabelNetEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class EntryPrinter {

    public static String createOutputEntryShort(Set<BabelNetEntry> babelNetEntrySet) {
        Map<String, List<BabelNetEntry>> babelNetMapByPos = babelNetEntrySet.stream()
                .collect(Collectors.groupingBy(BabelNetEntry::getPartOfSpeech));

        StringBuilder sb = new StringBuilder();

        Set<String> keys = new TreeSet<>(babelNetMapByPos.keySet());

        for (String key : keys) {
            List<BabelNetEntry> babelNetEntries = babelNetMapByPos.get(key);
            List<String> translations = new ArrayList<>();
            babelNetEntries.stream().map(BabelNetEntry::getKoreanTranslations).forEach(translations::addAll);

            sb.append("<i>").append(key).append("</i>: ");
            sb.append(String.join(", ", translations));
            sb.append(" ");
        }
        return sb.toString();
    }

    public static String createOutputEntryLong(Set<BabelNetEntry> babelNetEntrySet, boolean addEnglishSynonyms) {
        Map<String, List<BabelNetEntry>> babelNetMapByPartOfSpeech = babelNetEntrySet.stream()
                .collect(Collectors.groupingBy(BabelNetEntry::getPartOfSpeech));

        StringBuilder sb = new StringBuilder();
        sb.append("<ul>\n");

        Set<String> keys = new TreeSet<>(babelNetMapByPartOfSpeech.keySet());

        for (String partOfSpeech : keys) {
            List<BabelNetEntry> babelNetEntries = babelNetMapByPartOfSpeech.get(partOfSpeech);

            sb.append("<li>\n");
            sb.append("<i>").append(partOfSpeech).append("</i><br>\n");

            for (BabelNetEntry entry : babelNetEntries) {
                sb.append("<dt><span>&#8227;&nbsp;</span>")
                        .append(getStringFromSet(entry.getKoreanTranslations())).append("</dt><br>\n");
                sb.append("<dd>").append(getStringFromList(entry.getEnglishEntryDefinition())).append("</dd><br>\n");
                String definitionKo = getStringFromList(entry.getKoreanEntryDefinition());
                if ( definitionKo.trim().length() > 0 ) {
                    sb.append("<dd>").append(definitionKo).append("</dd><br>\n");
                }
                if ( addEnglishSynonyms ) {
                    sb.append("<dd><i>synonyms</i>: ").append(getStringFromSet(entry.getEnglishSynonyms())).append("</dd><br>\n");
                }

            }

            sb.append("</li>\n");
        }

        sb.append("</ul>\n");
        return sb.toString();
    }

    private static String getStringFromList(List<String> list) {
        List<String> noNullList = list.stream().filter(e -> e != null).collect(Collectors.toList());
        if (noNullList.size() > 0) {
            return String.join(", ", noNullList);
        }
        return "";
    }

    private static String getStringFromSet(Set<String> set) {
        set.removeIf(e -> e == null);
        if (set.size() > 0) {
            return String.join(", ", set);
        }
        return "";
    }
}
