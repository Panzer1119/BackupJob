/*
 *    Copyright 2018 Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class CompareTest {
    
    public static final Properties PROPERTIES = new Properties();
    
    public static final String FOLDER_PATH;
    public static final String FOLDER_PATH_2;
    
    static {
        try {
            PROPERTIES.load(new FileReader(new File("src/test/resources/path.txt")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        FOLDER_PATH = PROPERTIES.getProperty("FOLDER_PATH");
        FOLDER_PATH_2 = PROPERTIES.getProperty("FOLDER_PATH_2");
    }
    
    public static final void main(String[] args) {
        //putInSubFolders();
        compareSubFolders();
    }
    
    public static final void compareSubFolders() {
        final File folder = new File(FOLDER_PATH);
        final File folder_2 = new File(FOLDER_PATH_2);
        final Map<String, Object> data = new ConcurrentHashMap<>();
        compareSubFolder(folder, folder_2, data);
        deleteEmptyFolders(data);
        //System.out.println(data);
        for (Map.Entry<String, Object> map : data.entrySet()) {
            System.out.println(map.getKey() + " => " + map.getValue());
        }
    }
    
    public static final void deleteEmptyFolders(Map<String, Object> data) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            if (entry.getValue() instanceof Map) {
                final Map<String, Object> map = (Map<String, Object>) entry.getValue();
                if (map.isEmpty()) {
                    data.remove(entry.getKey());
                }
            }
        }
    }
    
    public static final void compareSubFolder(File folder, File folder_2, Map<String, Object> data) {
        final Map<String, Map.Entry<File, Map<String, Object>>> maps = new ConcurrentHashMap<>();
        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                data.put(file.getName(), file);
            } else if (file.isDirectory()) {
                final Map<String, Object> data_2 = new ConcurrentHashMap<>();
                data.put(file.getName(), data_2);
                maps.put(file.getName(), new AbstractMap.SimpleEntry<>(file, data_2));
            }
        }
        if (folder_2 != null) {
            for (File file : folder_2.listFiles()) {
                if (file.isFile()) {
                    data.remove(file.getName());
                } else if (file.isDirectory()) {
                    final Map.Entry<File, Map<String, Object>> map = maps.remove(file.getName());
                    if (map != null) {
                        compareSubFolder(map.getKey(), file, map.getValue());
                    }
                }
            }
        }
        for (Map.Entry<File, Map<String, Object>> map : maps.values()) {
            compareSubFolder(map.getKey(), null, map.getValue());
        }
    }
    
    public static final void putInSubFolders() {
        final File folder = new File(FOLDER_PATH);
        final Map<Character, File> subfolders = new ConcurrentHashMap<>();
        Arrays.asList(folder.listFiles()).parallelStream().forEach((file) -> {
            final Character c = file.getName().toCharArray()[0];
            final File subfolder = subfolders.computeIfAbsent(c, (c_) -> {
                final File f = new File(folder.getAbsolutePath() + File.separator + c_);
                f.mkdirs();
                return f;
            });
            final File file_new = new File(subfolder.getAbsolutePath() + File.separator + file.getName());
            try {
                Files.write(file_new.toPath(), Files.readAllBytes(file.toPath()));
                System.out.println(String.format("Moved %s to %s", file, file_new));
                if (file_new.exists()) {
                    file.delete();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
    
}
