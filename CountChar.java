/**
 * Program Header:
 * Course: COSC 2230 - Computer Science III
 * Term: Fall 2026
 * Author: Tsogt Rentsen
 * Assignment: Programming assignment: count a character - from recursion to loop
 *
 * AI Use Statement:
 * OpenAI Codex reviewed the program, ran the supplied test client, and revised
 * these comments for clarity and accuracy.
 *
 * Question 1: Trace countAcc("banana", 'a') and countLoop("banana", 'a').
 * Each row shows the helper call and the start of the matching loop pass.
 *
 * Helper call   i  acc  | Loop pass   i  acc
 *      1        0   0   |     1       0   0
 *      2        1   0   |     2       1   0
 *      3        2   1   |     3       2   1
 *      4        3   1   |     4       3   1
 *      5        4   2   |     5       4   2
 *      6        5   2   |     6       5   2
 *      7        6   3   |   Exit      6   3
 * Both return 3.
 *
 * Question 2: Why does recursion overflow the stack but the loop does not?
 * count adds a stack frame for each recursive call, so 100,000 characters
 * can fill the stack. countLoop reuses i and acc without adding frames.
 * countAcc can overflow too: Java does not turn its tail calls into a loop.
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
