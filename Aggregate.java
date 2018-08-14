/* Aggregate.java
	Ryan Russell
	V00873387
	CSC 225 Summer 2018
	June 10, 2018

	This program reads a table in csv format from an input file and performs grouping
	and aggregation using one of the four functions: count, sum, avg, and count_distinct. 
	The code for reading from a csv file was adopted from Bill Bird's assignment outline.
*/


import java.io.*;

public class Aggregate{

	
	public static void showUsage() {
		System.err.printf("Usage: java Aggregate <function> <aggregation column> <csv file> <group column 1> <group column 2> ...\n");
		System.err.printf("Where <function> is one of \"count\", \"count_distinct\", \"sum\", \"avg\"\n");	
	}


	// The compareArrays method takes two arrays as input and returns true if the arrays are exactly the same.
	// It returns false otherwise. This method was adapted from the solution to:
	// https://stackoverflow.com/questions/14897366/comparing-two-integer-arrays-in-java
	public static boolean compareArrays(String[] array1, String[] array2) {
		
		if (array1 != null && array2 != null) {
			
			for (int i = 0; i < array1.length; i++) {
				if (array1[i] == null) {
					break;
				}
				if (!(array1[i].equals(array2[i]))) {
					return false;
				}
			}
			
		} else {
			return false;
		}
		return true;
	}

	// The aggregation method performs all four aggregation functions: count, sum, avg, count_distinct
	// depending on the command line input. The aggregation function runs in O(n^2) in the worst case.
	// The worst case actually occurs in two different sets of input. Firstly, if each of the permutations
	// of grouping columns in unique, there will end up being n nodes in group_list, therefore it is n^2.
	// Secondly, if every row contains the exact same permutation of grouping columns, there will end up 
	// being n elements in g.aggregate[] of the only node g, therefore it is also n^2.
	public static void aggregation(RowLinkedList row_list, int n, String agg_function) {

		// Initializes a new list for the contents of each distinct grouping in the csv.
		GroupingLinkedList group_list = new GroupingLinkedList();

		// Initialization of variables used for the computation and arrangement of the aggregation.
		boolean is_added = false;
		boolean is_duplicate = false;
		int sum = 0;
		float avg = 0;
		float divisor = 0;
		int agg_value; // The value of the aggregate data as type int.

		// Set a pointer to the beginning of each list.
		RowNode r = row_list.head;
		GroupingNode g = group_list.head;

		// This code block loops through the row_list, where each node contains data for one 
		// row in the csv, and the group_list, where each node contains data for each distinct 
		// permutation of the grouping columns along with the aggregation column data for that
		// permutation. The group data for each node is compared and added to a group_list node.
		while (r != null) {
			while (g != null) {

				// If the grouping permutation exists in group_list, add the aggregation 
				// data to the existing node.
				if (compareArrays(r.group_data, g.grouping) == true) {
					for (int i = 0; i < g.agg_count; i++) {
						if (g.aggregate[i].equals(r.agg_data)) {
							is_duplicate = true;
						}
					}
					g.aggregate[g.agg_count++] = r.agg_data;

					if (is_duplicate == false) {
						g.agg_count_distinct++;
					}

					is_duplicate = false;
					is_added = true;
					break;
				}
				g = g.next;
			}

			// If the aggregation data is new, create a new node with the aggregation data.
			if (is_added == false) {
				group_list.add(r.group_data, r.agg_data, 1, n);
			}
			is_added = false;

			g = group_list.head;
			r = r.next;
		}

		// Reset the pointer to the beginning of the group_list.
		g = group_list.head;

		// This code block prints out the required data corresponding to the command line input.
		// For each node in group_list, the grouping data is printed verbatim, followed by 
		// the aggregation result in the final column. Each of the four if blocks computes 
		// one of the four aggregation functions.
		while (g != null) {

			for (int i = 0; i < g.grouping.length; i++) {
				if (g.grouping[i] == null) {
					break;
				}
				System.out.printf(g.grouping[i] + ",");
			}

			if (agg_function.equals("count")) {

				System.out.println(g.agg_count);

			} else if (agg_function.equals("sum")) {

				for (int j = 0; j < g.agg_count; j++) {
					agg_value = Integer.parseInt(g.aggregate[j]);
					sum = sum + agg_value;
				}

				System.out.println(sum);

				sum = 0;

			} else if (agg_function.equals("avg")) {

				for (int j = 0; j < g.agg_count; j++) {
					agg_value = Integer.parseInt(g.aggregate[j]);
					sum = sum + agg_value;
				}

				divisor = g.agg_count;
				avg = sum/divisor;
				System.out.printf("%.2f\n", avg);

				sum = 0;
				avg = 0;

			} else if (agg_function.equals("count_distinct")) {
				
				System.out.println(g.agg_count_distinct);

			}
			g = g.next;
		}
	}
	

	public static void main(String[] args) {
		
		// At least four arguments are needed.
		if (args.length < 4){
			showUsage();
			return;
		}

		// Set variables for the input information.
		String agg_function = args[0];
		String agg_column = args[1];
		String csv_filename = args[2];
		String[] group_columns = new String[args.length - 3];

		// Ensures that the input for the aggregate column is not also an input for a grouping column.
		for(int i = 3; i < args.length; i++) {
			group_columns[i-3] = args[i];
			if (args[i].equals(agg_column)) {
				System.err.println("Error: a column cannot be both the aggregation column and a grouping column.");
				showUsage();
				return;
			}
		}
		
		// Ensures that the input for the aggregate function is valid.
		if (!agg_function.equals("count") && !agg_function.equals("count_distinct") && !agg_function.equals("sum") && !agg_function.equals("avg")){
			showUsage();
			return;
		}

		BufferedReader br = null;
		
		try{
			br = new BufferedReader(new FileReader(csv_filename));
		}catch( IOException e ){
			System.err.printf("Error: Unable to open file %s\n",csv_filename);
			return;
		}
		
		String header_line;
		try{
			header_line = br.readLine(); //The readLine method returns either the next line of the file or null (EOF)
		} catch (IOException e){
			System.err.printf("Error reading file\n", csv_filename);
			return;
		}
		if (header_line == null){
			System.err.printf("Error: CSV file %s has no header row\n", csv_filename);
			return;
		}
		
		// Split the header_line string into an array of string values using a comma as the separator.
		String[] column_names = header_line.split(",");

		// This block of code ensures that each input for grouping column is actually a valid input.
		// This is done by comparing each grouping column to the column headers in the csv. 
		// If all column headers are checked and there are no matches, an error message is shown to the user.
		boolean is_valid = true; 
		for (int i = 0; i < group_columns.length; i++) {
			for (int j = 0; j < column_names.length; j++) {
				if (column_names[j].equals(group_columns[i])) {
					break;
				}
				if (j == column_names.length - 1) {
					is_valid = false;
				}
			}
			if (is_valid == false) {
				System.err.println("Error: the input included an invalid grouping column.");
				showUsage();
				return;
			}
		}

		// Initialize strings and arrays to be used for parsing of the csv data.
		String line = "";
		String agg_data = "";
		String[] current_row = new String[column_names.length];
		String[] output = new String[group_columns.length];
		int output_count = 0;
		int n = 0; // The input size n.

		// Prints out the grouping column headers.
		for (int i = 0; i < group_columns.length; i++) {
			System.out.printf(group_columns[i] + ",");
		}

		// TEMPORARY: print out the aggregation column header.
		System.out.printf(agg_function + "(" + agg_column + ")" + "\n" );

		// Initializes a new list for the contents of each row in the csv.
		RowLinkedList row_list = new RowLinkedList();

		// This code block reads the csv file line by line (row by row) until the end of the file is reached.
		while (line != null) {
			try{
				line = br.readLine(); 
			} catch (IOException e) {
				System.err.printf("Error reading file\n", csv_filename);
				return;
			}

			// Increments the input size n every time a new line is read.
			n++;

			// If the current line is not the end of the file, the data is split into a list.
			// If the data is relevant (in a grouping column), it is added to the output array.
			if (line != null && line.trim().replaceAll(",", "").length() > 0) {
				current_row = line.split(",");
				for (int i = 0; i < group_columns.length; i++) {
					for (int j = 0; j < column_names.length; j++) {
						if (group_columns[i].equals(column_names[j])) {
							output[output_count++] = current_row[j];
						} 
						if (column_names[j].equals(agg_column)) {
							agg_data = current_row[j]; 
						}
					}
				}

				// Add a new node to the row_list with the current output for the grouping column data,
				// the aggregation column data, and the input size up to this point.
				// NOTICE: Since n here has not fully incremented to include all rows in the csv, the 
				// n in each node will not actually be the correct input size until the last node.
				row_list.add(output, agg_data, group_columns.length);
			}
			output_count = 0; // Reset the size of the output array.
		}
		// Passes the row_list in and performs the aggregation.
		aggregation(row_list, n, agg_function);
	}
}