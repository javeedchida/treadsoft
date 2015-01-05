package com.treadsoft.yawn;

import com.treadsoft.yawn.xml.Connection;
import com.treadsoft.yawn.xml.Connections;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.ButtonGroup;
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
    
    // this text area shows status messages, e.g. when a connection is changed
    private static TextArea statusArea = new TextArea("Status Window");
    
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
        p.add(new TextArea("Query Window"), BorderLayout.CENTER);
        //p.add(new JLabel("Something goes here"), BorderLayout.LINE_END);
        p.add(statusArea, BorderLayout.PAGE_END);
        frame.setContentPane(p);

        //Display the window.
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
        Connections connections = readConfiguredConnections();
        for(Connection c: connections.getConnections()){
            System.out.println(c);
            connection = new JRadioButton(c.getName());
            connection.setActionCommand(c.getName());
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
        statusArea.setText(e.getActionCommand() + " was selected");
    }        
    
    private Connections readConfiguredConnections(){
        Connections connections = null;
        String path = "yawn-connections.xml";
        File file = new File(path);
        try{
            JAXBContext jaxbContext = JAXBContext.newInstance(Connections.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            connections = (Connections) jaxbUnmarshaller.unmarshal(file);
            System.out.println(connections);
        }catch(JAXBException e){
            e.printStackTrace();
            //TODO show an error message
        }
        return connections;
    }
    
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main().createAndShowGUI();
            }
        });
    }
}