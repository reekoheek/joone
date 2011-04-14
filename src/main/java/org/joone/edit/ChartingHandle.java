package org.joone.edit;

import java.util.TreeSet;
import java.io.*;
import org.joone.engine.*;
import org.joone.net.*;

/** This class acts as a container of all the characteristics of a serie
 * plotted by a charting component (i.e. a component implementing ChartInterface).
 * This component must be connected beetwen a layer and a charting component.
 */
public class ChartingHandle implements OutputPatternListener, Serializable {
    
    private ChartInterface chartSynapse;
    private int serie = 1;
    private int RedColor = 0;
    private int GreenColor = 0;
    private int BlueColor = 200;
    private String name;
    private Monitor mon;
    private int inputDimension;
    private boolean outputFull;
    private boolean enabled = true;
    
    private static final long serialVersionUID = 370424541089332025L;
    
    /** The constructor
     */
    public ChartingHandle() {
        chartSynapse = null;
        mon = null;
        inputDimension = 0;
    }
    
    /** Returns the name of the output synapse
     * @return String
     */
    public String getName() {
        return name;
    }
    
    /** Sets the name of the output synapse
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }
    
    //------ Methods and parameters mapped on the active input synapse -------
    
    /** Sets the dimension of the output synapse
     * @param newOutputDimension int
     */
    public void setInputDimension(int newInputDimension) {
        this.inputDimension = newInputDimension;
        if (chartSynapse != null)
            chartSynapse.setInputDimension(newInputDimension);
    }
    
    /** Returns the dimension of the output synapse
     * @return int
     */
    public int getInputDimension() {
        return inputDimension;
    }
    
    /** Returns the monitor
     * @return org.joone.engine.Monitor
     */
    public Monitor getMonitor() {
        return mon;
    }
    
    /** Sets the Monitor object of the input synapse
     * @param newMonitor org.joone.engine.Monitor
     */
    public void setMonitor(Monitor newMonitor) {
        this.mon = newMonitor;
        if (chartSynapse != null)
            chartSynapse.setMonitor(newMonitor);
    }
    
    protected void backward(double[] pattern) {
        // Not used
    }
    
    protected void forward(double[] pattern) {
        // Not used
    }
    
    
    /** Method to put a pattern forward to the next layer
     * @param pattern neural.engine.Pattern
     */
    public void fwdPut(Pattern pattern) {
        if (isEnabled() && (chartSynapse != null))  {
            if (getSerie() > pattern.getArray().length)  {
                new NetErrorManager(getMonitor(), "ChartingHandle: '"+getName()+"' - Serie greater than the pattern's length");
                return;
            }
            chartSynapse.fwdPut(pattern, this);
        }
    }
    
    /** Returns the error pattern coming from the next layer during the training phase
     * @return neural.engine.Pattern
     */
    public Pattern revGet() {
        return null;
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
        // Checks for the presense of an output charting synapse.
        if (chartSynapse == null) {
            checks.add(new NetCheck(NetCheck.WARNING,
            "Handle has no ChartOutput synapses attached.",
            this));
        }
        
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
    
    /** Getter for property chartSynapse.
     * @return Value of property chartSynapse.
     *
     */
    public ChartInterface getChartSynapse() {
        return chartSynapse;
    }
    
    /** Setter for property chartSynapse.
     * This property points to the charting component interested.
     * @param chartSynapse New value of property chartSynapse.
     *
     */
    public void setChartSynapse(ChartInterface chartSynapse) {
        if (chartSynapse != getChartSynapse()) {
            if (getChartSynapse() != null)
                getChartSynapse().removeHandle(this);
            this.chartSynapse = chartSynapse;
            if (chartSynapse != null)
                chartSynapse.setMonitor(mon);
        }
    }
    
    /** Getter for property serie.
     * @return Value of property serie.
     *
     */
    public int getSerie() {
        return serie;
    }
    
    /** Setter for property serie.
     * @param serie New value of property serie.
     *
     */
    public void setSerie(int serie) {
        this.serie = serie;
    }
    
    /** Getter for property RedColor.
     * @return Value of property RedColor.
     *
     */
    public int getRedColor() {
        return RedColor;
    }
    
    /** Setter for property RedColor.
     * @param RedColor New value of property RedColor.
     *
     */
    public void setRedColor(int RedColor) {
        this.RedColor = RedColor;
    }
    
    /** Getter for property GreenColor.
     * @return Value of property GreenColor.
     *
     */
    public int getGreenColor() {
        return GreenColor;
    }
    
    /** Setter for property GreenColor.
     * @param GreenColor New value of property GreenColor.
     *
     */
    public void setGreenColor(int GreenColor) {
        this.GreenColor = GreenColor;
    }
    
    /** Getter for property BlueColor.
     * @return Value of property BlueColor.
     *
     */
    public int getBlueColor() {
        return BlueColor;
    }
    
    /** Setter for property BlueColor.
     * @param BlueColor New value of property BlueColor.
     *
     */
    public void setBlueColor(int BlueColor) {
        this.BlueColor = BlueColor;
    }
    
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
    }

}
