/*
 
 * User: Harry Glasgow
 
 * Date: 12/12/2002
 
 * Time: 20:41:37
 
 * To change template for new class use
 
 * Code Style | Class Templates options (Tools | IDE Options).
 
 */

package org.joone.edit.inspection;

import CH.ifa.draw.util.Iconkit;
import CH.ifa.draw.framework.HJDError;
import javax.swing.*;
import java.util.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.joone.edit.JoonEdit;
import org.joone.inspection.Inspectable;
import org.joone.inspection.Inspection;

public class InspectionFrame extends JFrame {
    
    /** Collection of Inspection objects */
    
    Collection inspections = new ArrayList();
    javax.swing.JTabbedPane center;
    JTable[] tables;
    
    /**
     *
     * Constructor
     *
     * @param inspectableArg the Inspectable object.
     *
     */
    
    public InspectionFrame(Inspectable inspectableArg) {
        Iconkit kit = Iconkit.instance();
        final Collection copycol = inspectableArg.Inspections();
        if (kit == null)
            throw new HJDError("Iconkit instance isn't set");
        final Image img =
        kit.loadImageResource(JoonEdit.DIAGRAM_IMAGES + "JooneIcon.gif");
        this.setIconImage(img);
        if (inspectableArg != null) {
            inspections = inspectableArg.Inspections();
            setTitle("Inspection - " + inspectableArg.InspectableTitle());
        } else {
            setTitle("Inspection");
        }
        
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        JButton close = new JButton("Close");
        close.setToolTipText("Close the inspection frame");
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        JButton copybutton = new JButton("Copy");
        copybutton.setToolTipText("Copy data as tab delimeted");
        copybutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                String s = new String("");
                if (copycol == null) {} else {
                    Iterator iter = copycol.iterator();
                    
                    while (iter.hasNext()) {
                        Object o = iter.next();
                        
                        if (o instanceof Inspection) {
                            Inspection inspection = (Inspection)o;
                            Component comp =  makeTable(inspection);
                            try {
                                // If component is a JTable
                                if (comp.getClass().isInstance(new JTable())) {
                                    JTable tab = (JTable)comp;
                                    int f = inspection.rowNumbers() ? 1 : 0;
                                    for (int i = 0; i < tab.getRowCount(); i++) {
                                        for (int j = f; j < tab.getColumnCount(); j++) {
                                            if (j > f)
                                                s += "\t";
                                            s += tab.getModel().getValueAt(i, j).toString();
                                        }
                                        s += "\n";
                                    }
                                    StringSelection contents = new StringSelection(s);
                                    cb.setContents(contents, null);
                                } // End if JTable
                                
                            } catch (Throwable exc) {}
                        }
                    }
                }
            }
        });
        
        JButton pastebutton = new JButton("Paste");
        pastebutton.setToolTipText("Paste data from tab delimeted source");
        pastebutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable contents = cb.getContents(null);
                if (contents != null)  {
                    try {
                        // Read the system clipboard
                        String s = (String)contents.getTransferData(DataFlavor.stringFlavor);
                        java.io.StringReader sr = new java.io.StringReader(s);
                        java.io.LineNumberReader lnr = new java.io.LineNumberReader(sr);
                        // Gets the current inspetcion component
                        int curr = center.getSelectedIndex();
                        Object[] arr = inspections.toArray();
                        Inspection inspection = (Inspection)arr[curr];
                        Object[][] newValues = inspection.getComponent();
                        if (newValues == null)
                            return;
                        String line = null;
                        int iLine = 0;
                        while (((line = lnr.readLine()) != null) && (iLine < newValues.length)) {
                            StringTokenizer st = new StringTokenizer(line, " ;,\t\n\r\f");
                            int iCol = 0;
                            if (inspection.rowNumbers())
                                iCol = 1;
                            while ((st.hasMoreTokens()) && (iCol < newValues[0].length)) {
                                String token = st.nextToken();
                                Double val;
                                try {
                                    val = new Double(token);
                                    newValues[iLine][iCol] = val;
                                }
                                catch (NumberFormatException ignore) {}
                                ++iCol;
                            }
                            ++iLine;
                        }
                        // Sets the new values
                        inspection.setComponent(newValues);
                        // Substutes the JTable with a new one
                        center.removeTabAt(curr);
                        Component pan = createPanel(makeTable(inspection));
                        center.insertTab(inspection.getTitle(), null, pan, null, curr);
                        center.setSelectedIndex(curr);
                    } catch (Exception exc) {exc.printStackTrace();}
                }
            }
        });
        
        JPanel east = new JPanel();
        
        east.setLayout(new GridLayout(6, 1));
        
        east.add(close);
        east.add(copybutton);
        east.add(pastebutton);
        
        contentPane.add(east, BorderLayout.EAST);
        center = new JTabbedPane();
        
        if (inspectableArg == null) {
            addTab(center, "error", new JLabel("Inspectable object is null."));
        } else {
            Collection col = inspectableArg.Inspections();
            
            if (col == null) {
                addTab(
                center,
                "Error",
                new JLabel("Inspectable object Collection is null."));
            } else {
                Iterator iter = col.iterator();
                while (iter.hasNext()) {
                    Object o = iter.next();
                    if (o instanceof Inspection) {
                        Inspection inspection = (Inspection)o;
                        String title = inspection.getTitle();
                        addTab(center, title, makeTable(inspection));
                    } else {
                        addTab(
                        center,
                        "Error",
                        new Label("Object is not an Inspection."));
                    }
                }
            }
        }
        contentPane.add(center, BorderLayout.CENTER);
    }
    
    private void addTab(
    JTabbedPane jTabbedPane,
    String title,
    Component component) {
        jTabbedPane.addTab(title, createPanel(component));
    }
    
    private Component createPanel(Component component) {
        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        jPanel.add(component);
        
        JScrollPane jScrollPane =
        new JScrollPane(
        jPanel,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return jScrollPane;
    }
    
    public void show() {
        pack();
        if (getWidth() < 250) {
            setSize(250, getHeight());
        }
        if (getWidth() > 500) {
            setSize(500, getHeight());
        }
        if (getHeight() > 200) {
            setSize(getWidth(), 200);
        }
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(
        (screenSize.width - getWidth()) / 2,
        (screenSize.height - getHeight()) / 2);
        
        super.show();
    }
    
    private Component makeTable(Inspection inspection) {
        Object[][] array = inspection.getComponent();
        Object[] names = inspection.getNames();
        boolean rowsNumber = inspection.rowNumbers();
        if ((array != null) && (names != null)) {
            JTable jTable = new JTable(array, names);
            if (rowsNumber) {
                TableColumn col = jTable.getColumnModel().getColumn(0);
                DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
                renderer.setToolTipText("Row Number");
                renderer.setBackground(new Color(178, 178, 255));
                col.setCellRenderer(renderer);
            }
            jTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
            jTable.doLayout();
            jTable.setEnabled(false);
            return jTable;
        } else {
            return new JLabel("There are no values set for this item.");
        }
        
    }
}
