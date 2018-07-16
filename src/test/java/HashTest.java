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

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

public class HashTest {
    
    public static final void main(String[] args) throws Exception {
        final String test = "Test";
        System.out.println(test);
        final byte[] bytes = test.getBytes();
        System.out.println(Arrays.toString(bytes));
        final long hash = LongHashFunction.xx().hashBytes(bytes);
        System.out.println(hash);
        final LongHashFunction longHashFunction = LongHashFunction.xx();
        for (int i = 0; i <= 100; i++) {
            final long h = longHashFunction.hashInt(i);
            System.out.println(i + "=>" + (h < 0 ? "" : "+") + h);
        }
        final File file = new File("src/test/resources/test.xml.zip");
        System.out.println(file);
        final byte[] data = Files.readAllBytes(file.toPath());
        System.out.println(Arrays.toString(data));
        final long hash_ = longHashFunction.hashBytes(data);
        System.out.println(hash_);
        final File file_kopie = new File("src/test/resources/test.xml Kopie.zip");
        System.out.println(file_kopie);
        final byte[] data_kopie = Files.readAllBytes(file_kopie.toPath());
        System.out.println(Arrays.toString(data_kopie));
        final long hash_kopie = longHashFunction.hashBytes(data_kopie);
        System.out.println(hash_kopie);
        System.out.println(isFileSame(file, file_kopie, longHashFunction));
        final Properties properties = new Properties();
        properties.load(new FileReader(new File("src/test/resources/path.txt")));
        System.out.println(properties);
        final File folder = new File(properties.getProperty("test_folder"));
        System.out.println(folder + " exists: " + folder.exists());
        for (File f : folder.listFiles()) {
            if (f.isFile()) {
                System.out.println(f + "=>" + longHashFunction.hashBytes(Files.readAllBytes(f.toPath())));
            }
        }
    }
    
    public static final boolean isFileSame(File file_1, File file_2, LongHashFunction longHashFunction) {
        Objects.requireNonNull(file_1);
        Objects.requireNonNull(file_2);
        Objects.requireNonNull(longHashFunction);
        try {
            if (!file_1.getName().equals(file_2.getName())) {
                //return false; //TODO Files could be the same, even when they have different names!
            }
            if (file_1.length() != file_2.length()) {
                return false;
            }
            return longHashFunction.hashBytes(Files.readAllBytes(file_1.toPath())) == longHashFunction.hashBytes(Files.readAllBytes(file_2.toPath()));
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
}
