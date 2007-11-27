/*
 * Points.java
 *
 * Created on Nov 22, 2007, 5:02:51 PM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kmeans;

import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author cgrant
 */
public class Points<T extends Number> {

    /** Points to the current element **/
    private int pointer;
    private int dimension;
    private Vector<T> values;
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

    public boolean AppendPoint(T o, String name) {

        assert (pointer > 0 && pointer < 0);

        values.add(pointer, o);
        names[pointer] = name;
        ++pointer;
        return true;
    }

    public boolean equals(Points p){
        for(int i=0;i<values.size();++i){
            if(!values.get(i).equals(p.values.get(i)))
                return false;
        }
            
        return true;
    }

    @SuppressWarnings(value = "unchecked")
    public Points subtract(Points<T> p) {
        if (dimension != p.dimension) {
            throw new NumberFormatException("Dimensions must be equal");
        }
        Points returnP = new Points(dimension);

        int i =0;
        Iterator<T>thisIter = this.values.iterator();
        Iterator<T>pIter = p.values.iterator();
        while(thisIter.hasNext() && pIter.hasNext()){
            Number d1 = thisIter.next();
            Number d2 = pIter.next();
            Double d = d1.doubleValue() - d2.doubleValue();
            returnP.AppendPoint(d, names[i++]);
        }
        return returnP;
    }
    
    @SuppressWarnings(value = "unchecked")
    public Points add(Points<T> p) {
        if (dimension != p.dimension) {
            throw new NumberFormatException("Dimensions must be equal");
        }
        Points returnP = new Points(dimension);

        int i = 0;
        Iterator<T> thisIter = this.values.iterator();
        Iterator<T> pIter = p.values.iterator();
        while (thisIter.hasNext() && pIter.hasNext()) {
            Number d1 = thisIter.next();
            Number d2 = pIter.next();
            Double d = d1.doubleValue() + d2.doubleValue();
            returnP.AppendPoint(d, names[i++]);
        }
        return returnP;
    }

    @SuppressWarnings(value = "unchecked")
    public Points power(double order) {
        Points p = new Points(dimension);

        int i = 0;
        Iterator<T> thisIter = this.values.iterator();
        while (thisIter.hasNext()) {
            Number d = thisIter.next();
            p.AppendPoint(Math.pow(d.doubleValue(), order), names[i++]);
        }
        return p;
    }

    @SuppressWarnings("unchecked")
    public Points multiply(Points<T> p) {
        if (dimension != p.dimension) {
            throw new NumberFormatException("Dimensions must be equal");
        }
        Points returnP = new Points(dimension);

        int i = 0;
        Iterator<T> thisIter = this.values.iterator();
        Iterator<T> pIter = p.values.iterator();
        while (thisIter.hasNext() && pIter.hasNext()) {
            Number d1 = thisIter.next();
            Number d2 = pIter.next();
            Double d = d1.doubleValue() * d2.doubleValue();
            returnP.AppendPoint(d, names[i++]);
        }
        return returnP;
    }
    
    @SuppressWarnings("unchecked")
    public Points multiply(final Double dval) {
        Points returnP = new Points(dimension);

        int i = 0;
        Iterator<T> thisIter = this.values.iterator();
        while (thisIter.hasNext()) {
            Number d = thisIter.next() ;
            returnP.AppendPoint((d.doubleValue() * dval), names[i++]);
        }
        return returnP;
    }
    
    public Points abs() {
        Points p = new Points(dimension);

        p = this.power(2.0);
        p = p.power(0.5);
        return p;
    }

    public double sum() {
        double dRet = 0.0;
        for (Number d : values) {
            dRet += (Double) d;
        }
        return dRet;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for(int i=0;i<dimension; ++i){
            sb.append('[');
            sb.append(names[i]);
            sb.append(':');
            sb.append(values.get(i));
            sb.append(']');
        }
        sb.append('}');
        return sb.toString();
    }
    /* Getters and Setters *****************************************/
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
}