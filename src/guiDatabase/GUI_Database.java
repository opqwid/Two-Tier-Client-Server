/*
 * Name: Vincent Qiu
 * Course: CNT 4714 Summer 2022
 * Assignment title: Project 2 - A Two-tier Client-Server Application
 * Date: July 4, 2022
 * 
 * Class: Enterprise Computing
 */

package guiDatabase;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.Box;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;

import com.mysql.cj.jdbc.MysqlDataSource;

public class GUI_Database {

	private static final String DISPOSE_ON_CLOSE = null;
	private JFrame frame;
	private JComboBox jdbcField;
	private JComboBox urlField;
	private JTextArea sqlField;
	private JTextField userNameField;
	private JPasswordField passwordField;
	private JTable table_1;
	private ResultSetTableModel table;
	private static String url;
	private static String userName;
	private static String password;
	MysqlDataSource dataSource = new MysqlDataSource();

	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {

			public void run() {

				try {

					GUI_Database window = new GUI_Database();
					window.frame.setVisible(true);

				} catch (Exception e) {

					e.printStackTrace();

				}
			}
		});
	}

	public GUI_Database() {

		initialize();

	}

	private void initialize() {
		
		try {
			
			table = new ResultSetTableModel();
			frame = new JFrame();
			frame.getContentPane().setFont(new Font("Arial", Font.BOLD, 11));
			frame.setBounds(100, 100, 929, 551);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().setLayout(null);
		
			JLabel connectionLabel = new JLabel("Connection Details");
			connectionLabel.setFont(new Font("Arial", Font.BOLD, 13));
			connectionLabel.setForeground(Color.BLUE);
			connectionLabel.setBounds(10, 11, 164, 14);
			frame.getContentPane().add(connectionLabel);

			JLabel propertiesLabel = new JLabel("Properties File");
			propertiesLabel.setBackground(Color.WHITE);
			propertiesLabel.setBounds(10, 36, 134, 14);
			frame.getContentPane().add(propertiesLabel);
			
			String[] propertiesFile = {"root.properties","client.properties"};
			JComboBox comboBox = new JComboBox(propertiesFile);
			comboBox.setBounds(154, 34, 367, 22);
			frame.getContentPane().add(comboBox);

			JLabel usernameLabel = new JLabel("Username");
			usernameLabel.setBounds(10, 70, 134, 14);
			frame.getContentPane().add(usernameLabel);

			userNameField = new JTextField();
			userNameField.setBounds(154, 68, 367, 20);
			frame.getContentPane().add(userNameField);
			userNameField.setColumns(10);
			
			JLabel passwordLabel = new JLabel("Password");
			passwordLabel.setBounds(10, 104, 134, 14);
			frame.getContentPane().add(passwordLabel);
			
			passwordField = new JPasswordField();
			passwordField.setBounds(154, 102, 367, 20);
			frame.getContentPane().add(passwordField);

			JLabel commandInputLabel = new JLabel("Enter an SQL Command");
			commandInputLabel.setFont(new Font("Arial", Font.BOLD, 13));
			commandInputLabel.setForeground(Color.BLUE);
			commandInputLabel.setBounds(531, 11, 153, 14);
			frame.getContentPane().add(commandInputLabel);

			JTextArea sqlField = new JTextArea();
			sqlField.setBounds(531, 31, 372, 97);
			frame.getContentPane().add(sqlField);

			JButton clearSQL = new JButton("Clear SQL Command");
			clearSQL.setForeground(Color.RED);
			clearSQL.setBackground(Color.WHITE);
			clearSQL.setBounds(534, 136, 176, 23);
			frame.getContentPane().add(clearSQL);
			clearSQL.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
				
					sqlField.setText("");

				} 
			}); 

			JButton executeSQL = new JButton("Execute SQL Command");
			executeSQL.setFont(new Font("Arial", Font.BOLD, 12));
			executeSQL.setBackground(Color.GREEN);
			executeSQL.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					try {

						String queryToExecute = sqlField.getText();

						if (queryToExecute.startsWith("select") || queryToExecute.startsWith("SELECT")) {

							table.setQuery(queryToExecute);

						} else {

							table.setUpdate(queryToExecute);

						}

					} 
					catch (Exception exception) {

						System.out.println(exception);
						exception.printStackTrace();
						JOptionPane.showMessageDialog(null, exception.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);

					} 
				} 
			}); 
			executeSQL.setBounds(717, 136, 186, 23);
			frame.getContentPane().add(executeSQL);
		
			final JTextArea noConnectionText = new JTextArea("No Connection", 1, 1);
			noConnectionText.setForeground(Color.RED);
			noConnectionText.setBackground(Color.BLACK);
			noConnectionText.setBounds(10, 165, 893, 22);
			frame.getContentPane().add(noConnectionText);

			final JButton btnNewButton = new JButton("Connect to Database");
			btnNewButton.setForeground(Color.YELLOW);
			btnNewButton.setBackground(Color.BLUE);
			btnNewButton.setBounds(10, 136, 169, 23);
			frame.getContentPane().add(btnNewButton);
			btnNewButton.addActionListener(new ActionListener() {
			
				public void actionPerformed(ActionEvent e) {
					
					Properties properties = new Properties();
					FileInputStream filein = null;
					System.out.println("error");
				
					try {

						String username = userNameField.getText();
						String passsword = new String(passwordField.getPassword());  
						
						if(username.equals("root")) {

							filein = new FileInputStream("root.properties");
							properties.load(filein);
							url = properties.getProperty("MYSQL_DB_URL");
							userName = properties.getProperty("MYSQL_DB_USERNAME");
							password = properties.getProperty("MYSQL_DB_PASSWORD");
							table.Connection(url,username,password);
							
						}
						else if(username.equals("client")) {

							filein = new FileInputStream("client.properties");
							properties.load(filein);
							dataSource.setURL(properties.getProperty("MYSQL_DB_URL"));
							dataSource.setUser(properties.getProperty("MYSQL_DB_USERNAME"));
							dataSource.setPassword(properties.getProperty("MYSQL_DB_PASSWORD"));
							Connection connection = dataSource.getConnection();
							url = properties.getProperty("MYSQL_DB_URL");
							userName = properties.getProperty("MYSQL_DB_USERNAME");
							password = properties.getProperty("MYSQL_DB_PASSWORD");
							table.Connection(url,username,password);
							
						}

						noConnectionText.setText("Connected to jdbc:mysql://localhost:3306/project2 ");
						btnNewButton.setEnabled(false);
						btnNewButton.setText("CONNECTED");

					} 
					catch (IOException | SQLException ex) {

						JOptionPane.showMessageDialog(null, ex.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);

					} 
				} 
			}); 
			
			JLabel sqlResult = new JLabel("SQL Execution Result Window");
			sqlResult.setForeground(Color.BLUE);
			sqlResult.setBounds(10, 198, 207, 14);
			frame.getContentPane().add(sqlResult);
			
			table_1 = new JTable(table);
			table_1.setBounds(10, 223, 871, 225);
			frame.getContentPane().add(table_1);

			JButton clrResult = new JButton("Clear Result Window");
			clrResult.setBackground(Color.YELLOW);
			clrResult.setBounds(10, 480, 175, 23);
			frame.getContentPane().add(clrResult);
			clrResult.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					table.clearTable();

				} 
			});
		}
		catch (Exception sqlException) {

	         JOptionPane.showMessageDialog(null, sqlException.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
	         table.disconnectFromDatabase();
	         System.exit(1); 

	    } 

	    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
	         
			public void windowClosed(WindowEvent e) {

				table.disconnectFromDatabase();
				System.exit(0);

			} 

	    }); 
	}

}






