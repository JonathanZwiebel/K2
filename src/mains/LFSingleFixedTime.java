package mains;

import brightbodies.BrightBody;
import brightbodies.BrightBodyList;
import filter.MobilityFilter;
import filter.ReferenceMobilityFilter;
import locate.BinaryLocator;
import locate.BinaryLocator.ThresholdType;
import locate.Locator;
import nom.tam.fits.Fits;
import preprocess.K2Preprocessor;
import preprocess.Preprocessor;
import filter.ReferenceMobilityFilter.ReferenceBodyDetectionMethod;

import java.io.File;

/**
 * A top-level runnable type that will run locate-track given type and parameters on a single file and output
 * information about the distribution and placement of BBs into the the three categories.
 *
 * @author Jonathan Zwiebel
 * @version February 26th, 2016
 *
 * TODO: Consider the data output in a fifth macro step
 */
public final class LFSingleFixedTime {
    /**
     * Main method to be run for program execution
     * @param args Location, Initial locating threshold, Similarity threshold, Reference locating threshold, Timestamp
     */
    public static void run(String[] args) {
        assert args[0] == "LF_SINGLE_FIXED_TIME";

        // Start data parsing
        int argumentReadLoc = 1;

        String data_location = args[argumentReadLoc];
        argumentReadLoc++;

        float[] detection_args = null;
        ThresholdType detection_threshold_type = null;
        String detection_threshold_type_string = args[argumentReadLoc];
        argumentReadLoc++;
        switch(detection_threshold_type_string) {
            case "MEAN":
                detection_threshold_type = ThresholdType.MEAN;
                detection_args = new float[]{};
                break;
            case "ABSOLUTE":
                detection_threshold_type = ThresholdType.ABSOLUTE;
                detection_args = new float[]{Float.parseFloat(args[argumentReadLoc])};
                argumentReadLoc++;
                break;
            case "MEAN_SHIFTED":
                detection_threshold_type = ThresholdType.MEAN_SHIFTED;
                detection_args = new float[]{Float.parseFloat(args[argumentReadLoc])};
                argumentReadLoc++;
                break;
            case "MEAN_SCALED":
                detection_threshold_type = ThresholdType.MEAN_SCALED;
                detection_args = new float[]{Float.parseFloat(args[argumentReadLoc])};
                argumentReadLoc++;
                break;
            default:
                System.out.println("Illegal detection threshold type");
                System.exit(1);
        }

        float similarity_threshold = Float.parseFloat(args[argumentReadLoc]);
        argumentReadLoc++;

        float[] reference_frame_detection_args = null;
        ReferenceBodyDetectionMethod reference_frame_detection_threshold_type = null;
        String reference_frame_detection_threshold_type_string = args[argumentReadLoc];
        argumentReadLoc++;
        switch(reference_frame_detection_threshold_type_string) {
            case "MEAN":
                reference_frame_detection_threshold_type = ReferenceBodyDetectionMethod.MEAN;
                reference_frame_detection_args = new float[]{};
                break;
            case "ABSOLUTE":
                reference_frame_detection_threshold_type = ReferenceBodyDetectionMethod.ABSOLUTE;
                reference_frame_detection_args = new float[]{Float.parseFloat(args[argumentReadLoc])};
                argumentReadLoc++;
                break;
            case "MEAN_SHIFTED":
                reference_frame_detection_threshold_type = ReferenceBodyDetectionMethod.MEAN_SHIFTED;
                reference_frame_detection_args = new float[]{Float.parseFloat(args[argumentReadLoc])};
                argumentReadLoc++;
                break;
            case "MEAN_SCALED":
                reference_frame_detection_threshold_type = ReferenceBodyDetectionMethod.MEAN_SCALED;
                reference_frame_detection_args = new float[]{Float.parseFloat(args[argumentReadLoc])};
                argumentReadLoc++;
                break;
            default:
                System.out.println("Illegal reference frame detection method");
                System.exit(1);
        }

        int timestamp = Integer.parseInt(args[argumentReadLoc]);
        // End data parsing


        long start_time = System.currentTimeMillis();
        try {
            System.out.println("Preprocessing");
            Preprocessor preprocessor = new K2Preprocessor(new Fits(new File(data_location)));
            float[][][] data = preprocessor.read();

            System.out.println("Locating");
            Locator locator = new BinaryLocator(data, detection_threshold_type, detection_args);
            locator.initialize();
            BrightBodyList[] bodies = locator.locate();

            System.out.println("Filtering");
            MobilityFilter filter = new ReferenceMobilityFilter(bodies, data, similarity_threshold, reference_frame_detection_threshold_type, reference_frame_detection_args);
            BrightBodyList[][] filtered_bodies = filter.filter();
            BrightBodyList[] immobile_bodies = filtered_bodies[MobilityFilter.IBB_INDEX];
            BrightBodyList[] mobile_bodies = filtered_bodies[MobilityFilter.MBB_INDEX];
            BrightBodyList[] noise_bodies = filtered_bodies[MobilityFilter.NOISE_INDEX];

            printSimpleDetectionStatsTimestamped(mobile_bodies, immobile_bodies, noise_bodies, timestamp);
            printSimpleDetectionDataTimestamped(mobile_bodies, immobile_bodies, noise_bodies, timestamp);
        }
        catch(Exception e ) {
            e.printStackTrace();
        }

        long end_time = System.currentTimeMillis();
        System.out.println("Run time: " + (end_time - start_time) + " milliseconds");
    }

    /**
     * Prints a simplified set of data about mobile and immobile locating and filtering for a given timestamp
     *
     * @param mobile_bodies the BrightBodyList[] of mobile bright bodies generated by a mobility filter
     * @param immobile_bodies the BrightBodyList[] of immobile bright bodies generated by a mobility filter
     * @param timestamp the timestamp of which to output the data
     */
    private static void printSimpleDetectionStatsTimestamped(BrightBodyList[] mobile_bodies, BrightBodyList[] immobile_bodies, BrightBodyList[] noise, int timestamp) {
        int MBB_count = mobile_bodies[timestamp].size();
        int IBB_count = immobile_bodies[timestamp].size();
        int noise_count = noise[timestamp].size();
        int total_count = MBB_count + IBB_count + noise_count;
        System.out.println("\nTotal: " + total_count);
        System.out.println("Immobile: " + IBB_count);
        System.out.println("Mobile: " + MBB_count);
        System.out.println("Noise: " + noise_count);
        System.out.println("Mobile Rate: " + MBB_count / (float) total_count);
        System.out.print("Mean Mobile Area: ");
        float mobile_area_sum = 0.0f;
        for(BrightBody b : mobile_bodies[timestamp]) {
            mobile_area_sum += b.area;
        }
        System.out.print(mobile_area_sum / MBB_count);


        System.out.print("\nMean Mobile Size: ");
        float mobile_size_sum = 0.0f;
        for(BrightBody b : mobile_bodies[timestamp]) {
            mobile_size_sum += b.body.length;
        }
        System.out.println(mobile_size_sum / MBB_count + "\n");
    }

    /**
     * Prints the set of bright bodies (data) in the BrightBodyList[]s generated by a filter for a given timestamp
     *
     * @param mobile_bodies the BrightBodyList[] of mobile bright bodies generated by a mobility filter
     * @param immobile_bodies the BrightBodyList[] of immobile bright bodies generated by a mobility filter
     * @param timestamp the timestamp of which to output the data
     */
    private static void printSimpleDetectionDataTimestamped(BrightBodyList[] mobile_bodies, BrightBodyList[] immobile_bodies, BrightBodyList[] noise_bodies, int timestamp) {
        System.out.println();
        System.out.println("Mobile:\n" + mobile_bodies[timestamp]);
        System.out.println("Immobile:\n" + immobile_bodies[timestamp]);
        System.out.println("Noise: " + noise_bodies[timestamp]);
        System.out.println();
    }
}