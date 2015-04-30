package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by fedor on 29/04/15.
 */
public class Box implements Comparable{

    public enum BoxType {SAT, UNSAT, UNDET, UNDEFINED, DOMAIN};
    private List<Interval> intervals;
    //private Interval minInterval;
    //private Interval maxInterval;
    private BoxType type;
    private double time;

    public Box(List<Interval> intervals, double time, BoxType type) {
        this.intervals = intervals;
        Collections.sort(intervals);
        //this.minInterval = intervals.get(0);
        //this.maxInterval = intervals.get(intervals.size() - 1);
        this.type = type;
        this.time = time;
    }

    public Box(BoxType type) {
        this();
        this.type = type;
        this.time = 0;
    }

    public double getTime() {
        return time;
    }

    public Box(List<Interval> intervals, BoxType type) {
        this(intervals, 0, type);
    }

    public Box(List<Interval> intervals) {
        this(intervals, BoxType.UNDEFINED);
    }

    public Box() {
        intervals = new ArrayList<Interval>();
        time = 0;
    }

    public List<Interval> getIntervals() {
        return intervals;
    }

    /*
    public Interval getMinInterval() {
        return minInterval;
    }
    */

    /*
    public Interval getMaxInterval() {
        return maxInterval;
    }
    */

    public BoxType getType() {
        return type;
    }

    public boolean addInterval(Interval i) {
        if(!Arrays.asList(namesAsStringArray()).contains(i.getName())) {
            intervals.add(i);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeInterval(Interval i) {
        return intervals.remove(i);
    }

    @Override
    public String toString() {
        String s = "";
        for(int i = 0; i < intervals.size() - 1; i++) {
            s += intervals.get(i) + "x";
        }
        s += intervals.get(intervals.size() - 1);
        return s;
    }

    public String[] dataAsStringArray() {
        String[] res = new String[intervals.size()];
        for(int i = 0; i < intervals.size(); i++) {
            res[i] = intervals.get(i).toString();
        }
        return res;
    }

    public String[] namesAsStringArray() {
        String[] res = new String[intervals.size()];
        for(int i = 0; i < intervals.size(); i++) {
            res[i] = intervals.get(i).getName();
        }
        return res;
    }

    public String[] namesAsTableEntry() {
        String[] res = new String[intervals.size() + 2];
        for(int i = 0; i < intervals.size(); i++) {
            res[i] = intervals.get(i).getName();
        }
        res[intervals.size()] = "-Time-";
        res[intervals.size() + 1] = "-SMT-";
        return res;
    }

    public String[] dataAsTableEntry() {
        String[] res = new String[intervals.size() + 2];
        for(int i = 0; i < intervals.size(); i++) {
            res[i] = intervals.get(i).toString();
        }
        res[intervals.size()] = Double.toString(time);
        res[intervals.size() + 1] = getType().toString();
        return res;
    }

    public int compareTo(Object o) {
        Box b = (Box) o;

        if(this.getTime() < b.getTime()) {
            return -1;
        } else {
            if(this.getTime() > b.getTime()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

}
