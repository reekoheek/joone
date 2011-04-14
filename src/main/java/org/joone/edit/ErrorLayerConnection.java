/*
 * ErrorLayerConnection.java
 *
 * Created on 23 ottobre 2001, 23.04
 */

package org.joone.edit;

import CH.ifa.draw.framework.*;
//import java.awt.Color;
import org.joone.engine.*;
/**
 *
 * @author  PMLMAPA
 * @version 
 */
public class ErrorLayerConnection extends LayerConnection {

    private static final long serialVersionUID = -7643062865327340049L;
    
    /** Creates new ErrorLayerConnection */
    public ErrorLayerConnection() {
    }

    /** handle the disconnection behaviour
     * @param start starting layer
     * @param end ending layer
     */
    public void handleDisconnect(Figure start, Figure end) {
        super.handleDisconnect(start, end);
    }
    
    /** handle the connection behaviour
     * @param start starting layer
     * @param end ending layer
     */
    public void handleConnect(Figure start, Figure end) {
        TeacherLayerFigure source = (TeacherLayerFigure)start;
        OutputLayerFigure target = (OutputLayerFigure)end;
        mySynapse = (Synapse)target.getOutputLayer();
        target.addPreConn(source, mySynapse);
        source.addPostConn(target, mySynapse);
        source.notifyPostConn();
    }
    
    /** determines if the two layers can be connected
     * @param start starting layer
     * @param end ending layer
     * @return true if can be connected
     */
    public boolean canConnect(Figure start, Figure end) {
        boolean retValue;
        
        retValue = super.canConnect(start, end);
        if (!(end instanceof OutputLayerFigure))
            retValue = false;
        return retValue;
    }
    
}
