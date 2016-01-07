package filtering;

import brightbodies.BrightBodyList;
import helper.SubtractiveHelper;
import locating.BinaryLocator;

/**
 * @author Jonathan Zwiebel
 * @version January 6th, 2016
 *
 * Filters the bright bodies by generating a reference frame that is indicative of the cube as a whole. Locates the
 * bright bodies within that cube and then compares the bright bodies in each frame to that of the reference cube
 * through area overlap
 */
public class ReferenceMobilityFilter extends MobilityFilter {
    float[][][] processed_data_;
    float similarity_threshold_;

    /**
     * Constructs a ReferenceMobilityFilter object with the bright bodies from a locator and the data from a processor
     * @param bright_body_lists bright bodies from a locator
     * @param processed_data processed data from a processor
     */
    public ReferenceMobilityFilter(BrightBodyList[] bright_body_lists, float[][][] processed_data, float similarity_threshold) {
        super(bright_body_lists);
        processed_data_ = processed_data;
        similarity_threshold_ = similarity_threshold;
    }

    /**
     * Filters the bright bodies using reference frame filtration into mobile and immobile bright bodies
     * @return data cube with first slice as immobile bodies and second slice as mobile bodies
     */
    public BrightBodyList[][] filter() {
        float[][] reference_frame = generateReferenceFrame();
        BrightBodyList reference_bodies = generateReferenceFrameBodies(reference_frame);

        BrightBodyList[][] filtered_bodies = new BrightBodyList[2][bright_body_lists_.length];
        // TODO: Consider if this for loop is better replaced by a single method
        for(int index = 0; index < bright_body_lists_.length; index++) {
            BrightBodyList[] filtered_bodies_instance = mobilitySeparation(bright_body_lists_[index], reference_bodies, similarity_threshold_);
            assert filtered_bodies_instance.length == 2;
            filtered_bodies[0][index] = filtered_bodies_instance[0]; // immobile
            filtered_bodies[1][index] = filtered_bodies_instance[1]; // mobile
        }

        return new BrightBodyList[0][0];
    }

    /**
     * Generates the reference frame that will be used as a comparison to other frames
     * Currently only uses the mean image
     *
     * @return reference frame
     * TODO[Major]: Allow for more types of reference frames than just mean
     * TODO[Major]: Allow for shifts to the reference frame
     */
    private float[][] generateReferenceFrame() {
        return SubtractiveHelper.meanImage(processed_data_);
    }

    /**
     * Locates the bright bodies in the reference frame
     * Currently only does this through a simple mean binary locator
     *
     * @param reference_frame floating point reference frame
     * @return a list of all of the bright bodies in the reference frame
     * TODO[Minor]: Allow for parameters when doing BinaryLocating on reference frame
     * TODO[Major]: Allow for more location methods
     */
    private BrightBodyList generateReferenceFrameBodies(float[][] reference_frame) {
        float[][][] reference_frame_cube = {reference_frame};
        BinaryLocator reference_frame_locator = new BinaryLocator(reference_frame_cube, BinaryLocator.ThresholdType.MEAN);
        reference_frame_locator.initialize();
        return reference_frame_locator.locate()[0];
    }

    /**
     * Separates the pre-located bright bodies in input_bodies into two BrightBodyLists by comparing similarity to
     * reference_bodies. If they match by the threshold value
     * TODO: Allow for simple circle checks on centroid and body size as well as full check
     *
     * @param input_bodies bright bodies in the slice being searched
     * @param reference_bodies bright bodies in the reference slice
     * @return data cube with first slice as immobile and second slice as mobile bright bodies
     *
     * TODO: Consider if it is better to returns a special data type for sorted BrightBodyList
     *
     * Likely to be very memory intensive
     */
    private BrightBodyList[] mobilitySeparation(BrightBodyList input_bodies, BrightBodyList reference_bodies, float similarity_threshold_) {

    }
}