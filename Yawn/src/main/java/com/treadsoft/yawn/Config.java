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

import com.treadsoft.yawn.xml.YawnConfiguration;
import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * A singleton to retrieve the yawn configuration
 * @author jchida
 */
public class Config {
    
    private static String configFilePath;
    private static Config instance = null;
    private YawnConfiguration config;
    
    public Config(){
        readYawnConfiguration(configFilePath);
    }
    
    public static Config getInstance(){
        if( instance == null ){
            synchronized( Config.class ) {
                if( instance == null ) {
                    instance = new Config();
                }
            }
        }
        return instance;
    }
    
    public YawnConfiguration get() {
        return this.config;
    }
    
    /**
     * Read the yawn-connections.xml file and return a Connections object.
     * @return 
     */
    private YawnConfiguration readYawnConfiguration(String path){
        YawnConfiguration config = null;
        File file = new File(path);
        try{
            JAXBContext jaxbContext = JAXBContext.newInstance(YawnConfiguration.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            this.config = (YawnConfiguration) jaxbUnmarshaller.unmarshal(file);
            //System.out.println(config);
        }catch(JAXBException e){
            e.printStackTrace();
            //TODO show an error message
        }
        return config;
    }
    
    public static void setConfigFilePath(String path){
        configFilePath = path;
    }
        
}
