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
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cgrant
 */
public class DataSet implements Iterable<Points>, Iterator<Points>{

    private Vector<Points> dataset;
    private int dimensions;
    private int[] indexes;
    String[] header;
    
    public DataSet() {
        initialize();
    }
    private void initialize(){
        dataset = new Vector<Points>(179);
    }

    @SuppressWarnings("unchecked")
    public synchronized boolean InitializeDataSet(String fileName, char delim, int[] index, String[] colNames) throws FileNotFoundException, IOException{
        assert (index.length > 0);

        setDimensions(index.length);
        setIndexes(index);
        setHeader(colNames);

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

                StringTokenizer stok = new StringTokenizer(string, delim + "");

                int counter = 0;
                while (stok.hasMoreElements()) {
                    Object o = stok.nextElement();
                    Double d = 0.0;
                    try {
                        d = new Double((String) o);
                    } catch (java.lang.NumberFormatException nfe) {
                        ++counter;
                        continue;
                    }

                    boolean exists = false;
                    for (int i : indexes) {
                        exists = (counter == i) ? true : false;
                        if (exists) {
                            String nam = colNames[counter];
                            Number num = d;
                            points.AppendPoint(d, nam);
                            break;
                        }
                    }

                    ++counter;
                }
                dataset.add(points);
                successful = true;
            }
            
        } catch (FileNotFoundException ex) {
            successful = false;
            throw ex;
        } catch (IOException ex) {
            throw ex;
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                throw ex;
            }
        }
        return successful;
    }
    
    public synchronized boolean InitializeDataSet2(String fileName, char delim, int[] index, String[] colNames) {
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

                StringTokenizer stok = new StringTokenizer(string, delim + "");

                int counter = 0;
                while (stok.hasMoreElements()) {
                    Object o = stok.nextElement();
                    Double d = 0.0;
                    try {
                        d = new Double((String) o);
                    } catch (java.lang.NumberFormatException nfe) {
                        ++counter;
                        continue;
                    }

                    boolean exists = false;
                    for (int i : indexes) {
                        exists = (counter == i) ? true : false;
                        if (exists) {
                            String nam = colNames[counter];
                            Number num = d;
                            points.AppendPoint(d, nam);
                            break;
                        }
                    }

                    ++counter;
                }
                dataset.add(points);
                successful = true;
            }
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            successful = false;
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return successful;
    }
    
    public int GetSize(){
        return dataset.size();
    }
    
    public Points GetPointsByIndex(int i){
        return dataset.get(i);
    }
    
    public String[] GetHeadersUsed(){
        String[] s = new String[indexes.length];
        for(int i = 0; i < s.length; ++i){
            s[i] = header[indexes[i]];
        }
        return s;
    }
    /** Getters and Setters **********************************************/
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
    
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[DataSet (size: ");
        sb.append(dataset.size());
        sb.append("),(dim: ");
        sb.append(dimensions);
        sb.append(")]");
        return sb.toString();
    }

    /* Iterator Stuff ******************************************************/
    private int iteratorCounter;
    private boolean nextCalled;
    private boolean moreObjects;
    public Iterator<Points> iterator() {
        iteratorCounter = 0;
        nextCalled = true;
        return this;
    }    

    public boolean hasNext() {
        if (nextCalled || (iteratorCounter == 0)) {
            moreObjects = (iteratorCounter < dataset.size()) ? true : false;
            nextCalled = false;
        }
        return moreObjects;
    }

    public Points next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Points value = dataset.get(iteratorCounter++);
        nextCalled = true;
        return value;
    }

    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getCurrentIndex() {
        return iteratorCounter;
    }
    
    public Points getCurrentPoints(){
        Points value = dataset.get(getCurrentIndex());
        return value;
    }
    
}