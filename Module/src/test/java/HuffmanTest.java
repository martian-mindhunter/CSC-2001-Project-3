import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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
    public void testCreateCode(){
        String[] codes = Huffman.createCode(null);

        assertEquals(256, codes.length);

        for (int i = 0; i < codes.length; i++) {
            assertEquals("", codes[i]);
        }

        codes = Huffman.createCode(new HuffmanLeaf(5, 'a'));
        assertEquals("", codes['a']);
    }

    @Test
    public void testfillCodes(){
        String[] codes = new String[256];
        java.util.Arrays.fill(codes, "");

        // test for empty tree
        Huffman.fillCodes(null, "", codes);

        for (int i = 0; i < codes.length; i++) {
            assertEquals("", codes[i]);
        }

        // test for single character
        Huffman.fillCodes(new HuffmanLeaf(5, 'a'), "", codes);
        assertEquals("", codes['a']);

        // test for two characters
        HuffmanTree a = new HuffmanLeaf(1, 'a');
        HuffmanTree b = new HuffmanLeaf(2, 'b');
        HuffmanTree pair = new HuffmanNode(97, 3, a, b);

        Huffman.fillCodes(pair, "", codes);
        assertEquals("1", codes['b']);
        assertEquals("", codes['c']);

        // test when multiple levels
        HuffmanTree c = new HuffmanLeaf(4, 'c');
        HuffmanTree root = new HuffmanNode(97, 7, pair, c);

        Huffman.fillCodes(root, "", codes);
        assertEquals("01", codes['b']);
        assertEquals("1", codes['c']);
        assertEquals("", codes['d']);
    }

    @Test
    public void testparseHeader(){
        // Empty header
        assertArrayEquals(new int[256], Huffman.parseHeader(""));

        // Single character
        int[] expected = new int[256];
        expected[97] = 5;
        assertArrayEquals(expected, Huffman.parseHeader("97 5"));

        // Multiple characters, including newline and space
        expected = new int[256];
        expected[10] = 2;
        expected[32] = 6;
        expected[97] = 3;
        expected[98] = 4;
        expected[255] = 1;

        assertEquals(expected, Huffman.parseHeader("10 2 32 6 97 3 98 4 255 1"));
    }

    @Test
    public void testHuffmanDecode() throws IOException {
        // Multiple characters
        FileOutputStream file = new FileOutputStream("decode_test_input.txt");
        String text = "97 3 98 2\n11100";

        for (int i = 0; i < text.length(); i++) {
            file.write(text.charAt(i));
        }
        file.close();

        Huffman.huffmanDecode("decode_test_input.txt", "decode_test_output.txt");

        FileInputStream input = new FileInputStream("decode_test_output.txt");
        String expected = "aaabb";

        for (int i = 0; i < expected.length(); i++) {
            assertEquals((int) expected.charAt(i), input.read());
        }
        assertEquals(-1, input.read());
        input.close();

        // Single character; replaces previous output
        file = new FileOutputStream("decode_test_input.txt");
        text = "97 5\n";

        for (int i = 0; i < text.length(); i++) {
            file.write(text.charAt(i));
        }
        file.close();

        Huffman.huffmanDecode("decode_test_input.txt", "decode_test_output.txt");

        input = new FileInputStream("decode_test_output.txt");

        for (int i = 0; i < 5; i++) {
            assertEquals((int) 'a', input.read());
        }
        assertEquals(-1, input.read());
        input.close();

        // Empty the file and clear previous output
        file = new FileOutputStream("decode_test_input.txt");
        file.close();

        Huffman.huffmanDecode("decode_test_input.txt", "decode_test_output.txt");

        input = new FileInputStream("decode_test_output.txt");
        assertEquals(-1, input.read());
        input.close();
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
