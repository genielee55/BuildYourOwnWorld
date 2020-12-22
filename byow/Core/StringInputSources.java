package byow.Core;
/**
 * @source: InputDemo
 */

public class StringInputSources implements InputSources {
    private int index;
    private String input;

    public StringInputSources(String s) {
        input = s;
        index = 0;
    }

    @Override
    public char getNextKey() {
        char resultChar = Character.toUpperCase(input.charAt(index));
        index++;
        return resultChar;
    }

    @Override
    public boolean possibleNextInput() {
        return index < input.length();
    }
}
