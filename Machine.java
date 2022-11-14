package enigma;

import java.util.HashMap;
import java.util.Collection;


/** Class that represents a complete enigma machine.
 *  @author Flynn"TheFox"Gray
 */
class Machine<Foo> {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;

        for (Rotor r: allRotors) {
            _hashmapOfRotors.put(r.name(), r);
        }
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return _hashmapOfRotors.get(_rotorKeys[k]);
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _rotorKeys = rotors;
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 1; i < _numRotors; i += 1) {
            getRotor(i).set(setting.charAt(i - 1));
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return pbPerm;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        pbPerm = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        boolean fastRotorTurnedAlready = false;
        for (int k = 1; k < _numRotors - 1; k++) {
            if (getRotor(k).rotates() && getRotor(k + 1).atNotch()) {
                getRotor(k).advance();
                getRotor(k + 1).advance();

                if (k + 1 == _numRotors - 1) {
                    fastRotorTurnedAlready = true;
                }
                k += 1;
            }
        }
        if (!fastRotorTurnedAlready) {
            getRotor(_numRotors - 1).advance();
        }
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        for (int k = _numRotors - 1; k >= 0; k -= 1) {
            c = getRotor(k).convertForward(c);
        }
        for (int k = 1; k < _numRotors; k += 1) {
            c = getRotor(k).convertBackward(c);
        }
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String resultString = "";
        for (int i = 0; i < msg.length(); i++) {
            resultString += _alphabet.toChar
                    (convert(_alphabet.toInt(msg.charAt(i))));
        }
        return resultString;
    }

    HashMap accessHashofRotors() {
        return _hashmapOfRotors;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** number of rotors. */
    private int _numRotors;
    /** number of pawls. */
    private int _pawls;
    /** hashmap of rotors. */
    private HashMap<String, Rotor> _hashmapOfRotors = new HashMap<>(_numRotors);
    /** list of rotors names. */
    private String[] _rotorKeys;
    /** peanut butter permutation, or plugboard, same thing. */
    private Permutation pbPerm;
}
