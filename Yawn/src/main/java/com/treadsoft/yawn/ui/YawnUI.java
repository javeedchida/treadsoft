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
package com.treadsoft.yawn.ui;

import com.treadsoft.yawn.Config;
import com.treadsoft.yawn.Main;
import com.treadsoft.yawn.sql.SqlHelper;
import com.treadsoft.yawn.xml.Connection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

/**
 *
 * @author jchida
 */
public class YawnUI implements ActionListener {
    private static TextArea queryArea = new TextArea("");
    private static TextArea messageArea = new TextArea("");
    private static TextArea errorArea = new TextArea("");
    private static JTabbedPane statusTabs = new JTabbedPane();
    public static SimpleDateFormat statusDateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
    private static java.sql.Connection currentConnection;
    private static Connection currentYawnConnection;
    
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
    public void createAndShowGUI() {        
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
        for(Connection c: Config.getInstance().get().getYawnConnections().getConnections()){
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
                    String result = SqlHelper.executeCommand(currentYawnConnection.getName(), currentConnection, queryArea.getText());
                    if(result != null){ // error occurred
                        errorLog(result);
                    } else {
                        messageLog("Statement ran successfully");
                        
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
                currentConnection = SqlHelper.createSqlConnection(selectedConnection);
                currentYawnConnection = selectedConnection;
                messageLog("Connection established for " + selectedConnection.getName());
            }catch( SQLException sqle ){
                sqle.printStackTrace();
            }
        }
    }        
    
    private Connection getConnectionByName(String name){
        Connection conn = null;
        for( Connection c : Config.getInstance().get().getYawnConnections().getConnections() ){
            if(c.getName().equalsIgnoreCase(name)){
                conn = c;
                break;
            }
        }
        return conn;
    }

    private static void messageLog(String log){
        messageArea.setText("[" + statusDateFormat.format(new Date()) + "] " + log + "\n" + messageArea.getText());
        statusTabs.setSelectedIndex(0);
    }

    private static void errorLog(String log){
        errorArea.setText("[" + statusDateFormat.format(new Date()) + "] " + log + "\n" + errorArea.getText());
        statusTabs.setSelectedIndex(1);
    }
}
