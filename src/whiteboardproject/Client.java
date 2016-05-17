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

import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.*;
import javax.swing.SwingUtilities;

public class Client extends Thread
{
    private String host;
    private int port;
    private Whiteboard whiteboard;
    private Canvas canvas;
    
    public Client(Whiteboard whiteboard, Canvas canvas, String host, int port)
    {
        this.whiteboard = whiteboard;
        this.canvas = canvas;
        this.host = host;
        this.port = port;
    } //End Client constructor
    
    public void run()
    {
        try
        {
            //Obtain input from the server
            Socket toServer = new Socket(host, port);
            ObjectInputStream in = new ObjectInputStream(toServer.getInputStream());
            
            while(true)
            {
                //As per requirement, read from XML and pass the message object
                String xmlString = (String) in.readObject();
                XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(xmlString.getBytes()));
                MessageNotification message = (MessageNotification) decoder.readObject();
                
                //Read the message
                processMessage(message);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    //Client is responsible for processing the message sent by server
    public void processMessage(MessageNotification message)
    {
        SwingUtilities.invokeLater(new Runnable(){
            
            public void run()
            {
                DShape shape = canvas.getShapeID(message.getModel().getID());
                
                switch(message.getCommand())
                {
                    case MessageNotification.ADD:
                        if(shape == null)
                            //Send a one-time populate to all clients based on requirement
                            canvas.addShape(message.getModel());
                        break;
                    case MessageNotification.REMOVE:                       
                        if(shape != null)
                            canvas.removeCorrespondingShape(shape);
                        break;
                    case MessageNotification.FRONT:
                        if(shape != null)
                            canvas.clientMoveFront(shape);
                        break;
                    case MessageNotification.BACK:
                        if(shape != null)
                            canvas.clientMoveBack(shape);
                        break;
                    case MessageNotification.CHANGE:
                        if(shape != null)
                            shape.getModel().mimic(message.getModel());
                        whiteboard.updateTableSelection(shape);
                        break;
                    default: break;
                }
            }
        });
    }

} //End Client Class
