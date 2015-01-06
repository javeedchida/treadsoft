package com.treadsoft.yawn;

import com.treadsoft.yawn.xml.Connection;
import com.treadsoft.yawn.xml.Connections;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * The entry point into yawn. This class simply spawns a thread to listen and 
 * respond to UI events.
 * 
 * @author jchida
 */
public class Main implements ActionListener{
    
    private static TextArea queryArea = new TextArea("");
    private static TextArea statusArea = new TextArea("Yawn! " 
            + new SimpleDateFormat("MMM dd yyyy @ HH:mm:ss").format(new Date()));
    private static Connections configuredConnections;
    private static java.sql.Connection currentConnection;
    private static String yawnConfigurationPath;
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("MainLayout");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add a panel to hold the connections
        JPanel connectionsPanel = new JPanel(new FlowLayout());
        createConnectionButtons(connectionsPanel);
        
        //Add 
        JPanel p = new JPanel(new BorderLayout());
        p.add(connectionsPanel, BorderLayout.PAGE_START);
        //p.add(new JLabel("Menu tab"), BorderLayout.LINE_START);
        p.add(queryArea, BorderLayout.CENTER);
        
        JButton btnGo = new JButton("Go");
        btnGo.setBackground(Color.GREEN);
        btnGo.addActionListener(this);
        p.add(btnGo, BorderLayout.LINE_END);
        p.add(statusArea, BorderLayout.PAGE_END);
        
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
        configuredConnections = readConfiguredConnections();
        for(Connection c: configuredConnections.getConnections()){
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
                        statusArea.setText(statusArea.getText() + "\n" + result);
                    }
                }else{
                    statusArea.setText(statusArea.getText() + "\n No connection available");
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
                statusArea.setText(statusArea.getText() + "\nConnection established for " + selectedConnection.getName());
            }catch( SQLException sqle ){
                sqle.printStackTrace();
            }
        }
    }        
    
    /**
     * Read the yawn-connections.xml file and return a Connections object.
     * @return 
     */
    private Connections readConfiguredConnections(){
        Connections connections = null;
        File file = new File(yawnConfigurationPath);
        try{
            JAXBContext jaxbContext = JAXBContext.newInstance(Connections.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            connections = (Connections) jaxbUnmarshaller.unmarshal(file);
            //System.out.println(connections);
        }catch(JAXBException e){
            e.printStackTrace();
            //TODO show an error message
        }
        return connections;
    }
    
    private Connection getConnectionByName(String name){
        Connection conn = null;
        for( Connection c : configuredConnections.getConnections() ){
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
        System.out.println("Connected to database");
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
            statusArea.setText(statusArea.getText() + "\n" + metadata.getColumnCount() + " columns returned");
            /*
            while (rs.next()) {
                String coffeeName = rs.getString("COF_NAME");
                int supplierID = rs.getInt("SUP_ID");
            }
            */
        } catch (SQLException e) {
            e.printStackTrace();
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
                Main.yawnConfigurationPath = args[0];
                new Main().createAndShowGUI();
            }
        });
    }
}