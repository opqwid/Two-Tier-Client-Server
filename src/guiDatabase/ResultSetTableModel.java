/*
 * Name: Vincent Qiu
 * Course: CNT 4714 Summer 2022
 * Assignment title: Project 2 - A Two-tier Client-Server Application
 * Date: July 4, 2022
 * 
 * Class: Enterprise Computing
 */
package guiDatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.table.AbstractTableModel;

import com.mysql.cj.jdbc.MysqlDataSource;

public class ResultSetTableModel extends AbstractTableModel 
{
  
   private static final long serialVersionUID = 1L;
   private Connection connection;
   private Statement queryStatement;
   private Statement updateStatement;
   private ResultSet resultSet;
   private ResultSetMetaData metaData;
   private int numberOfRows;
   private int numberOfColumns = 0;
   private boolean performedUpdate = false;
   private boolean connectedToDatabase = false;
      
   public void Connection (String url, String username, String password) throws SQLException {

	   MysqlDataSource dataSource = null;
	    	
	   dataSource = new MysqlDataSource();
	   dataSource.setURL(url);
	   dataSource.setUser(username);
	   dataSource.setPassword(password); 	
	    
       Connection connection = dataSource.getConnection();
	
   	   queryStatement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
   	   updateStatement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

       connectedToDatabase = true;
      
   }

   public Class<?> getColumnClass(int column) throws IllegalStateException {

      if (!connectedToDatabase) {

         throw new IllegalStateException("Not Connected to Database");

      }
         
      try {

         String className = metaData.getColumnClassName(column + 1);
         return Class.forName(className);

      }
      catch (Exception exception) {

         exception.printStackTrace();

      } 
      
      return Object.class; 

   }

   public int getColumnCount() throws IllegalStateException {   
      
	   if (!connectedToDatabase) {

         return 0;

      }

	   if (performedUpdate) {

         return 0;

      }
	         
	   try {
		   
		   return metaData.getColumnCount();
		   
	   }
	   catch (SQLException sqlException) {
		   
		   sqlException.printStackTrace();
		   
	   }
	   return numberOfColumns;

   }

   public String getColumnName(int column) throws IllegalStateException
   {    
      if (!connectedToDatabase) {
         throw new IllegalStateException("Not Connected to Database");
      }

      try {

         return metaData.getColumnName(column + 1);  

      } 
      catch (SQLException sqlException) {

         sqlException.printStackTrace();

      } 
      
      return ""; 

   }

   public int getRowCount() throws IllegalStateException {    

	   if (!connectedToDatabase) {

         return 0;

      }
	         

	   if (performedUpdate) {

         return 0;

      }
	         

	   return numberOfRows;

	   } 
   
   public void clearTable() {

	      numberOfRows = 0;
	      numberOfColumns = 0;
	      fireTableStructureChanged();

	   }
   
   public Object getValueAt(int row, int column) throws IllegalStateException
   {
      if (!connectedToDatabase) {
         throw new IllegalStateException("Not Connected to Database");
      }
         
      try {

		   resultSet.next(); 
         resultSet.absolute(row + 1);
         return resultSet.getObject(column + 1);

      } 
      catch (SQLException sqlException) {

         sqlException.printStackTrace();

      } 
      
      return ""; 

   } 
   
   public void setQuery(String query) throws SQLException, IllegalStateException {

      if (!connectedToDatabase) {

         throw new IllegalStateException("Not Connected to Database");

      }

      resultSet = queryStatement.executeQuery(query);

      metaData = resultSet.getMetaData();

      resultSet.last();                 
      numberOfRows = resultSet.getRow();     
      numberOfColumns = metaData.getColumnCount();
      performedUpdate = false;
      fireTableStructureChanged();

   } 

   public void setUpdate(String query) throws SQLException, IllegalStateException {

	   int res;

      if (!connectedToDatabase) {

         throw new IllegalStateException( "Not Connected to Database" );

      }
         
      res = queryStatement.executeUpdate(query);
      
      System.out.println(res);

      performedUpdate = true;
      fireTableStructureChanged();

   } 
              
   public void disconnectFromDatabase() {    

      if (!connectedToDatabase) {

         return;

      }                  
         
      try {

         queryStatement.close();  
         updateStatement.close();
         connection.close();   

      }                               
      catch (SQLException sqlException) {

         sqlException.printStackTrace();           

      }                                
      finally {      

         connectedToDatabase = false;  

      }                           
   }         
}  
