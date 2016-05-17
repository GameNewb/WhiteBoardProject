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

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import java.awt.Graphics2D;

public class Canvas extends JPanel implements ModelListener
{
    private JPopupMenu popupMenu;
    private JMenuItem addBorder;
    private JMenuItem copyShape;
    private JMenuItem pasteShape;
    private JMenuItem shapeRemoval;
    
    public static final int SIZE = 400;
    public static final int INITIAL_POSITION = 10;
    public static final int INITIAL_SIZE = 20;
    
    private static final Random rand = new Random();
    
    private static int randomSize;
    private static int randomPosition;
    
    private ArrayList<DShape> shapes;
    private ArrayList<DShape> shapesWithBorders;
    private ArrayList<DShapeModel> modelShapes;
    
    private DShape selected;
    private DShape copiedShape;
    private DShapeModel copiedModel;
    
    private Point movingPoint;
    private Point anchorPoint;
    
    private int lastX, lastY;
    
    private Whiteboard whiteboard;
    
    private FileWriterAndSaver fws;
    
    private boolean hasPasted = false;
            
    public Canvas(Whiteboard board) 
    {
        setMinimumSize(new Dimension(SIZE, SIZE));
        setPreferredSize(getMinimumSize());
        setBackground(Color.white);
        
        generatePopupMenu();
        
        whiteboard = board;
        
        shapes = new ArrayList<DShape>();
        modelShapes = new ArrayList<DShapeModel>();
        shapesWithBorders = new ArrayList<DShape>();
        
        fws = new FileWriterAndSaver(modelShapes, this);
        
        selected = null;
        movingPoint = null;

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                
                //If whiteboard is running as a server, enable object selection
                if(!whiteboard.runningAsClient())
                {
                    selectObject(e.getPoint());
                    
                    //Enables or disables the font text field and font selector
                    notifyTextEnabling();
                }
                else
                {
                    //Disables buttons for clients
                    notifyTextEnabling();
                }
               
            }
            
            
            /*
             *  THIS IS ADDED FUNCTIONALITY - NOT PART OF THE REQUIREMENT
             *  Add border will only work on server/basic side
             *  And will only work for DRect and DOval
             */
            public void mouseReleased(MouseEvent e)
            {
                rightClickFunctionality(e.getX(), e.getY());
                popupMenu.removeAll();
                    
                popupMenu.add(addBorder);
                popupMenu.add(copyShape);
                popupMenu.add(pasteShape);
                popupMenu.add(shapeRemoval);
                
                //If clicked on a DRect or DOval shape
                if(selected != null && e.getButton() == MouseEvent.BUTTON3)
                {
                    if(selected instanceof DRect || selected instanceof DOval)
                    {
                        addBorder.setEnabled(true);
                        copyShape.setEnabled(true);
                    }
                    shapeRemoval.setEnabled(true);
                    popupMenu.show(Canvas.this, e.getX(), e.getY());   
                }
                else if(e.getButton() == MouseEvent.BUTTON3) //If clicked on canvas
                {
                    addBorder.setEnabled(false);
                    copyShape.setEnabled(false);
                    pasteShape.setEnabled(false);
                    shapeRemoval.setEnabled(false);
                    
                    pasteFunctionality(e.getX(), e.getY());
                    
                    popupMenu.show(Canvas.this, e.getX(), e.getY());
                }
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
           public void mouseDragged(MouseEvent e) {
               if(!whiteboard.runningAsClient())
               {
                   int dx = e.getX() - lastX;
                   int dy = e.getY() - lastY;
                   lastX = e.getX();
                   lastY = e.getY();

                   if(movingPoint != null) 
                   {
                       movingPoint.x += dx;
                       movingPoint.y += dy;
                       selected.modifyShapeWithPoints(anchorPoint, movingPoint);
                   } 
                   else if(selected != null) 
                   {
                       selected.move(dx, dy);
                   }
               }
           }
        });
        
    } //End Canvas constructor
    
    //Add shape to canvas
    public void addShape(DShapeModel model) 
    {
        if(!whiteboard.runningAsClient())
        {
            model.setID(Whiteboard.getNextID());
        }
        
    	modelShapes.add(model);
        
        //Repaint where the previous shape was
        //This makes the new shape move to the front
        if(selected != null) 
        {
            repaintShape(selected);
        }
        
        DShape shape = null;
        if(model instanceof DRectModel)
        {
            shape = new DRect(model, this);
        }
        else if(model instanceof DOvalModel)
        {
            shape = new DOval(model, this);
        }
        else if(model instanceof DLineModel)
        {
            shape = new DLine(model, this);
        }
        else if(model instanceof DTextModel)
        {
            shape = new DText(model, this);
            DText textShape = (DText) shape;
            whiteboard.updateFont(textShape.getText(), textShape.getFontStyle());
        }
        
        model.addListener(this);
        shapes.add(shape);
        whiteboard.addShapeToTable(shape);
        
        if(!whiteboard.runningAsClient())
        {
            selected = shape;
        }
        
        if(whiteboard.runningAsServer())
        {
            whiteboard.getServer().sendMessage(MessageNotification.ADD, model);
        }
        
        
        repaintShape(shape);
    } //End addShape
    
    //Returns a pointer to the list shapes
    public ArrayList<DShape> getShapes() 
    {
        return shapes;
    } //End getShapes
    
    public ArrayList<DShape> getShapesWithBorders()
    {
        return shapesWithBorders;
    } //End getShapesWithBords
    
    //Returns an array list of all the shape models of the shapes on the canvas
    public ArrayList<DShapeModel> getShapeModels() 
    {
        ArrayList<DShapeModel> models = new ArrayList<DShapeModel>();
        for(DShape shape : shapes)
            models.add(shape.getModel());
        return models;
    } //End getShapeModels
    
    //Repaints an area specified by the passed bounds
    public void repaintArea(Rectangle bounds) 
    {
        repaint(bounds);
    } //End repaintArea
    
    //Repaint the passed shape
    public void repaintShape(DShape shape) 
    {
        if(shape == selected)
        {
            repaint(shape.getBigBounds());
        }
        else
        {
            repaint(shape.getBounds());
        }
    } //End repaintShape
    
    
    //Paints and draw the shapes on canvas
    @Override
    public void paintComponent(Graphics g) 
    {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g2);
        
        for(DShape shape : shapes)
        {
            shape.draw(g2, (selected == shape));
            if(shapesWithBorders.contains(shape))
            {
                shape.drawBorders(g2,shape);
                repaint();
            }
        }
        
        
    } //End paintComponent
    
    //Select the object that contains the given point if it exists
    public void selectObject(Point pt) 
    {
        lastX = pt.x;
        lastY = pt.y;
        movingPoint = null;
        anchorPoint = null;
        
        //Check if there's a shape/object selected with knobs
        if(selected != null) 
        {
            for(Point point : selected.getKnobs())
            {
                //If a knob is selected, create new moving point
                //Also obtain which knob (anchor points) is selected
                if(selected.selectedKnob(pt, point)) 
                {
                    movingPoint = new Point(point);
                    anchorPoint = selected.getAnchorForSelectedKnob(point);
                    break;
                }
            }
        }
        
        //If there is no knob selected, check if an object is clicked
        if(movingPoint == null) 
        {
            selected = null;
            for(DShape shape : shapes)
            {
                if(shape.containsPoint(pt))
                    selected = shape;
            }
        }
        
        //If current whiteboard is in server mode
        //Send message to the client table for selection update
        if(selected != null && whiteboard.runningAsServer())
        {
            whiteboard.getServer().sendMessage(MessageNotification.CHANGE, selected.getModel());
        }
        
        //Update table selection 
        whiteboard.updateTableSelection(selected);
        
        repaint();
    } //End selectObject
    
    public DShape getSelected()
    {
        return selected;
    } //End getSelected
    
    public void setText(String text)
    {
        //Double checks if the selected shape is a text shape
        if(selected instanceof DText)
        {
            ((DText) selected).setText(text);
        }
    } //End setText
    
    public void setFont(String font)
    {
        //Double checks if the selected shape is a text shape
        if(selected instanceof DText)
        {
            ((DText) selected).setFontStyle(font);
        }
    } //End setFont
    
    public void setRandomizedColor(Color c)
    {
        Random rand = new Random();
        
        for(int i = 0; i < shapes.size(); i++)
        {
            float r = rand.nextFloat();
            float g = rand.nextFloat();
            float b = rand.nextFloat();

            Color randomColor = new Color(r,g,b);
            
            int s = rand.nextInt(shapes.size());
            shapes.get(s).setColor(randomColor);
        }
        
        this.repaint();
    } //End setRandomizedColor
    
    public DShape getShapeID(int ID)
    {
        for(DShape shape : shapes)
        {
            if(shape.getModelID() == ID)
            {
                return shape;
            }
        }
        
        return null;
    } //End getShapeID
    
    //Return a random size for each shape created
    //Size is limited to 200 so that it does not cover the entire canvas
    public int getRandomSize()
    {
        return randomSize = rand.nextInt(200) + 10;
    } //End getRandomSize
    
    //Return a random position for each shape created
    //Position is limited to 350 so that it does not go out of bounds
    public int getRandomPosition()
    {
        return randomPosition = rand.nextInt(350) + 1;
    } //End getRandomPosition
    
    
    private void notifyTextEnabling()
    {
        if(selected instanceof DText)
        {
            whiteboard.getTextField().setEnabled(true);
            whiteboard.getFontSelector().setEnabled(true);
        }
        else
        {
            whiteboard.getTextField().setEnabled(false);
            whiteboard.getFontSelector().setEnabled(false);
        }
    }
    
    private void rightClickFunctionality(int x, int y)
    {
        if(selected instanceof DRect || selected instanceof DOval)
        {
            addBorder.setEnabled(true);
            addBorder.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ex)
                {
                    shapesWithBorders.add(selected);
                }
            });
        }
        else
        {
            addBorder.setEnabled(false);
        }

        copyShape.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                if(selected instanceof DRect || selected instanceof DOval)
                {
                    copiedShape = selected;
                    copiedModel = copiedShape.getModel();
                }
            }
        });

        pasteFunctionality(x, y);
        
        shapeRemoval.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                removeCorrespondingShape(selected);
            }
        });
    } //End rightClickFunctionality
    
    private void pasteFunctionality(int x, int y)
    {
        if(copiedShape != null)
        {
            pasteShape.setEnabled(true);
            pasteShape.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e)
                {
                    //Add shape to current server and client
                    try
                    {
                        if(copiedModel instanceof DRectModel)
                        {
                            DRectModel rect = new DRectModel();
                            rect.setColor(copiedModel.getColor());
                            //rect.setBounds(copiedModel.getX()+10, copiedModel.getY()+10, copiedModel.getWidth(), copiedModel.getHeight());
                            rect.setBounds(x, y, copiedModel.getWidth(), copiedModel.getHeight());
                            addShape(rect);
                        }
                        else if(copiedModel instanceof DOvalModel)
                        {
                            DOvalModel oval = new DOvalModel();
                            oval.setColor(copiedModel.getColor());
                            oval.setBounds(x, y, copiedModel.getWidth(), copiedModel.getHeight());
                            addShape(oval);
                        }

                        if(whiteboard.runningAsServer())
                        {
                            whiteboard.getServer().sendMessage(MessageNotification.ADD, copiedShape.getModel());
                        }
                    }
                    catch(Exception ex){}

                    pasteShape.setEnabled(false);
                    copiedShape = null;
                }
            });
        }
    } //End pasteFunctionality
    
    private void generatePopupMenu()
    {
        //These popup items are not part of the requirement
        popupMenu = new JPopupMenu();
        addBorder = new JMenuItem("Add Border");
        copyShape = new JMenuItem("Copy Shape");
        pasteShape = new JMenuItem("Paste Shape");
        shapeRemoval = new JMenuItem("Remove Shape");
        addBorder.setEnabled(false);
        copyShape.setEnabled(false);
        pasteShape.setEnabled(false);
        shapeRemoval.setEnabled(false);
    }

    public void modelChanged(DShapeModel model) 
    {
        if(whiteboard.runningAsServer() && !model.modelRemoved())
        {
            whiteboard.getServer().sendMessage(MessageNotification.CHANGE, model);
        }
    }
    
    public void moveBack(){
    	if(selected == null) return;
        
        if(!shapes.isEmpty() && shapes.remove(selected))
        {
            shapes.add(0,selected);
        }
        
        if(!shapesWithBorders.isEmpty() && shapesWithBorders.remove(selected))
        {
            shapesWithBorders.add(0,selected);
        }
        
        /* Very shape specific, moves shape 1 by 1
    	for(int i = this.shapes.size() - 1; i > 0; i--)
        {
            if(selected.equals(shapes.get(i)))
            {
                int j = i - 1;

                if(j < 0) return;

                Collections.swap(shapes, i, j);
                selected = shapes.get(j);
                this.repaint();
                break;
            }
    	}*/
        
    	whiteboard.moveBack(selected);
        if(whiteboard.runningAsServer())
        {
            whiteboard.getServer().sendMessage(MessageNotification.BACK, selected.getModel());
        }
        this.repaint();
    }
    
    public void moveFront()
    {
    	if(selected == null) return;
        
        if(!shapes.isEmpty() && shapes.remove(selected))
        {
            shapes.add(selected);
        }
        
        if(!shapesWithBorders.isEmpty() && shapesWithBorders.remove(selected))
        {
            shapesWithBorders.add(selected);
        }
        
        /* Very shape specific, moves shape 1 by 1
    	for(int i = 0; i < shapes.size() - 1; i++)
        {
            if(selected.equals(shapes.get(i)))
            {
                    int j = i + 1;
                    if(j >= shapes.size()) return;
                    
                    Collections.swap(shapes, i, j);
                    selected = shapes.get(j);
                    this.repaint();
                    
                    break;
            }
    	}*/
        
    	whiteboard.moveFront(selected);
        if(whiteboard.runningAsServer())
        {
            whiteboard.getServer().sendMessage(MessageNotification.FRONT, selected.getModel());
        }
    	this.repaint();
        
    }
    
    public void clientMoveFront(DShape shape)
    {
        if(!shapes.isEmpty() && shapes.remove(shape))
        {
            shapes.add(shape);
        }
        
        whiteboard.moveFront(shape);
        repaintShape(shape);
    }
    
    public void clientMoveBack(DShape shape)
    {
        if(!shapes.isEmpty() && shapes.remove(shape))
        {
            shapes.add(0, shape);
        }
        
        whiteboard.moveBack(shape);
        
        repaintShape(shape);
    }
    
    
    void save(File file)
    {
    	fws.save(file);
    }
    
    void open(File file){
    	fws.open(file);
    }
    
    void saveImageFile(File f)
    {
        //Removes the knob selection before drawing
        DShape lastSelected = selected;
        selected = null;
        
        fws.saveImage(f);
        
        selected = lastSelected;
    }
    
    void clear(){
        
        //Clear everything from client
    	if(whiteboard.runningAsServer())
        {
            for(int i = 0; i < shapes.size(); i++)
            {
                whiteboard.getServer().sendMessage(MessageNotification.REMOVE, shapes.get(i).getModel());
            }
        }
        
    	shapes.clear();
        shapesWithBorders.clear();
        modelShapes.clear();
        
        selected = null;
        whiteboard.clearTable();
        
    	repaint();
    	 
    }
    
    public void remove(DShape shape){
  
    	shapes.remove(shape);
        whiteboard.removeShapeFromTable(shape);
        
        if(whiteboard.runningAsServer())
        {
            whiteboard.getServer().sendMessage(MessageNotification.REMOVE, selected.getModel());
        }
    	
    	repaint();
    }
    
    public void selectShapeForRemoval()
    {
        removeCorrespondingShape(selected);
        selected = null;
    }
    
    //Client side removal
    public void removeCorrespondingShape(DShape shape)
    {
        shape.getModel().removeListener(this); //Remove the listener for the model
        shape.removeCorrespondingShape(); //Remove the shape object
    }
    
    void setColor(Color c){
    	if(selected == null) return;
    	
    	selected.setColor(c);
    	this.repaint();
    }
    
} //End Canvas