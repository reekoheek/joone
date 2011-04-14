/*
 * TeacherLayerFigure.java
 *
 * Created on 31 maggio 2001, 23.07
 */

package org.joone.edit;

import java.util.*;
import java.awt.Color;
import CH.ifa.draw.standard.*;
import org.joone.engine.*;
import org.joone.engine.learning.*;
import org.joone.io.*;

public class TeacherLayerFigure extends OutputLayerFigure {
    
    private static final long serialVersionUID = -7092869490372528874L;
    
    /** Creates new TeacherLayerFigure */
    public TeacherLayerFigure() {
    }
    
    protected Vector addHandles(Vector handles) {
        handles.addElement(new SpecialConnectionHandle(this, RelativeLocator.north(),
        new DesiredLayerConnection(), Color.red));
        handles.addElement(new ConnectionHandle(this, RelativeLocator.east(),
        new LayerConnection()));
        //new ErrorLayerConnection()));
        return handles;
    }
    
    public boolean addPreConn(LayerFigure figure, InputPatternListener synapse) {
        boolean ret = super.addPreConn(figure, synapse);
        if (figure instanceof InputLayerFigure) {
            ComparingElement ts = (ComparingElement)getOutputLayer();
            ts.setDesired((StreamInputSynapse)synapse);
        }
        return ret;
    }
    
    public void removePreConn(LayerFigure figure, InputPatternListener conn) {
        super.removePreConn(figure);
        if (figure instanceof InputLayerFigure) {
            ComparingElement ts = (ComparingElement)getOutputLayer();
            ts.setDesired(null);
        }
    }
    
    public void addPostConn(LayerFigure figure, OutputPatternListener synapse) {
        super.addPostConn(figure, synapse);
        if (!fPostConn.contains(figure)) {
        //if (figure instanceof OutputLayerFigure) {
            ComparingElement ts = (ComparingElement)getOutputLayer();
            ts.addResultSynapse(synapse);
        }
    }
    
    public void removePostConn(LayerFigure figure, OutputPatternListener conn) {
        removePostConn(figure);
        //if (figure instanceof OutputLayerFigure) {
            ComparingElement ts = (ComparingElement)getOutputLayer();
            ts.removeResultSynapse(conn);
        //}
    }
    
}
