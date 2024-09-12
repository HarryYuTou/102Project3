package project4;

import java.io.IOException;
import java.util.Comparator;

/**
 * FileOnDiskComparatorBySize class compares two FileOnDisk objects based on their total size
 *
 * @author Leyan Yu
 * @version 11-7-2023
 */
public class FileOnDiskComparatorBySize implements Comparator<FileOnDisk> {

    /**
     * Compares two FileOnDisk objects based on their total size. If sizes are equal, it compares
     * them based on their path names in lexicographic order.
     *
     * @param o1 The first FileOnDisk object to compare.
     * @param o2 The second FileOnDisk object to compare.
     * @return A negative value if o1 is larger, a positive value if o2 is larger, or 0 if they are equal.
     * @throws RuntimeException if an IOException occurs while retrieving total sizes.
     */
    @Override
    public int compare(FileOnDisk o1, FileOnDisk o2) {
        // Compare by size (number of bytes)
        int sizeComparison = 0;
        try {
            sizeComparison = Long.compare(o1.getTotalSize(), o2.getTotalSize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (sizeComparison == 0) {
            // If sizes are equal, compare by path names in lexicographic order
            return o1.getPath().compareTo(o2.getPath());
        }

        return -1 * sizeComparison; // from largest to smallest
    }
}
