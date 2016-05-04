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

public class DOval extends DShape 
{

    public DOval(DShapeModel model, Canvas canvas) 
    {
        super(model, canvas);
    }
    
    public DOvalModel getModel() 
    {
        return (DOvalModel) model;
    }
    
    public void draw(Graphics g, boolean selected) 
    {
        g.setColor(model.getColor());
        Rectangle bounds = model.getBounds();
        g.fillOval(bounds.x, bounds.y, bounds.width, bounds.height);
        if(selected) drawKnobs(g);
    }

} //End DOval