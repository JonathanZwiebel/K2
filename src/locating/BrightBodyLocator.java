package locating;

import brightbodies.BrightBody;
import brightbodies.BrightBodyList;
import brightbodies.CartesianPoint;

import java.util.ArrayList;

/**
 * This class has static methods to extract all of the bright bodies from an image given the mask as a BrightBodyList
 */
public class BrightBodyLocator {
    /**
     * Method called by client to extract the BrightBodies
     * @param original_image original bright body image
     * @param binary_image binary mask showing points to search
     * @return BrightBodyList of all bright bodies in the original image
     */
    public static BrightBodyList binaryLocate(float[][] original_image, int[][] binary_image) {
        int[][] blob_labels = extractBlobLabels(binary_image);
        int blob_count = getMaxValue(blob_labels);

        ArrayList<CartesianPoint>[] bright_bodies = new ArrayList[blob_count];
        for(int i = 0; i < bright_bodies.length; i++) {
            bright_bodies[i] = new ArrayList();
        }
        for(int i = 0; i < original_image.length; i++) {
            for(int j = 0; j < original_image[0].length; j++) {
                if(blob_labels[i][j] != -1) {
                    // TODO: Fix bug where the position is being echoed
                    bright_bodies[blob_labels[i][j] - 1].add(new CartesianPoint(j, blob_labels.length - 1 - i));
                }
            }
        }

        BrightBodyList true_bright_bodies = new BrightBodyList();
        for(ArrayList bright_body : bright_bodies) {
            true_bright_bodies.add(new BrightBody(original_image, (CartesianPoint[]) bright_body.toArray(new CartesianPoint[bright_body.size()])));
        }
        return true_bright_bodies;
    }

    /**
     * Labels each cartesian point in the binary mask with an identifier such that negative points have a value of -1
     * and adjacent points share the same value. Non connected points will have different values.
     * @param binary_image binary mask to group upon
     * @return integer matrix of all labels
     */
    private static int[][] extractBlobLabels(int[][] binary_image) {
        // Creates a label matrix and initializes all values to 0
        // Within the label matrix: -1 is dark, 0 is not searced, +n is unique label n
        int[][] label_matrix = new int[binary_image.length][binary_image[0].length];
        for(int i = 0; i < label_matrix.length; i++) {
            for(int j = 0; j < label_matrix[0].length; j++) {
                label_matrix[i][j] = 0;
            }
        }

        int blobs = 1;
        for(int i = 0; i < binary_image.length; i++) {
            for(int j = 0; j < binary_image[0].length; j++) {
                if(addToLabelSet(-1, i, j, label_matrix, binary_image, blobs)){
                    blobs++;
                }
            }
        }
        return label_matrix;
    }

    /**
     * Adds a particular coordinate location to the label set, attaching it to an existing label if possible
     * @param attached_label label that parent coordinate location that called this method had, will be -1 if from extract
     * @param i the i value of the point to add
     * @param j the j value of the point to add
     * @param label_matrix the matrix of current labels that is being filled with this call
     * @param binary_image the nonmutable binary mask
     * @param blob_count the current number of blobs in the set
     * @return if there was a new blob by this call
     */
    private static boolean addToLabelSet(int attached_label, int i, int j, int[][] label_matrix, int[][] binary_image, int blob_count) {
        // Already been searched
        if(label_matrix[i][j] != 0) {
            return false;
        }

        // Has not been searched but has negative on the mask
        if(binary_image[i][j] == 0) {
            label_matrix[i][j] = -1;
            return false;
        }

        assert binary_image[i][j] == 1 && label_matrix[i][j] == 0; // light and not searched

        int new_label;
        // This is a new label set, begin new labelling system
        if(attached_label == -1) {
            new_label = blob_count;
        }
        // This is not a new label set, give this the same label as the coordiante that called this
        else {
            new_label = attached_label;
        }
        assert new_label != -1;
        label_matrix[i][j] = new_label;
        addNeighbors(new_label, i, j, label_matrix, binary_image, blob_count);
        return true;
    }

    /**
     * Adds the 4 neighbors of a particular address to the data set with the working label of the parent location
     * @param parent_label the label of the location that called this method
     * @param i the i value of the calling location
     * @param j the j value of the calling location
     * @param label_matrix the matrix of current labels that is being filled with this call
     * @param binary_image the nonmutable binary mastk
     * @param blob_count the current number of blods in the set
     * TODO: Don't call addToLabelSet if already searched
     */
    private static void addNeighbors(int parent_label, int i, int j, int[][] label_matrix, int[][] binary_image, int blob_count) {
        if(j != label_matrix[0].length - 1) {
            addToLabelSet(parent_label, i, j + 1, label_matrix, binary_image, blob_count);
        }
        if(i != label_matrix.length - 1) {
            addToLabelSet(parent_label, i + 1, j, label_matrix, binary_image, blob_count);
        }

        if(j != 0) {
            addToLabelSet(parent_label, i, j - 1, label_matrix, binary_image, blob_count);
        }
        if(i != 0) {
            addToLabelSet(parent_label, i - 1, j, label_matrix, binary_image, blob_count);
        }
    }

    // TODO: Move this out
    private static int getMaxValue(int[][] matrix) {
        int max = Integer.MIN_VALUE;
        for(int[] array : matrix) {
            for(int i : array) {
                if(i > max) {
                    max = i;
                }
            }
        }
        return max;
    }
}