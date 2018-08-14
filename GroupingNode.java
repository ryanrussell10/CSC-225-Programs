/* GroupingNode.java
	Ryan Russell
	V00873387
	CSC 225 Summer 2018
	June 14, 2018

	This file outlines the declaration of the GroupingNode for use in a list within Aggregate.java.
*/

public class GroupingNode {

    GroupingNode next;
    GroupingNode prev;
    String[] grouping;
    String[] aggregate;
    int agg_count;
    int agg_count_distinct;

    public GroupingNode(int n) {
        next = null;
        grouping = new String[n];
        aggregate = new String[n];
        agg_count = 0;
        agg_count_distinct = 0;
    }

    public GroupingNode(GroupingNode nxt, int n) {
        next = nxt;
        grouping = new String[n];
        aggregate = new String[n];
        agg_count = 0;
        agg_count_distinct = 0;
    }
}