/*
 * InputLayerConnection.java
 *
 * Created on 3 aprile 2001, 0.31
 */

package org.joone.edit;
import CH.ifa.draw.framework.*;
import org.joone.engine.*;
import org.joone.io.*;
/**
 *
 * @author  pmarrone
 */
public class InputLayerConnection extends org.joone.edit.LayerConnection {
    
    private static final long serialVersionUID = 2021057399294889866L;
    
    /** Creates new InputLayerConnection */
    public InputLayerConnection() {
        super();
    }
    
    public boolean canConnect(Figure start, Figure end) {
        boolean retVal;
        retVal = (start instanceof InputLayerFigure && end instanceof LayerFigure);
        retVal = retVal && !(end instanceof OutputLayerFigure);
        retVal = retVal && !(end instanceof InputPluginLayerFigure);
        if (retVal) {
            InputLayerFigure source = (InputLayerFigure)start;
            InputPatternListener ipl = source.getInputLayer();
            if (!(end instanceof InputConnectorLayerFigure))
                retVal = !ipl.isInputFull();
            else {
                InputConnectorLayerFigure target = (InputConnectorLayerFigure)end;
                InputConnector ic = (InputConnector)target.getInputLayer();
                retVal = !ic.isOutputFull();
            }
                
        }
        return retVal;
    }
    
    /** handle the connection behaviour
     * @param start starting layer
     * @param end ending layer
     */
    public void handleConnect(Figure start, Figure end) {
        InputLayerFigure source = (InputLayerFigure)start;
        LayerFigure target = (LayerFigure)end;
        InputPatternListener ipl = source.getInputLayer();
        target.addPreConn(source, ipl);
        source.addPostConn(target);
        source.notifyPostConn();
    }
    
    /** handle the disconnection behaviour
     * @param start starting layer
     * @param end ending layer
     */
    public void handleDisconnect(Figure start, Figure end) {
        InputLayerFigure source = (InputLayerFigure)start;
        LayerFigure target = (LayerFigure)end;
        if (target != null) {
            target.removePreConn(source, source.getInputLayer());
        }
        if (source != null)
            source.removePostConn(target);
    }
    
}
