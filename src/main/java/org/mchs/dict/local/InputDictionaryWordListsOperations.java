package org.mchs.dict.local;

import org.mchs.dict.file.LocalFileReader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


public class InputDictionaryWordListsOperations {

    public static void main(String[] args) throws Exception {

        Map<String, Set<String>> adjMap = combineBaseWordnetFilesWithInflections("en.words/in/base_adj", "en.words/in/adj.exc");
        Map<String, Set<String>> advMap = combineBaseWordnetFilesWithInflections("en.words/in/base_adv", "en.words/in/adv.exc");
        Map<String, Set<String>> verbMap = combineBaseWordnetFilesWithInflections("en.words/in/base_verb", "en.words/in/verb.exc");
        Map<String, Set<String>> nounsMap = combineBaseWordnetFilesWithInflections("en.words/in/base_nouns", "en.words/in/noun.exc");

        createPossessiveNounsList();

        LocalFileReader fileReader = new LocalFileReader();
        Map<String, Set<String>> baseNounsPossMap = fileReader.readBaseFileToMap("base_nouns_possessives.txt");
        Map<String, Set<String>> twelveDictsLemMap = fileReader.readBaseFileToMap("en.words/in/2+2+3lem.txt");

        Map<String, Set<String>> outputInflectionsMap = new TreeMap<>();
        combineInflectionMaps(outputInflectionsMap, adjMap);
        combineInflectionMaps(outputInflectionsMap, advMap);
        combineInflectionMaps(outputInflectionsMap, verbMap);
        combineInflectionMaps(outputInflectionsMap, nounsMap);
        combineInflectionMaps(outputInflectionsMap, twelveDictsLemMap);
        Files.write(Paths.get("src/main/resources/words_to_process.txt"), mapToWords(outputInflectionsMap));

        combineInflectionMaps(outputInflectionsMap, baseNounsPossMap);
        Files.write(Paths.get("src/main/resources/combined_inflections.txt"), mapToListOfLines(outputInflectionsMap));
    }

    private static List<String> mapToWords(Map<String, Set<String>> initialMap) {
        Set<String> treeSet = new TreeSet<>();
        treeSet.addAll(initialMap.keySet());
        initialMap.values().forEach(treeSet::addAll);
        List<String> list = new ArrayList<>(treeSet);
        Collections.sort(list);
        return list;
    }

    private static List<String> mapToListOfLines(Map<String, Set<String>> initialMap) {
        List<String> list = new ArrayList<>();
        Map<String, Set<String>> map = new TreeMap<>(initialMap);
        for (String s : map.keySet()) {
            if (map.get(s).size() == 0) {
                list.add(s);
            } else {
                list.addAll(Collections.singletonList(s + " " + String.join(" ", map.get(s))));
            }
        }
        return list;
    }

    private static void combineInflectionMaps(Map<String, Set<String>> outputMap, Map<String, Set<String>> setMap) {
        Set<String> firstSetOfValues;
        Set<String> secondSetOfValues;
        Set<String> allValues;
        Set<String> allKeys = combineSets(outputMap.keySet(), setMap.keySet());

        for (String key : allKeys) {
            firstSetOfValues = outputMap.containsKey(key) ? outputMap.get(key) : new HashSet<>();
            secondSetOfValues = setMap.containsKey(key) ? setMap.get(key) : new HashSet<>();
            allValues = combineSets(firstSetOfValues, secondSetOfValues);
            allValues.remove(key);
            outputMap.put(key, allValues);
        }
    }

    private static Set<String> combineSets(Set<String> firstSet, Set<String> secondSet) {
        Set<String> allKeys = new TreeSet<>();
        allKeys.addAll(firstSet);
        allKeys.addAll(secondSet);
        return allKeys;
    }

    private static void createPossessiveNounsList() throws Exception {
        LocalFileReader localFileReader = new LocalFileReader();
        List<String> baseNouns = localFileReader.readBaseFileToArray("en.words/in/base_nouns");
        List<String> possessives = new ArrayList<>();

        for (String baseNoun : baseNouns) {
            if (baseNoun.endsWith("s")) {
                possessives.add(baseNoun + "   " + baseNoun + "'");
            } else {
                possessives.add(baseNoun + "   " + baseNoun + "'s");
            }
        }
        Files.write(Paths.get("src/main/resources/base_nouns_possessives.txt"), possessives);
        addPath("src/main/resources");
    }

    private static Map<String, Set<String>> combineBaseWordnetFilesWithInflections(String baseFile, String excFile) throws IOException {
        LocalFileReader fileReader = new LocalFileReader();
        Map<String, Set<String>> baseMap = fileReader.readBaseFileToMap(baseFile);
        Map<String, Set<String>> excMap = fileReader.readExcFileToMap(excFile);
        combineInflectionMaps(baseMap, excMap);
        return baseMap;
    }

    public static void addPath(String s) throws Exception {
        // add created files to classpath
        File f = new File(s);
        URI u = f.toURI();
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(urlClassLoader, u.toURL());
    }
}
