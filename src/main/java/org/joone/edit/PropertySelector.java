
// Support for PropertyEditors that use tags.

package org.joone.edit;

import java.awt.event.*;
import java.beans.*;
import javax.swing.*;

class PropertySelector extends JComboBox implements ItemListener {

    PropertySelector(PropertyEditor pe) {
	editor = pe;
	String tags[] = editor.getTags();
	for (int i = 0; i < tags.length; i++) {
	    addItem(tags[i]);
	}
        setSelectedIndex(0);
	//select(0);
	// This is a noop if the getAsText is not a tag.
	//select(editor.getAsText());
        setSelectedItem(editor.getAsText());
	addItemListener(this);
    }

    public void itemStateChanged(ItemEvent evt) {
	String s = (String)getSelectedItem();
	editor.setAsText(s);
    }

    public void repaint() {
        super.repaint();
	//select(editor.getAsText());
        //setSelectedItem(editor.getAsText());
    }

    PropertyEditor editor;    
    
    private static final long serialVersionUID = -9078008640632296429L;
    
}
