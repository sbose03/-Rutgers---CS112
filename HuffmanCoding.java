package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class contains methods which, when used together, perform the
 * entire Huffman Coding encoding and decoding process
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class HuffmanCoding {
    private String fileName;
    private ArrayList<CharFreq> sortedCharFreqList;
    private TreeNode huffmanRoot;
    private String[] encodings;

    /**
     * Constructor used by the driver, sets filename
     * DO NOT EDIT
     * @param f The file we want to encode
     */
    public HuffmanCoding(String f) { 
        fileName = f; 
    }

    /**
     * Reads from filename character by character, and sets sortedCharFreqList
     * to a new ArrayList of CharFreq objects with frequency > 0, sorted by frequency
     */
    public void makeSortedList() {

        StdIn.setFile(fileName);
        sortedCharFreqList = new ArrayList(); // sorted list
        int[] charList = new int[128]; // list of numbers
        int count=0;

        while (StdIn.hasNextChar()){
            char index = StdIn.readChar();
            ++charList[index];
            ++count; // creates list of numbers
        }
        for (char i=(char)0;i<charList.length;i++){
            if (charList[i]!=0){
                CharFreq charfreq = new CharFreq(i,(double)charList[i]/count);
                sortedCharFreqList.add(charfreq);
            }
        }
        if (sortedCharFreqList.size()==1){
            CharFreq fix = new CharFreq((char)((sortedCharFreqList.get(0).getCharacter()+1)%128), 0.0);
            sortedCharFreqList.add(fix);
        }
        Collections.sort(sortedCharFreqList); // sorts list

	/* Your code goes here */
    }

    /**
     * Uses sortedCharFreqList to build a huffman coding tree, and stores its root
     * in huffmanRoot
     */
    public static TreeNode combineNodes(TreeNode left, TreeNode right){
        double combineFreqNodes = left.getData().getProbOcc() + right.getData().getProbOcc();
        CharFreq combineCharFreqNodes = new CharFreq(null, combineFreqNodes);
        TreeNode newNode = new TreeNode(combineCharFreqNodes, left, right);
        return newNode;
    }
    public static TreeNode dequeueSmall(Queue<TreeNode> node1, Queue<TreeNode> node2){
        TreeNode first = null;
        TreeNode second = null;
        if(!node1.isEmpty()){
            first = node1.peek();
        }
        if(!node2.isEmpty()){
            second = node2.peek();
        }
        TreeNode result;
        if (first != null && second != null){
            if (first.getData().getProbOcc()<=second.getData().getProbOcc())
                result = node1.dequeue();
            else
                result = node2.dequeue();
        }
        else if (first != null)
            result = node1.dequeue();
        else if (second != null)
            result= node2.dequeue();
        else
            result = null;
        return result;
    }
    public void makeTree() {
        Queue<TreeNode> tree= new Queue<>(), destination = new Queue<TreeNode>();

        for(CharFreq CharFreq: sortedCharFreqList) 
            tree.enqueue(new TreeNode(CharFreq, null, null));

        TreeNode left, right;
        while (!tree.isEmpty()) {
            left = dequeueSmall(tree, destination);
            right = dequeueSmall(tree, destination);
            if (left != null && right != null) {
                TreeNode combined_node = combineNodes(left, right);
                destination.enqueue(combined_node);
            }
            else if (left != null)
                destination.enqueue(left);
            else
                destination.enqueue(right);
        }
        
        while (destination.size() > 1){
            left = destination.dequeue();
            right = destination.dequeue();
            TreeNode combined_node = combineNodes(left, right);
            destination.enqueue(combined_node);
        }
        huffmanRoot = destination.dequeue();
	/* Your code goes here */
    }

    /**
     * Uses huffmanRoot to create a string array of size 128, where each
     * index in the array contains that ASCII character's bitstring encoding. Characters not
     * present in the huffman coding tree should have their spots in the array left null.
     * Set encodings to this array.
     */
    private void order(TreeNode node, String[] codeList, ArrayList<String> bitList){
        if (node.getData().getCharacter()!=null) {
            codeList[node.getData().getCharacter()]=String.join("", bitList);
            bitList.remove(bitList.size()-1);
            return;
        }

        if (node.getLeft()!=null){
            bitList.add("0");
        }

        order(node.getLeft(), codeList, bitList);
        if (node.getRight()!=null){
            bitList.add("1");
        }

        order(node.getRight(), codeList, bitList);
        if (!bitList.isEmpty()){
            bitList.remove(bitList.size()-1);
        }
    }
    public void makeEncodings() {

	/* Your code goes here */
    ArrayList<String> bitList = new ArrayList<>();
    String[] codeList = new String[128];
    order(huffmanRoot, codeList, bitList);
    encodings = codeList;
    }

    /**
     * Using encodings and filename, this method makes use of the writeBitString method
     * to write the final encoding of 1's and 0's to the encoded file.
     * 
     * @param encodedFile The file name into which the text file is to be encoded
     */

 

    public void encode(String encodedFile) {
        StdIn.setFile(fileName);
        String a = new String();
        char b = ' ';

        String c = "";
        while(StdIn.hasNextChar())
        {
            b = StdIn.readChar();
            c = encodings[b];
            a = a + c;
        }
        writeBitString(encodedFile, a);


	/* Your code goes here */
    }
    
    /**
     * Writes a given string of 1's and 0's to the given file byte by byte
     * and NOT as characters of 1 and 0 which take up 8 bits each
     * DO NOT EDIT
     * 
     * @param filename The file to write to (doesn't need to exist yet)
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
        for (int i = 0; i < padding-1; i++) pad = pad + "0";
        pad = pad + "1";
        bitString = pad + bitString;

        // For every bit, add it to the right spot in the corresponding byte,
        // and store bytes in the array when finished
        for (char c : bitString.toCharArray()) {
            if (c != '1' && c != '0') {
                System.out.println("Invalid characters in bitstring");
                return;
            }

            if (c == '1') currentByte += 1 << (7-byteIndex);
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
        }
        catch(Exception e) {
            System.err.println("Error when writing to file!");
        }
    }

    /**
     * Using a given encoded file name, this method makes use of the readBitString method 
     * to convert the file into a bit string, then decodes the bit string using the 
     * tree, and writes it to a decoded file. 
     * 
     * @param encodedFile The file which has already been encoded by encode()
     * @param decodedFile The name of the new file we want to decode into
     */
    public void decode(String encodedFile, String decodedFile) {
        StdOut.setFile(decodedFile);
        String a = new String();
        String b = readBitString(encodedFile);
       
        TreeNode c = new TreeNode();
        for(int i = 0; i<b.length(); i++)
        {
            if(b.charAt(i) == '0')
            {
                c = c.getLeft();
            }
            else if(b.charAt(i) == '1')
            {
                c = c.getRight();
            }
            if(c.getData().getCharacter() != null )
            {
                a = a + c.getData().getCharacter();
            }
            StdOut.print(a);
        }        

	/* Your code goes here */
    }

    /**
     * Reads a given file byte by byte, and returns a string of 1's and 0's
     * representing the bits in the file
     * DO NOT EDIT
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
                bitString = bitString + 
                String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }

            // Detect the first 1 signifying the end of padding, then remove the first few
            // characters, including the 1
            for (int i = 0; i < 8; i++) {
                if (bitString.charAt(i) == '1') return bitString.substring(i+1);
            }
            
            return bitString.substring(8);
        }
        catch(Exception e) {
            System.out.println("Error while reading file!");
            return "";
        }
    }

    /*
     * Getters used by the driver. 
     * DO NOT EDIT or REMOVE
     */

    public String getFileName() { 
        return fileName; 
    }

    public ArrayList<CharFreq> getSortedCharFreqList() { 
        return sortedCharFreqList; 
    }

    public TreeNode getHuffmanRoot() { 
        return huffmanRoot; 
    }

    public String[] getEncodings() { 
        return encodings; 
    }
}
