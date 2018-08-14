/* Ladder.java
  	Ryan Russell
	V00873387
	CSC 225 Summer 2018
	July 28, 2018

   Ladder.java contains a program that takes an input of n words and specified start and end words.
   If there is a ladder between these words that can be formed by changing only one character in 
   each step of the ladder, then this path is returned. To do this, the input is modelled as a graph
   and a modified BFS is performed on the graph. Starter code was designed by Bill Bird.
*/


import java.io.*;
import java.util.*;

public class Ladder{

	
	public static void showUsage(){
		System.err.printf("Usage: java Ladder <word list file> <start word> <end word>\n");
	}


	// The compareStrings method takes two strings and returns true if the strings differ by exactly
	// one character, and false otherwise.
	public static boolean compareStrings(String s1, String s2) {
		
		int differs = 0; // Stores the amount of characters each word differs by.

		// If the words are equivalent, they do not meet the word ladder requirements.
		if (s1.equals(s2)) {
			return false;
		}

		// Checks to see if each character differs by at most one character.
		// If not, the method returns false.
		if (s1.length() == s2.length()) {
			for (int i = 0; i < s1.length(); i++) {
				if (s1.charAt(i) != s2.charAt(i)) {
					differs++;
					if (differs > 1) {
						return false;	
					}
				}
			}
		} else {
			return false;
		}

		return true;
	}


	// The BFS method takes as input the start and end words as well as the adjacency list for the graph.
	// Since the graph is modelled using an adjacency list, the running time of BFS is O(n + m), where n 
	// is the number of vertices (words) in the graph and m is the number of edges. 
	public static LinkedList<Integer> BFS(int n, int start_word_index, int end_word_index, ArrayList<LinkedList<Integer>> adj_list) {

		// Initialize LinkedLists, Queue and visited array.
		Queue<LinkedList<Integer>> Q = new LinkedList<LinkedList<Integer>>();
		boolean[] visited = new boolean[n];
		LinkedList<Integer> cur_ladder;
		LinkedList<Integer> ladder = new LinkedList<Integer>();
		ladder.add(start_word_index);

		// Add the initial ladder with only the start word and mark it as visited.
		Q.add(ladder);
		visited[start_word_index] = true;

		while (!Q.isEmpty()) {
			cur_ladder = Q.remove();

			if (cur_ladder.getLast() == end_word_index) {
				return cur_ladder;
			}

			// This code block is O(deg(word)) each time it runs.
			for (Integer neighbour : adj_list.get(cur_ladder.getLast())) {
				if (visited[neighbour] == false) {
					visited[neighbour] = true;
					cur_ladder.add(neighbour);
					Q.add(cur_ladder);
					cur_ladder = new LinkedList<Integer>(cur_ladder);
					cur_ladder.removeLast();
				}
			}
		}
		return null;
	}
	

	public static void main(String[] args){
		
		//At least four arguments are needed
		if (args.length < 3){
			showUsage();
			return;
		}
		String wordListFile = args[0];
		String startWord = args[1].trim();
		String endWord = args[2].trim();
		
		
		//Read the contents of the word list file into a LinkedList (requires O(nk) time for
		//a list of n words whose maximum length is k).
		//(Feel free to use a different data structure)
		BufferedReader br = null;
		LinkedList<String> words = new LinkedList<String>();
		
		try{
			br = new BufferedReader(new FileReader(wordListFile));
		}catch( IOException e ){
			System.err.printf("Error: Unable to open file %s\n",wordListFile);
			return;
		}
		
		try{
			for (String nextLine = br.readLine(); nextLine != null; nextLine = br.readLine()){
				nextLine = nextLine.trim();
				if (nextLine.equals(""))
					continue; //Ignore blank lines
				//Verify that the line contains only lowercase letters
				for(int ci = 0; ci < nextLine.length(); ci++){
					//The test for lowercase values below is not really a good idea, but
					//unfortunately the provided Character.isLowerCase() method is not
					//strict enough about what is considered a lowercase letter.
					if ( nextLine.charAt(ci) < 'a' || nextLine.charAt(ci) > 'z' ){
						System.err.printf("Error: Word \"%s\" is invalid.\n", nextLine);
						return;
					}
				}
				words.add(nextLine);
			}
		} catch (IOException e){
			System.err.printf("Error reading file\n");
			return;
		}

		/* Find a word ladder between the two specified words. Ensure that the output format matches the assignment exactly. */
		
		// If the words are of different lengths, there is no word ladder.
		if (startWord.length() != endWord.length()) {
			System.out.printf("No word ladder found.");
			return;
		}

		// Checks if the provided start and end words are within the input.
		boolean start_valid = false;
		boolean end_valid = false;
		for (String word : words) {
			if (word.equals(startWord)) {
				start_valid = true;
			}
			if (word.equals(endWord)) {
				end_valid = true;
			}
		}

		// If one of the words isn't in the input, there is no word ladder.
		if (start_valid == false || end_valid == false) {
			System.out.printf("No word ladder found.");
			return;
		}

		int n = words.size();
		int i = 0;
		int j = 0;

		// adj_list is an ArrayList of LinkedLists that serves as the adjacency list to represent
		// the input words as a graph. Each index of adj_list has a LinkedList of integers (edges) 
		// that correspond to the indices of each word in the words LinkedList.
		ArrayList<LinkedList<Integer>> adj_list = new ArrayList<LinkedList<Integer>>();
		for (String word : words) {
			adj_list.add(new LinkedList<Integer>());
		}

		// This graph formation process is currently O(n^2). 
		for (String word1 : words) {
   			for (String word2 : words) {
   				if (compareStrings(word1, word2) == true) {
   					adj_list.get(i).add(j);	
   				}
   				j++;
   			}
   			j = 0;
   			i++;
		}

		// Printing the contents of the adjacency list.
		/*for (i = 0; i < adj_list.size(); i++) {
			System.out.println("index: " + i + " word: " + words.get(i));
			for (j = 0; j < adj_list.get(i).size(); j++) {
				System.out.println(words.get(adj_list.get(i).get(j)));
			}
		}*/

		LinkedList<Integer> ladder = BFS(n, words.indexOf(startWord), words.indexOf(endWord), adj_list);

		if (ladder == null) {
			System.out.printf("No word ladder found.");
			return;
		}

		for (Integer step : ladder) {
			System.out.println(words.get(step));
		}
	}
}