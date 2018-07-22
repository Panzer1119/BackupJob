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
import java.util.concurrent.atomic.AtomicLong;

public class FileCounter implements Runnable, Comparable<FileCounter> {
    
    private final File folder;
    private final AtomicLong files = new AtomicLong();
    private final AtomicLong directories = new AtomicLong();
    
    public FileCounter(File folder) {
        this.folder = folder;
    }
    
    public final File getFolder() {
        return folder;
    }
    
    public final long getFiles() {
        return files.get();
    }
    
    public final long getDirectories() {
        return directories.get();
    }
    
    public final FileCounter reset() {
        files.set(0);
        directories.set(0);
        return this;
    }
    
    @Override
    public final void run() {
        reset();
        System.out.println("Starting to count files and directories for: " + folder);
        final long start = System.currentTimeMillis();
        listFolder(folder);
        final long duration = System.currentTimeMillis() - start;
        //System.out.println("Finished to count files and directories for: " + folder + ", time taken: " + duration + "ms");
        //System.out.println(folder + " has " + files.get() + " files and " + directories.get() + " directories");
    }
    
    private final void listFolder(File folder) {
        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                files.incrementAndGet();
            } else if (file.isDirectory()) {
                directories.incrementAndGet();
                listFolder(file);
            }
        }
    }
    
    @Override
    public final int compareTo(FileCounter fileCounter) {
        return folder.compareTo(fileCounter.folder);
    }
    
    @Override
    public final String toString() {
        return "FileCounter{" + "folder=" + folder + ", files=" + files + ", directories=" + directories + '}';
    }
    
}
