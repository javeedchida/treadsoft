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

import com.treadsoft.yawn.ui.YawnUI;
import javax.swing.UIManager;

/**
 * The entry point into yawn. This class simply spawns a thread to listen and 
 * respond to UI events.
 * 
 * @author jchida
 */
public class Main {
    
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
                Config.setConfigFilePath(args[0]);
                if(args.length == 2 && args[1] != null && args[1].equalsIgnoreCase("windows")){
                    try{
                        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    }catch( Exception e ){
                        e.printStackTrace();
                    }
                }
                new YawnUI().createAndShowGUI();
            }
        });
    }
}