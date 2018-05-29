package org.mchs.dict.net.babelnet.parser.json;

import com.google.gson.Gson;
import org.mchs.dict.bo.babelnet.BabelNetEntry;
import org.mchs.dict.bo.babelnet.generated.BabelSynSetFromJson;
import org.mchs.dict.bo.babelnet.generated.Gloss;
import org.mchs.dict.bo.babelnet.generated.Sense;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class JsonParser {

    public static List<BabelSynSetFromJson> readJsonFromString(String jsonString) {
        Gson gson = new Gson();
        return Arrays.asList(gson.fromJson(jsonString, BabelSynSetFromJson[].class));
    }

    public static Set<BabelNetEntry> readJsonObjectsToBabelNetEntries(List<BabelSynSetFromJson> list, String word) {
        Set<BabelNetEntry> babelNetEntrySet = new HashSet<>();

        for (BabelSynSetFromJson bssfj : list) {

            Set<String> stringSet = bssfj.getSenses().stream()
                    .filter(e -> "KO".equals(e.getLanguage()))
                    .map(Sense::getFullLemma)
                    .map(e -> e.replaceAll("_", " "))
                    .collect(Collectors.toSet());

            Map<String, List<Gloss>> languageGlossesMap = getGlossesByLanguageMap(bssfj);

            BabelNetEntry babelNetEntry = new BabelNetEntry();
            babelNetEntry.setEntryName(word);
            babelNetEntry.setSynsetType(bssfj.getSynsetType());
            babelNetEntry.setPartOfSpeech(getPartOfSpeech(bssfj));
            babelNetEntry.setKoreanTranslations(stringSet);
            babelNetEntry.setEnglishEntryTitle(new TreeSet<>(Collections.singletonList(word)));
            babelNetEntry.setEnglishEntryDefinition(
                    Collections.singletonList(getDefinitionFromGloss(languageGlossesMap, "EN")));
            babelNetEntry.setKoreanEntryDefinition(
                    Collections.singletonList(getDefinitionFromGloss(languageGlossesMap, "KO")));

            if (containsHangul(babelNetEntry)) {
                babelNetEntrySet.add(babelNetEntry);
            }
        }
        return babelNetEntrySet;
    }

    private static boolean containsHangul(BabelNetEntry babelNetEntry) {
        String s = babelNetEntry.toString();
        return containsHangulCharacter(s);
    }

    public static boolean containsHangulCharacter(String s) {
        return s.codePoints().anyMatch(
                codepoint ->
                        Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HANGUL);
    }

    private static String getPartOfSpeech(BabelSynSetFromJson bssfj) {
        if (bssfj.getId().getPos() != null) {
            return bssfj.getId().getPos().toLowerCase();
        } else {
            return "unknown";
        }
    }

    private static Map<String, List<Gloss>> getGlossesByLanguageMap(BabelSynSetFromJson bssfj) {
        return bssfj.getGlosses().stream().collect(Collectors.groupingBy(Gloss::getLanguage));
    }

    private static String getDefinitionFromGloss(Map<String, List<Gloss>> languageGlossesMap, String language) {
        if (languageGlossesMap.containsKey(language)) {
            List<Gloss> glosses = languageGlossesMap.getOrDefault(language, Collections.singletonList(new Gloss()));
            return glosses.get(0).getGloss();
        }
        return null;
    }
}
