/*
 * Copyright (C) 2015 jchida
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.treadsoft.yawn;

import com.treadsoft.yawn.xml.Connection;
import com.treadsoft.yawn.xml.YawnConfiguration;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * The entry point into yawn. This class simply spawns a thread to listen and 
 * respond to UI events.
 * 
 * @author jchida
 */
public class Main implements ActionListener {
    
    private static TextArea queryArea = new TextArea("");
    private static SimpleDateFormat statusDateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
    private static TextArea messageArea = new TextArea("");
    private static TextArea errorArea = new TextArea("");
    private static JTabbedPane statusTabs = new JTabbedPane();
    private static YawnConfiguration yawnConfiguration;
    private static java.sql.Connection currentConnection;
    private static Connection currentYawnConnection;
    private static String yawnConfigurationPath;
    
    static{
        messageArea.setForeground(new Color(0, 180, 0));
        errorArea.setForeground(new Color(180, 0, 0));
        statusTabs.addTab("Messages", messageArea);
        statusTabs.addTab("Errors", errorArea);
        errorLog("Yawn! Error messages get posted here. Most recent on top.");
        messageLog("Yawn! Success/Informational messages get posted here. Most recent on top.");
    }
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {        
        //Create and set up the window.
        JFrame frame = new JFrame("MainLayout");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try{
                    if(currentConnection != null){
                        currentConnection.close();
                    }
                    System.out.println("Closing current connection, and exiting. Good bye!");
                }catch( SQLException sqle ){
                    sqle.printStackTrace();
                }
            }
          });

        //Add a panel to hold the connections
        JPanel connectionsPanel = new JPanel(new FlowLayout());
        createConnectionButtons(connectionsPanel);
        
        //Add 
        JPanel p = new JPanel(new BorderLayout());
        
        JButton btnGo = new JButton("Go");
        btnGo.setBackground(new Color(0, 180, 0));
        btnGo.addActionListener(this);
       
        JPanel headerPanel = new JPanel(new FlowLayout());
        headerPanel.add(connectionsPanel);
        headerPanel.add(btnGo);
        p.add(headerPanel, BorderLayout.PAGE_START);
        p.add(queryArea, BorderLayout.CENTER);
        
        p.add(statusTabs, BorderLayout.PAGE_END);
        frame.setContentPane(p);

        //Display the window.
        frame.setPreferredSize(new Dimension(800, 800));
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Creates one radio button for each configured connection.
     * @param panel a JPanel instance to which each radio button must be added
     */
    private void createConnectionButtons(JPanel panel){
        ButtonGroup group = new ButtonGroup();
        
        JRadioButton connection = null;
        //Read configured connections
        for(Connection c: yawnConfiguration.getYawnConnections().getConnections()){
            System.out.println(c);
            connection = new JRadioButton(c.getName());
            connection.setActionCommand("Database " + c.getName());
            connection.setName(c.getName());
            group.add(connection);
            connection.addActionListener(this);
            panel.add(connection);            
        }
        
        // invoke first connection by default
        if( panel.getComponentCount() > 0 ){
            ((JRadioButton)panel.getComponent(0)).doClick();    
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        if( e.getActionCommand().equalsIgnoreCase("Go") ){
            try{
                if(currentConnection != null){
                    String result = executeCommand(currentConnection, queryArea.getText());
                    if(result != null){ // error occurred
                        errorLog(result);
                    }
                }else{
                    messageLog("No connection available");
                }
            }catch( SQLException sqle ){
                sqle.printStackTrace();
            }
        }
        if( e.getActionCommand().startsWith("Database") ){
            //extract the new connection information, close the currentConnection
            //and establish a new one
            Connection selectedConnection = getConnectionByName(((JRadioButton)e.getSource()).getName());
            try{
                if(currentConnection != null){
                    currentConnection.close();
                }
                currentConnection = createSqlConnection(selectedConnection);
                currentYawnConnection = selectedConnection;
                messageLog("Connection established for " + selectedConnection.getName());
            }catch( SQLException sqle ){
                sqle.printStackTrace();
            }
        }
    }        

    /**
     * Read the yawn-connections.xml file and return a Connections object.
     * @return 
     */
    private static YawnConfiguration readYawnConfiguration(String path){
        YawnConfiguration config = null;
        File file = new File(path);
        try{
            JAXBContext jaxbContext = JAXBContext.newInstance(YawnConfiguration.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            config = (YawnConfiguration) jaxbUnmarshaller.unmarshal(file);
            System.out.println(config);
        }catch(JAXBException e){
            e.printStackTrace();
            //TODO show an error message
        }
        return config;
    }
    
    private Connection getConnectionByName(String name){
        Connection conn = null;
        for( Connection c : yawnConfiguration.getYawnConnections().getConnections() ){
            if(c.getName().equalsIgnoreCase(name)){
                conn = c;
                break;
            }
        }
        return conn;
    }
    
    private java.sql.Connection createSqlConnection(Connection c) throws SQLException {
        java.sql.Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", c.getUsername());
        connectionProps.put("password", c.getPassword());
        addToClasspath(c.getDriverJar());
        conn = DriverManager.getConnection(c.getJdbcConnectionString(), connectionProps);
        return conn;
    }
    
    private String executeCommand(java.sql.Connection con, String query) throws SQLException {
        Statement stmt = null;
        if( query.trim().length() == 0 ){
            return null;
        }
        try{
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData metadata = rs.getMetaData();
            messageLog(metadata.getColumnCount() + " columns returned");
            
            /*
            while (rs.next()) {
                String coffeeName = rs.getString("COF_NAME");
                int supplierID = rs.getInt("SUP_ID");
            }
            */
            writeLogs(metadata);
        } catch (SQLException e) {
            e.printStackTrace();
            writeLogs(e);
            return e.getMessage();
        } finally {
            if (stmt != null) { stmt.close(); }
        }        
        return null;
    }

    private java.sql.Connection getSqlConnection(Connection connection){
        java.sql.Connection con = null;
        
        return con;
    }
    
    public static void addToClasspath(String path) {
        try{
            URL u = new File(path).toURI().toURL();
            URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class urlClass = URLClassLoader.class;
            Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(urlClassLoader, new Object[]{u});
        }catch( Exception e ){
            e.printStackTrace();
        }
    }
    
    public static void main(final String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if( args.length == 0 ){
                    System.out.println( "Pass in the full path to the xml file "
                            + "containing yawn connection settings. See the "
                            + "README for more information");
                    System.exit(-1);
                }
                yawnConfiguration = readYawnConfiguration(args[0]);
                createLogRootFolder();
                if(args.length == 2 && args[1] != null && args[1].equalsIgnoreCase("windows")){
                    try{
                        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    }catch( Exception e ){
                        e.printStackTrace();
                    }
                }
                new Main().createAndShowGUI();
            }
        });
    }
    
    private static void messageLog(String log){
        messageArea.setText("[" + statusDateFormat.format(new Date()) + "] " + log + "\n" + messageArea.getText());
        statusTabs.setSelectedIndex(0);
    }

    private static void errorLog(String log){
        errorArea.setText("[" + statusDateFormat.format(new Date()) + "] " + log + "\n" + errorArea.getText());
        statusTabs.setSelectedIndex(1);
    }
    
    private static void createLogRootFolder(){
        File logroot = new File(yawnConfiguration.getLogRoot());
        if( !logroot.isDirectory() ){
            logroot.mkdir();
        }
    }
    
    private static String createDatabaseFolder(){
        File dbFolder = new File(yawnConfiguration.getLogRoot() + "/" + currentYawnConnection.getName());
        if( !dbFolder.isDirectory() ){
            dbFolder.mkdir();
        }
        return dbFolder.getAbsolutePath();
    }
    
    private static String createRunFolder(){
        String runFolderPath = new SimpleDateFormat(yawnConfiguration.getRunFolderPrefixDateFormat()).format(new Date()); 
        File runFolder = new File(createDatabaseFolder() + "/" + runFolderPath);
        if( !runFolder.isDirectory() ){
            runFolder.mkdir();
        }
        return runFolder.getAbsolutePath();
    }
    
    private void writeLogs(ResultSetMetaData metadata) throws SQLException {
        try{
            File runLog = new File(createRunFolder() + "/run.log");
            if( !runLog.isFile() ){
                runLog.createNewFile();
            }
            FileWriter runLogWriter = new FileWriter(runLog);
            runLogWriter.write(metadata.getColumnCount() + " columns returned");
            runLogWriter.close();
            
            //TODO csvWriter
            
            File runSql = new File(createRunFolder() + "/run.sql");
            if( !runSql.isFile() ){
                runSql.createNewFile();
            }
            FileWriter runSqlWriter = new FileWriter(runSql);
            runSqlWriter.write(queryArea.getText());
            runSqlWriter.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private void writeLogs(Exception e) throws SQLException {
        try{
            File runLog = new File(createRunFolder() + "/run.log");
            if( !runLog.isFile() ){
                runLog.createNewFile();
            }
            PrintWriter runLogWriter = new PrintWriter(runLog);
            e.printStackTrace(runLogWriter);
            runLogWriter.close();
            
            //TODO csvWriter
            
            File runSql = new File(createRunFolder() + "/run.sql");
            if( !runSql.isFile() ){
                runSql.createNewFile();
            }
            FileWriter runSqlWriter = new FileWriter(runSql);
            runSqlWriter.write(queryArea.getText());
            runSqlWriter.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }    

}