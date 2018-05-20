package org.mchs.dict.net.krdict;

import org.jsoup.select.Elements;
import org.mchs.dict.net.common.WebPageContentHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;

public class DictionaryHandler {

/*
    this queries English-Korean dictionary for a given word,
    parses output and stores it in a file (in current directory)
    named like searched word
*/

    private static final String DICTIONARY_URL = "https://krdict.korean.go.kr/eng/dicSearchDetail/searchDetailWordsResult?" +
            "nation=eng&nationCode=6&searchFlag=Y&sort=W&currentPage=1&ParaWordNo=" +
            "&syllablePosition=&actCategoryList=&gubun=W&all_wordNativeCode=ALL&" +
            "wordNativeCode=1&wordNativeCode=2&wordNativeCode=3&wordNativeCode=0" +
            "&all_sp_code=ALL&sp_code=1&sp_code=2&sp_code=3&sp_code=4&sp_code=5&sp_code=6" +
            "&sp_code=7&sp_code=8&sp_code=9&sp_code=10&sp_code=11&sp_code=12&sp_code=13&sp_code=14&sp_code=27" +
            "&all_imcnt=ALL&imcnt=1&imcnt=2&imcnt=3&imcnt=0&searchSyllableStart=&searchSyllableEnd=&searchOp=AND" +
            "&searchTarget=trans_word&searchOrglanguage=-1&wordCondition=wordSame&" +
            "query=%s&blockCount=100";

    public static void main(String[] args) throws Exception {
        String searchTerm = checkArgs(args);
        String webPage = WebPageContentHandler.getUrlContent(new URL(String.format(DICTIONARY_URL, searchTerm)));
        List<Elements> dictionaryEntries = KrDictPageParser.getDictionaryEntries(webPage);
        String outputEntryShort = KrDictPageParser.createOutputEntryShort(dictionaryEntries);
        String outputEntryLong = KrDictPageParser.createOutputEntryLong(dictionaryEntries);

        printToDictEntryFile(searchTerm, outputEntryShort + "<br>\n" + outputEntryLong);
    }

    private static void printToDictEntryFile(String term, String fullDictEntry) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter out = new PrintWriter(new File(term), "UTF-8");
        out.print(fullDictEntry);
        out.close();
    }

    private static String checkArgs(String[] args) {
        if (args.length != 1) {
            System.out.println("Need a search term");
            System.exit(0);
        }
        return args[0];
    }

}
