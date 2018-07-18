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

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BinaryTest {
    
    public static final List<Long> durations = new ArrayList<>();
    public static final String unit = "ms";
    public static final Supplier<Long> time = () -> System.currentTimeMillis();
    
    public static final void main(String[] args) throws Exception {
        final File file_1 = new File("src/test/resources/test.xml.zip");
        final File file_2 = new File("src/test/resources/test.xml Kopie.zip");
        //System.out.println("Are files equal: " + areFilesEqual(file_1, file_2, false));
        final int tries = 100;
        final int te = 1000;
        System.out.println("Test started");
        for (int i = 0; i < tries; i++) {
            for (int z = 0; z < te; z++) {
                areFilesEqual(file_1, file_2, true);
            }
        }
        System.out.println("Test finished");
        long sum = 0;
        for (long l : durations) {
            sum += l;
        }
        System.out.println("Tries: " + tries);
        System.out.println("File comparisons per try: " + te);
        System.out.println("Average time taken per try: " + (sum / (durations.size() / te)) + unit);
    }
    
    public static final boolean areFilesEqual(File file_1, File file_2, boolean add) throws Exception {
        //System.out.println(file_1 + ", length=" + file_1.length());
        //System.out.println(file_2 + ", length=" + file_2.length());
        boolean same = false;
        final long start = time.get();
        if (file_1.length() == file_2.length()) {
            try {
                final FileReader fileReader_1 = new FileReader(file_1);
                final FileReader fileReader_2 = new FileReader(file_2);
                while (true) {
                    final int i_1 = fileReader_1.read();
                    if (i_1 == -1) {
                        //System.out.println("Files are completely equal!");
                        same = true;
                        break;
                    } else if (i_1 != fileReader_2.read()) {
                        //System.out.println("Files are not equal!");
                        same = false;
                        break;
                    }
                }
                fileReader_1.close();
                fileReader_2.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                same = false;
            }
        } else {
            //System.out.println("Files have not the same length, this means, they are not equal!");
            same = false;
        }
        final long duration = time.get() - start;
        if (add) {
            durations.add(duration);
        } else {
            System.out.println("Time taken: " + duration + unit);
        }
        return same;
    }
    
}
