/*
 * ChartOutputSynapse.java
 *
 * Created on 17 marzo 2002, 16.53
 */

package org.joone.edit;

/**
 * This class plots a line chart
 *
 * @author  pmarrone
 */

import org.joone.engine.*;

import java.io.*;

public class ChartOutputSynapse extends AbstractChartSynapse {
    
    private static final long serialVersionUID = 7501332794591309201L;
    private transient DrawingRegion myDrawingRegion;
    private transient SharedBuffer grafBuffer;
    
    /** Creates new form ChartOutputSynapse */
    public ChartOutputSynapse() {
        super();
    }
    
    private void initDrawingRegion(int mainTrainingCycles) {
        iFrame.getContentPane().removeAll();
        myDrawingRegion = new DrawingRegion(new java.awt.Dimension(550, 350), getMaxYaxis(), mainTrainingCycles);
        iFrame.getContentPane().add(myDrawingRegion, java.awt.BorderLayout.CENTER);
        iFrame.setSize(560, 360);
        iFrame.pack();
    }
    
    /** Setter for property maxYaxis.
     * @param maxYaxis New value of property maxYaxis.
     */
    public void setMaxYaxis(double maxYaxis) {
        super.setMaxYaxis(maxYaxis);
        if (myDrawingRegion != null)
            myDrawingRegion.setMaxYvalue(maxYaxis);
    }
    
    /** Setter for property maxXaxis.
     * @param maxXaxis New value of property maxXaxis.
     */
    public void setMaxXaxis(int maxXaxis) {
        super.setMaxXaxis(maxXaxis);
        if (myDrawingRegion != null)
            myDrawingRegion.setMaxXvalue(maxXaxis);
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (getTitle() == null) {
            // to assure backward compatibility...
            setTitle("Chart");
            setName("Chart");
            setMaxXaxis(10000);
            setMaxYaxis(1.0);
            setResizable(true);
            setSerie(1);
            super.initComponents();
        }
        myDrawingRegion = null;
    }
    
    /** Method to plot a serie of a pattern in multi-serie mode
     * This method is used by a ChartingHandle to plot a specific serie
     * @param pattern The input pattern
     * @param handle The ChartingHandle class that describes the serie to plot
     */
    public void fwdPut(Pattern pattern, ChartingHandle handle) {
        if (isEnabled()) {
            SharedBuffer buffer = getDrawingRegion().getBuffer(handle);
            this.fwdPut(pattern, handle.getSerie(), buffer);
        }
    }
    
    /** Method to plot a serie in the chart in single-serie mode
     * @param pattern The input pattern
     */
    public void fwdPut(Pattern pattern) {
        if (isEnabled()) {
            if (getSerie() > pattern.getArray().length)  {
                new NetErrorManager(getMonitor(), "ChartOutputSynapse: '"+getName()+"' - Serie greater than the pattern's length");
                return;
            }
            grafBuffer = getDrawingRegion().getBuffer();
            this.fwdPut(pattern, getSerie(), grafBuffer);
        }
    }
    
    private void fwdPut(Pattern pattern, int numSerie, SharedBuffer buffer) {
        if (pattern.getCount() > -1) {
            double[] array = pattern.getArray();
            double cycle = pattern.getCount();
            buffer.put(array[numSerie - 1], cycle);
        }
//        else {
//            getDrawingRegion().stopPloting();
//            myDrawingRegion = null;
//        }
    }
    
    /** Getter for property myDrawingRegion.
     * @return Value of property myDrawingRegion.
     */
    private DrawingRegion getDrawingRegion() {
        if (myDrawingRegion == null)
            initDrawingRegion(maxXaxis);
        return myDrawingRegion;
    }
    
    public void removeHandle(ChartingHandle handle)  {
        if (myDrawingRegion != null)
            myDrawingRegion.removeHandle(handle);
    }
    
    
}
