package org.joone.edit;

/**
 * JoonEdit
 *
 */
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.print.*;
import java.io.*;
import java.net.*;
import javax.help.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.contrib.*;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.util.*;
import CH.ifa.draw.application.*;
import org.joone.net.*;
import org.joone.log.*;
import org.joone.samples.editor.som.*;

/**
 * This is the main class to start the JoonEdit application.
 *
 * @see CH.ifa.draw.application.DrawApplication
 */
public class JoonEdit extends DrawApplication {
    /**
     * Logger
     * */
    private static final ILogger log = LoggerFactory.getLogger( JoonEdit.class );
    
    // This release version of Editor
    private static final int MAJOR_RELEASE = 2;
    private static final int MINOR_RELEASE = 0;
    private static final int BUILD = 0;
    private static final String SUFFIX = "RC1";
    
    public static final String INI_FILE_NAME = "joonedit.config";
    
    // Recommended release version of Engine to function with this Editor
    private static final int RECO_ENG_MAJOR_RELEASE = 1;
    private static final int RECO_ENG_MINOR_RELEASE = 2;
    private static final int RECO_ENG_BUILD = 5;
    /**
     * Path where to find the pictures (relatively to where the class files are stored)
     */
    public static final String DIAGRAM_IMAGES = "/org/joone/images/";
    public static final String MENU_IMAGES = "/org/joone/images/menu/";
    private PropertySheet psp;
    private InputStream XMLParamsFile;
    private JooneFileChooser m_openDialog = null;
    private NetStorageFormatManager fNetStorageFormatManager;
    private NetStorageFormatManager xNetStorageFormatManager;
    private ToolsSAXParser tParser;
    private EditorParameters parameters;
    private MonitorPropertySheet ps;
    private static final long serialVersionUID = -4579162097589626753L;
    private AboutFrame af = null;
    private TodoFrame tf = null;
    private StorageFormat latestStorageFormat;
    private JMacroEditor macroEditor;
    private IniFile iniFile = null;
    
    // Have to keep track of width and height manually
    // because JHotDraw overrides default size.
    private int width = 800;
    private int height = 600;
    
    private class httpAuthenticateProxy extends Authenticator {
        private String userid;
        private String passw;
        
        public httpAuthenticateProxy(String userid, String passw) {
            this.userid = userid;
            this.passw = passw;
        }
        
        protected PasswordAuthentication getPasswordAuthentication() {
            // username, password
            // sets http authentication
            return new PasswordAuthentication(userid,passw.toCharArray());
        }
        
    }
    /**
     * Create a new instance of JoonEdit application
     * @param params Contains the name (with its path) of the XML parameter file
     * (see org.joone.data.layers.xml for an example)
     */
    public JoonEdit(String params) {
        super("JoonEdit - Joone Neural Net Editor");
        try {
            initJoonEdit(new FileInputStream(params));
        } catch (FileNotFoundException fnfe) {
            log.fatal( "FileNotFoundException thrown with params " + params + ". Message is " + fnfe.getMessage(),
            fnfe);
            System.exit(0);  // TO DO Define a proper return code ?
        }
    }
    
    public JoonEdit() {
        super("JoonEdit - Joone Neural Net Editor");
        InputStream is = getClass().getResourceAsStream("/org/joone/data/layers.xml");
        initJoonEdit(is);
    }
    
    private void initJoonEdit(InputStream isParams) {
        XMLParamsFile = isParams;
        fNetStorageFormatManager = createNetStorageFormatManager();
        xNetStorageFormatManager = createXMLStorageFormatManager();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); // Close handled by Joone.
        readIniFile();
    }
    
    public static void center(Window frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        frame.setLocation(
        screenSize.width / 2 - (frameSize.width / 2),
        screenSize.height / 2 - (frameSize.height / 2));
    }
    
    protected JComponent createContents(StandardDrawingView view) {
        JPanel jpan = new JPanel();
        jpan.setLayout(new BorderLayout());
        JScrollPane rsp = (JScrollPane)super.createContents(view);
        
        JPanel ptools = new JPanel(new BorderLayout());
        JToolBar[] tools = new JToolBar[2];
        for (int i=0; i < tools.length; ++i) {
            tools[i] = createToolPalette();
        }
        
        createMyTools(tools);
        
        ptools.add(tools[0], BorderLayout.NORTH);
        ptools.add(tools[1], BorderLayout.SOUTH);
        
        jpan.add(ptools, BorderLayout.NORTH);
        jpan.add(rsp, BorderLayout.CENTER);
        return jpan;
    }
    
    protected void createTools(JToolBar palette) {
        super.createTools(palette);
        
        palette.addSeparator();
        Tool tool = new TextTool(view(), new TextFigure());
        palette.add(createToolButton(IMAGES+"TEXT", "Label", tool));
        
        tool = new ConnectedTextTool(view(), new TextFigure());
        palette.add(createToolButton(IMAGES+"ATEXT", "Connected Text Tool", tool));
        
        palette.addSeparator();
        tool = new CreationTool(view(), new RectangleFigure());
        palette.add(createToolButton(IMAGES+"RECT", "Rectangle Tool", tool));
        
        tool = new CreationTool(view(), new RoundRectangleFigure());
        palette.add(createToolButton(IMAGES+"RRECT", "Round Rectangle Tool", tool));
        
        tool = new CreationTool(view(), new EllipseFigure());
        palette.add(createToolButton(IMAGES+"ELLIPSE", "Ellipse Tool", tool));
        
        tool = new CreationTool(view(), new TriangleFigure());
        palette.add(createToolButton(IMAGES+"TRIANGLE", "Triangle Tool", tool));
        
        tool = new CreationTool(view(), new DiamondFigure());
        palette.add(createToolButton(IMAGES+"DIAMOND", "Diamond Tool", tool));
        
        tool = new PolygonTool(view());
        palette.add(createToolButton(IMAGES+"POLYGON", "Polygon Tool", tool));
        
        tool = new CreationTool(view(), new LineFigure());
        palette.add(createToolButton(IMAGES+"LINE", "Line Tool", tool));
        
        tool = new BorderTool(view());
        palette.add(createToolButton(IMAGES+"BORDDEC", "Border Tool", tool));
        
        tool = new ScribbleTool(view());
        palette.add(createToolButton(IMAGES+"SCRIBBL", "Scribble Tool", tool));
        
        
    }
    /**
     * Create the tools for the toolbar. The tools are
     * described in the XML file passed as parameter of the application
     *
     * @param   palette toolbar to which the tools should be added
     */
    protected void createMyTools(JToolBar[] palettes) {
        LayerFigureCreationTool LFCtool;
        SynapseCreationTool SCtool;
        ToolElement te;
        tParser = new ToolsSAXParser(XMLParamsFile);
        Vector elements = tParser.getElements();
        JToolBar palette = palettes[0];
        for (int i=0; i < elements.size(); ++i) {
            te = (ToolElement)elements.elementAt(i);
            if (te.getType().compareToIgnoreCase("break") == 0) {
                palette = palettes[1];
            } 
            if (te.getType().compareToIgnoreCase("separator") == 0) {
                palette.addSeparator();
            }
            if (te.getType().compareToIgnoreCase("layer") == 0) {
                LFCtool = new LayerFigureCreationTool(view(), "org.joone.edit.LayerFigure");
                LFCtool.setParams(te.getParams());
                palette.add(createToolButton(DIAGRAM_IMAGES+(String)LFCtool.getParam("image"), "New "+(String)LFCtool.getParam("type")+" layer", LFCtool));
            }
            if (te.getType().compareToIgnoreCase("input_layer") == 0) {
                LFCtool = new LayerFigureCreationTool(view(), "org.joone.edit.InputLayerFigure");
                LFCtool.setParams(te.getParams());
                palette.add(createToolButton(DIAGRAM_IMAGES+(String)LFCtool.getParam("image"), "New "+(String)LFCtool.getParam("type")+" layer", LFCtool));
            }
            if (te.getType().compareToIgnoreCase("output_layer") == 0) {
                LFCtool = new LayerFigureCreationTool(view(), "org.joone.edit.OutputLayerFigure");
                LFCtool.setParams(te.getParams());
                palette.add(createToolButton(DIAGRAM_IMAGES+(String)LFCtool.getParam("image"), "New "+(String)LFCtool.getParam("type")+" layer", LFCtool));
            }
            if (te.getType().compareToIgnoreCase("teacher_layer") == 0) {
                LFCtool = new LayerFigureCreationTool(view(), "org.joone.edit.TeacherLayerFigure");
                LFCtool.setParams(te.getParams());
                palette.add(createToolButton(DIAGRAM_IMAGES+(String)LFCtool.getParam("image"), "New "+(String)LFCtool.getParam("type")+" layer", LFCtool));
            }
            if (te.getType().compareToIgnoreCase("input_plugin") == 0) {
                LFCtool = new LayerFigureCreationTool(view(), "org.joone.edit.InputPluginLayerFigure");
                LFCtool.setParams(te.getParams());
                palette.add(createToolButton(DIAGRAM_IMAGES+(String)LFCtool.getParam("image"), "New "+(String)LFCtool.getParam("type"), LFCtool));
            }
            if (te.getType().compareToIgnoreCase("monitor_plugin") == 0) {
                LFCtool = new LayerFigureCreationTool(view(), "org.joone.edit.MonitorPluginFigure");
                LFCtool.setParams(te.getParams());
                palette.add(createToolButton(DIAGRAM_IMAGES+(String)LFCtool.getParam("image"), "New "+(String)LFCtool.getParam("type"), LFCtool));
            }
            if (te.getType().compareToIgnoreCase("input_switch") == 0) {
                LFCtool = new LayerFigureCreationTool(view(), "org.joone.edit.InputSwitchLayerFigure");
                LFCtool.setParams(te.getParams());
                palette.add(createToolButton(DIAGRAM_IMAGES+(String)LFCtool.getParam("image"), "New "+(String)LFCtool.getParam("type"), LFCtool));
            }
            if (te.getType().compareToIgnoreCase("input_connector") == 0) {
                LFCtool = new LayerFigureCreationTool(view(), "org.joone.edit.InputConnectorLayerFigure");
                LFCtool.setParams(te.getParams());
                palette.add(createToolButton(DIAGRAM_IMAGES+(String)LFCtool.getParam("image"), "New "+(String)LFCtool.getParam("type"), LFCtool));
            }
            if (te.getType().compareToIgnoreCase("output_switch") == 0) {
                LFCtool = new LayerFigureCreationTool(view(), "org.joone.edit.OutputSwitchLayerFigure");
                LFCtool.setParams(te.getParams());
                palette.add(createToolButton(DIAGRAM_IMAGES+(String)LFCtool.getParam("image"), "New "+(String)LFCtool.getParam("type"), LFCtool));
            }
            if (te.getType().compareToIgnoreCase("script_plugin") == 0) {
                LFCtool = new LayerFigureCreationTool(view(), "org.joone.edit.ScriptPluginFigure");
                LFCtool.setParams(te.getParams());
                palette.add(createToolButton(DIAGRAM_IMAGES+(String)LFCtool.getParam("image"), "New "+(String)LFCtool.getParam("type"), LFCtool));
            }
            if (te.getType().compareToIgnoreCase("learning_switch") == 0) {
                LFCtool = new LayerFigureCreationTool(view(), "org.joone.edit.LearningSwitchLayerFigure");
                LFCtool.setParams(te.getParams());
                palette.add(createToolButton(DIAGRAM_IMAGES+(String)LFCtool.getParam("image"), "New "+(String)LFCtool.getParam("type"), LFCtool));
            }
            if (te.getType().compareToIgnoreCase("synapse") == 0) {
                SCtool = new SynapseCreationTool(view(), "org.joone.edit.LayerConnection");
                SCtool.setParams(te.getParams());
                palette.add(createToolButton(DIAGRAM_IMAGES+(String)SCtool.getParam("image"), "New "+(String)SCtool.getParam("type"), SCtool));
            }
            if (te.getType().compareToIgnoreCase("output_plugin") == 0) {
                LFCtool = new LayerFigureCreationTool(view(), "org.joone.edit.OutputPluginLayerFigure");
                LFCtool.setParams(te.getParams());
                palette.add(createToolButton(DIAGRAM_IMAGES+(String)LFCtool.getParam("image"), "New "+(String)LFCtool.getParam("type"), LFCtool));
            }
            if (te.getType().compareToIgnoreCase("chart_handle") == 0) {
                LFCtool = new LayerFigureCreationTool(view(), "org.joone.edit.ChartHandleLayerFigure");
                LFCtool.setParams(te.getParams());
                palette.add(createToolButton(DIAGRAM_IMAGES+(String)LFCtool.getParam("image"), "New "+(String)LFCtool.getParam("type"), LFCtool));
            }
        }
    }
    
    /**
     * Create a special selection tool which reacts on the right mouse button
     * to show a popup menu.
     *
     * @return  selection tool with special behaviour for the right mouse button
     */
    protected Tool createSelectionTool() {
        DelegationSelectionTool dst = new DelegationSelectionTool(view());
        psp = new PropertySheet(500,100);
        dst.setPropertyPanel(psp);
        return dst;
    }
    
    /**
     * Create the menues for a given menu bar.
     *
     * @param   mb  menu bar to which the menus should be added
     */
    protected void createMenus(JMenuBar mb) {
        mb.add(createFileMenu());
        mb.add(createEditMenu());
        mb.add(createAlignmentMenu());
        mb.add(createAttributesMenu());
        mb.add(createToolsMenu());
        mb.add(createWindowMenu());
        mb.add(createHelpMenu());
    }
    
    /**
     * Have to override this because it is hard coded in JHotDraw :-<
     */
    protected Dimension defaultSize() {
        return new Dimension(width, height);
    }
    
    /**
     * Create a Net menu.
     * The View menu permits to control some features of the neural net,
     * like showing the control panel, randomize the net, etc.
     *
     *  @return newly create net menu
     */
    protected JMenu createToolsMenu() {
        tf = new TodoFrame(this);
        JMenu menu = new JMenu("Tools");
        menu.setMnemonic(KeyEvent.VK_O);
        
        JMenuItem mi = new JMenuItem("Control Panel");
        mi.setMnemonic(KeyEvent.VK_C);
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                NeuralNetDrawing nnd = (NeuralNetDrawing)drawing();
                NeuralNet nn = nnd.getNeuralNet();
                ps = getMonitorPropertySheet(nn);
                ps.setParameters(parameters);
                ps.update();
                ps.setSize(330,350);
                ps.setVisible(true);
                ((JooneStandardDrawingView)view()).setModified(true);
            }
        });
        menu.add(mi);
        mi = new JMenuItem("To Do List");
        mi.setMnemonic(KeyEvent.VK_T);
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                tf.show();
            }
        });
        menu.add(mi);
        
        menu.addSeparator();
        
        mi = new JMenuItem("Add Noise");
        mi.setMnemonic(KeyEvent.VK_A);
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                NeuralNetDrawing nnd = (NeuralNetDrawing)drawing();
                NeuralNet nn = nnd.getNeuralNet();
                nn.addNoise(0.2);
                ((JooneStandardDrawingView)view()).setModified(true);
            }
        });
        menu.add(mi);
        
        mi = new JMenuItem("Randomize");
        mi.setMnemonic(KeyEvent.VK_R);
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                NeuralNetDrawing nnd = (NeuralNetDrawing)drawing();
                NeuralNet nn = nnd.getNeuralNet();
                nn.randomize(0.3);
                ((JooneStandardDrawingView)view()).setModified(true);
            }
        });
        menu.add(mi);
        
        menu.addSeparator();
        
        mi = new JMenuItem("Reset Input Streams");
        mi.setMnemonic(KeyEvent.VK_I);
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                NeuralNetDrawing nnd = (NeuralNetDrawing)drawing();
                NeuralNet nn = nnd.getNeuralNet();
                nn.resetInput();
                ((JooneStandardDrawingView)view()).setModified(true);
            }
        });
        
        menu.add(mi);
        menu.addSeparator();
        
        mi = new JMenuItem("Macro Editor");
        mi.setMnemonic(KeyEvent.VK_M);
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                NeuralNetDrawing nnd = (NeuralNetDrawing)drawing();
                NeuralNet nn = nnd.getNeuralNet();
                if (macroEditor == null) {
                    if (nn.getMacroPlugin() == null)
                        nn.setMacroPlugin(new org.joone.util.MacroPlugin());
                    macroEditor = new JMacroEditor(nn);
                }
                macroEditor.setVisible(true);
                ((JooneStandardDrawingView)view()).setModified(true);
            }
        });
        
        menu.add(mi);
        return menu;
    }
    
    protected MonitorPropertySheet getMonitorPropertySheet(NeuralNet nn) {
        if (ps == null) {
            Wrapper wr = new Wrapper(null, nn.getMonitor(), "Control Panel");
            ps = new MonitorPropertySheet(wr, nn);
        }
        return ps;
    }
    
    /**
     * Creates the alignment menu. Clients override this
     * method to add additional menu items.
     */
    protected JMenu createAlignmentMenu() {
        JooneCommandMenu menu = new JooneCommandMenu("Align");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.add(new ToggleGridCommand("Toggle Snap to Grid", view(), new Point(4,4)),  new MenuShortcut('t'));
        //JCheckBoxMenuItem jcme = new JCheckBoxMenuItem("Toggle Snap to Grid");
        menu.addSeparator();
        menu.add(new AlignCommand("Left", view(), AlignCommand.LEFTS),  new MenuShortcut('l'));
        menu.add(new AlignCommand("Center", view(), AlignCommand.CENTERS),  new MenuShortcut('c'));
        menu.add(new AlignCommand("Right", view(), AlignCommand.RIGHTS),  new MenuShortcut('r'));
        menu.addSeparator();
        menu.add(new AlignCommand("Top", view(), AlignCommand.TOPS),  new MenuShortcut('o'));
        menu.add(new AlignCommand("Middle", view(), AlignCommand.MIDDLES),  new MenuShortcut('m'));
        menu.add(new AlignCommand("Bottom", view(), AlignCommand.BOTTOMS),  new MenuShortcut('b'));
        return menu;
    }
    
    /**
     * Creates the file menu. Clients override this
     * method to add additional menu items.
     */
    protected JMenu createFileMenu() {
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        JMenuItem mi = new JMenuItem("New", new MenuShortcut('n').getKey());
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        mi.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int n = askForSave("Save changes to Neural Net?");
                if ((n == JOptionPane.NO_OPTION) || !((JooneStandardDrawingView)view()).isModified()) {
                    promptNew();
                    return;
                }
            }
            
        });
        menu.add(mi);
        
        mi = new JMenuItem("Open...", new MenuShortcut('o').getKey());
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        mi.addActionListener(
        new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                int n = askForSave("Save changes to Neural Net?");
                if ((n == JOptionPane.NO_OPTION) || !((JooneStandardDrawingView)view()).isModified()) {
                    promptOpen();
                }
            }
        });
        menu.add(mi);
        
        mi = new JMenuItem("Save", new MenuShortcut('s').getKey());
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        mi.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (latestStorageFormat != null &&
                getDrawingTitle() != null &&
                !getDrawingTitle().equals("")) {
                    saveDrawing(latestStorageFormat, getDrawingTitle());
                } else {
                    promptSaveAs();
                }
            }
        }
        );
        menu.add(mi);
        
        mi = new JMenuItem("Save As...", new MenuShortcut('a').getKey());
        mi.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                promptSaveAs();
            }
        }
        );
        menu.add(mi);
        
        mi = new JMenuItem("Export NeuralNet...", new MenuShortcut('e').getKey());
        mi.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                promptSaveNeuralNet();
            }
        }
        );
        menu.add(mi);
        
        mi = new JMenuItem("Export as XML...", new MenuShortcut('e').getKey());
        mi.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                promptSaveAsXML();
            }
        }
        );
        menu.add(mi);
        
        menu.addSeparator();
        mi = new JMenuItem("Print...", new MenuShortcut('p').getKey());
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        mi.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                print();
            }
        }
        );
        menu.add(mi);
        
        mi = new JMenuItem("Page Setup...", new MenuShortcut('u').getKey());
        mi.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                pageSetup();
            }
        }
        );
        menu.add(mi);
        
        menu.addSeparator();
        
        mi = new JMenuItem("Exit", new MenuShortcut('x').getKey());
        mi.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                exit();
            }
        }
        );
        menu.add(mi);
        return menu;
    }
    
    /** Creating a Help Menu */
    protected JMenu createHelpMenu() {
        CommandMenu menu = new CommandMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        af = new AboutFrame(this);
        
        try {
            
            // Create menu items.
            JMenuItem helpContents = new JMenuItem("Help Contents");
            helpContents.setAccelerator(KeyStroke.getKeyStroke("F1"));
            helpContents.setMnemonic(KeyEvent.VK_H);
            
            // Create broker for Joone help set.
            HelpSet hs = new HelpSet(null, HelpSet.findHelpSet(null, "org/joone/edit/help_contents/joone.hs"));
            HelpBroker hb = hs.createHelpBroker();
            hb.setSize(new Dimension(800, 600));
            hb.setFont(new Font("Helvetica", Font.PLAIN, 10));
            
            // Add listeners to menu item.
            helpContents.addActionListener(new CSH.DisplayHelpFromSource(hb));
            
            // Enable F1 key.
            hb.enableHelpKey(getRootPane(), "top", null);
            
            // Add items to the menu.
            menu.add(helpContents);
        } catch (Exception e) {
            log.warn( "Exception thrown while creating the MenuHelp. Message is : " + e.getMessage(),
            e);
        }
        
        JMenu samples = createSamplesMenu();
        menu.add(samples);
        menu.addSeparator();
        JMenuItem mi = new JMenuItem("About Joone");
        mi.setMnemonic(KeyEvent.VK_A);
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                
                // Center the About window on the center of this screen.
                Rectangle r = getBounds();
                af.place(r.x + r.width/2, r.y + r.height/2);
                af.setVisible(true);
            }
        });
        menu.add(mi);
        
        return menu;
    }
    
    private JMenu createSamplesMenu() {
        JMenu smp = new JMenu("Examples");
        JMenuItem smpi = new JMenuItem("SOM Image Tester");
        smpi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                new SOMImageTester().setVisible(true);
            }
        });
        smp.add(smpi);
        return smp;
    }
    
    /**
     * Shows a file dialog and exports the NeuralNet.
     */
    public void promptSaveNeuralNet() {
        toolDone();
        JFileChooser saveDialog;
        if (getDrawingTitle() == null || getDrawingTitle().equals("")) {
            saveDialog = createSaveFileChooser();
        } else {
            saveDialog = new JFileChooser(getDrawingTitle());
            saveDialog.setDialogTitle("Save File...");
        }
        
        getNetStorageFormatManager().registerFileFilters(saveDialog);
        if (saveDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            NetStorageFormat foundFormat = getNetStorageFormatManager().findStorageFormat(saveDialog.getFileFilter());
            if (foundFormat != null) {
                saveNeuralNet(foundFormat, saveDialog.getSelectedFile().getAbsolutePath());
                // Disable automatic Save, require SaveAs after this.
                latestStorageFormat = null;
//                ((JooneStandardDrawingView)view()).setModified(false);
            }
            else {
                showStatus("Not a valid file format: " + saveDialog.getFileFilter().getDescription());
            }
        }
    }
    /**
     * Shows a file dialog and saves the NeuralNet.
     */
    public void promptSaveAsXML() {
        toolDone();
        JFileChooser saveDialog;
        if (getDrawingTitle() == null || getDrawingTitle().equals("")) {
            saveDialog = createSaveFileChooser();
        } else {
            saveDialog = new JFileChooser(getDrawingTitle());
            saveDialog.setDialogTitle("Save File...");
        }
        
        getXMLStorageFormatManager().registerFileFilters(saveDialog);
        if (saveDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            NetStorageFormat foundFormat = getXMLStorageFormatManager().findStorageFormat(saveDialog.getFileFilter());
            if (foundFormat != null) {
                saveNeuralNet(foundFormat, saveDialog.getSelectedFile().getAbsolutePath());
                // Disable automatic Save, require SaveAs after this.
                latestStorageFormat = null;
//                ((JooneStandardDrawingView)view()).setModified(false);
            }
            else {
                showStatus("Not a valid file format: " + saveDialog.getFileFilter().getDescription());
            }
        }
    }
    /**
     * Save a NeuralNet in a file
     */
    protected void saveNeuralNet(NetStorageFormat storeFormat, String file) {
        try {
            NeuralNetDrawing nnd = (NeuralNetDrawing)drawing();
            NeuralNet nn = nnd.getNeuralNet();
            nn.getMonitor().setExporting(true);
//            setDrawingTitle(storeFormat.store(file, nn));
            storeFormat.store(file, nn);
            nn.getMonitor().setExporting(false);
        } catch (IOException e) {
            showStatus(e.toString());
        }
    }
    
    /**
     * The additional feature that is found in this
     * createOpenFileChooser is the ability to maintance
     * a list of history of directories that the user
     * has access previously, which is not found in the
     * original createOpenFileChooser and the ability
     * to preview the content of a text file.
     */
    protected JFileChooser createOpenFileChooser(String fn) {
        if (m_openDialog == null) {
            m_openDialog = new JooneFileChooser(fn);
        }
        return m_openDialog;
    }
    
    protected JFileChooser createOpenFileChooser() {
        if (m_openDialog == null) {
            m_openDialog = new JooneFileChooser();
        }
        return m_openDialog;
    }
    
    
    public StorageFormatManager createStorageFormatManager() {
        StorageFormatManager storageFormatManager = new StorageFormatManager();
        storageFormatManager.setDefaultStorageFormat(new SerializationStorageFormat());
        storageFormatManager.addStorageFormat(storageFormatManager.getDefaultStorageFormat());
        return storageFormatManager;
    }
    
    public NetStorageFormatManager createNetStorageFormatManager() {
        NetStorageFormatManager storageFormatManager = new NetStorageFormatManager();
        storageFormatManager.setDefaultStorageFormat(new StandardNetStorageFormat());
        storageFormatManager.addStorageFormat(storageFormatManager.getDefaultStorageFormat());
        return storageFormatManager;
    }
    public NetStorageFormatManager createXMLStorageFormatManager() {
        NetStorageFormatManager storageFormatManager = new NetStorageFormatManager();
        storageFormatManager.setDefaultStorageFormat(new XMLNetStorageFormat());
        storageFormatManager.addStorageFormat(storageFormatManager.getDefaultStorageFormat());
        return storageFormatManager;
    }
    /**
     * Set the NetStorageFormatManager for binary format
     * The NetStorageFormatManager is used when storing and
     * restoring NeuralNets from the file system.
     */
    private void setNetStorageFormatManager(NetStorageFormatManager storageFormatManager) {
        fNetStorageFormatManager = storageFormatManager;
    }
    
    /**
     * Return the NetStorageFormatManager for this application.
     * The NetStorageFormatManager is
     * used when storing and restoring NeuralNets from the file system.
     */
    public NetStorageFormatManager getNetStorageFormatManager() {
        return fNetStorageFormatManager;
    }
    
    /**
     * Set the NetStorageFormatManager for XML format
     * The NetStorageFormatManager is used when storing and
     * restoring NeuralNets from the file system.
     */
    private void setXMLStorageFormatManager(NetStorageFormatManager storageFormatManager) {
        xNetStorageFormatManager = storageFormatManager;
    }
    
    /**
     * Return the NetStorageFormatManager for this application.
     * The NetStorageFormatManager is
     * used when storing and restoring NeuralNets from the file system.
     */
    public NetStorageFormatManager getXMLStorageFormatManager() {
        return xNetStorageFormatManager;
    }
    
    protected Drawing createDrawing() {
        return new NeuralNetDrawing();
    }
    
    /**
     * Creates the drawing view used in this application.
     * You need to override this method to use a DrawingView
     * subclass in your application. By default a standard
     * DrawingView is returned.
     * @return the DrawingView
     */
    protected StandardDrawingView createDrawingView() {
        Dimension d = getDrawingViewSize();
        JooneStandardDrawingView newView = new JooneStandardDrawingView(this, d.width, d.height);
        newView.setBackground(Color.lightGray);
        return newView;
    }
    
    /**
     * Load a Drawing from a file
     */
    protected void loadDrawing(StorageFormat restoreFormat, String file) {
        try {
            Drawing restoredDrawing = restoreFormat.restore(file);
            if (restoredDrawing != null) {
                //newWindow();
                setDrawing(restoredDrawing);
                setDrawingTitle(file);
            } else {
                showStatus("Unknown file type: could not open file '" + file + "'");
            }
        } catch (IOException e) {
            showStatus("Error: " + e);
            log.error(e.getMessage(), e);
        }
    }
    
    /**
     * Start the application by creating an instance and open
     * the editor window.
     * @param args Command arguments
     */
    public static void main(String[] args) {
        if (args.length == 0)  {
            JoonEdit window = new JoonEdit();
            window.open();
        }
        else {
            JoonEdit window = new JoonEdit(args[0]);
            window.open();
        }
    }
    
    /**
     * Resets the NN to a new empty net.
     */
    public void promptNew() {
        super.promptNew();
        if (psp != null) {
            psp.setVisible(false);
            psp = null;
        }
        if (ps != null) {
            ps.setVisible(false);
            ps = null;
        }
        NeuralNetDrawing nnd = (NeuralNetDrawing)drawing();
        nnd.setNeuralNet(new NeuralNet());
        LayerFigure.setNumLayers(0);
        if (macroEditor != null) {
            macroEditor.setVisible(false);
            macroEditor = null;
        }
        ((JooneStandardDrawingView)view()).setModified(false);
    }
    
    /**
     * Opens the window and initializes its contents.
     */
    public void open() {
        // Splash Screen
        URL url = JoonEdit.class.getResource(JoonEdit.DIAGRAM_IMAGES + "splash.gif") ;
        final ImageIcon img = new ImageIcon(url);
        JWindow frame = new JWindow();
        JLabel info = new JLabel("Initializing....", SwingConstants.CENTER);
        info.setForeground(Color.black);
        
        try {
            //frame.setUndecorated(true); // JDK 1.3
            JLabel pan = new JLabel(img) {
                public void paint(Graphics g) {
                    super.paint(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setFont(new Font("Arial", Font.BOLD, 10));
                    g2.drawString("Neural Network Editor",15,145);
                    g2.drawString("v " + getVersion(), 45, 158);
                    g2.drawString(
                    "(c) "
                    + Calendar.getInstance().get(Calendar.YEAR)
                    + " Paolo Marrone",
                    10,
                    171);
                }
            };
            
            frame.getContentPane().add(pan, BorderLayout.CENTER);
            pan.setLayout(new BorderLayout());
            pan.add(info, BorderLayout.SOUTH);
            pan.setBorder(BorderFactory.createRaisedBevelBorder());
            info.setPreferredSize(new Dimension(pan.getWidth(), 24));
            frame.pack();
            center(frame);
            frame.setVisible(true);
            info.setText("Starting...");
            
            // Prepare and open the main window
            super.open();
            readParameters();
            Iconkit kit = Iconkit.instance();
            if (kit == null)
                throw new HJDError("Iconkit instance isn't set");
            final Image icn = kit.loadImageResource(JoonEdit.DIAGRAM_IMAGES + "JooneIcon.gif");
            this.setIconImage(icn);
            ((JooneStandardDrawingView)view()).setModified(false);
        } catch (Exception e) {
            info.setText(e.getMessage());
            e.printStackTrace();
            System.err.println(e.getMessage());
        } finally {
            frame.dispose();
        }
    }
    
    private void readParameters() {
        parameters = new EditorParameters();
        ToolElement te;
        Vector elements = tParser.getElements();
        for (int i=0; i < elements.size(); ++i) {
            te = (ToolElement)elements.elementAt(i);
            if (te.getType().compareToIgnoreCase("refreshing_rate") == 0)
                parameters.setRefreshingRate(Integer.parseInt((String)te.getParam("value")));
            if (te.getType().compareToIgnoreCase("http_proxy") == 0) {
                String hostname = (String)te.getParam("host");
                String port = (String)te.getParam("port");
                String userid = (String)te.getParam("userid");
                String passw = (String)te.getParam("passw");
                log.info("Using proxy: http://"+hostname+":"+port);
                System.setProperty("proxySet", "true" );
                System.setProperty("http.proxyHost", hostname );
                System.setProperty("http.proxyPort", port );
                Authenticator.setDefault( new httpAuthenticateProxy(userid, passw) );
            }
        }
    }
    
    /**
     * Method to get the version.
     */
    public static String getVersion() {
        return MAJOR_RELEASE +
        "." +
        MINOR_RELEASE +
        "." +
        BUILD + SUFFIX;
    }
    
    /**
     * Method to get the numeric version.
     */
    public static int getNumericVersion() {
        return MAJOR_RELEASE * 1000000 +
        MINOR_RELEASE * 1000 +
        BUILD;
    }
    
    /**
     * Method to get the recommended Engine version.
     */
    public static String getRecommendedEngineVersion() {
        return RECO_ENG_MAJOR_RELEASE +
        "." +
        RECO_ENG_MINOR_RELEASE +
        "." +
        RECO_ENG_BUILD;
    }
    
    /**
     * Method to get the numeric recommended Engine version.
     */
    public static int getNumericRecommendedEngineVersion() {
        return RECO_ENG_MAJOR_RELEASE * 1000000 +
        RECO_ENG_MINOR_RELEASE * 1000 +
        RECO_ENG_BUILD;
    }
    
    /**
     * Creates the attributes menu and its submenus. Clients override this
     * method to add additional menu items.
     */
    protected JMenu createAttributesMenu() {
        JMenu menu = new JMenu("Attributes");
        menu.setMnemonic(KeyEvent.VK_T);
        
        JMenuItem mi = createColorMenu("Fill Color", "FillColor");
        mi.setMnemonic(KeyEvent.VK_F);
        menu.add(mi);
        
        mi = createColorMenu("Pen Color", "FrameColor");
        mi.setMnemonic(KeyEvent.VK_P);
        menu.add(mi);
        
        mi = createArrowMenu();
        mi.setMnemonic(KeyEvent.VK_A);
        menu.add(mi);
        
        menu.addSeparator();
        
        mi = createFontMenu();
        mi.setMnemonic(KeyEvent.VK_O);
        menu.add(mi);
        
        mi = createFontSizeMenu();
        mi.setMnemonic(KeyEvent.VK_S);
        menu.add(mi);
        
        mi = createFontStyleMenu();
        mi.setMnemonic(KeyEvent.VK_N);
        menu.add(mi);
        
        mi = createColorMenu("Text Color", "TextColor");
        mi.setMnemonic(KeyEvent.VK_T);
        menu.add(mi);
        
        return menu;
    }
    
    /**
     * Create a menu which allows the user to select a different look and feel at runtime.
     */
    public JMenu createWindowMenu() {
        JMenu menu = new JMenu("Window");
        menu.setMnemonic(KeyEvent.VK_W);
        menu.add(createLookAndFeel());
        return menu;
    }
    
    public JMenu createLookAndFeel() {
        
        UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
        JMenu menu = new JMenu("Look and Feel");
        menu.setMnemonic(KeyEvent.VK_L);
        JMenuItem mi = null;
        
        for (int i = 0; i < lafs.length; i++) {
            mi = new JMenuItem(lafs[i].getName());
            final String lnfClassName = lafs[i].getClassName();
            mi.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    newLookAndFeel(lnfClassName);
                }
            }
            );
            menu.add(mi);
        }
        return menu;
    }
    
    /**
     * Switch to a new Look&Feel
     */
    private void newLookAndFeel(String landf) {
        try {
            UIManager.setLookAndFeel(landf);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            log.warn( "Exception thrown while adding a lookAndFeel. Message is : " + e.getMessage(),
            e);
        }
    }
    
    /**
     * Creates the edit menu. Clients override this
     * method to add additional menu items.
     */
    protected JMenu createEditMenu() {
        JooneCommandMenu menu = new JooneCommandMenu("Edit");
        menu.setMnemonic(KeyEvent.VK_E);
        menu.add(new JooneCutCommand("Cut", view()), new MenuShortcut('t'), KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        menu.add(new JooneCopyCommand("Copy", view()), new MenuShortcut('c'), KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        menu.add(new PasteCommand("Paste", view()), new MenuShortcut('p'), KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        menu.addSeparator();
        menu.add(new JooneDuplicateCommand("Duplicate", view()), new MenuShortcut('d'));
        menu.add(new DeleteCommand("Delete", view()), new MenuShortcut('e'), KeyStroke.getKeyStroke("DELETE"));
        menu.addSeparator();
        menu.add(new GroupCommand("Group", view()), new MenuShortcut('g'));
        menu.add(new UngroupCommand("Ungroup", view()), new MenuShortcut('u'));
        menu.addSeparator();
        menu.add(new SendToBackCommand("Send to Back", view()), new MenuShortcut('s'));
        menu.add(new BringToFrontCommand("Bring to Front", view()), new MenuShortcut('b'));
        return menu;
    }
    
    /**
     * Shows a file dialog and saves drawing.
     */
    public void promptSaveAs() {
        toolDone();
        JFileChooser saveDialog;
        if (getDrawingTitle() == null || getDrawingTitle().equals("")) {
            saveDialog = createSaveFileChooser();
        } else {
            saveDialog = new JFileChooser(getDrawingTitle());
            saveDialog.setDialogTitle("Save File...");
        }
        
        getStorageFormatManager().registerFileFilters(saveDialog);
        
        if (saveDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            StorageFormat foundFormat = getStorageFormatManager().findStorageFormat(saveDialog.getFileFilter());
            if (foundFormat != null) {
                saveDrawing(foundFormat, saveDialog.getSelectedFile().getAbsolutePath());
                // Remember format for Save and enable the save menu item.
                latestStorageFormat = foundFormat;
            } else {
                showStatus("Not a valid file format: " + saveDialog.getFileFilter().getDescription());
            }
        }
    }
    /**
     * Shows a file dialog and opens a drawing.
     */
    public void promptOpen() {
        toolDone();
        if (getDrawingTitle() == null || getDrawingTitle().equals("")) {
            createOpenFileChooser();
        } else {
            createOpenFileChooser(getDrawingTitle());
            m_openDialog.setDialogTitle("Open File...");
        }
        
        getStorageFormatManager().registerFileFilters(m_openDialog);
        if (m_openDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            StorageFormat foundFormat = getStorageFormatManager().findStorageFormat(m_openDialog.getFileFilter());
            if (foundFormat != null) {
                promptNew();
                loadDrawing(foundFormat, m_openDialog.getSelectedFile().getAbsolutePath());
                // Remember format for Save.
                latestStorageFormat = foundFormat;
            }
            else {
                showStatus("Not a valid file format: " + m_openDialog.getFileFilter().getDescription());
            }
        }
    }
    
    protected void initDrawing() {
        super.initDrawing();
        latestStorageFormat = null;
    }
    
    /**
     * Exits the application. You should never override this method
     */
    public void exit() {
        int n = askForSave("Save changes to Neural Net?");
        if ((n == JOptionPane.NO_OPTION) || !((JooneStandardDrawingView)view()).isModified()) {
            writeIniFile();
            destroy();
            setVisible(false);
            dispose();
            log.info("Exit invoked successfully. Frame disposed. Bye");
            System.exit(0);
        }
    }
    
    protected void destroy() {
        if (m_openDialog != null)
            m_openDialog.saveDirectoryEntries();
    }
    /**
     * Save a Drawing in a file
     */
    protected void saveDrawing(StorageFormat storeFormat, String file) {
        try {
            setDrawingTitle(storeFormat.store(file, drawing()));
            ((JooneStandardDrawingView)view()).setModified(false);
        }
        catch (IOException e) {
            showStatus(e.toString());
        }
    }
    
    protected int askForSave(String message) {
        int n = JOptionPane.CANCEL_OPTION;
        if (((JooneStandardDrawingView)view()).isModified()) {
            n = JOptionPane.showConfirmDialog(this,
            message, "JoonEdit", JOptionPane.YES_NO_CANCEL_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                if (latestStorageFormat != null &&
                getDrawingTitle() != null &&
                !getDrawingTitle().equals("")) {
                    saveDrawing(latestStorageFormat, getDrawingTitle());
                }
                else {
                    promptSaveAs();
                }
            }
        }
        return n;
    }
    
    private void readIniFile() {
        try {
            // Create .joone dir if not already in existance.
            File jooneHome = new File(System.getProperty("user.home") + File.separator + ".joone");
            if (!jooneHome.exists()) {
                jooneHome.mkdir();
            }
            
            // Get the ini file.
            iniFile = new IniFile(jooneHome + File.separator + INI_FILE_NAME);
            
            // Get JooneEdit bounds.
            int state = Integer.parseInt(iniFile.getParameter("gui", "state", "0"));
            int x = Integer.parseInt(iniFile.getParameter("gui", "x", "0"));
            int y = Integer.parseInt(iniFile.getParameter("gui", "y", "0"));
            
            // See defaultSize() for width and height.
            width = Integer.parseInt(iniFile.getParameter("gui", "width", "800"));
            height = Integer.parseInt(iniFile.getParameter("gui", "height", "600"));
            setLocation(x, y);
            
            // Set state, i.e. maximized, normal, iconified.
            if (state != -99) {
                setState(state);
            }
            
            // Set look and feel.
            String lafName = iniFile.getParameter("gui", "laf", null);
            if (lafName != null) {
                UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
                for (int i = 0; i < lafs.length; i++) {
                    if (lafName.toLowerCase().equals(lafs[i].getClassName().toLowerCase())) {
                        UIManager.setLookAndFeel(lafs[i].getClassName());
                        SwingUtilities.updateComponentTreeUI(this);
                    }
                }
            }
        } catch (Exception e) {
            log.warn( "Exception thrown reading writing config file. Message is : " + e.getMessage(),
            e);
        }
    }
    
    private void writeIniFile() {
        try {
            // Only write frame boundary if it is in the normal state.
            if (getState() == Frame.NORMAL) {
                iniFile.setParameter("gui", "x", String.valueOf(getX()));
                iniFile.setParameter("gui", "y", String.valueOf(getY()));
                iniFile.setParameter("gui", "width", String.valueOf(getWidth()));
                iniFile.setParameter("gui", "height", String.valueOf(getHeight()));
            }
            iniFile.setParameter("gui", "state", String.valueOf(getState()));
            iniFile.setParameter("gui", "laf", UIManager.getLookAndFeel().getClass().getName());
        } catch (Exception e) {
            log.warn( "Exception thrown while writing config file. Message is : " + e.getMessage(),
            e);
        }
    }
    
    private void pageSetup() {
        // Get a PrinterJob
        PrinterJob job = PrinterJob.getPrinterJob();
        
        // Ask user for page format (e.g., portrait/landscape)
        job.pageDialog(job.defaultPage());
    }
}
