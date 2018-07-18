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

import net.openhft.hashing.LongHashFunction;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class CompareTest {
    
    public static final Properties PROPERTIES = new Properties();
    
    public static final String FOLDER_PATH;
    public static final String FOLDER_PATH_2;
    public static final String FOLDER_PATH_3;
    public static final String FOLDER_PATH_4;
    public static final String XML_PATH_1;
    
    static {
        try {
            PROPERTIES.load(new FileReader(new File("src/test/resources/path.txt")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        FOLDER_PATH = PROPERTIES.getProperty("FOLDER_PATH");
        FOLDER_PATH_2 = PROPERTIES.getProperty("FOLDER_PATH_2");
        FOLDER_PATH_3 = PROPERTIES.getProperty("FOLDER_PATH_3");
        FOLDER_PATH_4 = PROPERTIES.getProperty("FOLDER_PATH_4");
        XML_PATH_1 = PROPERTIES.getProperty("XML_PATH_1");
    }
    
    public static final void main(String[] args) throws Exception {
        //putInSubFolders();
        //compareSubFolders();
        //putInSubFolders2();
        compareSubFolders2();
    }
    
    public static final void compareSubFolders2() throws Exception {
        final File folder = new File(FOLDER_PATH_3);
        final File folder_2 = new File(FOLDER_PATH_4);
        final SAXBuilder saxBuilder = new SAXBuilder();
        final File xmlFile = new File(XML_PATH_1);
        Files.write(xmlFile.toPath(), ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<root>\n" + "</root>").getBytes());
        final Document document = saxBuilder.build(xmlFile);
        final Element rootElement = document.getRootElement();
        System.out.println(rootElement);
        listFolder(folder, rootElement);
        unlistFolder(folder_2, rootElement, (f_) -> new File(folder.getAbsolutePath() + File.separator + (f_.getAbsolutePath().substring(folder_2.getAbsolutePath().length() + File.separator.length()))));
        final XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(Format.getPrettyFormat());
        xmlOutputter.output(document, new FileOutputStream(xmlFile));
    }
    
    public static final void listFolder(File folder, Element parentElement) {
        for (File f : folder.listFiles()) {
            if (f.isFile()) {
                final Element element = new Element("file");
                element.setAttribute("name", f.getName());
                parentElement.addContent(element);
            } else if (f.isDirectory()) {
                final Element element = new Element("directory");
                element.setAttribute("name", f.getName());
                parentElement.addContent(element);
                listFolder(f, element);
            }
        }
    }
    
    public static final void unlistFolder(File folder, Element parentElement, Function<File, File> toOtherFolder) {
        for (File f : folder.listFiles()) {
            if (f.isFile()) {
                final Element element = parentElement.getChildren("file").stream().filter((child) -> child.getAttributeValue("name").equals(f.getName())).findFirst().orElse(null);
                if (element != null) {
                    try {
                        final File f_ = toOtherFolder.apply(f);
                        if (f_.length() != f.length()) {
                            element.setAttribute("extra", "not same size");
                            continue;
                        }
                        if (LongHashFunction.xx().hashBytes(Files.readAllBytes(f_.toPath())) != LongHashFunction.xx().hashBytes(Files.readAllBytes(f.toPath()))) {
                            element.setAttribute("extra", "not same data");
                            continue;
                        }
                        parentElement.removeContent(element);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else if (f.isDirectory()) {
                final Element element = parentElement.getChildren("directory").stream().filter((child) -> child.getAttributeValue("name").equals(f.getName())).findFirst().orElse(null);
                if (element != null) {
                    unlistFolder(f, element, toOtherFolder);
                }
                if (element.getChildren().isEmpty()) {
                    parentElement.removeContent(element);
                }
            }
        }
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
    
    public static final void putInSubFolders2() {
        final File folder = new File(FOLDER_PATH_3);
        Arrays.asList(folder.listFiles()).parallelStream().forEach((file) -> {
            final char[] n = file.getName().toCharArray();
            final Character c_1 = n[0];
            final Character c_2 = n[1];
            final Character c_3 = n[2];
            final File folder_1 = new File(folder.getAbsolutePath() + File.separator + c_1);
            final File folder_2 = new File(folder_1.getAbsolutePath() + File.separator + c_2);
            final File folder_3 = new File(folder_2.getAbsolutePath() + File.separator + c_3);
            folder_3.mkdirs();
            final File file_new = new File(folder_3.getAbsolutePath() + File.separator + file.getName());
            try {
                Files.write(file_new.toPath(), Files.readAllBytes(file.toPath()));
                file.delete();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
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
