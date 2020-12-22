package byow.Core;
import edu.princeton.cs.introcs.StdDraw;
/**
 * @source: InputDemo
 */

public class KeyboardInputSources implements InputSources {

    @Override
    public char getNextKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                return Character.toUpperCase(StdDraw.nextKeyTyped());
            }
        }
    }

    @Override
    public boolean possibleNextInput() {
        return true;
    }
}
