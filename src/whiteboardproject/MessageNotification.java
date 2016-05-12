/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whiteboardproject;

/**
 *
 * @author Kiyeon
 */


public class MessageNotification 
{
    //Final "command" variables based on requirement
    public static final int ADD = 0;
    public static final int REMOVE = 1;
    public static final int FRONT = 2;
    public static final int BACK = 3;
    public static final int CHANGE = 4;
    
    public int command; //Command to be passed by server
    public DShapeModel model; //Model to be passed by server
    
    public MessageNotification()
    {
        command = -1;
        model = null;
        
    } 
    
    public MessageNotification(int command, DShapeModel model)
    {
        this.command = command;
        this.model = model;
    } //End MessageNotification constructor
    
    public int getCommand()
    {
        return command;
    }
    
    public DShapeModel getModel()
    {
        return model;
    }
    
    public void setModel(DShapeModel model)
    {
        this.model = model;
    }
    
    public void setCommand(int command)
    {
        this.command = command;
    }
    
} //End MessageNotification
