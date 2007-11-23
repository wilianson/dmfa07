/*
 * Points.java
 *
 * Created on Nov 22, 2007, 5:02:51 PM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kmeans;

import java.util.Vector;

/**
 *
 * @author cgrant
 */
public class Points<T extends Number> {

    /** Points to the current element **/
    private int pointer;
    private int dimension;
    Vector<T> values;
    String[] names;

    Points(int dimension) {
        assert (dimension > 0);
        this.dimension = dimension;
        initialize();
    }

    private void initialize() {
        //values = new T[dimension];
        values = new Vector<T>(dimension);
        names = new String[dimension];
        pointer = 0;
    }

    /**
     * @returns false if unsucessfull
     */
    public boolean AppendPoint(T o, String name) {

        if (pointer >= dimension) {
            return false;
        } else if (pointer < 0) {
            return false;
        } else {
            values.set(pointer, o);
            names[pointer] = name;
            ++pointer;
            return true;
        }
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public String[] getNames() {
        return names;
    }

    public void setNames(String[] names) {
//        this.names = names;
        this.names = new String[names.length];
        System.arraycopy(names, 0, this.names, 0, names.length);
    }

    public Vector<T> getValues() {
        return values;
    }

    public void setValues(Vector<T> values) {
//        this.header = header;
        values = new Vector<T>(values.size());
        System.arraycopy(values, 0, this.values, 0, values.size());
    }

    @SuppressWarnings(value = "unchecked")
    public Points subtract(Points<T> p) {
        if (dimension != p.dimension) {
            throw new NumberFormatException("Dimensions must be equal");
        }
        Points returnP = new Points(dimension);

        for (int i = 0; i < dimension; ++i) {

            double d1 = values.get(i).doubleValue();
            double d2 = p.values.get(i).doubleValue(); //.doubleValue();
            returnP.AppendPoint((Number) (d1 - d2), names[i]);
        }


        return returnP;
    }

    @SuppressWarnings(value = "unchecked")
    public Points power(int dim) {
        Points p = new Points(dimension);

        for (int i = 0; i < dimension; ++i) {
            double d = values.get(i).doubleValue();

            p.AppendPoint((Number) Math.pow( d, dim*1.0), names[i]);
        }

        return p;
    }

    public double sum() {
        double dRet = 0.0;
        for (Object d : values) {
            dRet += (Double) d;
        }
        return dRet;
    }
}