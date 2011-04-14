package org.joone.edit.visad;

import org.joone.engine.*;
import org.joone.edit.*;

import java.io.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.rmi.RemoteException;
import org.joone.engine.learning.ComparingElement;
import visad.*;
import visad.java2d.*;
import visad.util.*;
import org.joone.log.*;

/**
 * <P>This component implements the chart interface and allows data obtained via the use of ChartHandles to be plotted onto a graph.</P>
 * <P>The VisADOutputSynapse uses the visad display library, more details on this can be obtained from http://www.visad.org.</P>
 * <P>When the property show is set to true a JFrame is displayed, this will display a chart of any previously obtained data or no chart if
 * the data has not yet been obtained.  When the network starts the component collects the data in internal buffers and at the end of the
 * network run will display the data in a chart.  Serie lines are coloured according to the choice made in the specific ChartHandle object.</P>
 * <P>The displayed component allows the user to change the X and Y range of data that should be displayed, this allows ther user to zoom in ans out of the data range.</P>
 * <P>The user can also decided whether to display the data as points and whatthe point size can be.</P>
 * <P> Multi serie plots can be done by the use of ChartHandles.  Mono plots can be done by connecting this to a normal Layer.</P>
 *<P> This component will throw an network error if it runs out of memory, the network will then stop running.</P>
 */
public class VisADOutputSynapse implements org.joone.util.NotSerialize, java.io.Serializable, NeuralNetListener, ChartInterface {
    
    private static final long serialVersionUID = 7501932794591309201L;
    private static final ILogger log = LoggerFactory.getLogger( VisADOutputSynapse.class );
    private boolean show;
    private Monitor monitor;
    private String name = "";
    private String title = "VisAD Chart";
    private boolean resizable = true;
    private int maxXaxis = 1000;
    private double maxYaxis = 1.0;
    private int serie = 1;
    private boolean outputFull;
    private transient javax.swing.JFrame iFrame;
    private boolean enabled = true;
    
    private transient ChartingHandle def_handle = new ChartingHandle();
    
    private transient JLabel status_label = new JLabel("Waiting for network to run!");
    private transient JPanel pane;
    private transient JPanel ControlPane = new JPanel();
    private transient Hashtable ChartBuffers = new Hashtable();
    private transient Hashtable DataRefs = new Hashtable();
    private transient Hashtable DataColors = new Hashtable();
    private transient Hashtable flat_fields = new Hashtable();
    
    // Declare variables
    // The quantities to be displayed in x- and y-axis
    private transient RealType time, height;
    
    // The function height = f(time), represented by ( time -> height )
    private transient FunctionType func_time_height;
    
    // Our Data values for time are represented by the set
    private transient visad.Set time_set;
    
    // The Data class FlatField, which will hold time and height data.
    // time data are implicitely given by the Set time_set
    private transient FlatField vals_ff;
    
    // The DataReference from the data to display
    private transient DataReferenceImpl data_ref;
    
    // The 2D display, and its the maps
    private transient DisplayImplJ2D display;
    private transient GMCWidget control_widg;
    private transient ScalarMap timeMap, heightMap;
    private transient RangeWidget timeWidget;
    private transient RangeWidget heightWidget;
    
    private transient boolean visadinit = false;
    private transient int current_sample_index = 0;
    
    /**
     * Initialises the VisAd components.  Sets up the internal frame.
     * @param max_samples The initial number of samples that will be plotted.
     * @param msg The initial message that will be displayed in the status panel.
     */
    private void initVisAd(int max_samples, String msg) {
        try {
            
            if (getMonitor() != null)
                getMonitor().addNeuralNetListener(this);   // Ensure we get Net Events
            
            status_label.setText(msg);
            // Create the quantities
            // Use RealType(String name)
            time = RealType.getRealType("Sample");
            height = RealType.getRealType("Value");
            
            // Create a FunctionType, that is the class which represents our function
            // This is the MathType ( time -> height )
            // Use FunctionType(MathType domain, MathType range)
            func_time_height = new FunctionType(time, height);
            
            // Create the time_set, with 5 integer values, ranging from 0 to 4.
            // That means, that there should be 5 values for height.
            // Use Integer1DSet(MathType type, int length)
            time_set = new Integer1DSet(time,max_samples);
            
            // Create Display and its maps
            // A 2D display
            display = new DisplayImplJ2D("display1");
            display.setAutoAspect(true);
            // Get display's graphic mode control and draw scales
            GraphicsModeControl dispGMC = (GraphicsModeControl) display.getGraphicsModeControl();
            control_widg = new GMCWidget(dispGMC);
            dispGMC.setScaleEnable(true);
            
            // Create the ScalarMaps: quantity time is to be displayed along x-axis
            // and height along y-axis
            // Use ScalarMap(ScalarType scalar, DisplayRealType display_scalar)
            timeMap = new ScalarMap( time, Display.XAxis );
            heightMap = new ScalarMap( height, Display.YAxis );
            
            // Add maps to display
            display.addMap( timeMap );
            display.addMap( heightMap );
            
            // Scale heightMap. This will scale the y-axis, because heightMap has DisplayRealType YAXIS
            timeMap.setRange( 0.0, maxXaxis);
            heightMap.setRange( 0.0, maxYaxis);
            
            timeWidget = new RangeWidget( timeMap );
            heightWidget = new RangeWidget( heightMap );
            
            if ( (iFrame != null) ) {
                iFrame.getContentPane().removeAll();
                pane = new JPanel();
                ControlPane = new JPanel();
                pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
                pane.setAlignmentY(JPanel.TOP_ALIGNMENT);
                pane.setAlignmentX(JPanel.LEFT_ALIGNMENT);
                pane.add(display.getComponent());
                ((JPanel)display.getComponent()).setMinimumSize(new Dimension(500,250));
                ((JPanel)display.getComponent()).setMaximumSize(new Dimension(1000,500));
                iFrame.getContentPane().setLayout(new BorderLayout());
                iFrame.getContentPane().add(pane,BorderLayout.CENTER);
                iFrame.getContentPane().add(timeWidget,BorderLayout.NORTH);
                ControlPane.setLayout(new GridLayout(2,1));
                ControlPane.add(heightWidget);
                ControlPane.add(control_widg);
                iFrame.getContentPane().add(ControlPane,BorderLayout.SOUTH);
                
                // Set window size and make it visible
                iFrame.setSize(740, 450);
                //iFrame.setVisible(true);
            }
        }
        catch(VisADException ex) {
            log.error(ex.toString());
            if ( getMonitor() != null )
                new NetErrorManager(getMonitor(),"VisADException while attempting to initialise VisAd. Message is : " + ex.getMessage());
        }
        
        catch(RemoteException ex) {
            if ( getMonitor() != null )
                new NetErrorManager(getMonitor(),"Remote exception while attempting to initialise VisAd. Message is : " + ex.getMessage());
        }
        catch(Exception ex) {
            log.error(ex.toString());
            if ( getMonitor() != null )
                new NetErrorManager(getMonitor(),"Exception while attempting to initialise VisAd. Message is : " + ex.getMessage());
        }
    }
    
    /**
     * Plots the VisAd chart in the internal frame.
     */
    private void PlotVisAd() {
        Enumeration myEnum = ChartBuffers.elements();
        // Loop through all the shared buffers ...
        while(myEnum.hasMoreElements()) {
            try {
                SharedBuffer buffer = ((SharedBuffer)myEnum.nextElement());
                ChartingHandle handle = buffer.getHandle();
                double [][] arr = buffer.get();
                //double [][] copy = new double [1][getMaxXaxis()];
                // for ( int i=0;i<arr.length;i++) changed because it causes ArrayOutOfBoundException
                // The for() loop must be limited to the smallest array size
                int max = (int)timeMap.getRange()[1]; //getMaxXaxis();
                if (max > arr.length)
                    max = arr.length;
                if ( max > 0)   // Check that we have data to plot!
                {
                    // We set the buffer's dimension equal to the biggest value
                    double [][] copy = new double [1][(int)timeMap.getRange()[1]]; //max > getMaxXaxis() ? max : getMaxXaxis()];
                    for ( int i=0;i < max;i++)
                        copy[0][i]=arr[i][0];
                    ((FlatField)flat_fields.get(handle)).setSamples(copy);
                    ((DataReferenceImpl)DataRefs.get(handle)).setData((FlatField)flat_fields.get(handle));
                    display.addReference((DataReferenceImpl)DataRefs.get(handle),(ConstantMap [])DataColors.get(handle));
                }
                else {
                    log.error("No data to plot!");
                    if ( getMonitor() != null )
                        new NetErrorManager(getMonitor(),"Warning while attempting to plot VisAd chart. No data to plot!");
                }
            }
            catch(VisADException ex) {
                log.error(ex.toString());
                if ( getMonitor() != null )
                    new NetErrorManager(getMonitor(),"VisADException while attempting to plot VisAd chart. Message is : " + ex.getMessage());
            }
            catch(RemoteException ex) {
                if ( getMonitor() != null )
                    new NetErrorManager(getMonitor(),"Remote Exception while attempting to plot VisAd chart. Message is : " + ex.getMessage());
            }
            catch(Exception ex) {
                log.error(ex.toString());
                if ( getMonitor() != null )
                    new NetErrorManager(getMonitor(),"Exception while attempting to plot VisAd chart. Message is : " + ex.getMessage());
            }
        }
    }
    
    /** Creates new form ChartOutputSynapse */
    public VisADOutputSynapse() {
        initComponents();
        // Setup a default handle for mono plots.
        def_handle.setSerie(getSerie());
        def_handle.setBlueColor(230);
        def_handle.setGreenColor(0);
        def_handle.setRedColor(0);
        def_handle.setName("Serie "+getSerie());
    }
    
    /** This method is called from within the constructor to initialize the form.  The method initialises
     * the default handle for mono plots and calls the initVisAd method to construct the main frame.
     */
    private void initComponents() {
        iFrame = new javax.swing.JFrame();
        iFrame.setTitle(getTitle());
        iFrame.setResizable(isResizable());
        iFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
        iFrame.pack();
        ChartBuffers = new Hashtable();
        DataRefs = new Hashtable();
        DataColors = new Hashtable();
        flat_fields = new Hashtable();
        ControlPane = new JPanel();
        def_handle = new ChartingHandle();
        status_label = new JLabel("Waiting for network to run!");
        initVisAd(getMaxXaxis() , "Waiting...");
        visadinit = true;
    }
    
    /** Exits the form.
     * @param evt The window event.
     */
    private void exitForm(java.awt.event.WindowEvent evt) {
        setShow(false);
    }
    
    /** Sets the Monitor object of this VisADOutputSyanpse.
     * @param newMonitor org.joone.engine.Monitor
     */
    public void setMonitor(Monitor newMonitor) {
        monitor = newMonitor;
        if (monitor != null)
            monitor.addNeuralNetListener(this);
    }
    
    /** Method to plot a serie of a pattern in multi-serie mode
     * This method is used by a ChartingHandle to plot a specific serie
     * @param pattern The input pattern
     * @param handle The ChartingHandle class that describes the serie to plot
     */
    public void fwdPut(Pattern pattern, ChartingHandle handle) {
        SharedBuffer sb = null;
        if (isEnabled() && (pattern.getCount() > -1))  {
            if ( ChartBuffers.size() > 0 )
                sb = (SharedBuffer)ChartBuffers.get(handle);
            else
                sb = null;
            if ( sb == null ) {
                try {
                    sb = new SharedBuffer();
                    sb.setHandle(handle);
                    ChartBuffers.put(handle,sb);
                    time_set = new Integer1DSet(time,(int)timeMap.getRange()[1]);
                    flat_fields.put(handle,new FlatField( func_time_height, time_set));
                    ConstantMap[] lineCMap =
                    {   new ConstantMap( (float)handle.getRedColor()/255, Display.Red),
                        new ConstantMap( (float)handle.getGreenColor()/255, Display.Green),
                        new ConstantMap( (float)handle.getBlueColor()/255, Display.Blue),
                        new ConstantMap( 1.00f, Display.LineWidth)
                    };
                    DataColors.put(handle,lineCMap);
                    DataRefs.put(handle,new DataReferenceImpl(handle.getName()));
                }
                catch(VisADException ex) {
                    log.error(ex.toString());
                    if ( getMonitor() != null )
                        new NetErrorManager(getMonitor(),"VisADException while attempting to define VisAd compononent in fwdPut(handle,pattern) method. Message is : " + ex.getMessage());
                }
                catch(Exception ex) {
                    log.error(ex.toString());
                    if ( getMonitor() != null )
                        new NetErrorManager(getMonitor(),"Exception while attempting to define VisAd component in fwdPut(handle,pattern) method. Message is : " + ex.getMessage());
                }
                catch(java.lang.OutOfMemoryError err) {
                    log.error(err.toString());
                    if ( getMonitor() != null )
                        new NetErrorManager(getMonitor(),"Out of memory error while attempting to define VisAd component in fwdPut(handle,pattern) method. Message is : " + err.getMessage());
                }
            }
            sb.put(pattern.getArray()[handle.getSerie()-1],pattern.getCount());
        }
    }
    
    /** Called by the previous layer to place a pattern on this VisADOutputSynapse, supports single/mono serie plot.  The data plotted is defined by the
     * serie property.
     * @param pattern The pattern with the data to plot.
     */
    public void fwdPut(Pattern pattern) {
        if (isEnabled() && (pattern.getCount() > -1))  {
            SharedBuffer sb = (SharedBuffer)ChartBuffers.get(def_handle);
            if ( sb == null ) {
                try {
                    sb = new SharedBuffer();
                    sb.setHandle(def_handle);
                    ChartBuffers.put(def_handle,sb);
                    time_set = new Integer1DSet(time,(int)timeMap.getRange()[1]);
                    flat_fields.put(def_handle,new FlatField( func_time_height, time_set));
                    ConstantMap[] lineCMap =
                    {   new ConstantMap( (float)def_handle.getRedColor()/255, Display.Red),
                        new ConstantMap( (float)def_handle.getGreenColor()/255, Display.Green),
                        new ConstantMap( (float)def_handle.getBlueColor()/255, Display.Blue),
                        new ConstantMap( 1.00f, Display.LineWidth)
                    };
                    DataColors.put(def_handle,lineCMap);
                    DataRefs.put(def_handle,new DataReferenceImpl(def_handle.getName()));
                }
                catch(VisADException ex) {
                    log.error(ex.toString());
                    if ( getMonitor() != null )
                        new NetErrorManager(getMonitor(),"VisADException while attempting to define VisAd compononent in fwdPut(pattern) method. Message is : " + ex.getMessage());
                }
                catch(Exception ex) {
                    log.error(ex.toString());
                    if ( getMonitor() != null )
                        new NetErrorManager(getMonitor(),"Exception while attempting to define VisAd component in fwdPut(pattern) method. Message is : " + ex.getMessage());
                }
                catch(java.lang.OutOfMemoryError err) {
                    log.error(err.toString());
                    if ( getMonitor() != null )
                        new NetErrorManager(getMonitor(),"Out of memory error while attempting to define VisAd component in fwdPut(pattern) method. Message is : " + err.getMessage());
                }
            }
            sb.put(pattern.getArray()[getSerie()-1],pattern.getCount());
        }
    }
    
    /** Returns the error pattern coming from the next layer during the training phase
     * @return neural.engine.Pattern
     */
    public Pattern revGet() {
        // Not used
        return null;
    }
    
    /** Sets the dimension of the output synapse
     * @param newOutputDimension int
     */
    public void setInputDimension(int newInputDimension) {
    }
    
    /** Returns the monitor
     * @return org.joone.engine.Monitor
     */
    public Monitor getMonitor() {
        return monitor;
    }
    
    /** Returns the dimension of this VisADOutputSynapse which is always 0.
     * @return int Always 0.
     */
    public int getInputDimension() {
        return 0;
    }
    
    /** Getter for property show.  Is the frame/component/chart on display?
     * @return Value of property show.
     */
    public boolean isShow() {
        return show;
    }
    
    /** Setter for property show.
     * @param show true to display the chart component, false to hide it.
     */
    public void setShow(boolean show) {
        this.show = show;
        if (show)
            iFrame.setVisible(true);
        else
            iFrame.setVisible(false);
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
    
    /**
     * Loads this serialised object from the object stream.
     * @param in The serialised object stream.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        initComponents();
        // Setup a default handle for mono plots.
        def_handle.setSerie(getSerie());
        def_handle.setBlueColor(230);
        def_handle.setGreenColor(0);
        def_handle.setRedColor(0);
        def_handle.setName("Serie "+getSerie());
        setShow(false);
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        if (timeMap != null)   maxXaxis = (int)timeMap.getRange()[1];
        if (heightMap != null) maxYaxis = heightMap.getRange()[1];
        out.defaultWriteObject();
    }
    
    
    /** Getter for property serie.  Used for mono plots only.
     * @return Value of property serie.
     */
    public int getSerie() {
        if (serie < 1)  // Only for previously saved components
            serie = 1;
        return serie;
    }
    
    /** Setter for property serie. Used for mono plots only.
     * @param serie New value of property serie.
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
    
    /** Getter for property title.  Gets the title of the components frame.
     * @return Value of property title.
     */
    public java.lang.String getTitle() {
        return title;
    }
    
    /** Setter for property title.  Sets the title of the components frame.
     * @param title New value of property title.
     */
    public void setTitle(java.lang.String title) {
        this.title = title;
        if (iFrame != null) {
            iFrame.setTitle(title);
        }
    }
    
    /** Getter for property resizable.  Can user re-size the components frame or not.
     * @return Value of property resizable.
     */
    public boolean isResizable() {
        return resizable;
    }
    
    /** Setter for property resizable.  Can user re-size the components frame or not.
     * @param resizable New value of property resizable.
     */
    public void setResizable(boolean resizable) {
        this.resizable = resizable;
        if (iFrame != null) {
            iFrame.setResizable(resizable);
        }
    }
    
    /**
     *
     * Base for check messages.
     * Subclasses should call this method from their own check method.
     * @see OutputPaternListener
     * @return validation errors.
     */
    public TreeSet check() {
        // Prepare an empty set for check messages;
        TreeSet checks = new TreeSet();
        // Return check messages
        return checks;
    }
    
    /**
     * Processes the cicleTerminated. Currently no processing.
     */
    public void cicleTerminated(NeuralNetEvent e) {
    }
    
    /**
     * Processes the errorChanged event.  Currently no processing.
     */
    public void errorChanged(NeuralNetEvent e) {
    }
    
    /**
     * Processes the netStarted event.  Initialises VisAd components.
     */
    public void netStarted(NeuralNetEvent e) {
        // Re-make the memory to hold the recorded values.
        ChartBuffers.clear();
        DataRefs.clear();
        DataColors.clear();
        flat_fields.clear();
        if ( visadinit == false) {
            initVisAd(getMaxXaxis() , "Collecting data ....");
            visadinit = true;
        }
        else {
            try  {
                display.removeAllReferences();
            }
            catch (VisADException ve) {
                    log.error(ve.toString());
                    if ( getMonitor() != null )
                        new NetErrorManager(getMonitor(),"VisADException while attempting to remove Display references in netStarted event. Message is : " + ve.getMessage());
            }
            catch (RemoteException re) {
                    log.error(re.toString());
                    if ( getMonitor() != null )
                        new NetErrorManager(getMonitor(),"RemoteException while attempting to remove Display references in netStarted event. Message is : " + re.getMessage());
            }
        }
        status_label.setText("Collecting data ....");
    }
    
    /**
     * Processes the netStopped event. Plots the VisAd chart in a frame.
     */
    public void netStopped(NeuralNetEvent e) {
        
        try{
            status_label.setText("Data collected and displayed.");
            PlotVisAd();
        }
        catch(Exception ex){
            log.error(ex.toString());
        }
    }
    
    /**
     * Processes the netStoppedError event. Currently no processing.
     */
    public void netStoppedError(NeuralNetEvent e, String error) {
    }
    
    /**
     * Remove a chart handle. Currently no processing.
     */
    public void removeHandle(ChartingHandle handle) {
    }
    
    /** Getter for property enabled.  Is the component enabled or not.
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
    
    public boolean isOutputFull() {
        return(outputFull);
    }
    
    public void setOutputFull(boolean newoutputFull) {
        outputFull = newoutputFull;
    }
    
    public void init() {
        // Do nothing
    }
        
}

