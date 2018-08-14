/* NiceSimulator.java
  	Ryan Russell
	V00873387
	CSC 225 Summer 2018
	June 30, 2018

   NiceSimulator is a data structure that includes a variety of operations that can
   be called by Nice.java. The two programs effectively simulate basic CPU scheduling
   using nice values as the basis for priority. The data structure implementation 
   is an array-based heap. The structure of this program was designed by Bill Bird.
*/


import java.io.*;

public class NiceSimulator{

	public static final int SIMULATE_IDLE = -2;
	public static final int SIMULATE_NONE_FINISHED = -1;
	
	// Array to hold indicators that something an index exists within the heap.
	int[] indices;

	// 2D heap array where the second dimension includes:
	// taskID (index 0), nice value(index 1), time requirement(index 2), remaining time(index 3).
	int[][] heap;
	int heap_size = 0;

	/* Constructor(maxTasks)
	   Instantiate the data structure with the provided maximum 
	   number of tasks. No more than maxTasks different tasks will
	   be simultaneously added to the simulator, and additionally
	   you may assume that all task IDs will be in the range
	     0, 1, ..., maxTasks - 1
	*/
	public NiceSimulator(int maxTasks){	

		// Set the size of the arrays to maxTasks.
		indices = new int[maxTasks + 1];
		heap = new int[maxTasks + 1][4];
	}
	
	/* taskValid(taskID)
	   Given a task ID, return true if the ID is currently
	   in use by a valid task (i.e. a task with at least 1
	   unit of time remaining) and false otherwise.
	   
	   Note that you should include logic to check whether 
	   the ID is outside the valid range 0, 1, ..., maxTasks - 1
	   of task indices.
	*/
	public boolean taskValid(int taskID){

		if (taskID < 0 || taskID > heap.length - 1) {
			return false;
		}

		if (indices[taskID] > 0) {
			return true;
		}

		return false;
	}
	
	/* getPriority(taskID)
	   Return the current priority value for the provided
	   task ID. You may assume that the task ID provided
	   is valid.
	*/
	public int getPriority(int taskID){

		int index = indices[taskID];

		int priority = heap[index][1];

		return priority;
	}
	
	/* getRemaining(taskID)
	   Given a task ID, return the number of timesteps
	   remaining before the task completes. You may assume
	   that the task ID provided is valid.
	*/
	public int getRemaining(int taskID){

		int index = indices[taskID];

		int time_remaining = heap[index][3];

		return time_remaining;
	}
	
	
	/* add(taskID, time_required)
	   Add a task with the provided task ID and time requirement
	   to the system. You may assume that the provided task ID is in
	   the correct range and is not a currently-active task.
	   The new task will be assigned nice level 0.
	*/
	public void add(int taskID, int time_required){
		
		heap_size++;
		heap[heap_size][0] = taskID; 		// taskID
		heap[heap_size][1] = 0;				// nice value
		heap[heap_size][2] = time_required;	// time requirement
		heap[heap_size][3] = time_required;	// remaining time

		// Allows for quick lookup of taskID to determine if the task exists.
		indices[taskID] = heap_size;

		// O(logn) in the worst case, since the rest of this method is O(1).
		bubbleUp(heap_size);
	}
	
	
	/* kill(taskID)
	   Delete the task with the provided task ID from the system.
	   You may assume that the provided task ID is in the correct
	   range and is a currently-active task.
	*/
	public void kill(int taskID){

		int index = indices[taskID];
		indices[taskID] = 0;

		// Sets the last task in the heap to the location where
		// the killed task was.
		heap[index][0] = heap[heap_size][0];
		heap[index][1] = heap[heap_size][1];
		heap[index][2] = heap[heap_size][2];
		heap[index][3] = heap[heap_size][3];

		// Properly set the index of the task that got swapped
		// from the bottom of the heap.
		indices[heap[heap_size][0]] = index;

		// Reset the values of the last task in the heap to 0.
		heap[heap_size][0] = 0;
		heap[heap_size][1] = 0;
		heap[heap_size][2] = 0;
		heap[heap_size][3] = 0;

		// If the task being killed is located at the end of the heap,
		// it will not be swapped while bubbling down, so it needs to 
		// be reset to 0 within the indices array.
		if (index == heap_size) {
			indices[taskID] = 0;
		}

		heap_size--;

		// O(logn) in the worst case, since the rest of this method is O(1).
		bubbleDown(index); 
	}		
	
	
	/* renice(taskID, new_priority)
	   Change the priority of the the provided task ID to the new priority
       value provided. The change must take effect at the next simulate() step.
	   You may assume that the provided task ID is in the correct
	   range and is a currently-active task.
	*/
	public void renice(int taskID, int new_priority){

		int index = indices[taskID];

		heap[index][1] = new_priority;

		// Checks to see if the task has a lower nice value (and taskID) than 
		// its parent. If it does, bubble up. If it doesn't, bubble down.
		// Either way, the running time is O(logn) in the worst case since renice
		// only calls one of bubbleUp or bubbleDown.
		if (heap[index][1] < heap[parentIndex(index)][1]) {
			bubbleUp(index);
		} else if (heap[index][1] > heap[parentIndex(index)][1]) {
			bubbleDown(index);
		} else {
			if (heap[index][0] < heap[parentIndex(index)][0]) {
				bubbleUp(index);
			} else if (heap[index][0] > heap[parentIndex(index)][0]) {
				bubbleDown(index);
			}
		} 

	}

	
	/* simulate()
	   Run one step of the simulation:
		 - If no tasks are left in the system, the CPU is idle, so return
		   the value SIMULATE_IDLE.
		 - Identify the next task to run based on the criteria given in the
		   specification (tasks with the lowest priority value are ranked first,
		   and if multiple tasks have the lowest priority value, choose the 
		   task with the lowest task ID).
		 - Subtract one from the chosen task's time requirement (since it is
		   being run for one step). If the task now requires 0 units of time,
		   it has finished, so remove it from the system and return its task ID.
		 - If the task did not finish, return SIMULATE_NONE_FINISHED.
	*/
	public int simulate(){

		int current_taskID = heap[1][0];

		// If the currently running task has not yet finished, decrement its 
		// remaining time and move to the next timestep. If it's finished,
		// kill the task (which is O(logn) in the worst case) and return 
		// the taskID of the finished (killed) task.
		if (current_taskID < 0) {
			return SIMULATE_IDLE;
		} else if (heap[1][3] > 1) {
			heap[1][3]--;
			return SIMULATE_NONE_FINISHED;
		} else if (heap[1][3] == 1) {
			heap[1][3]--;
			kill(current_taskID);
			return current_taskID;
		}

		return SIMULATE_IDLE;
	}


	// The bubbleUp method is used to restore the heap property when a new task has been added.
	// The running time of this method is O(logn) in the worst case since we know that the height
	// of a complete binary tree (a heap) is <= logn (where n is the number of tasks in the heap).
	// In the worst case, bubbling up will perform h operations, where h is the height of the heap.
	public void bubbleUp(int index) {

		// Continue bubbling up and swapping tasks while the nice value of the parent is less
		// than the task we are bubbling and while we have not reached the root.
		while ((parentIndex(index) >= 1) && (heap[index][1] <= heap[parentIndex(index)][1])) {

			// This considers the case that the nice values are equivalent and then checks
			// which task has the lower taskID, since that task should be run first. 
			if (heap[index][1] == heap[parentIndex(index)][1]) {
				if (heap[index][0] < heap[parentIndex(index)][0]) {
					heapSwap(index, parentIndex(index));

					// Passes in two taskID's that are swapped within the indices array.
					swap(heap[index][0], heap[parentIndex(index)][0]);
				}
				index = parentIndex(index);
			} else {

				// The tasks are swapped if the nice values are not equivalent.
				heapSwap(index, parentIndex(index));

				// Passes in two taskID's that are swapped within the indices array.
				swap(heap[index][0], heap[parentIndex(index)][0]);
				index = parentIndex(index);
			}
		}
	}	


	// The bubbleDown method is used to restore the heap property when a task has been killed.
	// The running time of this method is O(logn) in the worst case since we know that the height
	// of a complete binary tree (a heap) is <= logn (where n is the number of tasks in the heap).
	// In the worst case, bubbling down will perform h operations, where h is the height of the heap.
	public void bubbleDown(int index) {

		int smaller_child = 0;

		// Continue bubbling down while the current 
		while (leftIndex(index) <= heap_size) {

			smaller_child = leftIndex(index);

			// Check to see if the right child has a smaller nice value (and taskID) than the left child.
			if ((rightIndex(index) <= heap_size) && (heap[rightIndex(index)][1] <= heap[leftIndex(index)][1])) {
				
				// This considers the case that the nice values are equivalent and then checks
				// which task has the lower taskID, since that task should be run first. 
				if (heap[rightIndex(index)][1] == heap[leftIndex(index)][1]) {
					if (heap[rightIndex(index)][0] < heap[leftIndex(index)][0]) {
						smaller_child = rightIndex(index);
					}
				} else {
					smaller_child = rightIndex(index);
				}
			}

			// If the nice value of the current task is >= the nice value of its smaller child,
			// then we want to swap the two tasks. But first, we have to make sure that if they 
			// have equivalent nice values that the task with the lower taskID will end up above 
			// the other task.
			if (heap[index][1] >= heap[smaller_child][1]) {
				if (heap[index][1] == heap[smaller_child][1]) {
					if (heap[index][0] > heap[smaller_child][0]) {
						heapSwap(index, smaller_child);
						swap(heap[index][0], heap[smaller_child][0]);
						index = smaller_child;
					} else {
						break;
					}
				} else {
					heapSwap(index, smaller_child);
					swap(heap[index][0], heap[smaller_child][0]);
					index = smaller_child;
				}
			} else {
				break;
			}
		}
	}
 

	// Returns the index of the left child.
	public int leftIndex(int index) {
		return index * 2;
	}


	// Returns the index of the right child.
	public int rightIndex(int index) {	
		return index * 2 + 1;
	}


	// Returns the index of the parent.
	public int parentIndex(int index) {
		return index / 2;
	}


	// Swaps two tasks given their indices within the heap.
	public void heapSwap(int index1, int index2) {

		// Temporary storage for the contents of the index1 array.
		int[] temp = new int[] {0,0,0,0};
		temp[0] = heap[index1][0];
		temp[1] = heap[index1][1];
		temp[2] = heap[index1][2];
		temp[3] = heap[index1][3];

		// Swap the contents of the internal array from index2 to index1.
		heap[index1][0] = heap[index2][0];
		heap[index1][1] = heap[index2][1];
		heap[index1][2] = heap[index2][2];
		heap[index1][3] = heap[index2][3];

		// Swap the contents of the internal array from index1 to index2.
		heap[index2][0] = temp[0];
		heap[index2][1] = temp[1];
		heap[index2][2] = temp[2];
		heap[index2][3] = temp[3];
	}


	// Swaps two of the values in the indices array that correspond 
	// to the index of tasks in the heap.
	public void swap(int index1, int index2) {

		int temp = indices[index1];

		indices[index1] = indices[index2];

		indices[index2] = temp;
	}
}