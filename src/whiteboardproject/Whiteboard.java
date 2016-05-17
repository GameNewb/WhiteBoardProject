package whiteboardproject;

/**
 *
 * @author Kiyeon
 * Kyle Del Castillo
 * Student #009445384
 * CS 151 - Object Oriented Design
 * Prof. Vidya Rangasayee
 * 
 */

import java.applet.Applet;
import java.applet.AudioClip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Whiteboard extends JFrame 
{
    private JPanel controlBox;
    
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu backgroundMenu;
    private JMenu randomizedMenu;
    private JMenu aboutMenu;
    private JMenu musicMenu;
    
    private JLabel whiteboardLabel;
    private JLabel addLabel;
    private JLabel modeLabel;
   
    private JButton rectButton;
    private JButton ovalButton; 
    private JButton lineButton; 
    private JButton textButton;
    
    private JButton removeButton;
    private JButton backButton;
    private JButton frontButton;
   
    private JButton setButton;
    private JButton saveButton;
    private JButton openButton;
    private JButton clientButton;
    private JButton serverButton;
    
    private JFileChooser fileChooser;
   
    private HashMap<String, Integer> fontMap;
    private JComboBox fontSelector;
    private JTextField textField;
    
    private JTable table;
    private TableModel tableModel;
    
    private ArrayList<JComponent> allComponents;
    
    private Server serverAccepter;
    private Client clientHandler;
    private ArrayList<ObjectOutputStream> outputList = new ArrayList<>();
    private String serverMode = "Server Mode";
    private String clientMode = "Client Mode";
    private String basicMode = "Basic Mode";
    private String currentMode;
    private static int nextID = 0;
    private static int userPort;
    
    private Random rand = new Random();
    private Color bgCustomColor = Color.decode("#04839a");

    private Canvas canvas;
    
    //Music and sound effects playing
    private AudioClip click;
    private AudioClip hover;
    private File music;
    private Clip musicClip;
    private JMenuItem lastPressed;
    
    public Whiteboard() 
    {
        this.setTitle("Whiteboard");
        setLayout(new BorderLayout());
        
        
        //Box component where all controls/buttons are
        controlBox = new JPanel();
        controlBox.setBackground(bgCustomColor);
        controlBox.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 2), "CS 151"));
        controlBox.setLayout(new GridLayout(6,0));
        
        allComponents = new ArrayList<>();
        
        createControlPanelSound();
        createMenuBar();
        createWhiteboardLabelBox();
        createButtonBox();
        createFontBox();
        createOperationButtonBox();
        createOperationButton();
        createTable();
        
        //Add control box to whiteboard
        add(controlBox, BorderLayout.WEST);
        
        //Set new canvas and add to whiteboard
        canvas = new Canvas(this);
        canvas.setBorder(new LineBorder(Color.black,2));
        add(canvas, BorderLayout.CENTER);
        
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        
    } //End Whiteboard Constructor
    
    
    //Add shape to the Canvas
    private void addShape(DShapeModel model) 
    {   
        if(model instanceof DLineModel)
        {
            //Cast as a DLine
            ((DLineModel) model).modifyWithPoints(new Point(Canvas.INITIAL_POSITION, Canvas.INITIAL_POSITION)
                    , new Point(Canvas.INITIAL_POSITION + Canvas.INITIAL_SIZE, Canvas.INITIAL_POSITION + Canvas.INITIAL_SIZE));
        }
        else
        {
            //Changed to an initial final size and position based on requirement
            model.setBounds(Canvas.INITIAL_POSITION, Canvas.INITIAL_POSITION, Canvas.INITIAL_SIZE, Canvas.INITIAL_SIZE);
        }
        
        canvas.addShape(model);
    } //End addShape
    
    private void addCopiedShape(DShapeModel model)
    {
        canvas.addShape(model);
    }
    
    private void addRandomizedShape(DShapeModel model)
    {
        if(model instanceof DLineModel)
        {
            //Cast as a DLine
            ((DLineModel) model).modifyWithPoints(new Point(canvas.getRandomPosition(), canvas.getRandomPosition())
                    , new Point(canvas.getRandomPosition() + canvas.getRandomSize(), canvas.getRandomPosition() + canvas.getRandomSize()));
        }
        else
        {
            //Changed to an initial final size and position based on requirement
            model.setBounds(canvas.getRandomPosition(), canvas.getRandomPosition(), canvas.getRandomSize(), canvas.getRandomSize());
        }
        
        canvas.addShape(model);
    } //End addRandomizedShape
    
    
    /**** Control Panel Buttons ****/
    
    private void createControlPanelSound()
    {
        //Create a clicking sound for the whiteboard buttons
        //When a button is click, play a mouse click sound
        URL urlClick = Whiteboard.class.getResource("soundsrc/mc.wav");
        URL urlHover = Whiteboard.class.getResource("soundsrc/menusound.wav");
        click = Applet.newAudioClip(urlClick);
        hover = Applet.newAudioClip(urlHover);
        
        
    } //End createControlPanelSound
    
    private void createMenuBar()
    {
        menuBar = new JMenuBar();
        
        fileMenu = new JMenu("File");
        backgroundMenu = new JMenu("Background");
        randomizedMenu = new JMenu("Random Shape Generator");
        musicMenu = new JMenu("Music");
        aboutMenu = new JMenu("About");
        
        JMenuItem save = new JMenuItem("Save Canvas");
        save.addMouseListener(new Hover());
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                saveCanvas();
            }
        });
        
        JMenuItem open = new JMenuItem("Open Existing Canvas");
        open.addMouseListener(new Hover());
        open.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                openCanvas();
            }
        });
        
        JMenuItem clear = new JMenuItem("Clear Canvas");
        clear.addMouseListener(new Hover());
        clear.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                canvas.clear();
            }
        });
        
        JMenuItem image = new JMenuItem("Save to PNG");
        image.addMouseListener(new Hover());
        image.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                saveCanvasImage();
            }
        });
        
        JMenuItem createWindow = new JMenuItem("Open New Window");
        createWindow.addMouseListener(new Hover());
        createWindow.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                createNewWindow();
            }
        });
        
        createControlBackgroundMenu();
        createRandomizedMenu();
        createAboutMenu();
        createMusicMenu();
        
        fileMenu.add(save);
        fileMenu.add(open);
        fileMenu.add(clear);
        fileMenu.add(image);
        fileMenu.addSeparator();
        fileMenu.add(createWindow);
        menuBar.add(fileMenu);
        menuBar.add(backgroundMenu);
        menuBar.add(randomizedMenu);
        menuBar.add(musicMenu);
        menuBar.add(aboutMenu);
        
        allComponents.add(createWindow);
        allComponents.add(open);
        allComponents.add(clear);
        
        setJMenuBar(menuBar);
    } //End createMenuBar
    
    private void createWhiteboardLabelBox()
    {
        Box panel = Box.createVerticalBox();
        panel.setBorder(new LineBorder(Color.black,2));
        panel.setPreferredSize(new Dimension(100,100));
        
        whiteboardLabel = new JLabel("<html><center>Whiteboard</center>Control Panel</html>", SwingConstants.CENTER);
        whiteboardLabel.setFont(new Font("Serif", Font.BOLD, 26));
        whiteboardLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        modeLabel = new JLabel(basicMode);
        currentMode = basicMode;
        modeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        modeLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        panel.add(whiteboardLabel);
        panel.add(modeLabel);
        
        controlBox.add(panel);
        
    } //End createWhiteboardLabelBox
    
    //Creates a box where the add shapes buttons are
    private void createButtonBox() 
    {
        //Create box for shape panel
        Box panel = Box.createHorizontalBox();
        
        panel.setBorder(new LineBorder(Color.BLACK, 1));
        
        addLabel = new JLabel("Add Shapes: ");
        
        //Rectangle button
        rectButton = new JButton("Rect");
        rectButton.addMouseListener(new Clicker());
        rectButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               addShape(new DRectModel());
           }
        });
        
        //Oval button
        ovalButton = new JButton("Oval");
        ovalButton.addMouseListener(new Clicker());
        ovalButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addShape(new DOvalModel());
            }
         });
        
        //Line button
        lineButton = new JButton("Line");
        lineButton.addMouseListener(new Clicker());
        lineButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addShape(new DLineModel());
            }
        });
        
        //Text button
        textButton = new JButton("Text");
        textButton.addMouseListener(new Clicker());
        textButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addShape(new DTextModel());
            }
        });
        
        panel.add(addLabel);
        panel.add(rectButton);
        panel.add(ovalButton);
        panel.add(lineButton);
        panel.add(textButton);
        
        allComponents.add(addLabel);
        allComponents.add(rectButton);
        allComponents.add(ovalButton);
        allComponents.add(lineButton);
        allComponents.add(textButton);
        
        allComponents.add(addLabel);
        
        fileChooser = new JFileChooser();
        
        //Add button box panel to overall control box
        controlBox.add(panel, BorderLayout.WEST); 
        
    } //End createButtonBox
    
    private void createOperationButton()
    {
    	Box panel = Box.createHorizontalBox();
    	
        panel.setBorder(new LineBorder(Color.BLACK, 1));
    	this.setButton = new JButton("Set Color");
    	this.setButton.addMouseListener(new Clicker());
    	this.setButton.addActionListener(new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = null;
                color = JColorChooser.showDialog(null, "Choose Color", color);


                canvas.setColor(color);
            }
    	});
    	
    	this.saveButton = new JButton("Save");
        this.saveButton.addMouseListener(new Clicker());
    	this.saveButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {

                saveCanvas();
            }
    	});
    	
    	this.openButton = new JButton("Open");
    	this.openButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                
                openCanvas();
            }
    	});
           
        serverButton = new JButton("Server");
        serverButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                //Prevents the user from initiating a new server
                if(Whiteboard.this.runningAsServer())
                {
                    JOptionPane.showMessageDialog(Whiteboard.this, "Current window is already running as a Server.", "Server Running", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    serverSide();
                }
            }
        });
        
        clientButton = new JButton("Client");
        clientButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                //Double checks to see if current window isn't in server mode already
                //This prevents the window from going to Server to Client
                if(Whiteboard.this.runningAsServer())
                {
                    JOptionPane.showMessageDialog(Whiteboard.this, "Current window is already running as a Server.", "Server Running", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    clientSide();
                }
            }
        });
    	
    	panel.add(setButton);
    	panel.add(openButton);
    	panel.add(saveButton);
        panel.add(serverButton);
        panel.add(clientButton);
        
        
        allComponents.add(setButton);
        allComponents.add(openButton);
        allComponents.add(saveButton);
        allComponents.add(serverButton);
        allComponents.add(clientButton);
    	
        for(Component comp : panel.getComponents())
            ((JComponent) comp).setAlignmentX(Box.LEFT_ALIGNMENT);
        
    	controlBox.add(panel, BorderLayout.WEST);
    } //End createOperationButton
    
    private void createOperationButtonBox()
    {
    	Box panel = Box.createHorizontalBox();
    	
        panel.setBorder(new LineBorder(Color.BLACK, 1));
        this.removeButton = new JButton("Remove Shape");
        this.removeButton.addMouseListener(new Clicker());
        this.removeButton.addActionListener(new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                if(canvas.getSelected() != null)
                {
                    canvas.selectShapeForRemoval();
                }

            }
        	
        });
        for(Component comp : panel.getComponents())
            ((JComponent) comp).setAlignmentX(Box.LEFT_ALIGNMENT);
        
        this.frontButton = new JButton("Move Front");
        this.frontButton.addMouseListener(new Clicker());
        this.frontButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                
                if(canvas.getSelected() != null)
                {
                    canvas.moveFront();
                }

            }
        	
        });
        
        
        this.backButton = new JButton("Move Back");
        this.backButton.addMouseListener(new Clicker());
        this.backButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                
                if(canvas.getSelected() != null)
                {
                    canvas.moveBack();
                }

            }
        });
       
        panel.add(frontButton);
        panel.add(backButton);
        panel.add(removeButton);
        
        allComponents.add(frontButton);
        allComponents.add(backButton);
        allComponents.add(removeButton);
        
        for(Component comp : panel.getComponents())
            ((JComponent) comp).setAlignmentX(Box.LEFT_ALIGNMENT);
        
        controlBox.add(panel, BorderLayout.WEST);
        
    } //End createOperationButtonBox
    
    private void createFontBox()
    {
        Box panel = Box.createHorizontalBox();
        
        panel.setBorder(new LineBorder(Color.BLACK, 1));
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        
        //Obtain directory fonts
        String fontStyles[] = ge.getAvailableFontFamilyNames();
        fontMap = new HashMap<String, Integer>();
        
        //Add every available font to the font hashmap
        for(int i = 0; i < fontStyles.length; i++)
        {
            fontMap.put(fontStyles[i], i);
        }
        
        fontSelector = new JComboBox(fontStyles);
        fontSelector.setMaximumSize(new Dimension(200,25));
        fontSelector.setPreferredSize(new Dimension(200,25));
        fontSelector.setEnabled(false);
        fontSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                if(canvas.getSelected() != null && canvas.getSelected() instanceof DText)
                {
                    canvas.setFont((String) fontSelector.getSelectedItem());
                }
            }
        });
        
        textField = new JTextField("");
        textField.setMaximumSize(new Dimension(200,25));
        textField.setPreferredSize(new Dimension(200,25));
        textField.setEnabled(false);
        textField.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e)
            {
                //If a text shape is selected, set the text
                if(canvas.getSelected() != null && canvas.getSelected() instanceof DText)
                {
                    canvas.setText(textField.getText());
                }
                else if(canvas.getSelected() == null || !(canvas.getSelected() instanceof DText) )
                {
                    textField.setEnabled(false);
                }
            }
            
            public void removeUpdate(DocumentEvent e)
            {
                if(canvas.getSelected() != null && canvas.getSelected() instanceof DText)
                {
                    canvas.setText(textField.getText());
                }
            }
            
            public void changedUpdate(DocumentEvent e)
            { 
            }
        });
        
        panel.add(fontSelector);
        panel.add(textField);
        
        allComponents.add(fontSelector);
        allComponents.add(textField);
        
        controlBox.add(panel, BorderLayout.WEST);
        
    } //End createFontBox
    
    private void createTable() 
    {
        String s[] = {"X","Y","Width","Height"};
        tableModel = new TableModel(s);
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollpane = new JScrollPane(table);
        scrollpane.setPreferredSize(new Dimension(100, 100));
        controlBox.add(scrollpane, BorderLayout.WEST);
    } //End createTable
    
    private void createControlBackgroundMenu()
    {
        
        JMenuItem bgGreen = new JMenuItem("Green");
        bgGreen.addMouseListener(new Hover());
        bgGreen.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                Color green = Color.decode("#017b1b");
                controlBox.setBackground(green);
            }
        });
        
        JMenuItem bgRed = new JMenuItem("Red");
        bgRed.addMouseListener(new Hover());
        bgRed.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                Color red = Color.decode("#980606");
                controlBox.setBackground(red);
            }
        });
        
        JMenuItem bgBlue = new JMenuItem("Blue");
        bgBlue.addMouseListener(new Hover());
        bgBlue.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                Color blue = Color.decode("#0a1bbf");
                controlBox.setBackground(blue);
            }
        });
        
        JMenuItem bgWhite = new JMenuItem("White");
        bgWhite.addMouseListener(new Hover());
        bgWhite.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                controlBox.setBackground(new Color(238,238,238));
            }
        });
        
        JMenuItem bgDefault = new JMenuItem("Default");
        bgDefault.addMouseListener(new Hover());
        bgDefault.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                controlBox.setBackground(bgCustomColor);
            }
        });
        
        
        backgroundMenu.add(bgRed);
        backgroundMenu.add(bgGreen);
        backgroundMenu.add(bgBlue);
        backgroundMenu.add(bgWhite);
        backgroundMenu.addSeparator();
        backgroundMenu.add(bgDefault);
        
    } //End createControlBackgroundMenu
    
    private void createRandomizedMenu()
    {
       JMenuItem randomizeShape = new JMenuItem("Create Random Shapes!");
       randomizeShape.addMouseListener(new Hover());
       randomizeShape.addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent e)
           {
               generateRandomShapes();
           }
       });
       
       JMenuItem randomizeColors = new JMenuItem("Create Colorful Shapes!");
       randomizeColors.addMouseListener(new Hover());
       randomizeColors.addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent e)
           {
               generateRandomColors();
           }
       });
       
       randomizedMenu.add(randomizeShape);
       randomizedMenu.add(randomizeColors);
       
       allComponents.add(randomizeShape);
       allComponents.add(randomizeColors);
    } //End createRandomizedMenu
    
    private void createAboutMenu()
    {
        JMenuItem about = new JMenuItem("About Us");
        about.addMouseListener(new Hover());
        about.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                String cs = "<h2>CS 151: Object Oriented Design</h2>";
                String prof = "<h3>Prof. Vidya Rangasayee</h3>";
                String s = "<html><body width ='200'>" + cs 
                        + prof
                        + "<h4>Created By: </h4>"
                        + "<p> Kyle Del Castillo - SID #009445384<br>" 
                        + "Huy Vo - SID #010106096 <br><br>"
                        + "";
                
                JOptionPane.showMessageDialog(null, s, "About Us", JOptionPane.INFORMATION_MESSAGE);
                
            }
        });
        
        aboutMenu.add(about);
    } //End createAboutMenu
    
    private void createMusicMenu()
    {
        JMenuItem spiritedAway = new JMenuItem("Play Spirited Away Soundtrack");
        spiritedAway.addMouseListener(new Hover());
        spiritedAway.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                if(spiritedAway == lastPressed)
                {
                    stopMusic();
                    musicPlayer();
                }
                else
                {
                    musicPlayer();
                }
                
                lastPressed = (JMenuItem) e.getSource();
            }
        });
        
        JMenuItem stopMusic = new JMenuItem("Stop music");
        stopMusic.addMouseListener(new Hover());
        stopMusic.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                stopMusic();
            }
        });
        
        musicMenu.add(spiritedAway);
        musicMenu.addSeparator();
        musicMenu.add(stopMusic);
    } //End createMusicMenu
    
    //Canvas utilizes updateFont to set the appropriate text 
    public void updateFont(String text, String font)
    {
        int index = fontMap.get(font);
        fontSelector.setSelectedIndex(index);
        textField.setText(text);
    } //End updateFont
    
    //Set alignment to the left
    private void alignLeft() 
    {
        for(Component comp : controlBox.getComponents())
            ((JComponent) comp).setAlignmentX(Box.LEFT_ALIGNMENT);
    } //End alignLeft
    
    
    /*** Table Contents ***/
    
    public void addShapeToTable(DShape shape)
    {
        tableModel.addModel(shape.getModel());
        updateTableSelection(shape);
    } //End addShapeToTable
    
    public void removeShapeFromTable(DShape shape)
    {
        tableModel.removeModel(shape.getModel());
        updateTableSelection(null);
    } //End removeShapeFromTable
    
    public void clearTable()
    {
        updateTableSelection(null);
        tableModel.clear();
    } //End clearTable
    
    public void moveBack(DShape shape)
    {
        tableModel.moveBack(shape.getModel());
        updateTableSelection(shape);
    } //End moveBack
    
    public void moveFront(DShape shape)
    {
        tableModel.moveFront(shape.getModel());
        updateTableSelection(shape);
    } //End moveFront
    
    
    public void updateTableSelection(DShape selected) 
    {
        table.clearSelection();
        if(selected != null) {
            int index = tableModel.getRowPerModel(selected.getModel());
            table.setRowSelectionInterval(index, index);
        }
    } //End updateTableSelection
    
    
    private void saveCanvas()
    {
        int value = fileChooser.showSaveDialog(this);
        
        if(value == JFileChooser.APPROVE_OPTION)
        {
            canvas.save(fileChooser.getSelectedFile()); //Get current file
        }
    } //End saveCanvas
    
    private void openCanvas() 
    {
        int value = fileChooser.showOpenDialog(this);
        
        if(value == JFileChooser.APPROVE_OPTION) 
        {
            canvas.open(fileChooser.getSelectedFile());
        }
    } //End openCanvas
    
    private void saveCanvasImage()
    {
        int value = fileChooser.showSaveDialog(this);
        
        if(value == JFileChooser.APPROVE_OPTION)
        {
            canvas.saveImageFile(fileChooser.getSelectedFile());
        }
    }
    
    public void serverSide()
    {
        String currentPort = JOptionPane.showInputDialog("Server Port: ", "39587");
        
        if(currentPort != null)
        {
            userPort = Integer.parseInt(currentPort); //User specified port
            Color color = Color.decode("#7c0320");
            
            currentMode = serverMode;
            modeLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
            modeLabel.setText(serverMode);
            modeLabel.setForeground(color);
            controlBox.setBackground(new Color(238,238,238));
            
            serverAccepter = new Server(this, canvas, userPort);
            serverAccepter.start();
        }
        
    } //End serverSide
    
    public void clientSide()
    {
        String connectingPort = JOptionPane.showInputDialog("Connect to server host:port: ", "127.0.0.1:" + userPort);
        
        if(connectingPort != null)
        {
            String[] portNumber = connectingPort.split(":");
            
            //Disable controls for Client side
            for(JComponent components: allComponents)
            {
                components.setEnabled(false);
            }
            
            currentMode = clientMode;
            modeLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
            modeLabel.setText(clientMode);
            modeLabel.setForeground(Color.decode("#7f6000"));
            
            clientHandler = new Client(this, canvas, portNumber[0].trim(), Integer.parseInt(portNumber[1]));
            clientHandler.start();
        }
    } //End clientSide
    
    public boolean runningAsServer()
    {
        return currentMode.equals(serverMode);
    } //End runningAsServer
    
    public boolean runningAsClient()
    {
        return currentMode.equals(clientMode);
    } //End runningAsClinet
    
    public JTextField getTextField()
    {
        return textField;
    }
    
    public JComboBox getFontSelector()
    {
        return fontSelector;
    }
    
    public ArrayList<ObjectOutputStream> getOutputs()
    {
        return outputList;
    }
    
    public static int getNextID()
    {
        return nextID++;
    }

    public void generateRandomShapes()
    {
        int nShapes = 0;
        
        try
        {
            nShapes = Integer.parseInt(JOptionPane.showInputDialog(this, "How many shapes to create?", "Shape Generator", JOptionPane.INFORMATION_MESSAGE));
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(this, "Invalid input.", "Invalid", JOptionPane.ERROR_MESSAGE);
        }
        
        if(nShapes > 100)
        {
            JOptionPane.showMessageDialog(this, "Too Many Shapes!!!!! Must be < 100.", "Shape Overload", JOptionPane.WARNING_MESSAGE);
        }
        else if(nShapes == 0)
        {
            JOptionPane.showMessageDialog(this, "Please enter a number.", "No number input", JOptionPane.INFORMATION_MESSAGE);
        }
        else
        {
            for(int i = 0; i < nShapes; i++)
            {
                int parse = rand.nextInt(4)+1;
                switch(parse)
                {
                    case 1: addRandomizedShape(new DRectModel());
                        break;
                    case 2: addRandomizedShape(new DOvalModel());
                        break;
                    case 3: addRandomizedShape(new DLineModel());
                        break;
                    case 4: addRandomizedShape(new DTextModel());
                        break;
                }
            }
        }
       
    } //End generateRandomShapes
    
    public Server getServer()
    {
        return serverAccepter;
    }
    
    public Client getClient()
    {
        return clientHandler;
    }
    
    public void generateRandomColors()
    {
        int nShapes = 0;
        
        try
        {
            nShapes = Integer.parseInt(JOptionPane.showInputDialog(this, "How many shapes to create?", "Shape Generator", JOptionPane.INFORMATION_MESSAGE));
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(this, "Invalid input.", "Invalid", JOptionPane.ERROR_MESSAGE);
        }
        
        if(nShapes > 100)
        {
            JOptionPane.showMessageDialog(this, "Too Many Shapes!!!!! Must be < 100.", "Shape Overload", JOptionPane.WARNING_MESSAGE);
        }
        else if(nShapes == 0)
        {
            JOptionPane.showMessageDialog(this, "Please enter a number.", "No number input", JOptionPane.INFORMATION_MESSAGE);
        }
        else
        {
            for(int i = 0; i < nShapes; i++)
            {
                int parse = rand.nextInt(4)+1;
                float r = rand.nextFloat();
                float g = rand.nextFloat();
                float b = rand.nextFloat();

                Color randomColor = new Color(r,g,b);
                canvas.setRandomizedColor(randomColor);
                switch(parse)
                {
                    case 1: 
                        addRandomizedShape(new DRectModel());
                        break;
                    case 2: 
                        addRandomizedShape(new DOvalModel());
                        break;
                    case 3:
                        addRandomizedShape(new DLineModel());
                        break;
                    case 4: 
                        addRandomizedShape(new DTextModel());
                        break;
                }
            }
        }
       
    } //End generateRandomShapes
    
    private void createNewWindow()
    {
        Whiteboard whiteboard = new Whiteboard();
    }
    
    public static void main(String[] args) 
    {
        Whiteboard whiteboard = new Whiteboard();
    } //End main
    
    
    /*
        NOT PART OF REQUIREMENT 
    */
    private void musicPlayer()
    {
        try{
            music = new File("src\\whiteboardproject\\soundsrc\\spiritedaway.wav");
            
            musicClip = AudioSystem.getClip();
            musicClip.open(AudioSystem.getAudioInputStream(music));
            musicClip.start();
        }
        catch(Exception e){
            System.out.println("No file");
            System.out.println(music.getAbsolutePath());
            System.out.println(e.getMessage());
        }
    }
    
    private void stopMusic()
    {
        try{
            
            musicClip.stop();
        }
        catch(Exception e){}
    }
    
    private class Clicker extends MouseAdapter{
        public void mouseClicked(MouseEvent e)
        {
            click.play();
        }
    }
    
    private class Hover extends MouseAdapter{
        public void mouseEntered(MouseEvent e)
        {
            hover.play();
        }
        
    }
    
} //End Whiteboard class