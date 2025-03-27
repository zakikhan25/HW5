/******************************************************************
 *
 * Zaki Khan / 272 001
 *
 * This java file contains the problem solutions of isSubSet, findKthLargest,
 * and sort2Arrays methods. You should utilize the Java Collection Framework for
 * these methods.
 *
 ********************************************************************/
import java.util.*;

class ProblemSolutions {
    /**
     * Method: isSubset()
     *
     * Given two arrays of integers, A and B, return whether
     * array B is a subset if array A. Example:
     * Input: [1,50,55,80,90], [55,90]
     * Output: true
     * Input: [1,50,55,80,90], [55,90, 99]
     * Output: false
     *
     * The solution time complexity must NOT be worse than O(n).
     * For the solution, use a Hash Table.
     *
     * @param list1 - Input array A
     * @param list2 - input array B
     * @return - returns boolean value B is a subset of A.
     */
    public boolean isSubset(int list1[], int list2[]) {
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int num : list1) {
            map.put(num, map.getOrDefault(num, 0) + 1);
        }
        for (int num : list2) {
            if (!map.containsKey(num) || map.get(num) == 0) {
                return false;
            }
            map.put(num, map.get(num) - 1);
        }
        return true;
    }

    /**
     * Method: findKthLargest
     *
     * Given an Array A and integer K, return the k-th maximum element in the array.
     * Example:
     * Input: [1,7,3,10,34,5,8], 4
     * Output: 7
     *
     * @param array - Array of integers
     * @param k - the kth maximum element
     * @return - the value in the array which is the kth maximum value
     */
    public int findKthLargest(int[] array, int k) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int num : array) {
            pq.offer(num);
            if (pq.size() > k) {
                pq.poll();
            }
        }
        return pq.peek();
    }

    /**
     * Method: sort2Arrays
     *
     * Given two arrays A and B with n and m integers respectively, return
     * a single array of all the elements in A and B in sorted order. Example:
     * Input: [4,1,5], [3,2]
     * Output: 1 2 3 4 5
     *
     * @param array1 - Input array 1
     * @param array2 - Input array 2
     * @return - Sorted array with all elements in A and B.
     */
    public int[] sort2Arrays(int[] array1, int[] array2) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int num : array1) {
            pq.offer(num);
        }
        for (int num : array2) {
            pq.offer(num);
        }
        int[] sortedArray = new int[array1.length + array2.length];
        int index = 0;
        while (!pq.isEmpty()) {
            sortedArray[index++] = pq.poll();
        }
        return sortedArray;
    }
}
