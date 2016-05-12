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
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.table.AbstractTableModel;

public class TableModel extends AbstractTableModel implements ModelListener
{
	
    private ArrayList<String> colNames;
    private ArrayList<ArrayList> data;
    private ArrayList<DShapeModel> models;
    
    public TableModel(){
        super();
        colNames = new ArrayList<String>();
        data = new ArrayList<ArrayList>();
        models = new ArrayList<DShapeModel>();
    }
    

    public TableModel(String...s){
        colNames = new ArrayList<String>();
        data = new ArrayList<ArrayList>();
        models = new ArrayList<DShapeModel>();
        
        for(int i =0; i <s.length; i++){
                colNames.add(s[i]);
        } 
    }
    
    public void addModel(DShapeModel model)
    {
        models.add(0, model);
        model.addListener(this);
        fireTableDataChanged();
    } //End addModel
    
    public void removeModel(DShapeModel model)
    {
        model.removeListener(this);
        models.remove(model);
        fireTableDataChanged();
    } //End removeModel
    
    public String getColumnName(int col){
        return colNames.get(col);
    }

    @Override
    public int getRowCount() {
        //Pass the model data instead of just data
        return models.size();
    }
    
    //Returns the row index for a model
    public int getRowPerModel(DShapeModel model)
    {
        return models.indexOf(model);
    } //End getRowPerModel

    @Override
    public int getColumnCount() {

        return colNames.size();
    }
    
    public void clear()
    {
        models.clear();
        fireTableDataChanged();
    }
    
    public ArrayList<String> getColumnNames(int col)
    {
        return colNames;
    } //End getColumnNames

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) 
    {
        Rectangle bounds = models.get(rowIndex).getBounds(); //Obtain bounds of the current row model
        switch(columnIndex)
        {
            case 0: return bounds.x;
            case 1: return bounds.y;
            case 2: return bounds.width;
            case 3: return bounds.height;
            default: return null;
        }
    }
    
    public void moveFront(DShapeModel model)
    {
        //Base case - check if there are models present
        if(!models.isEmpty() && models.remove(model))
        {
            models.add(0,model);
        }
        
        fireTableDataChanged();
    } //End moveFront
    
    public void moveBack(DShapeModel model)
    {
        if(!models.isEmpty() && models.remove(model))
        {
            models.add(model);
        }
        
        fireTableDataChanged();
    } //End moveBack

    public void addColumn(String name){
        colNames.add(name);
        fireTableStructureChanged();

    }
    public int addRow(ArrayList row){
        data.add(row);
        fireTableRowsInserted(data.size()-1, data.size() -1);
        return data.size() -1;
    }
    public int addRow(){
        ArrayList row = new ArrayList();
        return addRow(row);
    }

    public void deleteRow(int row){
        if(row == -1) return;
        data.remove(row);
        fireTableRowsDeleted(row, row);
    }
    
    public void modelChanged(DShapeModel model)
    {
        int index = models.indexOf(model);
        fireTableRowsUpdated(index, index);
    }

}