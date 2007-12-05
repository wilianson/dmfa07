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
            }
            else if(value == null){
                System.err.println("DF: Centroid is " + centroid);
                System.err.println("DF: Value is " + value);
            }
                
            return Double.MAX_VALUE;
        }
        value = centroid.subtract(value);
        value = value.abs();
        value = value.power((double)value.getDimension());
        double d = value.sum();

        return Math.pow(d, 1.0 / value.getDimension());
    }
}