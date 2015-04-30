package model;

import main.BioPSy;

/**
 * Created by Fedor Shmarov on 23/03/15.
 */
public class AdvancedOptionsModel {

    private static String drealBinPath = "./dReal";

    private static String drealOptions = "-precision=1e-3";

    private static String parsynBinPath = "./ParSyn";

    private static String parsynOptions = "-e 1e-3";

    private static int parsynPID = -1;

    public static int getParsynPID() {
        return parsynPID;
    }

    public static void setParsynPID(int parsynPID) {
        AdvancedOptionsModel.parsynPID = parsynPID;
    }

    public static String getDrealBinPath() {
        return drealBinPath;
    }

    public static void setDrealBinPath(String drealBinPath) {
        AdvancedOptionsModel.drealBinPath = drealBinPath;
    }

    public static String getDrealOptions() {
        return drealOptions;
    }

    public static void setDrealOptions(String drealOptions) {
        AdvancedOptionsModel.drealOptions = drealOptions;
    }

    public static String getParsynBinPath() {
        return parsynBinPath;
    }

    public static void setParsynBinPath(String parsynBinPath) {
        AdvancedOptionsModel.parsynBinPath = parsynBinPath;
    }

    public static String getParsynOptions() {
        return parsynOptions;
    }

    public static void setParsynOptions(String parsynOptions) {
        AdvancedOptionsModel.parsynOptions = parsynOptions;
    }
}
