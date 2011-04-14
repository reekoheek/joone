/*
 * DesiredLayerConnection.java
 *
 * Created on 31 maggio 2001, 23.41
 */

package org.joone.edit;

import CH.ifa.draw.framework.*;
import CH.ifa.draw.figures.*;
import java.awt.Color;
import org.joone.engine.*;
import org.joone.engine.learning.*;

public class DesiredLayerConnection extends LayerConnection {
    
    private static final long serialVersionUID = 2270555361169892354L;
    
    /** Creates new DesiredLayerConnection */
    public DesiredLayerConnection() {
        setEndDecoration(null);
        setStartDecoration(new ArrowTip());
        setAttribute("FrameColor", Color.yellow);
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
        TeacherLayerFigure tlf = (TeacherLayerFigure)start;
        ComparingElement ts = (ComparingElement)tlf.getOutputLayer();
        if (ts.getDesired() != null)
            retValue = false;
        return retValue;
    }
    
    /** handle the connection behaviour
     * @param start starting layer
     * @param end ending layer
     */
    public void handleConnect(Figure start, Figure end) {
        /* source = end and target = start
         * because the arrow goes from InputLayerFigure to TeacherLayerFigure
         */
        InputLayerFigure source = (InputLayerFigure)end;
        TeacherLayerFigure target = (TeacherLayerFigure)start;
        InputPatternListener myInputSynapse = source.getInputLayer();
        target.addPreConn(source, myInputSynapse);
        source.addPostConn(target);
        source.notifyPostConn();
        setAttribute("FrameColor", Color.yellow);
    }
    
    /** handle the disconnection behaviour
     * @param start starting layer
     * @param end ending layer
     */
    public void handleDisconnect(Figure start, Figure end) {
        /* source = end and target = start
         * because the arrow goes from InputLayerFigure to TeacherLayerFigure
         */
        InputLayerFigure source = (InputLayerFigure)end;
        TeacherLayerFigure target = (TeacherLayerFigure)start;
        if (target != null) {
            target.removePreConn(source, this.getSynapse());
        }
        if (source != null) {
            source.removePostConn(target);
            source.getInputLayer().setInputFull(false);
        }
    }
    
}
