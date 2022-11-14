package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Flynn"TheFox"Gray
 */
class MovingRotor extends Rotor {

    /**
     * A rotor named NAME whose permutation in its default setting is
     * PERM, and whose notches are at the positions indicated in NOTCHES.
     * The Rotor is initally in its 0 setting (first character of its
     * alphabet).
     *
     * @author Flynn"TheFox"Gray
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    boolean rotates() {
        return true;
    }

    @Override
    void advance() {
        if (setting() == alphabet().size() - 1) {
            set(0);
        } else {
            set(setting() + 1);
        }
    }

    @Override
    String notches() {
        return _notches;
    }

    /** notches of my rotor. */
    private String _notches;

}
