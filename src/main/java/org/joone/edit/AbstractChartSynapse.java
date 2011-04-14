/*
 * ChartOutputSynapse.java
 *
 * Created on 17 marzo 2002, 16.53
 */

package org.joone.edit;


import org.joone.engine.*;

import java.io.*;
import java.awt.*;
import java.util.TreeSet;

import CH.ifa.draw.util.*;
import CH.ifa.draw.framework.*;

/** Abstract class implemented by all the classes that want to plot some kind of
 * chart using the values contained into the patterns passed as parameter of the
 * fwdPut methods. It support also the plotting of multi-series charts.
 * @author P.Marrone
 * @see org.joone.edit.ChartInterface
 * @see org.joone.edit.ChartingHandle
 */
public abstract class AbstractChartSynapse implements ChartInterface, org.joone.util.NotSerialize, java.io.Serializable {
    
    private static final long serialVersionUID = 2726512966931459796L;
    protected boolean show;
    protected Monitor monitor;
    protected String name = "";
    protected String title = "";
    protected boolean resizable = true;
    protected int maxXaxis = 10000;
    protected double maxYaxis = 1.0;
    protected int serie = 1;
    protected boolean outputFull;
    protected transient javax.swing.JFrame iFrame;
    
    private boolean enabled = true;
    
    //    private transient DrawingRegion myDrawingRegion;
    //    private transient SharedBuffer grafBuffer;
    
    /** Creates new form ChartOutputSynapse */
    public AbstractChartSynapse() {
        initComponents();
    }
    
    protected javax.swing.JFrame getFrame() {
        if (iFrame == null)
            iFrame = new javax.swing.JFrame();
        return iFrame;
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     */
    protected void initComponents() {
        getFrame().setTitle(getTitle());
        iFrame.setResizable(isResizable());
        iFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
        Iconkit kit = Iconkit.instance();
        if (kit == null)
            throw new HJDError("Iconkit instance isn't set");
        final Image img = kit.loadImageResource(JoonEdit.DIAGRAM_IMAGES + "jooneIcon.gif");
        iFrame.setIconImage(img);
        
        iFrame.pack();
    }
    
    /** Exit the form */
    private void exitForm(java.awt.event.WindowEvent evt) {
        setShow(false);
    }
    
    /** Sets the Monitor object of the output synapse
     * @param newMonitor org.joone.engine.Monitor
     */
    public void setMonitor(Monitor newMonitor) {
        monitor = newMonitor;
    }
        
    /** Returns the error pattern coming from the next layer during the training phase
     * @return neural.engine.Pattern
     */
    public Pattern revGet() {
        // Not used
        return null;
    }
    
    /** Sets the dimension of the output synapse
     * *** NOT USED ***
     * @param newInputDimension int
     */
    public void setInputDimension(int newInputDimension) {
    }
    
    /** Returns the monitor
     * @return org.joone.engine.Monitor
     */
    public Monitor getMonitor() {
        return monitor;
    }
    
    /** Returns the dimension of the output synapse
     * @return int
     */
    public int getInputDimension() {
        return 0;
    }
    
    /** Getter for property show.
     * @return Value of property show.
     */
    public boolean isShow() {
        return show;
    }
    
    /** Setter for property show.
     * @param show New value of property show.
     */
    public void setShow(boolean show) {
        this.show = show;
        if (show)
            getFrame().setVisible(true);
        else
            getFrame().setVisible(false);
    }
    
    /** Getter for property maxYaxis.
     * @return Value of property maxYaxis.
     */
    public double getMaxYaxis() {
        return maxYaxis;
    }
    
    /** Setter for property maxYaxis.
     * @param maxYaxis New value of property maxYaxis.
     */
    public void setMaxYaxis(double maxYaxis) {
        this.maxYaxis = maxYaxis;
    }
    
    /** Getter for property maxXaxis.
     * @return Value of property maxXaxis.
     */
    public int getMaxXaxis() {
        return maxXaxis;
    }
    
    /** Setter for property maxXaxis.
     * @param maxXaxis New value of property maxXaxis.
     */
    public void setMaxXaxis(int maxXaxis) {
        this.maxXaxis = maxXaxis;
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        initComponents();
        setShow(false);
    }
    
    /** Getter for property serie.
     * @return Value of property serie.
     */
    public int getSerie() {
        if (serie < 1)  // Only for previously saved components
            serie = 1;
        return serie;
    }
    
    /** Setter for property serie.
     * @param newSerie The column (serie) of the pattern to plot.
     */
    public void setSerie(int newSerie) {
        if (newSerie < 1)
            this.serie = 1;
        else
            this.serie = newSerie;
    }
    
    /** Getter for property name.
     * @return Value of property name.
     */
    public java.lang.String getName() {
        return name;
    }
    
    /** Setter for property name.
     * @param name New value of property name.
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }
    
    /** Getter for property title.
     * @return Value of property title.
     */
    public java.lang.String getTitle() {
        return title;
    }
    
    /** Setter for property title.
     * @param title New value of property title.
     */
    public void setTitle(java.lang.String title) {
        this.title = title;
        if (iFrame != null) {
            iFrame.setTitle(title);
        }
    }
    
    /** Getter for property resizable.
     * @return Value of property resizable.
     */
    public boolean isResizable() {
        return resizable;
    }
    
    /** Setter for property resizable.
     * @param resizable New value of property resizable.
     */
    public void setResizable(boolean resizable) {
        this.resizable = resizable;
        if (iFrame != null) {
            iFrame.setResizable(resizable);
        }
    }
    
    /**
     * Base for check messages.
     * Subclasses should call this method from thier own check method.
     *
     * @see OutputPaternListener
     * @return validation errors.
     */
    public TreeSet check() {
        
        // Prepare an empty set for check messages;
        TreeSet checks = new TreeSet();
        
        // Return check messages
        return checks;
    }
    
    /** Getter for property outputFull.
     * @return Value of property outputFull.
     *
     */
    public boolean isOutputFull() {
        return outputFull;
    }
    
    /** Setter for property outputFull.
     * @param outputFull New value of property outputFull.
     *
     */
    public void setOutputFull(boolean outputFull) {
        this.outputFull = outputFull;
    }
    
    /** Method to plot a serie in the chart in mono-serie mode
     * @param pattern neural.engine.Pattern
     */
    public abstract void fwdPut(Pattern pattern);
    
    /** Method to plot a serie in the chart in multi-serie mode
     * This method is used by a ChartingHandle to plot a specific serie
     * @param pattern The input pattern
     * @param handle The ChartingHandle class that describes the serie to plot
     */
    public abstract void fwdPut(Pattern pattern, ChartingHandle handle);
    
    public abstract void removeHandle(ChartingHandle handle);
    
    /** Getter for property enabled.
     * @return Value of property enabled.
     *
     */
    public boolean isEnabled() {
        return enabled;
    }    
    
    /** Setter for property enabled.
     * @param enabled New value of property enabled.
     *
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public void init() {
        // Do nothing
    }
        
}
