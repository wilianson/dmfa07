/*
 * Main.java
 *
 * Created on Nov 22, 2007, 3:16:34 PM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kmeans;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author cgrant
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        if (1 == 1) {
            try {
                //Do Single link
                SingleLink();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                System.exit(2);
            }
        } else {
            JFrame j = new JFrame();
            j.setTitle("Parameters");
            KMeansMetadata kmmd = new KMeansMetadata(j, true);
            kmmd.setVisible(true);

            while (kmmd.isVisible()) {
            }

            try {
//            String fileName = "C:" + System.getProperty("file.separator") + "economyrankings.dat";
                String fileName = kmmd.getJtfLocationDataSet().getText();
                System.out.println(fileName);
                char delimiter = '\t';
                int[] colsToUse = {1, 8};
                String[] columnNames = {"Economy", "Ease of Doing Business Rank", "Starting a Business", "Dealing with Licenses", "Employing Workers", "Registering Property", "Getting Credit", "Protecting Investors", "Paying Taxes", "Trading Across Borders", "Enforcing Contracts", "Closing a Business"};

                KMeans kmeans = new KMeans(Integer.parseInt(kmmd.getJtfNumberOfClusters().getText()));
                kmeans.setClusterLocation(kmmd.getJtfLocationMatlabFiles().getText());
                kmeans.LoadData(fileName, delimiter, colsToUse, columnNames);
                while (!kmeans.ready()) {
                }
//            System.out.println(kmmd.getJtfMinError().getText());
                Number n = new Double(kmmd.getJtfMinError().getText());
                kmeans.RunKMeans(n.longValue());
                kmeans.WriteOutClusters();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                System.out.print("GoodBye!");
                System.exit(0);
            }
        }
    }
    // Single link stuff--------------------------------------------------------
    //--------------------------------------------------------------------------
    static LinkedList<Apoint> priorityQueue;
    //Stores a container that has pointers to indexesi n the dataset
    static Vector<ClusterRAJ> clusters;
    static Vector<Integer> listOfPoints;
    static DataSet dataset;
    
    public static void SingleLink() throws FileNotFoundException {
        KMeansMetadata kmmd = new KMeansMetadata(new JFrame("This is Single Link (ignore kmeans"), true);
        kmmd.setVisible(true);

        while (kmmd.isVisible()) {
        }

        //String fileName = "F:" + System.getProperty("file.separator") + "economyrankings.txt";
        String fileName = kmmd.getJtfLocationDataSet().getText();
        char delimiter = '\t';
        int[] colsToUse = {1, 8};
        String[] columnNames = {"Economy", "Ease of Doing Business Rank", "Starting a Business", "Dealing with Licenses", "Employing Workers", "Registering Property", "Getting Credit", "Protecting Investors", "Paying Taxes", "Trading Across Borders", "Enforcing Contracts", "Closing a Business"};

        // KMeans kmeans = new KMeans(3);
        // kmeans.LoadData(fileName, delimiter, colsToUse, columnNames);
        // while(!kmeans.ready()){}
        // kmeans.RunKMeans((long) 1.0);
        dataset = new DataSet();
        dataset.InitializeDataSet2(fileName, delimiter, colsToUse, columnNames);
        //for (Iterator<Points> iter = dataset.iterator(); iter.hasNext(); iter.next())
        Vector<Points> vec = dataset.getDataset();
        double[][] distanceMatrix = new double[vec.size()][vec.size()];
        
        int j = 0;
        for (int i = 0; i < distanceMatrix.length; ++i) {
            for (j = i; j < distanceMatrix[i].length; ++j) {
                distanceMatrix[i][j] = DistanceFunctions.Euclidian(vec.get(i), vec.get(j));
            }
        }

        priorityQueue = new LinkedList<Apoint>();
        for (int r = 0, c = 0; r < distanceMatrix.length; ++r) {
            do {
                priorityQueue.offer(new Apoint(r, c, distanceMatrix[r][c]));
                c++;
            } while (c < distanceMatrix.length);
            c = r + 1;
        }

        listOfPoints = new Vector<Integer>();
        for (int i = 0; i < distanceMatrix.length; ++i) {
            listOfPoints.add(i);
        }

        clusters = new Vector<ClusterRAJ>();

        
        Collections.sort(priorityQueue);
//        Collections.reverse(priorityQueue);
        ClusterRAJ craj;
        Apoint p;
        while (!listOfPoints.isEmpty()) {
            p = priorityQueue.poll();
            if(p.distance == 0)
                continue;
            if (!hasPointBeenClusterd(p.p2) && !hasPointBeenClusterd(p.p1)) {
                craj = new ClusterRAJ();
                craj.addPoint(p.p1);
                craj.addPoint(p.p2);
                craj.distance = p.distance;
                clusters.add(craj);
                listOfPoints.remove(new Integer(p.p1)); // removed from list of available pointers
                listOfPoints.remove(new Integer(p.p2));
            } else if (!hasPointBeenClusterd(p.p1)) {
                listOfPoints.remove(new Integer(p.p1));
                craj = new ClusterRAJ();
                craj.addPoint(p.p1);

                //Find largest cluster that holds p2
                for (int i = clusters.size() - 1; i >= 0; --i) {
                    if (clusters.get(i).vec.contains(new Integer(p.p2))) {
                        for (Integer in : clusters.get(i).vec) {
                            craj.addPoint(in); // put all elements in the cluster that hold p2 into new cluser
                        }
                        break;
                    }
                }
                
                craj.distance = p.distance;
                clusters.add(craj);
            } else if (!hasPointBeenClusterd(p.p2)) {
                listOfPoints.remove(new Integer(p.p2));
                craj = new ClusterRAJ();
                craj.addPoint(p.p2);

                //Find largest cluster that holds p1
                for (int i = clusters.size() - 1; i >= 0; --i) {
                    if (clusters.get(i).vec.contains(new Integer(p.p1))) {
                        for (Integer in : clusters.get(i).vec) {
                            craj.addPoint(in); // put all elements in the cluster that hold p1 into new cluser
                        }
                        break;
                    }
                }
                craj.distance = p.distance;
                clusters.add(craj);
            } else {
                // move on to the next point
            }
            Apoint pp = new Apoint(p.p2,p.p1,p.distance);
            
            // take out the other point from the priority queue
            if(priorityQueue.contains(pp)){
                System.out.print("REMOVE DUP");
                priorityQueue.remove(pp);
            }
        }
        //We now a list of clustered points in the order that they are clustered
        PrintSingleLinkTest();
        return;
    }

    public static boolean hasPointBeenClusterd(int i) {
        boolean ret = false;
        for (ClusterRAJ c : clusters) {
            if (c.vec.contains(i)) {
                return true;
            }
        }
        return ret;
    }
    
    //Print the order in which points are clusters
    //Cluser2 - size 2
    //points: 
    public static void PrintSingleLinkTest(){
        
        int clusterCounter = 0;
        for(ClusterRAJ craj : clusters){
            System.out.println("Cluster: " + clusterCounter + "Distance: " + craj.distance);
            System.out.println("-----------------------------------------------------");
            for(Integer i : craj.vec){
                System.out.println(dataset.GetPointsByIndex(i));
            }
            System.out.println();
            System.out.println();
            ++clusterCounter;
        }
    }
}

//**Stores pointers to the dataset for particular queries*/
class ClusterRAJ {

    //**Stores pointers to the dataset for particular queries*/
    public Vector<Integer> vec;
    public Double distance;

    public ClusterRAJ() {
        distance = 0.0;
        vec = new Vector<Integer>();
    }

    public void addPoint(int i) {
        vec.add(new Integer(i));
    }
    

    public Vector<Integer> GetVec() {
        return vec;
    }
}

class Apoint implements Comparable {

    public int p1;
    public int p2;
    public double distance;

    public Apoint(int p1, int p2, double distance) {
        this.p1 = p1;
        this.p2 = p2;
        this.distance = distance;
    }

    public int compareTo(Object o) {
        assert (o instanceof Apoint);

        if (this.distance > ((Apoint) o).distance) {
            return 1;
        } else if (this.distance < ((Apoint) o).distance) {
            return -1;
        } else {
            return 0;
        }
    }
}