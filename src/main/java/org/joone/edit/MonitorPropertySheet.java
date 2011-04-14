/*
 * MonitorPropertySheet.java
 *
 * Created on 17 maggio 2001, 23.09
 */

package org.joone.edit;

import javax.swing.*;
import java.awt.*;
import org.joone.net.NeuralNet;

public class MonitorPropertySheet extends PropertySheet {
    
    private ControlPanel cp;
    private static final long serialVersionUID = 529383379412839729L;
    
    /** Creates new MonitorPropertySheet */
    public MonitorPropertySheet(Wrapper wr, NeuralNet nn) {
        super(wr, 300, 100, nn);
    }
    
    protected JComponent getContents() {
        JComponent propPanel;
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        propPanel = super.getPanel();
        cp = new ControlPanel(nNet, this);
        jp.add(cp, BorderLayout.NORTH);
        jp.add(propPanel, BorderLayout.CENTER);
        return jp;
    }
    
    public void setParameters(EditorParameters parameters) {
        if (cp != null)
            cp.setParameters(parameters);
    }    
}
