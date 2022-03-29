package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class contains methods which, when used together, perform the entire
 * Huffman Coding encoding and decoding process
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class HuffmanCoding {
    /**
     * Writes a given string of 1's and 0's to the given file byte by byte and NOT
     * as characters of 1 and 0 which take up 8 bits each
     * 
     * @param filename  The file to write to (doesn't need to exist yet)
     * @param bitString The string of 1's and 0's to write to the file in bits
     */
    public static void writeBitString(String filename, String bitString) {
        byte[] bytes = new byte[bitString.length() / 8 + 1];
        int bytesIndex = 0, byteIndex = 0, currentByte = 0;

        // Pad the string with initial zeroes and then a one in order to bring
        // its length to a multiple of 8. When reading, the 1 signifies the
        // end of padding.
        int padding = 8 - (bitString.length() % 8);
        String pad = "";
        for (int i = 0; i < padding - 1; i++)
            pad = pad + "0";
        pad = pad + "1";
        bitString = pad + bitString;

        // For every bit, add it to the right spot in the corresponding byte,
        // and store bytes in the array when finished
        for (char c : bitString.toCharArray()) {
            if (c != '1' && c != '0') {
                System.out.println("Invalid characters in bitstring");
                System.exit(1);
            }

            if (c == '1')
                currentByte += 1 << (7 - byteIndex);
            byteIndex++;

            if (byteIndex == 8) {
                bytes[bytesIndex] = (byte) currentByte;
                bytesIndex++;
                currentByte = 0;
                byteIndex = 0;
            }
        }

        // Write the array of bytes to the provided file
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(bytes);
            out.close();
        } catch (Exception e) {
            System.err.println("Error when writing to file!");
        }
    }

    /**
     * Reads a given file byte by byte, and returns a string of 1's and 0's
     * representing the bits in the file
     * 
     * @param filename The encoded file to read from
     * @return String of 1's and 0's representing the bits in the file
     */
    public static String readBitString(String filename) {
        String bitString = "";

        try {
            FileInputStream in = new FileInputStream(filename);
            File file = new File(filename);

            byte bytes[] = new byte[(int) file.length()];
            in.read(bytes);
            in.close();

            // For each byte read, convert it to a binary string of length 8 and add it
            // to the bit string
            for (byte b : bytes) {
                bitString = bitString + String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }

            // Detect the first 1 signifying the end of padding, then remove the first few
            // characters, including the 1
            for (int i = 0; i < 8; i++) {
                if (bitString.charAt(i) == '1')
                    return bitString.substring(i + 1);
            }

            return bitString.substring(8);
        } catch (Exception e) {
            System.out.println("Error while reading file!");
            return "";
        }
    }

    /**
     * Reads a given text file character by character, and returns an arraylist of
     * CharFreq objects with frequency > 0, sorted by frequency
     * 
     * @param filename The text file to read from
     * @return Arraylist of CharFreq objects, sorted by frequency
     */
    public static ArrayList<CharFreq> makeSortedList(String filename) {
        StdIn.setFile(filename);
        /* Your code goes here */
        ArrayList<CharFreq> arr = new ArrayList<CharFreq>();
        int[] charOcc = new int[128];
        CharFreq newChar;
        double total = 0;
        int counter = 0;
        int iSave = 0;
        while (StdIn.hasNextChar()) {
            charOcc[StdIn.readChar()]++;
            total++;
        }
        for (int i = 0; i < charOcc.length; i++) {
            if (charOcc[i] > 0) {
                newChar = new CharFreq((char) i, charOcc[i] / total);
                arr.add(newChar);
                counter++;
                iSave = i;
            }
        }
        if (counter == 1) {
            CharFreq newChar2 = new CharFreq((char) (iSave + 1), 0);
            arr.add(newChar2);
        }
        Collections.sort(arr);
        return arr;
        // ADD THE EDGE CASE OF IF THERES ONLY ONE
    }

    public static TreeNode makeTree(ArrayList<CharFreq> sortedList) {
        /* Your code goes here */
        Queue<CharFreq> source = new Queue<CharFreq>();
        Queue<TreeNode> target = new Queue<TreeNode>();
        for (int i = 0; i < sortedList.size(); i++)
            source.enqueue(sortedList.get(i));
        while (!source.isEmpty() || target.size() > 1) {
            TreeNode newNode1 = recHelp(source, target);
            TreeNode newNode2 = recHelp(source, target);
            TreeNode newNode3 = new TreeNode(
                    new CharFreq(null, newNode1.getData().getProbOccurrence() + newNode2.getData().getProbOccurrence()),
                    newNode1, newNode2);
            target.enqueue(newNode3);
        }
        return target.dequeue();

    }

    private static TreeNode recHelp(Queue<CharFreq> source, Queue<TreeNode> target) {
        if (target.size() == 0 && !source.isEmpty()) {
            return new TreeNode(source.dequeue(), null, null);
        }
        if (source.isEmpty()){
            return target.dequeue();
        }
        if (target.size() == 1) {
            if (source.peek().getProbOccurrence() <= (target.peek().getData().getProbOccurrence())){
                return new TreeNode(source.dequeue(), null, null);
            }
            else{
                return target.dequeue();
            }
        }
        if (source.peek().getProbOccurrence() <= (target.peek().getData().getProbOccurrence())){
            return new TreeNode(source.dequeue(), null, null);
        }
        else{
            return target.dequeue();
        }
    }

    /**
     * Uses a given huffman coding tree to create a string array of size 128, where
     * each index in the array contains that ASCII character's bitstring encoding.
     * Characters not present in the huffman coding tree should have their spots in
     * the array left null
     * 
     * @param root The root of the given huffman coding tree
     * @return Array of strings containing only 1's and 0's representing character
     *         encodings
     */

    public static String[] makeEncodings(TreeNode root) {
        /* Your code goes here */
        String[] arr = new String[128];
        return makeEncodings(root, "", arr); // Delete this line
    }
    
    private static String[] makeEncodings(TreeNode root, String s, String[] array) {
        /* Your code goes here */
        if (root.getLeft() == null && root.getRight() == null) {
            array[(int) root.getData().getCharacter()] = s;
        } else {
            if (root.getLeft() != null) {
                makeEncodings(root.getLeft(), s + "0", array);
            }
            if (root.getRight() != null) {
                makeEncodings(root.getRight(), s + "1", array);
            }
        }
        return array; // Delete this line
    }

    /**
     * Using a given string array of encodings, a given text file, and a file name
     * to encode into, this method makes use of the writeBitString method to write
     * the final encoding of 1's and 0's to the encoded file.
     * 
     * @param encodings   The array containing binary string encodings for each
     *                    ASCII character
     * @param textFile    The text file which is to be encoded
     * @param encodedFile The file name into which the text file is to be encoded
     */
    public static void encodeFromArray(String[] encodings, String textFile, String encodedFile) {
        StdIn.setFile(textFile);
        /* Your code goes here */
        String code = "";
        while (StdIn.hasNextChar()) {
            int counter = 0;
            char nextChar = StdIn.readChar();
            while (!((int) (nextChar) == counter)) {
                counter++;
                if (counter == 129)
                    counter = 0;
            }
            code += encodings[counter];
        }
        writeBitString(encodedFile, code);
    }

    /**
     * Using a given encoded file name and a huffman coding tree, this method makes
     * use of the readBitString method to convert the file into a bit string, then
     * decodes the bit string using the tree, and writes it to a file.
     * 
     * @param encodedFile The file which contains the encoded text we want to decode
     * @param root        The root of your Huffman Coding tree
     * @param decodedFile The file which you want to decode into
     */
    public static void decode(String encodedFile, TreeNode root, String decodedFile) {
        StdOut.setFile(decodedFile);
        String code = readBitString(encodedFile);
        /* Your code goes here */
        while (code.length() > 0) {
            TreeNode temp = root;
            while (!(temp.getLeft() == null && temp.getRight() == null)) {
                if (code.substring(0, 1).equals("0"))
                    temp = temp.getLeft();
                else
                    temp = temp.getRight();
                code = code.substring(1);
            }
            StdOut.print(temp.getData().getCharacter());
        }
    }
}
