/*
 * KMeans.java
 * 
 * Created on Nov 22, 2007, 11:37:35 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kmeans;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class KMeans {

    private String fileName;
    private DataSet dataset;
    private int K;
//    private Vector<Points>centroids;
    private ArrayBlockingQueue<Cluster>clusters;
    
    private boolean dataSetInitialized;

    
    public enum DistanceFunction {Manhattan, SquaredEuclidean, Cosine}
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
//        centroids = new Vector<Points>(K);
        clusters = new ArrayBlockingQueue<Cluster>(K);
        fileName = new String();
        dataset = new DataSet();
        dataSetInitialized = false;
    }
    
    public synchronized void LoadData(final String filename, final char FileDelimiter, final int[] columnsToUse, final String[] columnName) {
        setFileName(filename);
        final Thread t = new Thread(new Runnable() {
            public void run() {
                dataset.InitializeDataSet(fileName, FileDelimiter, columnsToUse, columnName);
                dataSetInitialized = true;
            }
        });
        t.start();
    }
    
    public double SumSquaredError() {
        double d = 0.0;
        for (int i = 0; i < K; ++i) {
            Cluster c = clusters.poll();
            try {
                clusters.put(c);
                d += SquaredError(c);
            } catch (InterruptedException ex) {
                Logger.getLogger(KMeans.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return d;
    }
    
    @SuppressWarnings(value = "unchecked")
    private double SquaredError(Cluster c) {
        double d = 0.0;
        Vector<Integer> v = c.getPoints();
        for (Integer i : v) {
            double d1 = DistanceFunctions.Minkowski(c.getCentroid(), dataset.GetPointsByIndex(i));
            d += Math.pow(d1, 2.0);
        }

        return d;
    }
    
    public void ChooseCentroids(int val){
        switch(val){
        default:
            ChooseCentroidsBySample();
            break;
        }
    }
    
    private void ChooseCentroidsBySample(){
        Random r = new Random();
//        centroids.clear();
        
        int sz = dataset.GetSize();
        int num[] = new int[K]; // index of centroid point
                                // initially the centroid is an existing point
        for (int i = 0; i < K; ++i) {
            num[i] = Math.abs(r.nextInt(32) % sz);
        }

        clusters.clear();
        // Add points to clusters
        for(int i : num){
            try {
                Cluster c = new Cluster(dataset.GetPointsByIndex(i));
                clusters.put(c);
            } catch (InterruptedException ex) {
                Logger.getLogger(KMeans.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public void UpdateCentroids() {
        
        for (int j = 0; j < K; ++j) {
            Cluster c = clusters.poll();

            Vector<Integer> clusterPoints = c.getPoints();
            Points tot = null;
            int sz = clusterPoints.size();
            for (int i = 0; i < clusterPoints.size(); ++i) {
                if (i > 0) {
                    tot = tot.add(dataset.GetPointsByIndex(i));
                } else {
                    tot = dataset.GetPointsByIndex(i);
                }
            }
            if(sz != 0)
                tot = tot.multiply(1.0 / sz);

            // New centroid is the mean of its points
            c.defineCentroid(tot);
            try {
                clusters.put(c);
            } catch (InterruptedException ex) {
                Logger.getLogger(KMeans.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @SuppressWarnings(value = "unchecked")
    public void RunKMeans(final long sigma) {
        ChooseCentroids(1);
        
        int iteration = 0;
        double error_change = 0.0;
        do {
            for (int i = 0; i < K; ++i) {
                Cluster c = clusters.poll();
                c.clearDataPoints();
                try {
                    clusters.put(c);
                } catch (InterruptedException ex) {
                    Logger.getLogger(KMeans.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            // Compare the distance of each point add it to the nearest cluster
            for (Iterator<Points> iter = dataset.iterator(); iter.hasNext(); iter.next()) {
                //Iterate through the points
                PriorityQueue<Double> q_distances = new PriorityQueue<Double>(K);
                int closestClusterIndex = 0;
                for (int i = 0; i < K; ++i) {
                    Cluster c = clusters.poll();
                    Double d = DistanceFunctions.Minkowski(c.getCentroid(), ((DataSet) iter).getCurrentPoints());
                    q_distances.offer(d);
                    // If it is in the front of the PriorityQuene it is smallest
                    if (q_distances.peek().equals(d)) {
                        closestClusterIndex = i;
                    }
                    try {
                        clusters.put(c);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(KMeans.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                //Cycle around to correct point
                while (closestClusterIndex-- > 0) {
                    Cluster c = clusters.poll();
                    try {
                        clusters.put(c);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(KMeans.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                //Now put points in cluster
                Cluster c = clusters.poll();
                c.addPoint(((DataSet) iter).getCurrentIndex());
                try {
                    clusters.put(c);
                } catch (InterruptedException ex) {
                    Logger.getLogger(KMeans.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            System.out.println("Iteration: " + iteration++);
            printCentroid();
            UpdateCentroids();
            error_change = SumSquaredError() - error_change;
        } while (error_change > sigma);
        System.out.println("Final Centroids:\n\t ");
        printCentroid();
    }

    /**
     * Prints clusters to 'filename'.cluster
     * **/
    public void printCentroid(){
        int i = 0;
        for(Cluster c : clusters){
            System.out.println("Cluster["+i+"] Size: "+c.ClusterSize());
            try{
            System.out.println("Centroid["+i+"]: "+c.getCentroid().toString());
            }catch(NullPointerException npe){
                System.out.println("Centroid["+i+"]: null");
            }
            ++i;
        }
    }
    
    public boolean ready(){
        return dataSetInitialized;
    }
    
    /* Getters and Setters *********************************/
    public int getK() {
        return K;
    }

    public void setK(int K) {
        this.K = K;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
}
