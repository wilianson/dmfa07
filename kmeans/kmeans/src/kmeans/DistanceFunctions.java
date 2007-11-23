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
 * @author Administrator
 */
public class DistanceFunctions {

    @SuppressWarnings(value = "unchecked")
    public static double Minkowski(Points<Number> centroid, Points<Number> value) {

        value = centroid.subtract(value);
        value = value.power(value.getDimension());
        double d = value.sum();

        return Math.pow(d, 1 / value.getDimension());
    }
}