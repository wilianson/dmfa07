/*
 * KMeans.java
 * 
 * Created on Nov 22, 2007, 11:37:35 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kmeans;

import java.util.Vector;

/**
 *
 * @author Administrator
 */
public class KMeans {

    private DataSet dataset;
    private int K;
    private Vector<Points>centroids;

    /* Constructor *****************************************/
    public KMeans() {
        setK(2);
        initialize();
    }
    public KMeans(int k) {
        setK(k);
        initialize();
    }
    
    private void initialize(){
        centroids = new Vector<Points>(K);
    }
    
    public void ChooseCentroids(int val){
        switch(val){
        default:
            ChooseCentroidsBySample();
            break;
        }
    }
    
    public void ChooseCentroidsBySample(){
        
    }
    
    
    
    
    /* Getters and Setters *********************************/
    public int getK() {
        return K;
    }

    public void setK(int K) {
        this.K = K;
    }
    
}
