/*
 * Main.java
 * 
 * Created on Nov 22, 2007, 3:16:34 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kmeans;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cgrant
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String fileName = "C:" + System.getProperty("file.separator") + "economyrankings.txt";
            char delimiter = '\t';
            int[] colsToUse = {1, 8};
            String[] columnNames = {"Economy", "Ease of Doing Business Rank", "Starting a Business", "Dealing with Licenses", "Employing Workers", "Registering Property", "Getting Credit", "Protecting Investors", "Paying Taxes", "Trading Across Borders", "Enforcing Contracts", "Closing a Business"};

            KMeans kmeans = new KMeans(3);
            kmeans.LoadData(fileName, delimiter, colsToUse, columnNames);
            while (!kmeans.ready()) {
            }
            kmeans.RunKMeans((long) 1.0);
            kmeans.WriteOutClusters();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
