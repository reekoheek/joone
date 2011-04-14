/*
 * DrawingRegion.java
 *
 * Created on 4. juli 2002, 15:24
 */

package org.joone.edit;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import org.joone.log.*;

/**
 * @author  Jan Erik Garshol, jangar@idi.ntnu.no
 * @version 0.1 beta
 *
 * This object plot what it's told to plott by the assigned SharedBuffer object.
 * (Adapted to Joone by P. Marrone pmarrone@users.sourceforge.net)
 */
public class DrawingRegion extends java.awt.Canvas implements java.lang.Runnable{
    /**
     * Logger
     * */
    private static final ILogger log = LoggerFactory.getLogger(DrawingRegion.class);
    
    private static final int ERROR = 0; // error[i][ERROR] --> returns the error value, Y axis value
    private static final int CYCLE = 1; // error[i][CYCLE] --> returns the cycle value, X axis value
    private int X_ENHETER = 0;           // Number of labels on the x-axis
    private double X_INTERVALL = 0;      // Interval between each label on the x-axis
    private int Y_ENHETER = 10;           // Number of labels on the y-axis
    private int MAX_X;        // Calculated due to myDim max absolute value on x-axis
    private int MAX_Y;        // Calculated due to myDim max absolute value p� y-axis
    // Data buffers
    private double[] grafData;       // a local copy of the sharedBuffer
    private Hashtable grafDataMul;  // a local copy of the sheardBuffers for the multiple plotting
    private SharedBuffer grafBuffer;
    private Hashtable buffers;
    
    private Thread myThread;
    private java.awt.Dimension myDim;
    private int currentCycle = 0;
    private boolean keepPlotting = true;
    private java.awt.Color original;
    private java.awt.Color background;
    private java.awt.Color gray;
    private int max_x_value;
    private double max_y_value;
    private double stepY;
    private double stepX;
    private int X = 45;   // Space between the frame border and the y-axis of the graph
    private int Y = 25;   // Space between the frame border and the x-axis of the graph
    private BufferedImage bi;
    private Graphics big;
    
    private static final long serialVersionUID = -1681735154132358889L;
    /**
     * Creates a drawing area, with a interval between each label on
     * the x-axis calculated by the int xSpredning. Use number of trainig cycles
     * as a roule of thumb.
     */
    public DrawingRegion(java.awt.Dimension dimension, double ySpredning, int xSpredning) {
        if ( dimension.width < X && dimension.height < Y)
            log.warn( "Drawing area too small, use min " + X + " * " +  Y + " pixels.");
        else if (xSpredning < 2)
            log.warn("X Spredning must be > 1");
        else if (ySpredning <= 0.0)
            log.warn("Y Spredning must be > 0.0");
        else  {
            myDim = dimension;
            max_x_value = xSpredning;
            max_y_value = ySpredning;
            setSize(myDim);
            getXEnhet(xSpredning);
            constructorInit();
        }
    }
    
    public DrawingRegion(java.awt.Dimension dimension, int xSpredning) {
        this(dimension, 1.0, xSpredning);
    }
    
    /**
     * Creates a drowing area of 550x350, with a interval between each label on
     * the x-axis calculated by the int xSpredning. Use number of trainig cycles
     * as a roule of thumb.
     */
    public DrawingRegion(int xSpredning) {
        this(new java.awt.Dimension(550,350),xSpredning);
    }
    
    /**
     * Initialize the different values describe above and starts the shared
     * buffer.
     */
    private void constructorInit() {
        buffers = new Hashtable();
        //        grafBuffer = new SharedBuffer();
        grafData = new double[max_x_value + 1];
        grafDataMul = new Hashtable();
        original = new java.awt.Color(0,0,0);
        background = new java.awt.Color(200,200,200);
        gray = new java.awt.Color(160,160,160);
        setBackground(background);
        setDimensions(myDim);
        start();
    }
    
    private void setDimensions(java.awt.Dimension dimension) {
        double tempX, tempY;
        myDim = dimension;
        tempX = (myDim.width  - (2 * X));
        tempY = (myDim.height - (2 * Y));
        
        MAX_X = new Double(tempX).intValue();
        MAX_Y = new Double(tempY).intValue();
        
        stepX = MAX_X / (double)max_x_value;
        stepY = MAX_Y / max_y_value;
    }
    
    /**
     * Calculate a sensible interval between each label on the x-axis based on
     * a drowing area of 650x650, with a given int xSpredning.
     */
    private void getXEnhet(int xSpredning) {
        X_INTERVALL = xSpredning / 5;
        X_ENHETER = new Double(xSpredning/X_INTERVALL).intValue();
    }
    
    /**
     * This will stop the object and end the thread.
     */
    public void stopPloting() {
        keepPlotting = false;
    }
    
    /**
     * Return the sharedBuffer object for a mono-serie plotting, have to be called by the Producer object.
     * @return a SharedBuffer not linked to a particular ChartingHandle
     */
    public SharedBuffer getBuffer() {
        if (grafBuffer == null)
            grafBuffer = new SharedBuffer();
        return grafBuffer;
    }
    
    /**
     * Return the sharedBuffer object for a multi-serie plotting, have to be called by the Producer object.
     * @return a SharedBuffer linked to the passed ChartingHandle
     */
    public SharedBuffer getBuffer(ChartingHandle handle) {
        SharedBuffer sb = (SharedBuffer)buffers.get(handle);
        if (sb == null) {
            sb = new SharedBuffer();
            sb.setHandle(handle);
            buffers.put(handle, sb);
        }
        return sb;
    }
    
    /**
     *  Is called by the System, to paint the drawing area. Do not use this!
     */
    public void paint(Graphics g) {
        update(g);
    }
    
    public void update(Graphics g) {
        java.awt.Dimension dim = getSize();
        if ((bi == null) || (bi.getWidth() != dim.width) || (bi.getHeight() != dim.height)) {
            bi = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
            big = bi.getGraphics();
        }
        big.setColor(background);
        big.fillRect(0,0, dim.width, dim.height);
        if ((myDim.height != dim.height)
        ||  (myDim.width != dim.width)) {
            setDimensions(dim);
        }
        initDrawingRegion(big);
        if (grafBuffer != null)
            plot(big, grafData, new Color(0,0,200));
        for (Enumeration k=buffers.keys(); k.hasMoreElements();) {
            ChartingHandle handle = (ChartingHandle)k.nextElement();
            double[] data = (double[])grafDataMul.get(handle);
            Color col = new Color(handle.getRedColor(), handle.getGreenColor(), handle.getBlueColor());
            if (data != null)
                plot(big, data, col);
        }
        big.setColor(original);
        // Plots last cycle value
        int plassering = new Double(myDim.width*0.5).intValue();
        big.drawString("Current cycle is "+currentCycle, plassering, 70);
        
        g.drawImage(bi,0,0,this);
    }
    /**
     * Plots the x and Y axis, the labels and the grid.
     */
    private void initDrawingRegion(Graphics g) {
        g.setColor(original);
        g.drawRect(0, 0, myDim.width-1, myDim.height-1);    // Paints a border
        g.drawString("Y", 20, 12);                          // Paints the y-axis
        plotLine(g,0,0,0,max_y_value);
        g.drawString("X",  myDim.width-20, myDim.height-20); // Paints tha x-axis
        plotLine(g,0,0,max_x_value,0);
        g.drawString("0,0", 10, myDim.height-10);           // Paints origo
        
        // Paints numbers and intervals on x-axis (cycle)
        int xx = new Float(max_x_value/X_ENHETER).intValue();
        int x = 0;
        String text = new Integer(new Double(X_INTERVALL).intValue()).toString();
        for (int i = 1; i < X_ENHETER+1;) {
            x = x + xx;
            g.drawString(text, getAbsX(x)-15, myDim.height-2);
            plotLine(g, x, 5/stepY, x, -5/stepY);
            plotXGridLine(g, x);
            text = new Integer(new Double(X_INTERVALL * ++i).intValue()).toString();
        }
        
        // Paints numbers and intervals on y-axis (error)
        Y_ENHETER = 10;    // Splits the y-axis in 10 egual parts
        double yy = max_y_value/Y_ENHETER;
        double y = 0;
        // The numbers are rounded to the min needed decimals
        int prc = getPrecision(max_y_value);
        int exp = (int) Math.pow(10.0, prc);
        //int exp = new Double(Math.pow(10.0, (double)prc)).intValue();
        for (int i = 1; i < Y_ENHETER+1; i++) {
            y = y + yy;
            double valY = (max_y_value/Y_ENHETER) * (Y_ENHETER+1 - i);
            int truncY = new Double(valY * exp).intValue();
            valY = new Double(truncY).doubleValue() / exp;
            text = new Double(valY).toString();
            plotLine(g, new Double(-5/stepX).intValue(), y, new Double(5/stepX).intValue(), y);
            plotYGridLine(g, y);
            g.drawString(text, 2, getAbsY(max_y_value - y + yy)+2);
        }
    }
    
    private int getPrecision(double xMax) {
        int div = new Double(10.0/xMax).intValue();
        if (div == 0) div = 1;
        double log10 = Math.log(div) / Math.log(10);
        int dec = new Double(log10).intValue()+2;
        return dec;
    }
    /**
     * Plots a line from (start_x,start_y) to (slutt_x,slutt_y)
     */
    private void plotLine(Graphics g, int start_x, double start_y, int slutt_x, double slutt_y) {
        int x1 = getAbsX(start_x);
        int y1 = getAbsY(start_y);
        int x2 = getAbsX(slutt_x);
        int y2 = getAbsY(slutt_y);
        g.drawLine(x1, y1, x2, y2);
    }
    
    private int getAbsX(int x) {
        int x1 = new Double(x * stepX).intValue();
        return x1+X;
    }
    
    private int getAbsY(double y) {
        int y1 = new Double(y * stepY).intValue();
        return myDim.height-Y-y1;
    }
    /**
     * Plots the y-axis grid lines
     */
    private void plotYGridLine(Graphics g, double y) {
        g.setColor(gray);
        plotLine(g,0,y,max_x_value,y);
        g.setColor(original);
    }
    
    /**
     * Plots the x-axis grid lines
     */
    private void plotXGridLine(Graphics g, int x) {
        g.setColor(gray);
        plotLine(g,x,0,x,max_y_value);
        g.setColor(original);
    }
    
    /**
     * Plots the graph from the local absolut grafData[]
     */
    private void plot(Graphics g, double[] grafData, Color col) {
        boolean firstPoint = false;
        g.setColor(col);
        for (int i = 2; i <= currentCycle; i++) {
            if ((grafData[i-1] != 0) || (firstPoint)) { // Does not plot the initial zero values
                plotLine(g, i - 1, grafData[i-1], i, grafData[i]);
                firstPoint = true;
            }
        }
    }
    /**
     * Starts the thread
     */
    public void start() {                        //implements java.lang.Runnable
        if (myThread == null) {
            myThread = new Thread(this, "DrawingRegion");
            try { myThread.start(); }
            catch (Exception e) {
                String msg = "DrawingRegion:start() : " + e.getMessage();
                log.fatal(msg, e);
                System.exit(1);
            }
        }
    }
    
    /**
     * Gets the new data from the producer and store them to grafData[].
     */
    public void run() { //implements java.lang.Runnable
        int i;
        int imax;
        
        i = 0;
        while (keepPlotting) {
            if (grafBuffer != null)
                i = getData(null);
            imax = i;
            for (Enumeration k=buffers.keys(); k.hasMoreElements();) {
                ChartingHandle handle = (ChartingHandle)k.nextElement();
                i = getData(handle);
                if (i > imax)
                    imax = i;
            }
            if ( imax > 0 ) {
                // There have been updates on the screen, calls repaint()
                currentCycle = imax;
                repaint();
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) { }
        }
        myThread = null;  // Stop running
    }
    
    private int getData(ChartingHandle handle) {
        double[][] error;
        double[] data;
        if (handle == null) {
            error = grafBuffer.get();   // get the new data
            data = grafData;
        }
        else {
            SharedBuffer buff = (SharedBuffer)buffers.get(handle);
            error = buff.getNoWait();
            data = (double[])grafDataMul.get(handle);
            if (data == null) {
                data = new double[max_x_value+1];
                grafDataMul.put(handle, data);
            }
        }
        int cycle = 0;
        if (error != null)
            for (int i = 0; i < error.length; i++) {
                int x = new Double(error[i][CYCLE]).intValue();
                if ((x == -1) || (x >= data.length)) { // reached the end
                    break;
                }
                data[x] = error[i][ERROR];
                cycle = x;
            }
        return cycle;
    }
    /** Getter for property max_x_value.
     * @return Value of property max_x_value.
     */
    public int getMaxXvalue() {
        return max_x_value;
    }
    
    /** Setter for property max_x_value.
     * @param max_x_value New value of property max_x_value.
     */
    public void setMaxXvalue(int max_x_value) {
        this.max_x_value = max_x_value;
        copyBuffer(max_x_value);
        getXEnhet(max_x_value);
        setDimensions(myDim);
        repaint();
    }
    
    /** Getter for property max_y_value.
     * @return Value of property max_y_value.
     */
    public double getMaxYvalue() {
        return max_y_value;
    }
    
    /** Setter for property max_y_value.
     * @param max_y_value New value of property max_y_value.
     */
    public void setMaxYvalue(double max_y_value) {
        this.max_y_value = max_y_value;
        setDimensions(myDim);
        repaint();
    }
    
    private void copyBuffer(int newSize) {
        double[] newBuff = new double[newSize+1];
        int xMax = newSize;
        if (xMax > grafData.length)
            xMax = grafData.length;
        for (int i=0; i < xMax; ++i) {
            newBuff[i] = grafData[i];
        }
        if (currentCycle >= xMax)
            currentCycle = xMax - 1;
        grafData = newBuff;
    }
    
    protected void removeHandle(ChartingHandle handle)  {
        if (buffers != null)
            buffers.remove(handle);
        if (grafDataMul != null)
            grafDataMul.remove(handle);
    }
}
