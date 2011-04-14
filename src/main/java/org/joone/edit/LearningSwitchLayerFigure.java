/*
 * LearningSwitchLayerFigure.java
 *
 * Created on 30 aprile 2002, 17.02
 */

package org.joone.edit;

import java.awt.*;
import java.util.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import org.joone.engine.*;
import org.joone.util.LearningSwitch;
import org.joone.io.*;

/**
 *
 * @author  pmarrone
 */
public class LearningSwitchLayerFigure extends InputLayerFigure {
    
    private static final long serialVersionUID = -7060434046498637411L;
    
    /** Creates a new instance of LearningSwitchLayerFigure */
    public LearningSwitchLayerFigure() {
    }
    
    protected void initContent() {
        fPostConn = new Vector();
        fPreConn = new Vector();
        
        Font fb = new Font("Helvetica", Font.BOLD, 12);
        
        myInputLayer = (StreamInputSynapse)createLayer();
        
        TextFigure name = new UpdatableTextFigure() {
            public void setText(String newText) {
                super.setText(newText);
                getInputLayer().setName(newText);
            }
            public void update() {
                setText(getInputLayer().getName());
            }
        };
        
        name.setFont(fb);
        name.setText("LearningSwitch " + ++numLayers);
        name.setAttribute("TextColor", Color.blue);
        add(name);
    }
    
    protected Vector addHandles(Vector handles) {
        handles.addElement(new ConnectionHandle(this, RelativeLocator.east(),
        new InputLayerConnection()));
        handles.addElement(new SpecialConnectionHandle(this, RelativeLocator.north(),
        new ValidationLayerConnection(), Color.red));
        return handles;
    }
    
    public boolean addPreConn(LayerFigure layerFigure, InputPatternListener synapse) {
        boolean ret = super.addPreConn(layerFigure, synapse);
        LearningSwitch ls = (LearningSwitch)getInputLayer();
        ls.addTrainingSet((StreamInputSynapse)synapse);
        return ret;
    }
    
    public void addPreValConn(LayerFigure layerFigure, InputPatternListener synapse) {
        super.addPreConn(layerFigure, synapse);
        LearningSwitch ls = (LearningSwitch)getInputLayer();
        ls.addValidationSet((StreamInputSynapse)synapse);
    }
        
    public void removePreConn(LayerFigure figure, InputPatternListener conn) {
        this.removePreConn(figure);
        LearningSwitch ls = (LearningSwitch)getInputLayer();
        ls.removeTrainingSet();
    }
    
    public void removePreValConn(LayerFigure figure, InputPatternListener conn) {
        this.removePreConn(figure);
        LearningSwitch ls = (LearningSwitch)getInputLayer();
        ls.removeValidationSet();
    }
    
}
