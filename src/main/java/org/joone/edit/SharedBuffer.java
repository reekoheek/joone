/*
 * SharedBuffer.java
 *
 * Created on 4. juli 2002, 14:56
 */

package org.joone.edit;

import org.joone.engine.Fifo;
import org.joone.engine.Pattern;

/**
 * @author  Jan Erik Garshol, jangar@idi.ntnu.no
 * @version 0.1 beta
 * This class is, as it's name points out, only a buffer for some shared data.
 * It uses a double[int bufferSize][2] to store the shared data, produced by a
 * Producer object and consumed by a Consumer object.
 *
 * Use public synchronized void put(double value, double cycle) to store data
 * and public synchronized double[][] get() to get the data, the buffer will be
 * cleed after a get().
 *
 * A DrawingRegion object will have the role as the Consumer object in a joint
 * SharedBuffer object with a Producer object, which roll you will have to fill
 * by calling the put() metod. The DrawingRegion object
 * will create a instance of this class use public SharedBuffer getBuffer() to
 * get it.
 *
 * Short variant:
 *      - Creat a Joone Neural Net object
 *      - Creat a DrawingRegion object, DrawingRegion(int)
 *      - Add the DrawingRegion object to the GUI (JFrame)
 *      - Get a referance to the SharedBuffer object, from the DrawingRegion
 *        object, public SharedBuffer getBuffer()
 *      - Call SharedBuffer.put(error, cycle) from the Joone Neural Net object
 *        in the public void cicleTerminated(org.joone.engine.NeuralNetEvent)
 *        metod.
 */
public class SharedBuffer {    
    private final int ERROR = 0;
    private final int CYCLE = 1;
    private double[][] buffer;
    private Fifo fifoBuffer;
    private ChartingHandle handle;
    /** Creates new SharedBuffer */
    public SharedBuffer() {
        fifoBuffer = new Fifo();
    }
    
    
    /**
     * Returns the buffer and empty it.
     * The calling Thread falls in a wait state if the buffer is empty
     */
    public synchronized double[][] get() {
        while (fifoBuffer.isEmpty()) {
            try { // Buffer is empty
                wait();
            }
            catch (InterruptedException e) { }
        }
        buffer = extractBuffer(fifoBuffer);
        notifyAll();
        return buffer;
    }
    
    /**
     * Returns the buffer and empty it.
     * The calling Thread doesn't fall in a wait state if the buffer is empty,
     * but simply returns a null value.
     */
    public synchronized double[][] getNoWait() {
        if (fifoBuffer.isEmpty()) 
            return null;
        buffer = extractBuffer(fifoBuffer);
        return buffer;
    }
    
    private double[][] extractBuffer(Fifo fifo) {
        int size = fifo.size();
        double[][] buff = new double[size + 1][2];
        for (int i = 0; i < size; ++i) {
            Pattern patt = (Pattern)fifo.pop();
            buff[i][CYCLE] = patt.getCount();
            buff[i][ERROR] = patt.getArray()[0];
        }
        // Mark this as the end of the buffer
        buff[size][ERROR] = 0.0; 
        buff[size][CYCLE] = -1.0; 
        return buff;
    }
    /**
     * Placees a pair of double in the next free place in the buffer.
     */
    public synchronized void put(double value, double cycle) {
        Pattern patt = new Pattern(new double[] {value});
        patt.setCount(new Double(cycle).intValue());
        fifoBuffer.push(patt);
        notifyAll();
    }
    
    /** Getter for property handle.
     * @return Value of property handle.
     *
     */
    public ChartingHandle getHandle() {
        return handle;
    }
    
    /** Setter for property handle.
     * @param handle New value of property handle.
     *
     */
    public void setHandle(ChartingHandle handle) {
        this.handle = handle;
    }
    
}
