package util;

/**
 * Created by fedor on 29/04/15.
 */
public class Interval implements Comparable {

    private double left;
    private double right;
    private double mid;
    private double width;
    private String name;
    private static int counter = 0;

    public Interval(double left, double right, String name) throws Exception {

        if (left > right) throw new Exception("Left endpoint is greater than the right one");
        if (name == null) throw new Exception("Interval name cannot be NULL");

        this.left = left;
        this.right = right;

        this.width = right - left;
        this.mid = (right + left) / 2;
        this.name = name;
    }

    public Interval(double left, double right) throws Exception {

        this(left, right, "var" + Integer.toString(counter));
        counter++;
    }

    public Interval(double point) throws Exception {

        this(point, point);
    }

    public double getLeft() {
        return left;
    }

    public double getRight() {
        return right;
    }

    public double getMid() {
        return mid;
    }

    public double getWidth() {
        return width;
    }

    public String getName() {
        return name;
    }

    @Override
    /*
    public int compareTo(Object o) {

        Interval i = (Interval) o;

        if (this.getWidth() < i.getWidth()){
            return -1;
        } else {
            if (this.getWidth() > i.getWidth()){
                return 1;
            } else {
                return 0;
            }
        }
    }
    */

    public int compareTo(Object o) {
        Interval i = (Interval) o;
        return this.getName().compareTo(i.getName());
    }

    @Override
    public String toString()
    {
        return "[" + Double.toString(left) + "," + Double.toString(right) + "]";
    }

}
