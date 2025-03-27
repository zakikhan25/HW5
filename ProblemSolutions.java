/******************************************************************
 *
 *   Zaki Khan / 272 001
 *
 *   This java file contains the problem solutions of isSubSet, findKthLargest,
 *   and sort2Arrays methods. You should utilize the Java Collection Framework for
 *   these methods.
 *
 ********************************************************************/

import java.util.*;

class ProblemSolutions {

    public boolean isSubset(int list1[], int list2[]) {
        Set<Integer> set = new HashSet<>();
        for (int num : list1) {
            set.add(num);
        }
        for (int num : list2) {
            if (!set.contains(num)) {
                return false;
            }
        }
        return true;
    }

    public int findKthLargest(int[] array, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        for (int num : array) {
            minHeap.add(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }
        return minHeap.peek();
    }

    public int[] sort2Arrays(int[] array1, int[] array2) {
        List<Integer> merged = new ArrayList<>();
        for (int num : array1) merged.add(num);
        for (int num : array2) merged.add(num);
        Collections.sort(merged);
        return merged.stream().mapToInt(i -> i).toArray();
    }
}
