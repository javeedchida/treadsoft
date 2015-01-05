package com.treadsoft.yawn.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A Connection POJO for use with JAXB
 * @author jchida
 */
public class Connection {
    private String name;
    private String driverJar;
    private String driverClass;
    private String jdbcConnectionString;
    private String username;
    private String password;

    @XmlAttribute
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public String getDriverJar() {
        return driverJar;
    }
    
    public void setDriverJar(String driverJar) {
        this.driverJar = driverJar;
    }

    @XmlElement
    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    @XmlElement
    public String getJdbcConnectionString() {
        return jdbcConnectionString;
    }

    public void setJdbcConnectionString(String jdbcConnectionString) {
        this.jdbcConnectionString = jdbcConnectionString;
    }

    @XmlElement
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @XmlElement
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String toString(){
        StringBuilder sb = new StringBuilder("");
        sb.append("name: ").append(this.name).append("\n")
            .append("driverJar:").append(this.driverJar).append("\n")
            .append("driverClass:").append(this.driverClass).append("\n")
            .append("jdbcConnectionString:").append(this.jdbcConnectionString).append("\n")
            .append("username:").append(this.username).append("\n")
            .append("password:").append(this.password).append("\n\n");            
        return sb.toString();
    }
    
}
