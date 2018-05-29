package org.mchs.dict.net.babelnet.api;


import com.google.gson.Gson;
import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelNetQuery;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.jlt.util.Language;
import org.mchs.dict.file.DirectoryAndFileOperations;
import org.mchs.dict.file.LocalFileReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class BabelNetHandler {

    public static final String OUTPUT_FOLDER_PATH = "babelnet/json";
    public static final String EOMW_FOLDER_PATH = "eomw";

    public static void main(String[] args) throws IOException {
        try {

            System.out.println("It's not what you want to do");
            System.exit(1);

            Set<String> nouns = getWordList();

            BabelNet bn = BabelNet.getInstance();

            for ( String wordToSearch : nouns) {
                BabelNetQuery query = new BabelNetQuery.Builder(wordToSearch)
                        .from(Language.EN)
                        .filterSenses(babelSense -> Language.KO.equals(babelSense.getLanguage()))
                        .to(Collections.singletonList(Language.KO))
                        .build();

                List<BabelSynset> list = bn.getSynsets(query);

                Gson gson = new Gson();
                String listJson = gson.toJson(list);

                Files.write(Paths.get(OUTPUT_FOLDER_PATH + "/" + wordToSearch + ".json"), listJson.getBytes());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Set<String> getWordList() throws IOException {
        LocalFileReader fileReader = new LocalFileReader();
        List<String> list = fileReader.readBaseFileToArray("babelnet/in/base_nouns.txt");
        Map<String, String> fileSetFromKrDict = DirectoryAndFileOperations.createFileList(OUTPUT_FOLDER_PATH, false);
        Map<String, String> fileSetFromEomw = DirectoryAndFileOperations.createFileList(EOMW_FOLDER_PATH, true);
        return list.stream()
                .filter(e -> !fileSetFromKrDict.containsKey(e + ".json"))
                .filter(e -> !fileSetFromEomw.containsKey(e)).collect(Collectors.toCollection(TreeSet::new));
    }
}
