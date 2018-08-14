/* GroupingLinkedList.java
	Ryan Russell
	V00873387
	CSC 225 Summer 2018
	June 14, 2018

	This file outlines the declaration of the GroupingLinkedList which implements lists within Aggregate.java.
*/

public class GroupingLinkedList {

    GroupingNode head;
    GroupingNode tail;
    int count = 0;

    public GroupingLinkedList() {
        head = null;
        tail = null;
        count = 0;
    }

    public void add(String[] grouping, String agg_data, int agg_count, int input_size) {
        GroupingNode n = new GroupingNode(input_size);
        for (int i = 0; i < grouping.length; i++) {
            n.grouping[i] = grouping[i];
        }
        n.aggregate[0] = agg_data;
        n.agg_count = agg_count;
        n.agg_count_distinct = agg_count;
        n.prev = null;
        n.next = head;
        head = n;
        
        if (count == 0) {
            tail = n;
        } else {
            n.next.prev = n;
        }

        count++;
    }

    public void clear()	
	{
		head = null;
		tail = null;
		count = 0;
	}
}