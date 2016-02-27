package mains;

import brightbodies.BrightBody;
import brightbodies.BrightBodyList;
import filtering.MobilityFilter;
import filtering.ReferenceMobilityFilter;
import helper.FitsHelper;
import locating.BinaryLocator;
import locating.Locator;
import mask.BinaryImageMask;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import preprocessing.K2Preprocessor;
import preprocessing.Preprocessor;
import locating.BinaryLocator.ThresholdType;
import filtering.ReferenceMobilityFilter.ReferenceBodyDetectionMethod;

import java.io.File;
import java.io.IOException;

/**
 * @author Jonathan Zwiebel
 * @version February 3rd, 2016
 *
 * TODO: Consider the data output in a fifth macro step
 */
public class Run {
    /**
     * Main method to be run for program execution
     * @param args Location, Initial locating threshold, Similarity threshold, Reference locating threshold, Timestamp
     */
    public static void main(String[] args) {
        // Start data parsing
        int argumentReadLoc = 0;

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
            case "GIVEN":
                detection_threshold_type = ThresholdType.GIVEN;
                detection_args = new float[]{Float.parseFloat(args[argumentReadLoc])};
                argumentReadLoc++;
                break;
            case "MEAN_SHIFTED":
                detection_threshold_type = ThresholdType.MEAN_SHIFTED;
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
            case "GIVEN":
                reference_frame_detection_threshold_type = ReferenceBodyDetectionMethod.ABSOLUTE;
                reference_frame_detection_args = new float[]{Float.parseFloat(args[argumentReadLoc])};
                argumentReadLoc++;
                break;
            default:
                System.out.println("Illegal reference frame detection method");
                break;
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
            writeBinaryImageMask("data/mask/905-timestamp" + timestamp + "bin-" + (int) detection_args[0] + ".fits", data[timestamp], detection_args[0]);

            System.out.println("Filtering");
            MobilityFilter filter = new ReferenceMobilityFilter(bodies, data, similarity_threshold, reference_frame_detection_threshold_type, reference_frame_detection_args);
            BrightBodyList[][] filtered_bodies = filter.filter();
            BrightBodyList[] immobile_bodies = filtered_bodies[MobilityFilter.IMMOBILE_INDEX];
            BrightBodyList[] mobile_bodies = filtered_bodies[MobilityFilter.MOBILE_INDEX];

            printSimpleDetectionStatsTimestamped(mobile_bodies, immobile_bodies, timestamp);
            printSimpleDetectionDataTimestamped(mobile_bodies, immobile_bodies, timestamp);
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
    public static void printSimpleDetectionStatsTimestamped(BrightBodyList[] mobile_bodies, BrightBodyList[] immobile_bodies, int timestamp) {
        int mob_count = mobile_bodies[timestamp].size();
        int imob_count = immobile_bodies[timestamp].size();
        int total_count = mob_count + imob_count;
        System.out.println("\nTotal: " + total_count);
        System.out.println("Immobile: " + imob_count);
        System.out.println("Mobile: " + mob_count);
        System.out.println("Mobile Rate: " + mob_count / (float) total_count);
        System.out.print("Mean Mobile Area: ");
        float mobile_area_sum = 0.0f;
        for(BrightBody b : mobile_bodies[timestamp]) {
            mobile_area_sum += b.area;
        }
        System.out.print(mobile_area_sum / mob_count);


        System.out.print("\nMean Mobile Size: ");
        float mobile_size_sum = 0.0f;
        for(BrightBody b : mobile_bodies[timestamp]) {
            mobile_size_sum += b.body.length;
        }
        System.out.println(mobile_size_sum / mob_count + "\n");
    }

    /**
     * Prints the set of bright bodies (data) in the BrightBodyList[]s generated by a filter for a given timestamp
     *
     * @param mobile_bodies the BrightBodyList[] of mobile bright bodies generated by a mobility filter
     * @param immobile_bodies the BrightBodyList[] of immobile bright bodies generated by a mobility filter
     * @param timestamp the timestamp of which to output the data
     */
    public static void printSimpleDetectionDataTimestamped(BrightBodyList[] mobile_bodies, BrightBodyList[] immobile_bodies, int timestamp) {
        System.out.println("\nMobile:\n" + mobile_bodies[timestamp]);
        System.out.println("Immobile:\n" + immobile_bodies[timestamp] + "\n");
    }


    /**
     * Writes a quickly generated BinaryImageMask to a fits file to make analysis of detected patterns easier
     *
     * @param location location in the filepath to write to
     * @param data floating point astro-data
     * @param threshold threshold for the BinaryImageMask
     * @throws FitsException
     * @throws IOException
     *
     * TODO[Minor]: Make this work with mean
     */
    public static void writeBinaryImageMask(String location, float[][] data, float threshold) throws FitsException, IOException {
        BinaryImageMask mask_generator = new BinaryImageMask(data);
        boolean[][] mask = mask_generator.mask(threshold);
        FitsHelper.write2DImage(mask, location);
    }
}