/*
 * DistanceFunctions.java
 *
 * Created on Nov 22, 2007, 8:53:12 PM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kmeans;

/**
 *
 * @author cgrant
 */
public class DistanceFunctions {

    @SuppressWarnings(value = "unchecked")
    public static double Minkowski(Points<Number> centroid, Points<Number> value) {
        if ((value == null) || (centroid == null)) {
            if(centroid == null){
                System.err.println("DF: Centroid is " + centroid);
                //System.err.println("DF: Value is " + value);
                System.exit(3);
            }
            else if(value == null){
                System.err.println("DF: Centroid is " + centroid);
                System.err.println("DF: Value is " + value);
            }
            return Double.MIN_VALUE;
        }
        
        value = centroid.subtract(value);
        value = value.power(new Double(value.getDimension()).doubleValue());
        Double d1 = value.sum();
        Double d2 = new Double(1.0 / value.getDimension());
        return Math.pow(d1, d2);
    }
    
    public static double Euclidian(Points<Number> p1, Points<Number> p2) {
        double ret = 0.0;
        if (p1 != p2) {
            p2 = p1.subtract(p2);
            p2 = p2.abs();
            p2 = p2.power(2);
            ret = p2.sum();
            ret = Math.sqrt(ret);
        }
        return ret;
    }
}