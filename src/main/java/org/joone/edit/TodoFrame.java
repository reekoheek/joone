package org.joone.edit;

import org.joone.net.NetChecker;
import org.joone.net.NeuralNet;
import org.joone.net.NetCheck;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.TreeSet;
import java.util.Iterator;

/**
 * This class creates a To Do frame centered on the coordinates of the parent JoonEdit application.
 *
 * @author Harry Glasgow
 */
public class TodoFrame extends JFrame {

    /** The patenr JoonEdit application. */
    private JoonEdit owner = null;

    /** The center panel that holds the details of the checks. */
    private JPanel centerPanel = new JPanel();

    /**
     * Constructor.
     *
     * @param ownerArg the parent JooneEdit application.
     */
    public TodoFrame(Frame ownerArg) {

        // Remember the parent for later.
        owner = (JoonEdit) ownerArg;

        setTitle("To Do List");
        setResizable(false);

        // JFrames have to work with the RootPane, not the default container
        JRootPane rp = getRootPane();
        rp.setLayout(new BorderLayout());

        // Set up a panel in the CENTER to display validation checks.
        centerPanel.setLayout(new GridLayout(0, 1));
        rp.add(centerPanel, BorderLayout.CENTER);

        // Set up a Revalidate button.
        Button b = new Button("Revalidate");
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                check();
                pack();
                repaint();
            }
        });
        JPanel jp2 = new JPanel();
        jp2.add(b);
        rp.add(jp2, BorderLayout.SOUTH);
    }

    /**
     * Method to perform checks and display the validation checks.
     */
    public void show() {
        check();
        pack();
        setIconImage(owner.getIconImage());
        setLocation((owner.getWidth() - owner.getX() - getWidth()) / 2,
                (owner.getHeight() - owner.getY() - getHeight()) / 2);
        super.show();
    }

    /**
     * Method to check the network and displa the new validation errors.
     */
    private void check() {
        NeuralNetDrawing nnd = (NeuralNetDrawing) owner.drawing();
        NeuralNet nn = nnd.getNeuralNet();
        NetChecker nc = new NetChecker(nn);
        TreeSet checks = nc.check();
        centerPanel.removeAll();
        if (checks.isEmpty()) {
            centerPanel.add(new JLabel("Network checks out okay."));
        } else {
            Iterator iter = checks.iterator();
            while (iter.hasNext()) {
                NetCheck netCheck = (NetCheck) iter.next();
                centerPanel.add(new JLabel(netCheck.toString()));
            }
        }
    }
}