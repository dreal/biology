package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fedor on 30/04/15.
 */
public class TimeSeriesModel {

    private static List<Double> timePoints = new ArrayList<Double>();

    public static boolean addTimePoint(Double t) {
        return timePoints.add(t);
    }

    public static List<Double> getTimePoints() {
        return timePoints;
    }

}
