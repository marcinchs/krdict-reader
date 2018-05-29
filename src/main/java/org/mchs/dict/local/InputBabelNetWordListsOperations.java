package org.mchs.dict.local;

import org.mchs.dict.file.DirectoryAndFileOperations;
import org.mchs.dict.file.LocalFileReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class InputBabelNetWordListsOperations {

    // this assumes BabelNet has to be queried
    // only for words that weren't found in Korean Learner's Dictionary

    private static final String FILE_FOLDER_PATH = "dict.entries.in";

    public void prepareFilesForBabelNet() throws IOException {
        //same for nouns, adverbs, etc.
        LocalFileReader fileReader = new LocalFileReader();
        List<String> nouns = fileReader.readBaseFileToArray("en.words/in/base_verb");

        Map<String, String> fileSet = DirectoryAndFileOperations.createFileList(FILE_FOLDER_PATH, true);

        Set<String> notProcessedNouns = new TreeSet<>(nouns.stream().filter(e -> !fileSet.containsKey(e)).collect(Collectors.toSet()));
        notProcessedNouns.removeIf(s ->  s.length() == 1 || s.contains("-") || s.contains("'") || s.contains("."));

        Files.write(Paths.get("src/main/resources/babelnet/in/base_verb.txt"), notProcessedNouns);

    }
}
