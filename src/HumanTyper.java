import java.awt.Robot;                    // Allows Java to control mouse/keyboard
import java.awt.Toolkit;                  // Used to access the clipboard
import java.awt.datatransfer.Clipboard;   // Clipboard object
import java.awt.datatransfer.DataFlavor;  // Lets us get text from clipboard
import java.awt.event.KeyEvent;           // Contains keyboard key codes
import java.util.Random;                  // Used to generate randomness

public class HumanTyper {

    // Robot object that will simulate keyboard typing
    private static Robot robot;

    // Random object for generating delays, typos, etc.
    private static Random random = new Random();

    // Object to stop typing if a key is pressed

    /*
     * Timing settings (all measured in milliseconds)
     * These control how "human" the typing appears.
     */

    // Delay range between normal letters
    private static final int LETTER_MIN = 10;
    private static final int LETTER_MAX = 40;

    // Delay range after spaces
    private static final int SPACE_MIN = 20;
    private static final int SPACE_MAX = 70;

    // Delay range after punctuation marks
    private static final int PUNCTUATION_MIN = 70;
    private static final int PUNCTUATION_MAX = 300;

    // Humans type in bursts instead of perfectly steady typing
    private static final int BURST_MIN = 5;
    private static final int BURST_MAX = 20;

    // Probability of making a typo (4%)
    private static final double TYPO_CHANCE = 0.04;



    public static void main(String[] args) throws Exception {

        // Create the Robot instance
        robot = new Robot();

        // Prevent Robot from adding its own delay
        robot.setAutoDelay(0);

        // Give user time to click into the document/text box
        System.out.println("Copy text to clipboard.");
        System.out.println("Switch to your text field. Typing starts in 5 seconds...");
        Thread.sleep(5000);

        // Get text from clipboard
        String text = getClipboardText();

        // Start human-like typing
        typeHumanLike(text);

        System.out.println("Finished typing.");
    }



    /*
     * Reads text from the system clipboard
     * This allows you to copy text and have the program type it.
     */
    private static String getClipboardText() throws Exception {

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        // Extract text data from clipboard
        return (String) clipboard.getData(DataFlavor.stringFlavor);
    }



    /*
     * Main typing algorithm.
     * Goes through every character and types it with human-like timing.
     */
    private static void typeHumanLike(String text) throws InterruptedException {

        // Decide how long the current typing burst will be
        int burstLength = randomBetween(BURST_MIN, BURST_MAX);

        // Counter for characters typed in the burst
        int typedInBurst = 0;

        // Loop through every character in the text
        for (char c : text.toCharArray()) {

            /*
             * TYPO SIMULATION
             * Occasionally type the wrong letter,
             * then press backspace and correct it.
             */
            if (Character.isLetter(c) && random.nextDouble() < TYPO_CHANCE) {

                // Generate random wrong letter
                char wrongChar = (char) ('a' + random.nextInt(26));

                typeChar(wrongChar);

                // Pause like someone noticing their mistake
                Thread.sleep(randomBetween(100, 250));

                // Press backspace to delete typo
                pressKey(KeyEvent.VK_BACK_SPACE);

                Thread.sleep(randomBetween(50, 150));
            }

            // Type the actual character
            typeChar(c);

            typedInBurst++;

            /*
             * BURST PAUSE
             * Humans pause briefly after typing several characters.
             */
            if (typedInBurst >= burstLength) {

                Thread.sleep(randomBetween(400, 900));

                // Reset burst
                burstLength = randomBetween(BURST_MIN, BURST_MAX);
                typedInBurst = 0;
            }

            /*
             * TIMING LOGIC
             * Different characters cause different delays.
             */

            if (c == ' ') {
                // Slight pause between words
                Thread.sleep(randomBetween(SPACE_MIN, SPACE_MAX));

            } else if (".,!?:;".indexOf(c) >= 0) {
                // Longer pause after punctuation
                Thread.sleep(randomBetween(PUNCTUATION_MIN, PUNCTUATION_MAX));

            } else {
                // Normal typing delay
                Thread.sleep(randomBetween(LETTER_MIN, LETTER_MAX));
            }
        }
    }



    /*
     * Types a single character.
     * Handles uppercase letters automatically.
     */
    private static void typeChar(char c) {

        try {

            // Check if the letter is uppercase
            boolean upperCase = Character.isUpperCase(c);

            // Convert character to keyboard key code
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);

            // If key is invalid, skip it
            if (keyCode == KeyEvent.VK_UNDEFINED) return;

            // Hold shift for uppercase letters
            if (upperCase) robot.keyPress(KeyEvent.VK_SHIFT);

            // Press and release the key
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);

            // Release shift if used
            if (upperCase) robot.keyRelease(KeyEvent.VK_SHIFT);

        } catch (Exception e) {

            // Print error information if something goes wrong
            e.printStackTrace();
        }
    }



    /*
     * Presses a special key (like backspace)
     */
    private static void pressKey(int keyCode) {

        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);
    }



    /*
     * Utility function that returns a random number
     * between min and max (inclusive)
     */
    private static int randomBetween(int min, int max) {

        return random.nextInt(max - min + 1) + min;
    }
}