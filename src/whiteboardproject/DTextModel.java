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

public class DTextModel extends DShapeModel
{
    public static final String TEXT = "Hello";
    public static final String FONT = "Dialog";
    
    private String text;
    private String fontStyle;
    
    public DTextModel()
    {
        super();
        text = TEXT;
        fontStyle = FONT;
    } //End DTextModel constructor
    
    public String getText()
    {
        return text;
    } //End getText
    
    public String getFontStyle()
    {
        return fontStyle;
    } //End getFontStyle
    
    public void setText(String t)
    {
        text = t;
        notifyListeners();
    } //End setText
    
    public void setFontStyle(String f)
    {
        fontStyle = f;
        notifyListeners();
    } //End setFontStyle
    
    
} //End DTextModel
