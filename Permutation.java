package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Flynn"TheFox"Gray
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
    }

    String[] cycleSplitter() {
        _cycles = _cycles.replaceAll(" ", "");
        String[] split = _cycles.split("\\)");
        for (int i = 0; i < split.length; i++) {
            if (!_cycles.equals("")) {
                if (split[i].length() > 0) {
                    split[i] = split[i].substring(1);
                }
            }
        }
        return split;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        return _alphabet.toInt(
                permute(_alphabet.toChar(p % size()))
        );
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        return _alphabet.toInt(
                invert(_alphabet.toChar(c % size()))
        );
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        String[] splittedCycle = cycleSplitter();
        for (int i = 0; i < splittedCycle.length; i++) {
            for (int j = 0; j < splittedCycle[i].length(); j++) {
                if (splittedCycle[i].charAt(j) == p) {
                    if (j + 1 == splittedCycle[i].length()) {
                        return splittedCycle[i].charAt(0);
                    } else {
                        return splittedCycle[i].charAt(j + 1);
                    }
                }
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        String[] splittedCycle = cycleSplitter();
        for (int i = 0; i < splittedCycle.length; i++) {
            for (int j = 0; j < splittedCycle[i].length(); j++) {
                if (splittedCycle[i].charAt(j) == c) {
                    if (j == 0) {
                        return splittedCycle[i].charAt
                                (splittedCycle[i].length() - 1);
                    } else {
                        return splittedCycle[i].charAt(j - 1);
                    }
                }
            }
        }
        return c;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < size(); i++) {
            if (permute(i) == i) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** cycles of this permutation. */
    private String _cycles;
}
