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

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;

public class XMLTest {
    
    public static final void main(String[] args) throws Exception {
        System.out.println("xD");
        final File file = new File("src/test/resources/test.xml");
        final SAXBuilder saxBuilder = new SAXBuilder();
        final Document document = saxBuilder.build(file);
        final Element element_root = document.getRootElement();
        System.out.println(element_root);
        for (Element element : element_root.getChildren()) {
            System.out.println("    " + element);
        }
    }
    
}
