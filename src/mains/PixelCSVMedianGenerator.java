package mains;

import core.preprocess.K2Preprocessor;
import core.preprocess.Preprocessor;
import nom.tam.fits.Fits;
import stats.MedianValue;

import java.io.File;
import java.io.FileWriter;

/**
 * A top level runnable type that creates pixel .csv files for a given pixel, the median, pixel, the difference
 * between the given pixel and the median pixel, and the ratio between the given pixel and the median pixel
 *
 * @author Jonathan Zwiebel
 * @version 15 November 2016
 */
public class PixelCSVMedianGenerator {
    public static void run(String[] args) throws Exception {
        assert args[0].equals("PIXEL_CSV_MEDIAN_GENERATOR");

        int current_arg = 1;

        String file_in = args[current_arg];
        current_arg++;

        int x_position = Integer.parseInt(args[current_arg]);
        current_arg++;
        int y_position = Integer.parseInt(args[current_arg]);
        current_arg++;

        String pixel_out = args[current_arg];
        current_arg++;
        String median_out = args[current_arg];
        current_arg++;
        String difference_out = args[current_arg];
        current_arg++;
        String ratio_out = args[current_arg];
        current_arg++;

        Fits fits = new Fits(new File(file_in));
        FileWriter pixel_writer = new FileWriter(new File(pixel_out));
        FileWriter median_writer = new FileWriter(new File(median_out));
        FileWriter difference_writer = new FileWriter(new File(difference_out));
        FileWriter ratio_writer = new FileWriter(new File(ratio_out));

        Preprocessor preprocessor = new K2Preprocessor(fits);
        float[][][] data = preprocessor.read();

        int x_reflected = data[0].length - 1 - y_position;
        int y_reflected = x_position;

        for(int i = 0; i < data.length; i++) {
            float value = data[i][x_reflected][y_reflected];
            float median = MedianValue.medianValue(data[i]);
            float difference = value - median;
            float ratio = value / median;

            pixel_writer.write(i + "," + value + "\n");
            median_writer.write(i + "," + median + "\n");
            difference_writer.write(i + "," + difference + "\n");
            ratio_writer.write(i + "," + ratio + "\n");
        }

        pixel_writer.close();
        median_writer.close();
        difference_writer.close();
    }
}
