/* RowLinkedList.java
	Ryan Russell
	V00873387
	CSC 225 Summer 2018
	June 14, 2018

	This file outlines the declaration of the RowLinkedList which implements a list within Aggregate.java.
*/

public class RowLinkedList {
	RowNode head;
    RowNode tail;
    int count = 0;

    public RowLinkedList() {
        head = null;
        tail = null;
        count = 0;
    }

    public void add(String[] group_data, String agg_data, int input_size) {
		RowNode n = new RowNode(input_size);
		for (int i = 0; i < group_data.length; i++) {
			n.group_data[i] = group_data[i];
		}
        n.agg_data = agg_data;
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