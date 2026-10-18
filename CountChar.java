/**
 * Program Header:
 * Course: COSC 2230 - Computer Science III
 * Term: Fall 2026
 * Author: Tsogt Rentsen
 * Assignment: Programming assignment: count a character - from recursion to loop
 *
 * AI Use Statement:
 * Generative AI was used to assist in designing the tail-recursive accumulator implementation
 * (countAcc), converting it into the equivalent 4-step while loop (countLoop), constructing
 * the side-by-side trace table comparing recursive states to loop passes on "banana", and
 * articulating the memory mechanics of stack frame allocation vs. in-place variable updates
 * regarding JVM StackOverflowError and tail-call optimization behavior.
 *
 * -------------------------------------------------------------------------------------------------------------
 * Question 1: Trace countAcc("banana", 'a') side-by-side with countLoop("banana", 'a')
 * -------------------------------------------------------------------------------------------------------------
 * String s = "banana", length = 6. Indices: 0:'b', 1:'a', 2:'n', 3:'a', 4:'n', 5:'a'. Target char c = 'a'.
 *
 * Call #  countAcc Call / State            i   acc  |  Matching countLoop Pass    i (start)  acc (start)  Char Checked & Action
 * -------------------------------------------------------------------------------------------------------------
 * 1       countAcc("banana", 'a', 0, 0)    0   0    |  Pass 1                     0          0            s.charAt(0) is 'b' != 'a' -> acc stays 0, i becomes 1
 * 2       countAcc("banana", 'a', 1, 0)    1   0    |  Pass 2                     1          0            s.charAt(1) is 'a' == 'a' -> acc becomes 1, i becomes 2
 * 3       countAcc("banana", 'a', 2, 1)    2   1    |  Pass 3                     2          1            s.charAt(2) is 'n' != 'a' -> acc stays 1, i becomes 3
 * 4       countAcc("banana", 'a', 3, 1)    3   1    |  Pass 4                     3          1            s.charAt(3) is 'a' == 'a' -> acc becomes 2, i becomes 4
 * 5       countAcc("banana", 'a', 4, 2)    4   2    |  Pass 5                     4          2            s.charAt(4) is 'n' != 'a' -> acc stays 2, i becomes 5
 * 6       countAcc("banana", 'a', 5, 2)    5   2    |  Pass 6                     5          2            s.charAt(5) is 'a' == 'a' -> acc becomes 3, i becomes 6
 * 7       countAcc("banana", 'a', 6, 3)    6   3    |  Loop Exit (i == length)    6          3            Base case returns acc = 3 / Loop terminates and returns acc = 3
 * -------------------------------------------------------------------------------------------------------------
 *
 * -------------------------------------------------------------------------------------------------------------
 * Question 2: Why count crashes on a string of 100,000 characters, countLoop does not, and would countAcc crash?
 * -------------------------------------------------------------------------------------------------------------
 * 1. Why count crashes:
 *    The method count is non-tail-recursive because work remains after the recursive call returns
 *    (specifically, 1 + count(...)). Every call to count must allocate a separate activation record
 *    (stack frame) on the JVM call stack to retain the caller's local variables, parameters, and return
 *    address. For a string containing 100,000 characters, 100,001 nested stack frames are created.
 *    This exceeds the maximum memory capacity allocated to the thread call stack (typically ~1 MB by
 *    default on the JVM), immediately resulting in a java.lang.StackOverflowError.
 *
 * 2. Why countLoop does not crash:
 *    countLoop executes entirely within a single stack frame. Instead of adding a new frame to the call
 *    stack for each character in the string, each iteration of the while loop updates the existing local
 *    variables (i and acc) in-place in memory. Because it consumes O(1) constant stack space regardless of
 *    how large the input string is, it completes without overflowing the stack.
 *
 * 3. Would countAcc crash, too?
 *    Yes, countAcc would also crash with a java.lang.StackOverflowError on 100,000 characters. While
 *    countAcc is written in tail-recursive form (the recursive call is the very last operation with the
 *    accumulated value passed directly), standard Java (HotSpot JVM) does NOT support Tail-Call
 *    Optimization (TCO) or tail-call elimination. Therefore, the JVM still pushes a new stack frame onto
 *    the call stack for every recursive call to countAcc, exhausting the stack just like count.
 */

public class CountChar
{
    // Recursive version (as given in assignment): work remains after call (1 + ...)
    public static int count(String s, char c)
    {
        return count(s, c, 0);
    }

    public static int count(String s, char c, int i)
    {
        if (i == s.length()) return 0;
        if (s.charAt(i) == c) return 1 + count(s, c, i + 1);
        else                  return count(s, c, i + 1);
    }

    // Move 1: write countAcc(String s, char c), a recursive version in which the call is the last thing:
    // carry the count along in an extra parameter acc. Like count, it needs a second method with more
    // parameters that the first one starts.
    public static int countAcc(String s, char c)
    {
        return countAcc(s, c, 0, 0);
    }

    public static int countAcc(String s, char c, int i, int acc)
    {
        if (i == s.length()) return acc;
        if (s.charAt(i) == c) return countAcc(s, c, i + 1, acc + 1);
        else                  return countAcc(s, c, i + 1, acc);
    }

    // Move 2: write countLoop(String s, char c) with a while loop, following the four steps:
    // variables and start values, loop condition, assignments, return after the loop.
    public static int countLoop(String s, char c)
    {
        // Step 1: variables and start values
        int i = 0;
        int acc = 0;

        // Step 2: loop condition
        while (i != s.length())
        {
            // Step 3: assignments
            if (s.charAt(i) == c)
            {
                acc = acc + 1;
            }
            i = i + 1;
        }

        // Step 4: return after the loop
        return acc;
    }

    // Test client (as specified in assignment):
    public static void main(String[] args)
    {
        String[] words = { "mississippi", "mississippi", "mississippi", "banana", "" };
        char[]   chars = { 's',           'p',           'x',           'a',      'a' };
        for (int k = 0; k < words.length; k++)
            StdOut.printf("%-13s %c   recursive %d   accumulator %d   loop %d%n",
                          "\"" + words[k] + "\"", chars[k],
                          count(words[k], chars[k]),
                          countAcc(words[k], chars[k]),
                          countLoop(words[k], chars[k]));

        String big = "a".repeat(100000);
        StdOut.println("100000 a's    loop " + countLoop(big, 'a'));
        try
        {
            StdOut.println("100000 a's    recursive " + count(big, 'a'));
        }
        catch (StackOverflowError e)
        {
            StdOut.println("100000 a's    recursive " + e);
        }
    }
}
