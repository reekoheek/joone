
/**
 * Pop up a (modal) Message dialog and wait for a user to press "continue".
 */

package org.joone.edit;

import java.awt.*;
import java.awt.event.*;

public class MessageDialog extends Dialog implements ActionListener {

    private static final long serialVersionUID = -5288039878156013941L;
    
    public MessageDialog(Frame frame, String title, String message) {
	this(frame, title, message, false);
    }

    public MessageDialog(Frame frame, String title,
			 String message, boolean leftIndented) {
	super(frame, title, true);
    	new WindowCloser(this);
	
	GridBagLayout gridBag = new GridBagLayout();
	setLayout(gridBag);
	GridBagConstraints cons = new GridBagConstraints();
	cons.gridwidth = GridBagConstraints.REMAINDER;
	if (leftIndented) {
	    cons.anchor = GridBagConstraints.WEST;
	}

	// Add a "Label" for reach line of text.
	int height = 5;
	while (message.length() > 0) {
	    int ix = message.indexOf('\n');
	    String line;
	    if (ix >= 0) {
		line = message.substring(0, ix);
		message = message.substring(ix+1);
	    } else {
		line = message;
		message = "";
	    }
	    Label l = new Label(line);
	    gridBag.setConstraints(l, cons);
	    add(l);
	    height += 20;
	}

	cons.anchor = GridBagConstraints.CENTER;
	Button b = new Button("Continue");
	b.addActionListener(this);
	gridBag.setConstraints(b, cons);
	add(b);
	height += 25;
	height += 35;

        int x = frame.getLocation().x + 30;
        int y = frame.getLocation().y + 100;
        setBounds(x, y, 400, height+5);
        show();
    }

    public void actionPerformed(ActionEvent evt) {
	// our button got pushed.
        dispose();
    }

}
