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
package com.treadsoft.yawn.sql;

import com.treadsoft.yawn.logging.YawnLogger;
import com.treadsoft.yawn.xml.Connection;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;

/**
 * A JDBC helper class for query execution and result set retrieval
 * @author jchida
 */
public class SqlHelper {
    
    public static java.sql.Connection createSqlConnection(Connection c) throws SQLException {
        java.sql.Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", c.getUsername());
        connectionProps.put("password", c.getPassword());
        addToClasspath(c.getDriverJar());
        conn = DriverManager.getConnection(c.getJdbcConnectionString(), connectionProps);
        return conn;
    }
    
    public static String executeCommand(String connectionName, java.sql.Connection con, String query) throws SQLException {
        YawnLogger logger = null;
        Statement stmt = null;
        if( query.trim().length() == 0 ){
            return null;
        }
        try{
            long start = new Date().getTime();
            logger = new YawnLogger(connectionName);
            logger.setSqlText(query);
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            long end = new Date().getTime();
            ResultSetMetaData metadata = rs.getMetaData();
            ResultSetReader reader = new ResultSetReader(rs, logger);
            logger.setResultSetReader(reader);
            logger.setExecutionTime(end - start);
            logger.writeLogs(metadata);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.writeLogs(e);
            return e.getMessage();
        } finally {
            if (stmt != null) { 
                stmt.close(); 
            }
        }        
        return null;
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
}
