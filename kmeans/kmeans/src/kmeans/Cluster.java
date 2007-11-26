/*
 * Cluster.java
 * 
 * Created on Nov 23, 2007, 12:27:54 AM
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
public class Cluster {
    
    private Points centroid;
    /** These are indexes to the points with belong to this cluster */
    private Vector<Integer>dataPoints;

    public Cluster(){
        initialize();
    }

    public Cluster(Points centroid) {
        initialize();
        defineCentroid(centroid);
    }

    private void initialize(){
        centroid = null;
        dataPoints = new Vector<Integer>();
    }
    
    public void defineCentroid(Points c){
        setCentroid(c);
    }
    public int ClusterSize(){
        return dataPoints.size();
    }
    public boolean addPoint(int index){
        return dataPoints.add(index);
    }
    public void clearDataPoints(){
        dataPoints.clear();
    }
    
    /* Getter Setters *****************************/
    public Points getCentroid() {
        return centroid;
    }

    public void setCentroid(Points centroid) {
        this.centroid = centroid;
    }

    public Vector<Integer> getPoints() {
        return dataPoints;
    }

    public void setPoints(Vector<Integer> points) {
        this.dataPoints = points;
    }
    
    
}
