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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class Whiteboard extends JFrame 
{
    private Box controlBox;
    
    private JLabel addLabel;
    
    private JButton rectButton;
    private JButton ovalButton; 
    private JButton lineButton; 
    private JButton textButton;
   
    private Canvas canvas;
    
    public Whiteboard() 
    {
        setLayout(new BorderLayout());
        
        //Box component where all controls/buttons are
        controlBox = Box.createVerticalBox();
        
        createButtonBox();
        alignLeft();
        
        //Add control box to whiteboard
        add(controlBox, BorderLayout.WEST);
        
        //Set new canvas and add to whiteboard
        canvas = new Canvas(this);
        add(canvas, BorderLayout.CENTER);
        
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        
    } //End Whiteboard Constructor
    
    
    //Add shape to the Canvas
    private void addShape(DShapeModel model) 
    {
        model.setBounds(canvas.getRandomPosition(), canvas.getRandomPosition(), canvas.getRandomSize(), canvas.getRandomSize());
        canvas.addShape(model);
    } //End addShape
    
    //Creates a box where the add shapes buttons are
    private void createButtonBox() 
    {
        //Create box for shape panel
        Box panel = Box.createHorizontalBox();
        
        addLabel = new JLabel("Add: ");
        
        //Rectangle button
        rectButton = new JButton("Rect");
        rectButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               addShape(new DRectModel());
           }
        });
        
        //Oval button
        ovalButton = new JButton("Oval");
        ovalButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addShape(new DOvalModel());
            }
         });
        
        //Line button
        lineButton = new JButton("Line");
        lineButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        
        //Text button
        textButton = new JButton("Text");
        textButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        
        panel.add(addLabel);
        panel.add(rectButton);
        panel.add(ovalButton);
        panel.add(lineButton);
        panel.add(textButton);
        
        //Add button box panel to overall control box
        controlBox.add(panel, BorderLayout.WEST); 
     
    } //End createButtonBox
    
    //Set alignment to the left
    private void alignLeft() 
    {
        for(Component comp : controlBox.getComponents())
            ((JComponent) comp).setAlignmentX(Box.LEFT_ALIGNMENT);
    } //End alignLeft
    
    
    public static void main(String[] args) 
    {
        Whiteboard whiteboard = new Whiteboard();

    } //End main
    
    
} //End Whiteboard class