package stars;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author Jonathan Zwiebel
 * @version November 23rd, 2015
 */
public abstract class TrackerInstance implements Serializable{
    public abstract void toTextFile(String filename) throws IOException;
    public abstract void toCSV(String filename) throws IOException;
    public abstract void serialize(String filename) throws IOException;

}