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
import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import javax.swing.*;


public class Canvas extends JPanel implements ModelListener
{
    public static final int SIZE = 400;
    public static final int INITIAL_SHAPE_POS = 10;
    public static final int INITIAL_SHAPE_SIZE = 20;
    
    private static final Random rand = new Random();
    
    private static int randomSize;
    private static int randomPosition;
    
    private ArrayList<DShape> shapes;
    private DShape selected;
    private Point movingPoint;
    private Point anchorPoint;
    
    private int lastX, lastY;
    
    private Whiteboard whiteboard;
    
    public Canvas(Whiteboard bard) 
    {
        setMinimumSize(new Dimension(SIZE, SIZE));
        setPreferredSize(getMinimumSize());
        setBackground(Color.white);
        
        whiteboard = bard;
        
        shapes = new ArrayList<DShape>();
        selected = null;
        movingPoint = null;
        
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                //if(whiteboard.isNotClient())
                    selectObject(e.getPoint());
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
           public void mouseDragged(MouseEvent e) {
               
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
        });
    } //End Canvas constructor
    
    //Add shape to canvas
    public void addShape(DShapeModel model) 
    {
        //Repaint are where previous shape was
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
        
        model.addListener(this);
        shapes.add(shape);
       
        repaintShape(shape);
    } //End addShape
    
    //Returns a pointer to the list shapes
    public ArrayList<DShape> getShapes() 
    {
        return shapes;
    } //End getShapes
    
    
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
        super.paintComponent(g);
        for(DShape shape : shapes)
        {
            shape.draw(g, (selected == shape));
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
        
        repaint();
    } //End selectObject
    
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

    public void modelChanged(DShapeModel model) 
    {
    }
    
} //End Canvas