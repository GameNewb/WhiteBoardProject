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
import java.util.*;

public abstract class DShape implements ModelListener 
{
    public static final int KNOB_SIZE = 9;
    public static final Color KNOB_COLOR = Color.black;
    
    protected DShapeModel model;
    protected Canvas canvas;
    protected Rectangle lastBounds;
    
    protected ArrayList<Point> knobs;
    
    protected boolean needsRecomputeKnobs;
    
    public DShape(DShapeModel model, Canvas canvas) 
    {
        this.model = model;
        this.canvas = canvas;
        lastBounds = new Rectangle(getBounds());
        knobs = null;
        needsRecomputeKnobs = false;
        model.addListener(this);
    } //End DShape constructor
    
    public void move(int dx, int dy) 
    {
        needsRecomputeKnobs = true;
        model.move(dx, dy);
    } //End move
    
    //Return the bounds of the shape
    public Rectangle getBounds() 
    {
        return model.getBounds();
    } //End getBounds
    
    //Return the bounds of the shape with knobs
    public Rectangle getBigBounds() 
    {
        return getBigBoundsForModel(model);
    } //End getBigBounds
    
    public static Rectangle getBigBoundsForModel(DShapeModel model) 
    {
        Rectangle bounds = model.getBounds();
        return new Rectangle(bounds.x - KNOB_SIZE/2, bounds.y - KNOB_SIZE/2, bounds.width + KNOB_SIZE, bounds.height + KNOB_SIZE);
    } //End getBigBoundsForModel
    
    //Return the bounds of the last position with knobs
    public Rectangle getBigBoundsOfLastPosition() 
    {
        //Rectangle(x,y, width + knobsize, height + knobsize)
        //x and y are halved to prevent the "trailing" of the knob
        return new Rectangle(lastBounds.x - KNOB_SIZE/2, lastBounds.y - KNOB_SIZE/2, lastBounds.width + KNOB_SIZE, lastBounds.height + KNOB_SIZE);
    } //End getBigBoundsOfLastPosition
    
    public void modifyShapeWithPoints(Point anchorPoint, Point movingPoint) 
    {
        needsRecomputeKnobs = true;
        model.modifyWithPoints(anchorPoint, movingPoint);
    } //End modifyWithPoints
    
    //Check if the shape contains specific points
    public boolean containsPoint(Point pt) 
    {
        Rectangle bounds = getBounds();
        
        if(bounds.contains(pt))
            return true;
        
        // Handle conditions where the shape has width or height 0
        if(bounds.width == 0
           && Math.abs(pt.x - bounds.x) <= 3
           && pt.y <= bounds.y + bounds.height
           && pt.y >= bounds.y)
            return true;
        
        if(bounds.height == 0
           && Math.abs(pt.y - bounds.y) <= 3
           && pt.x >= bounds.x
           && pt.x <= bounds.x + bounds.width)
            return true;
        
        return false;
    } //End containsPoint
    
    //Get color of the model
    public Color getColor() 
    {
        return model.getColor();
    }
    
    //Set the color of the shape
    public void setColor(Color color) 
    {
        model.setColor(color);
    } //End setColor
    
    //Arraylist of points that corresponds to the knobs
    public ArrayList<Point> getKnobs() 
    {
        //Generate new knob points for each model
        if(knobs == null || needsRecomputeKnobs) 
        {
            knobs = new ArrayList<Point>();
            Rectangle bounds = model.getBounds();
            for(int i = 0; i < 2; i++)
            {
                for(int j = 0; j < 2; j++)
                {
                    knobs.add(new Point(bounds.x + bounds.width * i, bounds.y + bounds.height * j));
                    
                }
            }
            
            //4-point specific, orders the knobs to make it easier to compute an anchor
            Point temp = knobs.remove(2);
            knobs.add(temp);
            needsRecomputeKnobs = false;
        }
        
        return knobs;
        
    } //End getKnobs
    
    //Checks to see if a knob is selected
    public boolean selectedKnob(Point click, Point knobCenter) 
    {
        Rectangle knob = new Rectangle(knobCenter.x - KNOB_SIZE/2, knobCenter.y - KNOB_SIZE/2, KNOB_SIZE, KNOB_SIZE);
        return knob.contains(click);
    } //End selectedKnob
    
    //Computes the anchor of a given knob
    public Point getAnchorForSelectedKnob(Point pt) 
    {
        int index = getKnobs().indexOf(pt);
        return new Point(knobs.get((index + knobs.size()/2) % knobs.size()));
    } //End getAnchorForSelectedKnob
    
    //Updates canvas if the model has changed
    public void modelChanged(DShapeModel model) 
    {
        if(this.model == model) 
        {
            canvas.repaintShape(this);
            
            //If bounds have changed, repaint the area
            if(!lastBounds.equals(getBounds())) 
            {
                canvas.repaintArea(getBigBoundsOfLastPosition());
                lastBounds = new Rectangle(getBounds());
            }
        } 
    } //End modelChanged
    
    protected void drawKnobs(Graphics g) 
    {
        g.setColor(KNOB_COLOR);
        for(Point point : getKnobs())
        {
            g.fillRect(point.x - KNOB_SIZE/2, point.y - KNOB_SIZE/2, KNOB_SIZE, KNOB_SIZE);
        }
    } //End drawKnobs
    
    abstract public DShapeModel getModel();
    
    abstract public void draw(Graphics g, boolean selected);
    
} //End DShape