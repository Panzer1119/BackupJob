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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class FileCounterTest {
    
    public static final File RESOURCES = new File("src/test/resources");
    public static final File PATHS = new File(RESOURCES.getAbsolutePath() + File.separator + "paths.txt");
    public static final Properties PROPERTIES = new Properties();
    public static final File FOLDER_TO_INDEX;
    public static final File OUTPUT_FILE;
    public static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    public static final List<FileCounter> FILE_COUNTERS = new ArrayList<>();
    
    static {
        try {
            PROPERTIES.load(new FileReader(PATHS));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        FOLDER_TO_INDEX = new File(PROPERTIES.getProperty("FOLDER_TO_INDEX"));
        OUTPUT_FILE = new File(PROPERTIES.getProperty("OUTPUT_FILE"));
    }
    
    public static final void main(String[] args) throws Exception {
        for (File file : FOLDER_TO_INDEX.listFiles()) {
            if (file.isFile()) {
                //System.out.println(file);
            } else if (file.isDirectory()) {
                final FileCounter fileCounter = new FileCounter(file);
                FILE_COUNTERS.add(fileCounter);
                EXECUTOR.submit(fileCounter);
            }
        }
        EXECUTOR.shutdown();
        EXECUTOR.awaitTermination(1, TimeUnit.HOURS);
        final AtomicLong files = new AtomicLong();
        final AtomicLong directories = new AtomicLong();
        final AtomicLong name_longest = new AtomicLong();
        FILE_COUNTERS.forEach((fileCounter) -> {
            files.addAndGet(fileCounter.getFiles());
            directories.addAndGet(fileCounter.getDirectories());
            final long name_length = fileCounter.getFolder().toString().length();
            if (name_length > name_longest.get()) {
                name_longest.set(name_length);
            }
        });
        final long filesAndDirectories = files.get() + directories.get();
        final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(OUTPUT_FILE, false));
        bufferedWriter.write("#");
        bufferedWriter.write(String.format("%" + (name_longest.get() - 1) + "s |%s|%s|%s|%s|%s|%s", "Folder", " File Count  ", " Percentage   ", "Directory Count ", " Percantage", "Files + Directories", "Percentage"));
        bufferedWriter.newLine();
        bufferedWriter.flush();
        FILE_COUNTERS.stream().sorted().forEach((fileCounter) -> {
            System.out.println(fileCounter);
            try {
                final long name_length = fileCounter.getFolder().toString().length();
                final long delta = name_longest.get() - name_length;
                bufferedWriter.write(fileCounter.getFolder().toString());
                for (long l = -1; l < delta; l++) {
                    bufferedWriter.write(" ");
                }
                final String files_string = String.format("| f%010d ", fileCounter.getFiles());
                bufferedWriter.write(files_string);
                final String files_p_string = String.format("| pf%010.5f ", (fileCounter.getFiles() * 100.0 / (files.get() * 1.0)));
                bufferedWriter.write(files_p_string);
                final String directories_string = String.format("| d%010d ", fileCounter.getDirectories());
                bufferedWriter.write(directories_string);
                final String directories_p_string = String.format("| pd%010.5f ", (fileCounter.getDirectories() * 100.0 / (directories.get() * 1.0)));
                bufferedWriter.write(directories_p_string);
                final String fd_string = String.format("| fd%010d ", (fileCounter.getFiles() + fileCounter.getDirectories()));
                bufferedWriter.write(fd_string);
                final String fd_p_string = String.format("| pfd%010.5f", (fileCounter.getFiles() + fileCounter.getDirectories()) * 100.0 / (filesAndDirectories * 1.0));
                bufferedWriter.write(fd_p_string);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        bufferedWriter.flush();
        bufferedWriter.close();
    }
    
}
