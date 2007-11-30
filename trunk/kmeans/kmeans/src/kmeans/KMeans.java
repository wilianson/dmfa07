/*
 * KMeans.java
 *
 * Created on Nov 22, 2007, 11:37:35 PM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kmeans;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author Administrator
 */
public class KMeans {

    private String fileName;
    private DataSet dataset;
    private int K;
//    private Vector<Points>centroids;
    private BlockingQueue<Cluster> clusters;
    private boolean dataSetInitialized;
    private Vector<String> colorlist;

    public enum DistanceFunction {

        Manhattan, SquaredEuclidean, Cosine
    }

    /* Constructor *****************************************/
    public KMeans() {
        setK(2);
        initialize();
    }

    public KMeans(int k) {
        setK(k);
        initialize();
    }

    private void initialize() {
//        centroids = new Vector<Points>(K);
        clusters = new ArrayBlockingQueue<Cluster>(K);
        fileName = new String();
        dataset = new DataSet();
        dataSetInitialized = false;
        initializeColorList();
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
        long d = (long) 0.0;
        for (int i = 0; i < K; ++i) {
            Cluster c = clusters.poll();
            clusters.offer(c);
            d += SquaredError(c);
        }
        return Math.sqrt(d);
    }

    @SuppressWarnings(value = "unchecked")
    private double SquaredError(final Cluster c) {
        long d = (long) 0.0;
        Vector<Integer> v = c.getPoints();
        for (Integer i : v) {
            double d1 = DistanceFunctions.Minkowski(c.getCentroid(), dataset.GetPointsByIndex(i));
            d += (long) Math.pow(d1, 2.0);
        }

        return d;
    }

    public void ChooseCentroids(int val) {
        switch (val) {
            default:
                ChooseCentroidsBySample();
                break;
        }
    }

    private void ChooseCentroidsBySample() {
        Random r = new Random();
//        centroids.clear();
        int sz = dataset.GetSize();
        int[] num = new int[K]; // index of centroid point
        // initially the centroid is an existing point
        for (int i = 0; i < K; ++i) {
            num[i] = Math.abs(r.nextInt(32) % sz);
        }

        clusters.clear();
        // Add points to clusters
        for (int i : num) {
            Cluster c = new Cluster(dataset.GetPointsByIndex(i));
            clusters.offer(c);
        }
    }

    @SuppressWarnings(value = "unchecked")
    public void UpdateCentroids() {
        // Find the mean point of all the points in the cluster
        // Make that the centroid
        for (int j = 0; j < K; ++j) {
            Cluster c = clusters.poll();

            Vector<Integer> clusterPoints = c.getPoints();
            Points tot = null;
            double sz = (double) clusterPoints.size();
            for (int i = 0; i < sz; ++i) {
                if (i > 0) {
                    tot = tot.add(dataset.GetPointsByIndex(i));
                } else {
                    tot = dataset.GetPointsByIndex(i);
                }
            }
            if (sz != 0.0) {
                tot = tot.multiply(1.0 / sz);
            }
            // New centroid is the mean of its points
            c.defineCentroid(tot);

            clusters.offer(c);
        }
    }

    @SuppressWarnings(value = "unchecked")
    public synchronized void RunKMeans(final long sigma) {
        ChooseCentroids(1);

        int iteration = 0;
        long error_change = (long) 0.0;
        long last_err = (long) 0.0;

        do {
            for (int i = 0; i < K; ++i) {
                Cluster c = clusters.poll();
                c.clearDataPoints();
                clusters.offer(c);
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
                    clusters.offer(c);
                }

                //Cycle around to correct point
                while (closestClusterIndex-- > 0) {
                    Cluster c = clusters.poll();
                    clusters.offer(c);
                }
                //Now put points in cluster
                Cluster c = clusters.poll();
                c.addPoint(((DataSet) iter).getCurrentIndex());
                clusters.offer(c);
            }

            printCentroid();
            UpdateCentroids();
            long sse = (long) SumSquaredError();
            error_change = Math.abs(sse - last_err); //- error_change;
            last_err = sse;
            System.out.println("Iteration: " + iteration++ + "Error Change: " + error_change);
        } while (error_change > sigma);

        System.out.println("Final Centroids:\n\t------------------------");
        printCentroid();
    }

    
    public void printCentroid() {
        int i = -1;
        for (Cluster c : clusters) {
            System.out.println("(" + (++i) + "" + c);
        }
    }

    /**
     * This method writes outs this clusters to a file named 'filename'[CLUSTERNo].dat
     * Where filename is a parameter and ClusterNo is the number of the cluster.
     * This will be a text file.The program will also write out a file called
     * filename[TIME].m  Where TIME is the current system time.
     * This will output a matlab script
     *
     */
    public synchronized void WriteOutClusters() throws IOException {

        int clusterCount = 0;
        for (int counter = 0; counter < K; ++counter) {
            Cluster c = clusters.poll();
            File f = new File(fileName + clusterCount++ + ".dat");
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            //First print the headers
            for (int i = 0; i < dataset.GetHeadersUsed().length; ++i) {
                bw.write(dataset.GetHeadersUsed()[i]);
                if ((dataset.GetHeadersUsed().length - i) > 1) {
                    //while its not the last element
                    bw.write('\t');
                }
            }

            bw.newLine();

            // Write values to the text file
            Vector<Integer> thePoints = c.getPoints();
            for (int i = 0; i < thePoints.size(); ++i) {
                Vector<Number> vec = dataset.GetPointsByIndex(thePoints.get(i)).getValues();
                for (int j = 0; j < vec.size(); ++j) {
                    bw.write(vec.get(j).toString());
                    if ((vec.size() - j) > 1) {
                        //while its not the last element
                        bw.write('\t');
                    }
                }
                bw.newLine();
            }

            bw.flush();
            bw.close();
            f.setReadable(true);

            clusters.offer(c);
        }

        //Build the script to plot the clusters
        Long time = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        sb.append("%\tFile:  " + fileName + ".m\n");
        sb.append("%\tThis will plot all the clusters of as different colors  " + time + "\n");
        sb.append("\n");
        sb.append("addpath(\'C:\\\');");
        
        for (clusterCount = 0; clusterCount < K; ++clusterCount) {
     
            sb.append("\n%\tCluster" + clusterCount + ".dat\n");
            sb.append("[labels,col1,matrix");

            sb.append("] = readColData(\'" + fileName + clusterCount + ".dat\',");
            sb.append(K-1 + ","); // Number of cols
            sb.append(1 + ");"); // One Header row

            sb.append("\nhold on\n");
            sb.append("plot(");
            sb.append("col1,matrix(:,1),");
            sb.append("\'" + colorlist.remove(0) + "\'");
            for (int i = 2; i < dataset.getDimensions(); ++i) {
                sb.append(",");
                sb.append("col1,matrix(:," + i + "),");
                sb.append("\'" + colorlist.remove(0) + "\'");
            }
            sb.append(");");

            sb.append("\n\n");
        }
        
        
        //Plot the centroids
        sb.append("%\tPlot the centroids\n");
        for(clusterCount = 0; clusterCount < K; ++clusterCount){
            Cluster c = clusters.poll();
            
            sb.append("plot(");
            Vector<Number>v = c.getCentroid().getValues();
            sb.append(v.remove(0)+"");
            for(Number n : v){
                sb.append(","+n);
            }
            sb.append(",");
            sb.append("\'" + colorlist.remove(0) + "\'");
            sb.append(");\n");
            
            clusters.offer(c);        
        }
        
        sb.append("hold off\n");
        sb.append("title(\'" + fileName + "\');");


        // Write the script out
        File f = new File(fileName + ".m");
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.write(sb.toString());
        bw.flush();
        bw.close();
        f.setReadable(true);
    }

    public boolean ready() {
        return dataSetInitialized;
    }

    private void initializeColorList() {
        colorlist = new Vector<String>();
        colorlist.add("ro");
        colorlist.add("go");
        colorlist.add("bo");
        colorlist.add("ko");
        colorlist.add("wo");
        colorlist.add("co");
        colorlist.add("mo");
        colorlist.add("yo");
        colorlist.add("r+");
        colorlist.add("g+");
        colorlist.add("b+");
        colorlist.add("k+");
        colorlist.add("w+");
        colorlist.add("c+");
        colorlist.add("m+");
        colorlist.add("y+");
        colorlist.add("r-");
        colorlist.add("g-");
        colorlist.add("b-");
        colorlist.add("k-");
        colorlist.add("w-");
        colorlist.add("c-");
        colorlist.add("m-");
        colorlist.add("y-");
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