package org.mchs.dict;

import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mchs.dict.file.LocalFileReader;
import org.mchs.dict.net.common.WebPageContentHandler;
import org.mchs.dict.net.krdict.KrDictPageParser;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class ParseTest {

    public static final String OUT_FILE_EXAMPLE = "src/test/resources/train.comp";

    @Test
    public void shouldParseWholePage() throws Exception {
        // given
        String expectedOutput = LocalFileReader.readExternalFileToArray(OUT_FILE_EXAMPLE).stream().collect(Collectors.joining(""));

        // when
        String mainSearchResultsPage = WebPageContentHandler.getUrlContent(
                new File(getClass().getClassLoader().getResource("train.htm").getFile())
                        .toURI().toURL());
        List<Elements> dictionaryEntries = KrDictPageParser.getDictionaryEntries(mainSearchResultsPage);
        String outputShortEntry = KrDictPageParser.createOutputEntryShort(dictionaryEntries);
        String outputEntryLong = KrDictPageParser.createOutputEntryLong(dictionaryEntries);

        // then
        assertThat(expectedOutput.equals(outputShortEntry + outputEntryLong));
    }
}
