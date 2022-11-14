package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;
import java.util.NoSuchElementException;

import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Flynn"TheFox"Gray
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {

        Machine M = readConfig();

        boolean empty = true;
        boolean alreadyGivenConfig = false;

        while (_input.hasNext()) {
            empty = false;

            String currLineString = _input.nextLine();
            String[] currLineList = currLineString.split(" ");

            if (currLineList[0].equals("*")) {
                alreadyGivenConfig = true;
                setUp(M, currLineList);
            } else if (currLineList.length == 0) {
                _output.println();
            } else {
                if (!alreadyGivenConfig) {
                    throw new EnigmaException("No config");
                }
                printMessageLine(M, currLineString);
            }
        }
        if (empty) {
            throw new EnigmaException("empty input");
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.nextLine());
            int numRotors = _config.nextInt();
            int numPawls = _config.nextInt();
            ArrayList<Rotor> rotorsArrayList = new ArrayList<Rotor>();
            while (_config.hasNext()) {
                rotorsArrayList.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, numPawls, rotorsArrayList);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next();
            String typeAndNotches = _config.next();
            String rotorType = typeAndNotches.substring(0, 1);
            String notches = typeAndNotches.substring(1);
            String permcycles = "";
            while (_config.hasNext("\\(.*\\)")) {
                permcycles += _config.next() + " ";
            }
            Permutation perm = new Permutation(permcycles, _alphabet);

            if (rotorType.equals("M")) {
                return new MovingRotor(name, perm, notches);
            } else if (rotorType.equals("N")) {
                return new FixedRotor(name, perm);
            } else {
                return new Reflector(name, perm);
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment.
    @param M the Machine we set up;
     @param currLine the line of input we are dealing with*/
    private void setUp(Machine M, String[] currLine) {
        int currIndex = 0;
        if (currLine[currIndex].equals("*")) {
            currIndex++;
        }
        int currIndexAfterAsterisk = currIndex;
        String rotorsString = "";
        for (; currIndex < M.numRotors() + currIndexAfterAsterisk;
             currIndex++) {
            rotorsString += currLine[currIndex] + " ";
        }
        String[] rotorsList = rotorsString.split(" ");

        for (int i = 0; i < rotorsList.length; i++) {

            if (!M.accessHashofRotors().containsKey(rotorsList[i])) {
                throw new EnigmaException("Bad rotor name");
            }
        }
        M.insertRotors(rotorsList);

        if (!M.getRotor(0).reflecting()) {
            throw new EnigmaException("first rotor should reflect");
        }

        int numMovingRotors = 0;

        for (int i = 0; i < rotorsList.length; i++) {
            if (M.getRotor(i).rotates()) {
                numMovingRotors += 1;
            }
        }
        if (numMovingRotors != M.numPawls()) {
            throw new EnigmaException("#pawls != number of moving rotors");
        }

        String settings = currLine[currIndex];

        if (settings.length() != rotorsList.length - 1) {
            throw new EnigmaException("incorrect number of settings");
        }

        M.setRotors(settings);
        currIndex++;
        String pbpermcycles = "";
        for (; currIndex < currLine.length; currIndex++) {
            pbpermcycles += currLine[currIndex] + " ";
        }
        Permutation pbperm = new Permutation(pbpermcycles, _alphabet);
        M.setPlugboard(pbperm);
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters).
     * @param M the Machine we are using
     * @param msg code to translate*/
    private void printMessageLine(Machine M, String msg) {
        msg = msg.replaceAll(" ", "");
        String outPut = M.convert(msg);
        for (int pos = 0; pos + 5 < outPut.length();) {
            pos += 5;
            outPut = outPut.substring(0, pos) + " " + outPut.substring(pos);
            pos += 1;
        }
        _output.println(outPut);
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;
}
