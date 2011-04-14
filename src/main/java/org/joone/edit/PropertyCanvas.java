
// Support for drawing a property value in a Canvas.

package org.joone.edit;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.beans.*;

class PropertyCanvas extends JPanel implements MouseListener {
    
    PropertyCanvas(JFrame frame, PropertyEditor pe) {
        this.frame = frame;
        editor = pe;
        addMouseListener(this);
    }
    
    public void paint(Graphics g) {
        Rectangle box = new Rectangle(2, 2, getSize().width - 4, getSize().height - 4);
        editor.paintValue(g, box);
    }
    
    private static boolean ignoreClick = false;
    
    public void mouseClicked(MouseEvent evt) {
        if (!ignoreClick) {
            try {
                ignoreClick = true;
                int x = frame.getLocation().x - 30;
                int y = frame.getLocation().y + 50;
                if (editor instanceof JooneFileChooserEditor) {
                    JooneFileChooser fileChooser = (JooneFileChooser)editor.getCustomEditor();
                    fileChooser.showOpenDialog(this);
                } else
                    new PropertyDialog(frame, editor, x, y);
            } finally {
                ignoreClick = false;
            }
        }
    }
    
    public void mousePressed(MouseEvent evt) {
    }
    
    public void mouseReleased(MouseEvent evt) {
    }
    
    public void mouseEntered(MouseEvent evt) {
    }
    
    public void mouseExited(MouseEvent evt) {
    }
    
    private JFrame frame;
    private PropertyEditor editor;
    
    private static final long serialVersionUID = -5920674472834991956L;
    
}