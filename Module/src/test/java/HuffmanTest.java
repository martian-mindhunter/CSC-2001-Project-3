import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Starter test suite for Project 3 (Huffman Encoding/Decoding).
 * Write more tests!
 * These tests assume the test files are in the same directory as HuffmanTest.java
 */
public class HuffmanTest {

    @Test
    public void testCntFreq() throws IOException {
        int[] freqList = Huffman.cntFreq("src/test/java/file2.txt");
        int[] ansList = {2, 4, 8, 16, 0, 2, 0}; // counts for ascii 97..103
        for (int i = 0; i < ansList.length; i++) {
            assertEquals(ansList[i], freqList[97 + i], "mismatch at ascii " + (97 + i));
        }
    }

    @Test
    public void testComesBefore() {
        HuffmanLeaf d = new HuffmanLeaf(5, 'd');
        HuffmanLeaf k = new HuffmanLeaf(5, 'k');
        assertTrue(d.comesBefore(k));
        assertFalse(k.comesBefore(d));
    }

    @Test
    public void testComesBeforeNode(){
//        HuffmanNode d = new HuffmanNode()
        // TODO: TEST HUFFMAN NODE
    }

    @Test
    public void testCombine(){
        HuffmanLeaf d = new HuffmanLeaf(5, 'd');
        HuffmanLeaf k = new HuffmanLeaf(5, 'k');
        assertEquals(d.combine(k), new HuffmanNode(0,0,d,k));
    }

    @Test
    public void testCreateHeader() throws IOException {
        int[] freqList = Huffman.cntFreq("src/test/java/file2.txt");
        assertEquals("97 2 98 4 99 8 100 16 102 2", Huffman.createHeader(freqList));
    }

    @Test
    public void test01Textfile() throws IOException {
        Huffman.huffmanEncode("src/test/java/file1.txt", "src/test/java/file1_out.txt");
        assertTrue(compareFiles("src/test/java/file1_out.txt", "src/test/java/file1_soln.txt"));
    }
    @Test
    public void test02Textfile() throws IOException {
        Huffman.huffmanEncode("src/test/java/file2.txt", "src/test/java/file2_out.txt");
        assertTrue(compareFiles("src/test/java/file2_out.txt", "src/test/java/file2_soln.txt"));
    }
    @Test
    public void testMultiTextfile() throws IOException {
        Huffman.huffmanEncode("src/test/java/multiline.txt", "src/test/java/multiline_out.txt");
        assertTrue(compareFiles("src/test/java/multiline_out.txt", "src/test/java/multiline_soln.txt"));
    }
    @Test
    public void testOneCharTextFile() throws IOException {
        Huffman.huffmanEncode("src/test/java/onechar.txt", "src/test/java/onechar_out.txt");
        assertTrue(compareFiles("src/test/java/onechar_out.txt", "src/test/java/onechar_soln.txt"));
    }

    /**
     * Compares two files line by line, trimming trailing CR/LF so the
     * comparison works the same on Windows and Unix-style line endings.
     */
    private static boolean compareFiles(String file1, String file2) throws IOException {
        try (BufferedReader r1 = new BufferedReader(new FileReader(file1));
             BufferedReader r2 = new BufferedReader(new FileReader(file2))) {
            String line1, line2;
            while (true) {
                line1 = r1.readLine();
                line2 = r2.readLine();
                if (line1 == null && line2 == null) {
                    return true;
                }
                if (line1 == null || line2 == null) {
                    return false;
                }
                if (!line1.trim().equals(line2.trim())) {
                    return false;
                }
            }
        }
    }
}
