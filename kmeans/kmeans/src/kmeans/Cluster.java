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
    private Vector<Integer>points;

    
    
    
    
    
    /* Getter Setters *****************************/
    public Points getCentroid() {
        return centroid;
    }

    public void setCentroid(Points centroid) {
        this.centroid = centroid;
    }

    public Vector<Integer> getPoints() {
        return points;
    }

    public void setPoints(Vector<Integer> points) {
        this.points = points;
    }
    
    
}
