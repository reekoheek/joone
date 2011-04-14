/*
 * OutputLayerFigure.java
 *
 * Created on 5 aprile 2001, 1.05
 */

package org.joone.edit;
import java.awt.*;
import java.util.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import org.joone.engine.*;
import org.joone.io.*;
import org.joone.util.OutputConverterPlugIn;

public class OutputLayerFigure extends org.joone.edit.LayerFigure {
    
    protected OutputPatternListener myOutputLayer;
    
    private static final long serialVersionUID = 2256335810481848745L;
    
    /** Creates new OutputLayerFigure */
    public OutputLayerFigure() {
    }
    
    protected void initContent() {
        fPostConn = new Vector();
        fPreConn = new Vector();
        
        Font fb = new Font("Helvetica", Font.BOLD, 12);
        
        myOutputLayer = (OutputPatternListener)createLayer();
        
        TextFigure name = new UpdatableTextFigure() {
            public void setText(String newText) {
                super.setText(newText);
                getOutputLayer().setName(newText);
            }
            public void update() {
                if (myOutputLayer.isEnabled())
                    this.setAttribute("TextColor", Color.blue);
                else
                    this.setAttribute("TextColor", Color.gray);
                setText(getOutputLayer().getName());
            }
        };
        
        name.setFont(fb);
        name.setAttribute("TextColor", Color.blue);
        name.setText("OutputLayer " + ++numLayers);
        add(name);
    }
    
    
    public void addPostConn(LayerFigure figure, OutputPatternListener synapse) {
    }
    
    public boolean addPreConn(LayerFigure figure) {
        if (!fPreConn.contains(figure)) {
            fPreConn.addElement(figure);
            return true;
        }
        else
            return false;
    }
    
    public void removePreConn(LayerFigure figure) {
        super.removePreConn(figure);
    }
    
    public void addPreConn(LayerFigure layerFigure, OutputConverterPlugIn plugin) {
        OutputPatternListener opl = getOutputLayer();
        if (opl instanceof StreamOutputSynapse) {
            StreamOutputSynapse sos = (StreamOutputSynapse)opl;
            sos.addPlugIn(plugin);
            if (!fPreConn.contains(layerFigure)) {
                fPreConn.addElement(layerFigure);
            }
        }
    }
    
    public void removePreConn(LayerFigure figure, OutputConverterPlugIn plugin) {
        fPreConn.removeElement(figure);
        OutputPatternListener opl = getOutputLayer();
        if (opl instanceof StreamOutputSynapse) {
            StreamOutputSynapse sos = (StreamOutputSynapse)opl;
            sos.addPlugIn(null);
        }
    }
    
    protected Vector addHandles(Vector handles) {
        handles.addElement(new SpecialConnectionHandle(this, RelativeLocator.south(),
        new OutputPluginConnection(), Color.magenta));
        return handles;
    }
    
    
    /** Get the InputLayer object
     * @return the InputLayer object (org.joone.StreamInputSynapse)
     */
    
    public OutputPatternListener getOutputLayer() {
        return myOutputLayer;
    }
    
    /** Returns the wrapper for the Figure
     * @return the Wrapper object
     */
    public Wrapper getWrapper() {
        return new Wrapper(this, getOutputLayer(), getOutputLayer().getName());
    }
    
    
    public boolean canConnect() {
        boolean retValue;
        
        retValue = super.canConnect() && !getOutputLayer().isOutputFull();
        return retValue;
    }
    
}
