
package org.joone.edit;

import java.awt.*;
import java.util.*;
import java.io.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.util.*;
import org.joone.engine.*;

/** Graphic representation of a Layer in the net
 */
public class LayerFigure extends ConcreteGenericFigure {
    protected Vector fPostConn;
    protected Vector fPreConn;
    protected Layer myLayer;
    
        /*
         * Serialization support.
         */
    private static final long serialVersionUID = -1148000139027412009L;
    
    
    public LayerFigure() {
        super();
    }
    
    
    protected void initContent() {
        fPostConn = new Vector();
        fPreConn = new Vector();
        
        Font f = new Font("Helvetica", Font.PLAIN, 12);
        Font fb = new Font("Helvetica", Font.BOLD, 12);
        
        myLayer = (Layer)createLayer();
        
        
        TextFigure name = new UpdatableTextFigure() {
            public void setText(String newText) {
                super.setText(newText);
                Layer ly = (Layer)getLayer();
                ly.setLayerName(newText);
            }
            public void update() {
                Layer ly = (Layer)getLayer();
                setText(ly.getLayerName());
            }
        };
        
        name.setFont(fb);
        name.setText("Layer " + ++numLayers);
        name.setAttribute("TextColor", Color.blue);
        add(name);
        NumberTextFigure inputDim = new UpdatableNumberTextFigure() {
            public void setText(String newText) {
                super.setText(newText);
                Layer ly = (Layer)getLayer();
                ly.setRows(Integer.parseInt(newText));
            }
            public void update() {
                Layer ly = (Layer)getLayer();
                setValue(ly.getRows());
            }
        };
        
        inputDim.setValue(1);
        inputDim.setFont(f);
        add(inputDim);
    }
    
    public boolean hasCycle(Figure start) {
        if (start == this)
            return true;
        Enumeration i = fPreConn.elements();
        while (i.hasMoreElements()) {
            if (((LayerFigure) i.nextElement()).hasCycle(start))
                return true;
        }
        return false;
    }
    
    //-- store / load ----------------------------------------------
    
    public void write(StorableOutput dw) {
        super.write(dw);
        writeConn(dw, fPreConn);
        writeConn(dw, fPostConn);
    }
    
    public void writeConn(StorableOutput dw, Vector v) {
        dw.writeInt(v.size());
        Enumeration i = v.elements();
        while (i.hasMoreElements())
            dw.writeStorable((Storable) i.nextElement());
    }
    
    public void read(StorableInput dr) throws IOException {
        super.read(dr);
        fPreConn = readConn(dr);
        fPostConn = readConn(dr);
    }
    
    public Vector readConn(StorableInput dr) throws IOException {
        int size = dr.readInt();
        Vector v = new Vector(size);
        for (int i=0; i<size; i++)
            v.addElement(dr.readStorable());
        return v;
    }
    
    public void removePostConn(LayerFigure figure) {
        fPostConn.removeElement(figure);
    }
    
    public void removePreConn(LayerFigure figure) {
        fPreConn.removeElement(figure);
    }
    
    public void removePostConn(LayerFigure figure, OutputPatternListener conn) {
        this.removePostConn(figure);
        Layer ly = (Layer)getLayer();
        if (ly != null)
            ly.removeOutputSynapse(conn);
    }
    
    public void removePreConn(LayerFigure figure, InputPatternListener conn) {
        this.removePreConn(figure);
        Layer ly = (Layer)getLayer();
        if (ly != null)
            ly.removeInputSynapse(conn);
    }
    
    public void addPostConn(LayerFigure figure, OutputPatternListener conn) {
        if (!fPostConn.contains(figure)) {
            fPostConn.addElement(figure);
            Layer ly = (Layer)getLayer();
            if (ly != null)
                ly.addOutputSynapse(conn);
        }
    }
    /** Adds an incoming connection.
     * @return true if the connection doesn't exist
     */
    public boolean addPreConn(LayerFigure figure, InputPatternListener conn) {
        if (!fPreConn.contains(figure)) {
            fPreConn.addElement(figure);
            Layer ly = (Layer)getLayer();
            if (ly != null)
                ly.addInputSynapse(conn);
            return true;
        }
        else
            return false;
    }
    
    /** Can be used to set the Layer object represented (not used)
     * @param newMyLayer org.joone.engine.Layer
     */
    public void setLayer(Object newMyLayer) {
        myLayer = (Layer)newMyLayer;
    }
    
    public boolean canConnect(GenericFigure start, ConnectionFigure conn) {
        return true;
    }
    
    public static void setNumLayers(int newValue) {
        numLayers = newValue;
    }
    
    /** Returns the wrapper for the Figure
     * @return the Wrapper object
     */
    public Wrapper getWrapper() {
        Layer ly = (Layer)getLayer();
        return new Wrapper(this, ly, ly.getLayerName());
    }
    
    /** Get the Layer object
     * @return the Layer object
     */
    public Object getLayer() {
        return myLayer;
    }
    
    protected Vector addHandles(Vector handles) {
        LayerConnection lc = new LayerConnection();
        lc.setParam("class","org.joone.engine.FullSynapse");
        lc.setParam("label","F");
        handles.addElement(new ConnectionHandle(this, RelativeLocator.east(), lc));
        return handles;
    }
    
    
}