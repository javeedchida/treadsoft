package com.treadsoft.yawn.xml;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jchida
 */
@XmlRootElement (name="connections")
public class Connections {
    private List<Connection> connections;

    @XmlElement (name="connection")
    public List<Connection> getConnections() {
        return connections;
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }
    
    
}
