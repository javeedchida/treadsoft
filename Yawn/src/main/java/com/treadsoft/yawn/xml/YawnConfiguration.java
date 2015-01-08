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
package com.treadsoft.yawn.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jchida
 */
@XmlRootElement (name="yawn")
public class YawnConfiguration {
    private Connections connections;
    private String logRoot;
    private String runFolderPrefixDateFormat;
    private String yawnLog;

    @XmlElement (name="connections")
    public Connections getYawnConnections() {
        return connections;
    }

    public void setYawnConnections(Connections connections) {
        this.connections = connections;
    }

    @XmlElement
    public String getLogRoot() {
        return logRoot;
    }

    public void setLogRoot(String logRoot) {
        this.logRoot = logRoot;
    }

    @XmlElement
    public String getRunFolderPrefixDateFormat() {
        return runFolderPrefixDateFormat;
    }

    public void setRunFolderPrefixDateFormat(String runFolderPrefixDateFormat) {
        this.runFolderPrefixDateFormat = runFolderPrefixDateFormat;
    }
    
    public String toString(){
        StringBuilder sb = new StringBuilder("");
        sb.append("logRoot: ").append(this.logRoot).append("\n")
            .append("runFolderPrefixDateFormat: ").append("\n")
            .append(this.getYawnConnections().toString());
        return sb.toString();
    }
}
