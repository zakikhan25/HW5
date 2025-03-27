/******************************************************************
 *
 * Zaki Khan / 272 001
 *
 * Note, additional comments provided throughout source code is
 * for educational purposes.
 *
 ********************************************************************/
import java.util.BitSet;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;
import java.security.SecureRandom;
import java.lang.Math;

/**
 * Bloom Filters
 *
 * A Bloom filter is an implementation of a set which allows a certain 
 * probability of 'false positives' when determining if a given object is 
 * a member of that set, in return for a reduction in the amount of memory 
 * required for the set. It effectively works as follows:
 * 1) We allocate 'm' bits to represent the set data.
 * 2) We provide a hash function, which, instead of a single hash code, 
 produces'k' hash codes and sets those bits.
 * 3) To add an element to the set, we derive bit indexes from all 'k' 
 hash codes and set those bits.
 * 4) To determine if an element is in the set, we again calculate the 
 * corresponding hash codes and bit indexes, and say it is likely 
 * present if and only if all corresponding bits are set.
 *
 * The margin of error (or false positive rate) thus comes from the fact 
 * that as we add more and more objects to the set, we increase the likelihood
 * of "accidentally" setting a combination of bits that corresponds to an 
 * element that isn't actually in the set. However, through tuning the bloom 
 * filter setup based on the expected data, we mathematically have control 
 * over the desired false positive probability rate that we want to received
 * based on probability theory.
 *
 * False Positive rate discussion:
 *
 * The Bloom filter performance changes as we change parameters discussed 
 * below with the class constructors. There are two key variables that impact 
 * the false positive rate:
 * 1) number of bits per item
 * 2) number of hash codes
 *
 * In other words, how many more bits are there in the filter than the 
 * maximum number of items we want to represent in the set, and hence the 
 * number of bits that we actually set for each element that we add to the 
 * set. The more bits we require to be marked as set to '1' in order to mark 
 * an element as 'present' - e.g., the more hash code per item - the lower the 
 * chance of false positives, because for a given element potentially in
 * the set, there's less chance of some random combination of bits from other 
 * elements also accidentally marking that element as present when it isn't.
 *
 * But, for a given bit filter size, there is a 'point of no return', at 
 * which having more hash codes simply means that we fill up the bit set too 
 * quickly as we add elements -- and hence get more false positives -- than 
 * with fewer hash codes.
 *
 * Based on this discussion, you can find many Bloom Filter calculators 
 * available online to determine how to adjust the variables inorder to 
 * achieve the desired probability of false positive rates that you can 
 * tolerate and/or desire for your application, e.g.,:
 * - https://toolslick.com/programming/data-structure/bloom-filter-calculator
 * - https://www.engineersedge.com/calculators/bloom_filter_calculator_15596.htm
 * - https://www.di-mgt.com.au/bloom-calculator.html
 * - https://programming.guide/bloom-filter-calculator.html
 */
class BloomFilter {
    private static final int MAX_HASHES = 8;
    private static final long[] byteTable;
    private static final long HSTART = 0xBB40E64DA205B064L;
    private static final long HMULT = 7664345821815920749L;

    static {
        byteTable = new long[256 * MAX_HASHES];
        long h = 0x544B2FBACAAF1684L;
        for (int i = 0; i < byteTable.length; i++) {
            for (int j = 0; j < 31; j++)
                h = (h >>> 7) ^ h; h = (h << 11) ^ h; h = (h >>> 10) ^ h;
            byteTable[i] = h;
        }
    }

    private long hashCode(String s, int hcNo) {
        long h = HSTART;
        final long hmult = HMULT;
        final long[] ht = byteTable;
        int startIx = 256 * hcNo;
        for (int len = s.length(), i = 0; i < len; i++) {
            char ch = s.charAt(i);
            h = (h * hmult) ^ ht[startIx + (ch & 0xff)];
            h = (h * hmult) ^ ht[startIx +
