package org.mchs.dict.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class LocalFileReader {

    public static List<String> readExternalFileToArray(String filePath) throws IOException {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(
                new File(filePath)))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                list.add(sCurrentLine);
            }
        }
        return list;
    }

    public List<String> readBaseFileToArray(String filename) throws IOException {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(
                new File(getClass().getClassLoader().getResource(filename).getFile())))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                list.add(sCurrentLine);
            }
        }
        return list;
    }

    /*
        reading exc files we create keys off the last elements in line
    */
    public Map<String, Set<String>> readExcFileToMap(String filename) throws IOException {
        Map<String, Set<String>> listMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(
                new File(getClass().getClassLoader().getResource(filename).getFile())))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                String[] list = sCurrentLine.split("[\\s]+");
                String key = list[list.length - 1];
                Set<String> values = new TreeSet<>(Arrays.asList(list));

                if (listMap.containsKey(key)) {
                    Set<String> existingValues = listMap.get(key);
                    values.addAll(existingValues);
                }
                listMap.put(key, values);
            }
        }
        return listMap;
    }

    public Map<String, Set<String>> readBaseFileToMap(String filename) throws IOException {
        Map<String, Set<String>> listMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(
                new File(getClass().getClassLoader().getResource(filename).getFile())))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                String[] list = sCurrentLine.split("[\\s]+");
                String key = list[0];
                Set<String> values = new TreeSet<>(Arrays.asList(list));

                if (listMap.containsKey(key)) {
                    Set<String> existingValues = listMap.get(key);
                    values.addAll(existingValues);

                }
                listMap.put(key, values);
            }
        }
        return listMap;
    }
}
