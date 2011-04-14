
// Support for a PropertyEditor that uses text.

package org.joone.edit;

import java.awt.event.*;
import java.beans.*;
import javax.swing.*;

class PropertyText extends JTextField implements KeyListener, FocusListener {
    
    public PropertyText(String text) {
        super(text);
        addKeyListener(this);
        addFocusListener(this);
    }
    
    public void repaint() {
        super.repaint();
    }
    
    protected void updateEditor() {
        try {
            editor.setAsText(getText());
        } catch (IllegalArgumentException ex) {
            // Quietly ignore.
        }
    }
    
    //----------------------------------------------------------------------
    // Focus listener methods.
    
    public void focusGained(FocusEvent e) {
    }
    
    public void focusLost(FocusEvent e) {
        updateEditor();
    }
    
    //----------------------------------------------------------------------
    // Keyboard listener methods.
    
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            updateEditor();
        }
    }
    
    public void keyPressed(KeyEvent e) {
    }
    
    public void keyTyped(KeyEvent e) {
    }
    
    /** Getter for property editor.
     * @return Value of property editor.
     */
    public java.beans.PropertyEditor getEditor() {
        return editor;
    }
    
    /** Setter for property editor.
     * @param editor New value of property editor.
     */
    public void setEditor(java.beans.PropertyEditor editor) {
        this.editor = editor;
    }
    
    //----------------------------------------------------------------------
    private PropertyEditor editor;
    
    private static final long serialVersionUID = -4456338011062875884L;
    
}
