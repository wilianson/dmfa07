/*
 * DataSet.java
 *
 * Created on Nov 22, 2007, 5:13:48 PM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kmeans;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cgrant
 */
public class DataSet {

    private Vector<Points> dataset;
    private int dimensions;
    private int[] indexes;
    String[] header;

    public DataSet() {
    }

    @SuppressWarnings("unchecked")
    public boolean InitializeDataSet(String fileName, char delim, int[] index, String[] colNames) {
        assert (index.length > 0);

        setDimensions(index.length);
        setIndexes(index);

        FileInputStream fis = null;
        boolean successful = false;
        
        try {
            File f = new File(fileName);
            fis = new FileInputStream(f);
            DataInputStream dis = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(dis));

            while (br.ready()) {
                Points points = new Points(dimensions);
                String string = br.readLine();
                if (string == null) {
                    break;
                }
                
                StringTokenizer stok = new StringTokenizer(string);
                int counter = 0;
                while (stok.hasMoreElements()) {
                    Object o = stok.nextElement();
                    
                    boolean exists = false;
                    for(int i : indexes){
                        exists = (counter == i)?true:false;
                        if(exists){
                            points.AppendPoint((Number)o, colNames[counter]);
                            break;
                        }    
                    }
                    
                    ++counter;
                }
                dataset.add(points);
                successful = true;
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataSet.class.getName()).log(Level.SEVERE, null, ex);
            successful = false;
        } catch (IOException ex) {
            Logger.getLogger(DataSet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(DataSet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return successful;
    }
    
    
    public Vector<Points> getDataset() {
        return dataset;
    }
    public void setDataset(Vector<Points> dataset) {
//      this.dataset   = dataset;
        this.dataset = new Vector<Points>(dataset.size());
        System.arraycopy(dataset, 0, this.dataset, 0, dataset.size());
    }
    public int getDimensions() {
        return dimensions;
    }
    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }
    public String[] getHeader() {
        return header;
    }
    public void setHeader(String[] header) {
//        this.header = header;
        this.header = new String[header.length];
        System.arraycopy(header, 0, this.header, 0, header.length);
    }
    public int[] getIndexes() {
        return indexes;
    }
    public void setIndexes(int[] indexes) {
//        this.indexes = indexes;
        this.indexes = new int[indexes.length];
        System.arraycopy(indexes, 0, this.indexes, 0, indexes.length);
    }
}