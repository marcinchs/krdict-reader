package org.mchs.dict.bo.babelnet;


import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class BabelNetEntry {
    private String entryName;
    private String partOfSpeech;
    private String synsetType;
    private Set<String> englishEntryTitle;
    private List<String> englishEntryDefinition;
    private Set<String> koreanTranslations;
    private List<String> koreanEntryDefinition;
    private Set<String> englishSynonyms;
    private String linkToFullEntry;
}
