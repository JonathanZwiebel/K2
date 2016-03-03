package mains;

import filter.ReferenceFrameGenerationMethod;
import locate.BinaryLocatorThresholdType;

/**
 * A top level runnable type that will perform locate-track within a range of parameters on a given file and output
 * information for a fixed timestamp comparing the different parameters. This works by performing single locates, and
 * then all filters over that given location data.
 *
 * @author Jonathan Zwiebel
 * @version February 26th, 2016
 */
public final class LFBinRefMassFixedTime {
    private enum BinaryLocatorMassType {
        SINGLE, // specify type and arguments that go with it
        GIVEN_RANGE, // specify increment, min, and max
        MEAN_SHIFTED_RANGE, // specify increment, low, and high count
        MEAN_SCALED_RANGE // specify scalar increment, low, and high count
    }

    private enum ReferenceFrameMassType {
        SINGLE, // specify type and arguments that go with it
        GIVEN_RANGE, // specify increment, min, and max
        MEAN_SHIFTED_RANGE, // specify increment, low, and high count
        MEAN_SCALED_RANGE // specify scalar increment, low, and high count
    }

    public static void run(String[] args) {
        assert args[0].equals("LF_MASS_FIXED_TIME");

        // TODO: Consider making this only accessible by method so every time it is checked, the number actually increments
        int current_arg = 1;

        String filename = args[current_arg];
        current_arg++;

        BinaryLocatorMassType binaryLocatorMassType = null;
        BinaryLocatorThresholdType binaryLocatorMassTypeSingleType = null; // this one is only filled in the SINGLE case
        float[] binaryLocatorMassTypeArgs = null;
        String binaryLocatorMassTypeString = args[current_arg];
        current_arg++;
        switch(binaryLocatorMassTypeString) {
            case "SINGLE":
                binaryLocatorMassType = BinaryLocatorMassType.SINGLE;
                String binaryLocatorMassTypeSingleTypeString = args[current_arg];
                current_arg++;
                switch(binaryLocatorMassTypeSingleTypeString) {
                    case "ABSOLUTE":
                        binaryLocatorMassTypeSingleType = BinaryLocatorThresholdType.ABSOLUTE;
                        binaryLocatorMassTypeArgs = new float[]{Float.parseFloat(args[current_arg])};
                        current_arg++;
                        break;
                    case "MEAN":
                        binaryLocatorMassTypeSingleType = BinaryLocatorThresholdType.MEAN;
                        binaryLocatorMassTypeArgs = new float[]{};
                        break;
                    case "MEAN_SHIFTED":
                        binaryLocatorMassTypeSingleType = BinaryLocatorThresholdType.MEAN_SHIFTED;
                        binaryLocatorMassTypeArgs = new float[]{Float.parseFloat(args[current_arg])};
                        current_arg++;
                        break;
                    case "MEAN_SCALED":
                        binaryLocatorMassTypeSingleType = BinaryLocatorThresholdType.MEAN_SCALED;
                        binaryLocatorMassTypeArgs = new float[]{Float.parseFloat(args[current_arg])};
                        current_arg++;
                        break;
                    default:
                        System.err.println("Illegal binaryLocatorMassTypeSingleType: " + binaryLocatorMassTypeSingleTypeString);
                        System.exit(1);
                }
                break;
            case "GIVEN_RANGE":
                binaryLocatorMassType = BinaryLocatorMassType.GIVEN_RANGE;
                binaryLocatorMassTypeArgs = new float[]{Float.parseFloat(args[current_arg]), Float.parseFloat(args[current_arg + 1]), Float.parseFloat(args[current_arg + 2])};
                current_arg += 3;
                break;
            case "MEAN_SHIFTED_RANGE":
                binaryLocatorMassType = BinaryLocatorMassType.MEAN_SHIFTED_RANGE;
                binaryLocatorMassTypeArgs = new float[]{Float.parseFloat(args[current_arg]), Float.parseFloat(args[current_arg + 1]), Float.parseFloat(args[current_arg + 2])};
                current_arg += 3;
                break;
            case "MEAN_SCALED_RANGE":
                binaryLocatorMassType = BinaryLocatorMassType.MEAN_SCALED_RANGE;
                binaryLocatorMassTypeArgs = new float[]{Float.parseFloat(args[current_arg]), Float.parseFloat(args[current_arg + 1]), Float.parseFloat(args[current_arg + 2])};
                current_arg += 3;
                break;
            default:
                System.err.println("Illegal BinaryLocatorMassType: " + binaryLocatorMassTypeString);
                System.exit(1);
        }

        float similarity_threshold = Float.parseFloat(args[current_arg]);
        current_arg++;

        // TODO[Moderate]: refFrameMassType should be of a different enum then BinaryLocatorMassType
        ReferenceFrameMassType refFrameMassType = null;
        ReferenceFrameGenerationMethod refFrameMassTypeSingleType = null; // this one is only filled in the SINGLE case
        float[] refFrameMassTypeArgs = null;
        String refFrameMassTypeString = args[current_arg];
        current_arg++;
        switch(refFrameMassTypeString) {
            case "SINGLE":
                refFrameMassType = ReferenceFrameMassType.SINGLE;
                String refFrameMassTypeSingleTypeString = args[current_arg];
                current_arg++;
                switch(refFrameMassTypeSingleTypeString) {
                    case "BINARY_LOCATOR_ABSOLUTE":
                        refFrameMassTypeSingleType = ReferenceFrameGenerationMethod.BINARY_LOCATOR_ABSOLUTE;
                        refFrameMassTypeArgs = new float[]{Float.parseFloat(args[current_arg])};
                        current_arg++;
                        break;
                    case "BINARY_LOCATOR_MEAN":
                        refFrameMassTypeSingleType = ReferenceFrameGenerationMethod.BINARY_LOCATOR_MEAN;
                        refFrameMassTypeArgs = new float[]{};
                        break;
                    case "BINARY_LOCATOR_MEAN_SHIFTED":
                        refFrameMassTypeSingleType = ReferenceFrameGenerationMethod.BINARY_LOCATOR_MEAN_SHIFTED;
                        refFrameMassTypeArgs = new float[]{Float.parseFloat(args[current_arg])};
                        current_arg++;
                        break;
                    case "BINARY_LOCATOR_MEAN_SCALED":
                        refFrameMassTypeSingleType = ReferenceFrameGenerationMethod.BINARY_LOCATOR_MEAN_SCALED;
                        refFrameMassTypeArgs = new float[]{Float.parseFloat(args[current_arg])};
                        current_arg++;
                        break;
                    default:
                        System.err.println("Illegal binaryLocatorMassTypeSingleType: " + refFrameMassTypeSingleTypeString);
                        System.exit(1);
                }
                break;
            case "GIVEN_RANGE":
                refFrameMassType = ReferenceFrameMassType.GIVEN_RANGE;
                refFrameMassTypeArgs = new float[]{Float.parseFloat(args[current_arg]), Float.parseFloat(args[current_arg + 1]), Float.parseFloat(args[current_arg + 2])};
                current_arg += 3;
                break;
            case "MEAN_SHIFTED_RANGE":
                refFrameMassType = ReferenceFrameMassType.MEAN_SHIFTED_RANGE;
                refFrameMassTypeArgs = new float[]{Float.parseFloat(args[current_arg]), Float.parseFloat(args[current_arg + 1]), Float.parseFloat(args[current_arg + 2])};
                current_arg += 3;
                break;
            case "MEAN_SCALED_RANGE":
                refFrameMassType = ReferenceFrameMassType.MEAN_SCALED_RANGE;
                refFrameMassTypeArgs = new float[]{Float.parseFloat(args[current_arg]), Float.parseFloat(args[current_arg + 1]), Float.parseFloat(args[current_arg + 2])};
                current_arg += 3;
                break;
            default:
                System.err.println("Illegal RefFrameMassType: " + binaryLocatorMassTypeString);
                System.exit(1);
        }

        int timestamp = Integer.parseInt(args[current_arg]);
        current_arg++;
    }
}
