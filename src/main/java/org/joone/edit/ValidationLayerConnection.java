/*
 * ValidationLayerConnection.java
 *
 * Created on 30 aprile 2002, 17.32
 */

package org.joone.edit;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.figures.*;
import java.awt.Color;
import org.joone.engine.*;

/**
 *
 * @author  pmarrone
 */
public class ValidationLayerConnection extends LayerConnection {
    
    private static final long serialVersionUID = 2591688272592747396L;
    
    /** Creates a new instance of ValidationLayerConnection */
    public ValidationLayerConnection() {
        setEndDecoration(null);
        setStartDecoration(new ArrowTip());
        setAttribute("FrameColor", Color.blue);
    }
    /** determines if the two layers can be connected
     * @param start starting layer
     * @param end ending layer
     * @return true if can be connected
     */
    public boolean canConnect(Figure start, Figure end) {
        boolean retValue;
        
        retValue = super.canConnect(start, end);
        if (!(end instanceof InputLayerFigure))
            retValue = false;
        return retValue;
    }
    
    /** handle the connection behaviour
     * @param start starting layer
     * @param end ending layer
     */
    public void handleConnect(Figure start, Figure end) {
        /* source = end and target = start
         * because the arrow goes from InputLayerFigure to LearningSwitchLayerFigure
         */
        InputLayerFigure source = (InputLayerFigure)end;
        LearningSwitchLayerFigure target = (LearningSwitchLayerFigure)start;
        mySynapse = source.getInputLayer();
        target.addPreValConn(source, mySynapse);
        source.addPostConn(target);
        source.notifyPostConn();
    }
    
    /** handle the disconnection behaviour
     * @param start starting layer
     * @param end ending layer
     */
    public void handleDisconnect(Figure start, Figure end) {
        /* source = end and target = start
         * because the arrow goes from InputLayerFigure to LearningSwitchLayerFigure
         */
        InputLayerFigure source = (InputLayerFigure)end;
        LearningSwitchLayerFigure target = (LearningSwitchLayerFigure)start;
        if (target != null) {
            target.removePreValConn(source, this.getSynapse());
        }
        if (source != null)
            source.removePostConn(target);
    }
    
}
