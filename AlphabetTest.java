package enigma;

import org.junit.Test;
import static org.junit.Assert.*;

/** All the tests for the Alphabet class
 * @Flynn"TheFox"Gray
 */

public class AlphabetTest {

    @Test
    public void test1() {
        Alphabet test =  new Alphabet("ABCD");
        assertEquals(4, test.size());

        assertEquals('A', test.toChar(0));
        assertEquals('B', test.toChar(1));

        assertEquals(2, test.toInt('C'));
        assertEquals(3, test.toInt('D'));

        assertTrue(test.contains('A'));
        assertFalse(test.contains('a'));
    }

    @Test
    public void testMoreComplicated() {
        String testString = "0123456/abc)!";
        Alphabet test = new Alphabet(testString);
        assertEquals(13, test.size());
        for (int i = 0; i < test.size(); i++) {
            char curr = testString.charAt(i);
            assertEquals(curr, test.toChar(i));
            assertEquals(i, test.toInt(curr));
            assertTrue(test.contains(curr));
        }
        assertFalse(test.contains('9'));
    }

}

