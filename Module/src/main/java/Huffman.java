import java.io.*;
import java.util.*;

/**
 * Project 3: Huffman Encoding / Decoding.

 * Implement the methods below to compress and decompress text files using
 * Huffman coding. See the assignment write-up for the full specification.

 *  - Treat characters as 8-bit values in the range [0, 255].
 *  - When writing output files, write the newline character explicitly as
 *    '\n' (do not rely on System.lineSeparator() / println()), so your
 *    output is identical across Windows/Mac/Linux. A BufferedWriter over
 *    a FileWriter, writing "\n" yourself, works well for this.
 *  - Encoded files store the code as literal '0'/'1' characters (one byte
 *    each), not as packed bits, so the "encoded" file will actually be
 *    larger than the original -- that's expected for this assignment.
 *  - createHuffTree() is built on top of addNode()/removeMin(), which
 *    operate generically over a java.util.Collection<HuffmanNode>.
 *  - HuffmanNode is a record, so its components are read with
 *    node.charAscii(), node.freq(), node.left(), and node.right() -- no
 *    "get" prefix.
 */
public class Huffman {

    /**
     * Number of possible 8-bit character values.
     */
    public static final int NUM_CHARS = 256;

    /**
     * Opens the text file named filename and counts the frequency of
     * occurrence of every character in it.
     * Returns an int array of size NUM_CHARS, indexed by ASCII value, with
     * the count of each character (0 for characters that do not occur).
     */

    //Counts each character's occurrences and returns an array
    // indexed by character value, and it gives an IOException if the file isn't opened or read
    public static int[] cntFreq(String filename) throws IOException {
        int[] freq = new int[NUM_CHARS];
        FileInputStream input = new FileInputStream(filename);

        int character = input.read();

        while (character != -1) {
            freq[character] += 1;
            character = input.read();
        }

        input.close();
        return freq;
    }


    /**
     * Builds a Huffman tree from an array of character frequencies (as
     * produced by cntFreq()) and returns the root HuffmanNode of the tree.
     * <p>
     * Use an HQueue to manage the collection of
     * not-yet-combined nodes as you build the tree.
     * <p>
     * Special cases:
     * - If freq contains no non-zero counts (empty input file), return null
     * - If freq contains exactly one non-zero count (input file has only
     * one unique character), return a single leaf HuffmanNode for that
     * character (no combine() call needed).
     */
    public static HuffmanNode createHuffTree(int[] freq) {

        HuffmanNode[] trees = new HuffmanNode[NUM_CHARS];
        int size = 0;

        // ordered list
        for (int ascii = 0; ascii < NUM_CHARS; ascii++) {

            if (freq[ascii] > 0) {
                HuffmanNode newTree = new HuffmanNode(ascii, freq[ascii], null, null);
                int index = size;

                // insert sort loop
                while (index > 0 && newTree.comesBefore(trees[index - 1])) {
                    trees[index] = trees[index - 1];
                    index--;
                }
                trees[index] = newTree;
                size++;

            }
        }
        if (size == 0) {
            return null;
        }

        while (size > 1) {
            HuffmanNode first = trees[0];
            HuffmanNode second = trees[1];

            for (int i = 0; i < size - 2; i++) {
                trees[i] = trees[i + 2];
            }
            size -= 2;

            HuffmanNode combined = (HuffmanNode) first.combine(second);
            int index = size;
            while (index > 0 && combined.comesBefore(trees[index - 1])) {
                trees[index] = trees[index - 1];
                index--;
            }

            trees[index] = combined;
            size++;
        }

        return trees[0];
    }

    /**
     * Traverses the Huffman tree rooted at root and builds the Huffman
     * code for every character that appears in it. Going left appends
     * '0' to the code, going right appends '1'.
     * <p>
     * Returns a String array of size NUM_CHARS, indexed by ASCII value.
     * Characters that do not appear in the tree have an empty string ""
     * at their index. The same is true of the lone character in a
     * single-node tree (the only-one-unique-character case) - it has no
     * left/right branch to build a code out of; huffmanEncode/huffmanDecode
     * handle that case entirely through the header.
     */
    public static String[] createCode(HuffmanTree root) {
        String[] codes = new String[NUM_CHARS];
        Arrays.fill(codes, "");
        fillCodes(root, "", codes);
        return codes;
    }

    //This takes a subtree, the path to it, and the code array
    //It stores each leaf's code in the array and doesn't return anything
    static void fillCodes(HuffmanTree root, String code, String[] codes) {
        if (root instanceof HuffmanLeaf) {
            codes[HuffmanTree.ascii(root)] = code;
        } else if (root instanceof HuffmanNode) {
            HuffmanNode node = (HuffmanNode) root;

            if (node.left() == null && node.right() == null) {

                codes[node.charAscii()] = code;
                return;
            }
            fillCodes(node.left(), code + "0", codes);
            fillCodes(node.right(), code + "1", codes);
        }
    }

    /**
     * Given a frequency array (as produced by cntFreq()), returns a
     * header string listing the ASCII values and their associated
     * frequencies, separated by single spaces, in ascending order of
     * ASCII value. Only characters with a non-zero count are included.
     * <p>
     * Example: for the text "aaabbbbcc" this returns "97 3 98 4 99 2".
     */
    public static String createHeader(int[] freq) {

        String asciiString = "";

        for (int ascii = 0; ascii < 256; ascii++) {
            if (freq[ascii] > 0) {
                if (!asciiString.equals("")) {
                    asciiString += " ";
                }
                asciiString += ascii + " " + freq[ascii];
            }
        }

        return asciiString;
    }

    /**
     * Reads the input text file inFile and writes to outFile:
     * 1. A header line (see createHeader), followed by a newline
     * 2. The Huffman-encoded contents of inFile, using the codes from
     * createCode() -- UNLESS inFile has only one unique character,
     * see below.
     * If outFile already exists, its old contents are overwritten.
     * <p>
     * Special cases:
     * - Empty input file: outFile should end up empty too
     * - Input file with only one unique character: should contain only the
     * header line (e.g. "97 5") and nothing else - no second line of code bits.
     */
    public static void huffmanEncode(String inFile, String outFile) throws IOException {

        int[] cntFreq = cntFreq(inFile);
        HuffmanTree root = createHuffTree(cntFreq(inFile));

        try (FileWriter output = new FileWriter(outFile)) {
            if (root == null) {
                return;
            }

            String headerLine = createHeader(cntFreq);
            output.write(headerLine);
            output.write("\n");
            String[] codes = createCode(root);

            try (FileInputStream input = new FileInputStream(inFile)) {
                int character = input.read();
                while (character != -1) {
//                    System.out.println(codes[character]);
                    output.write(codes[character]);
//                    output.write(codes[0]);
                    character = input.read();
                }
            }
        }
    }

    /**
     * Parses a header string (in the format produced by createHeader) and
     * returns the corresponding frequency array of size NUM_CHARS, indexed
     * by ASCII value, i.e. the same format returned by cntFreq().
     * <p>
     * Example: for the header "97 3 98 4 99 2", freq[97, 98, 99] == {3, 4, 2}.
     */
    // Converts headerString into a frequency array and returns it
    public static int[] parseHeader(String headerString) {
        int[] freq = new int[NUM_CHARS];

        if (!headerString.isEmpty()) {
            String[] values = headerString.split(" ");

            for (int i = 0; i < values.length; i += 2) {
                int ascii = Integer.parseInt(values[i]);
                int count = Integer.parseInt(values[i + 1]);

                freq[ascii] = count;
            }
        }

        return freq;
    }

    /**
     * Reads an encoded file (in the format written by huffmanEncode) and
     * writes the decoded text to decodeFile.
     * <p>
     * Recreate the frequency array from the header with parseHeader(),
     * rebuild the Huffman tree with createHuffTree(), then walk the tree
     * for each bit of the second line: '0' -> left child, '1' -> right
     * child. When a leaf is reached, emit its character and restart at
     * the root.
     * <p>
     * Special case: if the recreated tree is a single leaf (the
     * only-one-unique-character case), there is no second line to read
     * The header's count alone tells you how many times that character occurred,
     * so write it out that many times directly.
     */
    // Decodes encodedFile into decodeFile. Returns nothing
    public static void huffmanDecode(String encodedFile, String decodeFile) throws IOException {
        FileInputStream input = new FileInputStream(encodedFile);

        String header = "";
        int character = input.read();

        // Read the header until a newline or the end of the file.
        while (character != '\n' && character != -1) {
            header += (char) character;
            character = input.read();
        }

        int[] freq = parseHeader(header);
        HuffmanTree root = createHuffTree(freq);

        FileOutputStream output = new FileOutputStream(decodeFile);

        if (root instanceof HuffmanNode && ((HuffmanNode) root).left() == null && ((HuffmanNode) root).right() == null) {
            int ascii = HuffmanTree.ascii(root);
            int count = HuffmanTree.frequency(root);

            for(int i = 0; i < count; i++){
                output.write(ascii);
            }
        }
        else if(root instanceof HuffmanNode){
            HuffmanTree current = root;
            int bit = input.read();

            while(bit != -1){
                if(bit == '0' || bit == '1'){
                    HuffmanNode node = (HuffmanNode) current;

                    if(bit == '0') {
                        current = node.left();
                    }
                    else{
                        current = node.right();
                    }
                    if (current instanceof HuffmanNode && ((HuffmanNode) current).left() == null && ((HuffmanNode) current).right() == null){
                        output.write(HuffmanTree.ascii(current));
                        current = root;
                    }
                }
                bit = input.read();
            }
        }
        input.close();
        output.close();
    }
}