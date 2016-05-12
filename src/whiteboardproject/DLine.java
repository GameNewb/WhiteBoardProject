/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

public class DLine extends DShape
{
    public DLine(DShapeModel model, Canvas canvas)
    {
        super(model, canvas);
    } //End DLine constructor
    
    @Override
    public void draw(Graphics g, boolean selected)
    {
        DLineModel line = getModel();
        g.setColor(getColor());
        g.drawLine(line.getPoint1().x, line.getPoint1().y, line.getPoint2().x, line.getPoint2().y);
        if(selected)
        {
            drawKnobs(g);
        }
    } //End draw
    
    @Override
    public DLineModel getModel()
    {
        return (DLineModel) model;
    } //End getModel
    
    public ArrayList<Point> getKnobs()
    {
        if(knobs == null || needsRecomputeKnobs)
        {
            knobs = new ArrayList<Point>();
            DLineModel line = (DLineModel) model;
            knobs.add(new Point(line.getPoint1()));
            knobs.add(new Point(line.getPoint2()));
        }
        
        return knobs;
    } //End getKnobs
}
