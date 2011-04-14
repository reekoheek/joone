package org.joone.edit;

import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.beans.PropertyEditorSupport;

/**
 * Implementation of a custom property editor using
 * the <code>JooneFileChooserEditor</code>.
 *
 * @author christian ribeaud <christian.ribeaud@genedata.com>
 */
public class JooneFileChooserEditor
extends PropertyEditorSupport
implements ActionListener {
    
    private JooneFileChooser fileChooser;
    private CaseAwareTextDisplay fileChooserPanel;
    
    public JooneFileChooserEditor() {
        fileChooser = new JooneFileChooser();
        fileChooser.addActionListener(this);
        fileChooserPanel = new CaseAwareTextDisplay();
        fileChooserPanel.setText("wordA wordB");
    }
    
    // Updates the display to reflect the user's selection.
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(JooneFileChooser.APPROVE_SELECTION)) {
            setValue(getValue());
        }
    }
    
    /**
     * @see java.beans.PropertyEditorSupport#isPaintable()
     */
    public boolean isPaintable() {
        return true;
    }
    
    /**
     * @see java.beans.PropertyEditor#paintValue(Graphics, Rectangle)
     */
    public void paintValue(Graphics g, Rectangle box) {
        g.clipRect(box.x, box.y, box.width, box.height);
        g.translate(box.x, box.y);
        g.setColor(Color.white);
        g.fillRect(0, 0, box.width, box.height);
        g.setColor(Color.black);
        fileChooserPanel.paint(g);
    }
    
    // Updates the display and the file chooser to reflect the
    // specified value. It also informs the tool of the property change by
    // firing a property change event.
    public void setValue(Object value) {
        File file = new File((String)value);
        fileChooser.setSelectedFile(file);
        fileChooserPanel.setText(file.getName());
        firePropertyChange();
    }
    
    // Gets the currently selected property value in the user interface.
    public Object getValue() {
        return fileChooser.getSelectedFile().getAbsolutePath();
    }
    
    /**
     * @see java.beans.PropertyEditor#getCustomEditor()
     */
    public Component getCustomEditor() {
        return fileChooser;
    }
    
    /**
     * @see java.beans.PropertyEditor#supportsCustomEditor()
     */
    public boolean supportsCustomEditor() {
        return true;
    }
    
}