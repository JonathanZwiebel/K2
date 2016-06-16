package brightbodies;

/**
 * This is a generic superclass to represent a linkage of MBBs into a predicted asteroid path. This is to be subclassed
 * by more specific Linkedsets
 *
 * @author Jonathan Zwiebel
 * @version 16 June 2016
 */
public class BrightBodyLinkedSet {
    protected BrightBody[] mobile_bodies_;
    protected int[] timestamps_;

    /**
     * Creates a BrightBodyLinkedSet object given the bodies and a reference to what timestamp they come from.
     *
     * @param mobile_bodies the set of mobile bright bodies that are linked together
     * @param timestamps the set of timestamps that correspond to the bright bodies
     */
    public BrightBodyLinkedSet(BrightBody[] mobile_bodies, int[] timestamps) {
        mobile_bodies_ = mobile_bodies;
        timestamps_ = timestamps;
    }
}
