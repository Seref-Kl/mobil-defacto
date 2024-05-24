package com.mobildefacto.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobildefacto.model.ElementInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum StoreHelper {
    INSTANCE;
    Logger logger = LoggerFactory.getLogger(getClass());

    static File[] fileList = null;
    String currentWorkingDir = System.getProperty("user.dir");
    ConcurrentMap<String, Object> elementMapList;
    //private static final String DEFAULT_DIRECTORY_PATH = "element-values";


    StoreHelper() {
        try {
            String currentWorkingDir = System.getProperty("user.dir");
            initMap(getFileList(currentWorkingDir + "/src"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initMap(List<File> fileList) {
        elementMapList = new ConcurrentHashMap<>();
        Type elementType = new TypeToken<List<ElementInfo>>() {}.getType();
        Gson gson = new Gson();

        for (File file : fileList) {
            try {
                List<ElementInfo> elementInfoList = gson.fromJson(new FileReader(file), elementType);

                elementInfoList.parallelStream().forEach(elementInfo -> {
                    String key = elementInfo.getKey();
                    if (elementMapList.putIfAbsent(key, elementInfo) != null) {
                        System.out.println("Duplicate key found in file " + file.getName() + ": " + key);
                        throw new IllegalStateException("Duplicate key bulundu: " + key);
                    }
                });
            } catch (FileNotFoundException e) {
                System.out.println("Dosya bulunamadı: "+e.getMessage());
            }
        }
    }



    private ElementInfo getElementInfo(ElementInfo elementInfo) {

        return elementInfo;
    }
    private List<File> getFileList(String directoryName) throws IOException {
        List<File> dirList = new ArrayList<>();
        try (Stream<Path> walkStream = Files.walk(Paths.get(directoryName))) {
            walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
                if (f.toString().endsWith(".json")) {
                  //logger.info(f.toFile().getName() + " adlı json dosyası bulundu.");
                    dirList.add(f.toFile());
                }
            });
        }
        return dirList;
    }

    public void printAllValues() {
        elementMapList.forEach((key, value) -> logger.info("Key = {} value = {}", key, value));
    }


    public ElementInfo findElementInfoByKey(String key) {
        return (ElementInfo) elementMapList.get(key);
    }

    public void saveValue(String key, String value) {
        elementMapList.put(key, value);
    }

    public String getValue(String key) {
        return elementMapList.get(key).toString();
    }

}
