/* RowNode.java
	Ryan Russell
	V00873387
	CSC 225 Summer 2018
	June 14, 2018

	This file outlines the declaration of the RowNode for use in a list within Aggregate.java.
*/

public class RowNode {

    RowNode next;
    RowNode prev;
    String[] group_data;
    String agg_data;

    public RowNode(int n) {
        next = null;
        group_data = new String[n];
        agg_data = "";
    }

    public RowNode(RowNode nxt, int n) {
        next = nxt;
        group_data = new String[n];
        agg_data = "";
    }
}