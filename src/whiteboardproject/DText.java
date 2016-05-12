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
import java.awt.font.FontRenderContext;

public class DText extends DShape
{
    public static final double INIT_SIZE = 1.0;
    
    private Font font;
    private String previousFont;
    private int previousHeight;
    private boolean needsRecomputeFont;
    
    public DText(DShapeModel model, Canvas canvas)
    {
        super(model, canvas);
        font = null;
        needsRecomputeFont = true;
        previousFont = " ";
        previousHeight = -1;
    } //End DText constructor
    
    public String getText()
    {
        return getModel().getText();
    } //End getText
    
    public String getFontStyle()
    {
        return getModel().getFontStyle();
    } //End getFontStyle
    
    @Override
    public DTextModel getModel()
    {
        return (DTextModel) model;
    } //End getModel
    
    public void setText(String t)
    {
        getModel().setText(t);
    } //End setText
    
    public void setFontStyle(String newFont)
    {
        if(newFont.equals(getModel().getFontStyle()))
        {
            return;
        }
        
        getModel().setFontStyle(newFont);
    } //End setFontStyle
    
    @Override
    public void modifyShapeWithPoints(Point anchorPoint, Point movingPoint)
    {
        super.modifyShapeWithPoints(anchorPoint, movingPoint);
    } //End modifyShapeWithPoints
    
    @Override
    public void draw(Graphics g, boolean selected)
    {
        Shape clip = g.getClip(); //Get current clip area
        Font f = computeFont(g); //Set font and its size
        
        //Set an offset based on the fonts descent of the text
        int fontOffset = (int) f.getLineMetrics(getModel().getText(), ((Graphics2D) g).getFontRenderContext()).getDescent();
        int dy = getBounds().y + getBounds().height - fontOffset; 
        
        g.setClip(clip.getBounds().createIntersection(getBounds()));//Set it to the bounds of the text area
        g.setColor(getColor());
        g.setFont(font);
        g.drawString(getModel().getText(), getBounds().x, dy);
        g.setClip(clip);
        
        if(selected)
        {
            drawKnobs(g);
        }
        
    } //End draw
    
    public Font computeFont(Graphics g)
    {
        //Resizing
        if(needsRecomputeFont)
        {
            double size = INIT_SIZE;
            double previousSize = size;
            
            //While resizing
            while(true)
            {
                //Font object creation, (int) size gets the font object
                font = new Font(getFontStyle(), Font.PLAIN, (int) size);
                FontRenderContext context = ((Graphics2D) g).getFontRenderContext();
                double overallSize = font.getLineMetrics(getText(), context).getHeight();
                
                //If the font size does not fit, break out
                if(overallSize > getModel().getBounds().getHeight())
                {
                    break;
                }
                
                previousSize = size;
                size = (size * 1.10)+1;
            }
            
            //Set new font
            font = new Font(getFontStyle(), Font.PLAIN, (int) previousSize);
            needsRecomputeFont = false;
            
        } //End if
        
        return font;
    } //End computeFont
    
    @Override
    public void modelChanged(DShapeModel model)
    {
        //Cast as a text model
        DTextModel textModel = (DTextModel) model;
        
        //If text has changed or moved
        if(!textModel.getFontStyle().equals(previousFont) || textModel.getBounds().height != previousHeight)
        {
            //Set new bounds and text
            previousHeight = textModel.getBounds().height;
            previousFont = textModel.getFontStyle();
            needsRecomputeFont = true;
        }
        
        super.modelChanged(textModel);
    } //End modelChanged
} //End DText