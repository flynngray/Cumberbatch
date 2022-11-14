package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @Flynn"TheFox"Gray
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */
    @Test
    public void test1() {
        Permutation test = new Permutation("(abc) (def)",
                new Alphabet("abcdefg"));
        assertEquals(7, test.size());

        String[] expectedSplit = {"abc", "def"};
        for (int i = 0; i < expectedSplit.length; i++) {
            assertEquals(expectedSplit[i], test.cycleSplitter()[i]);
        }

        assertEquals('b', test.permute('a'));
        assertEquals('e', test.permute('d'));
        assertEquals('d', test.permute('f'));
        assertEquals('a', test.permute('c'));
        assertEquals('g', test.invert('g'));

        assertEquals('b', test.invert('c'));
        assertEquals('a', test.invert('b'));
        assertEquals('c', test.invert('a'));
        assertEquals('e', test.invert('f'));
        assertEquals('f', test.invert('d'));
        assertEquals('d', test.invert('e'));
        assertEquals('g', test.invert('g'));

        Permutation test2 =
                new Permutation("(ade) (fhc)",
                        new Alphabet("abcdefgh"));

        assertEquals(3, test2.permute(0));
        assertEquals(4, test2.permute(3));
        assertEquals(0, test2.permute(4));
        assertEquals(5, test2.permute(2));

        assertEquals(4, test2.invert(0));
        assertEquals(0, test2.invert(3));
        assertEquals(3, test2.invert(4));
        assertEquals(7, test2.invert(2));

        assertFalse(test.derangement());
        assertFalse(test2.derangement());
    }

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

}
