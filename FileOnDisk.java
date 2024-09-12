package project4;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * FileOnDisk class represents a file or directory with the total size
 * and list of all files in the directory
 *
 * @author Leyan Yu
 * @version 11-7-2023
 */
public class FileOnDisk extends File {
    private long totalSize; // Total size of files and subdirectories in the directory
    private List<FileOnDisk> allFiles; // List of all files in the directory

    /**
     * Constructor for the FileOnDisk class.
     *
     * @param pathname The pathname of the file or directory.
     * @throws NullPointerException if pathname is null.
     */
    public FileOnDisk(String pathname) {
        super(pathname);
        if (pathname == null) {
            throw new NullPointerException("File path cannot be null.");
        }
        this.totalSize = -1; // Initialize to -1, indicating that the size has not been computed yet
        this.allFiles = new ArrayList<>();
    }

    /**
     * Get the total size of the file or directory, including subdirectories and files.
     *
     * @return The total size in bytes.
     * @throws IOException if there are issues accessing the file system.
     */
    public long getTotalSize() throws IOException {
        if (totalSize == -1) { // if it has not been calculated, then explore directory to get the total size
            totalSize = exploreDir(this);
        }
        return totalSize;
    }

    /**
     * Recursively explore a directory and calculate its total size.
     *
     * @param directory The directory to explore.
     * @return The total size of all files in the directory and its subdirectory in bytes.
     * @throws IOException if there are issues accessing the file system.
     */
    private long exploreDir(FileOnDisk directory) throws IOException {
        long size = 0;
        File[] files = directory.listFiles();
        if (files != null) { // if it is a directory
            for (File file : files) {
                FileOnDisk fileOnDisk = new FileOnDisk(file.getCanonicalPath());
                size += fileOnDisk.getTotalSize(); // get the total size of new fileOnDisk
                if (fileOnDisk.isDirectory()) // if it is a directory, add the list of all files
                    directory.allFiles.addAll(fileOnDisk.allFiles);
                else // if it is a file, add the file itself
                    directory.allFiles.add(fileOnDisk);
            }
        }
        else { // if it is a file
            size = directory.length();
        }
        directory.totalSize = size;
        return size;
    }

    /**
     * Get a list of the largest files in the directory.
     *
     * @param numOfFiles The maximum number of largest files to return.
     * @return A list of the largest files.
     * @throws IOException if there are issues accessing the file system.
     */
    public List<FileOnDisk> getLargestFiles(int numOfFiles) throws IOException {
        if (this.isDirectory()) {
            this.getTotalSize(); // init the total size if not calculated
            List<FileOnDisk> largestFiles = new ArrayList<>(this.allFiles);
            largestFiles.sort(new FileOnDiskComparatorBySize());
            return largestFiles.subList(0, Math.min(numOfFiles, largestFiles.size()));
        }
        return null;
    }

    /**
     * Returns a string representation of the FileOnDisk object, including its total size, size unit, and canonical path.
     *
     * @return A string representation of the FileOnDisk object with size, size unit, and canonical path.
     * @throws RuntimeException if an IOException occurs when retrieving total size or canonical path.
     */
    @Override
    public String toString() {
        long fileSize = 0;
        try {
            fileSize = this.getTotalSize(); // get total size
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String sizeUnit = getSizeUnit(fileSize); // get size unit
        try {
            return String.format("%8.2f %-7s%s", convertToSize(fileSize), sizeUnit, getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert the file size to a more readable format with units (bytes, KB, MB, GB).
     *
     * @param size The file size in bytes.
     * @return The size as a formatted string with units.
     */
    private String getSizeUnit(long size) {
        String[] units = {"bytes", "KB", "MB", "GB"};
        int unitIndex = 0;
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        return units[unitIndex];
    }

    /**
     * Convert the file size to a more human-readable size with units.
     *
     * @param size The file size in bytes.
     * @return The size as a human-readable double value.
     */
    private double convertToSize(long size) {
        return size / Math.pow(1024, getSizeUnit(size).equals("bytes") ? 0 :
                getSizeUnit(size).equals("KB") ? 1 : getSizeUnit(size).equals("MB") ? 2 : 3);
    }
}
