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
package com.treadsoft.yawn.logging;

import com.treadsoft.yawn.Config;
import com.treadsoft.yawn.sql.ResultSetReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class manages the specifics of creating the run folder and writing to it.
 * The YawnLogger constructor takes the yawnConfiguration, the sql command, the 
 * result set and the 
 * @author jchida
 */
public class YawnLogger {
    
    private String connectionName; 
    private String sqlText;
    private ResultSetReader resultSetReader;
    private String runFolderPath;
    private long executionTime;
    
    public YawnLogger(String connectionName){
        this.connectionName = connectionName;
        createLogRootFolder();
        this.runFolderPath = createRunFolder();
    }
    
    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }
    
    public void setResultSetReader(ResultSetReader reader){
        this.resultSetReader = reader;
    }
    
    public String getRunFolderPath() {
        return this.runFolderPath;
    }
    
    public void setRunFolderPath(String path){
        this.runFolderPath = path;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
    
    public void writeLogs(ResultSetMetaData metadata) throws SQLException {
        try{
            File runLog = new File(this.runFolderPath + "/run.log");
            if( !runLog.isFile() ){
                runLog.createNewFile();
            }
            FileWriter runLogWriter = new FileWriter(runLog);
            runLogWriter.write(resultSetReader.getRows().size() + " row(s) returned");
            runLogWriter.write(" (" + getFormattedExecutionTime() + ")");
            runLogWriter.close();
            
            resultSetReader.writeToCsv();
            
            File runSql = new File(this.runFolderPath + "/run.sql");
            if( !runSql.isFile() ){
                runSql.createNewFile();
            }
            FileWriter runSqlWriter = new FileWriter(runSql);
            runSqlWriter.write(sqlText);
            runSqlWriter.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void writeLogs(Exception e) throws SQLException {
        try{
            File runLog = new File(this.runFolderPath + "/run.log");
            if( !runLog.isFile() ){
                runLog.createNewFile();
            }
            PrintWriter runLogWriter = new PrintWriter(runLog);
            e.printStackTrace(runLogWriter);
            runLogWriter.close();
            
            //TODO csvWriter
            
            File runSql = new File(this.runFolderPath + "/run.sql");
            if( !runSql.isFile() ){
                runSql.createNewFile();
            }
            FileWriter runSqlWriter = new FileWriter(runSql);
            runSqlWriter.write(sqlText);
            runSqlWriter.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }   
    
    private void createLogRootFolder(){
        File logroot = new File(Config.getInstance().get().getLogRoot());
        if( !logroot.isDirectory() ){
            logroot.mkdir();
        }
    }
    
    private String createDatabaseFolder(){
        File dbFolder = new File(Config.getInstance().get().getLogRoot() + "/" + this.connectionName);
        if( !dbFolder.isDirectory() ){
            dbFolder.mkdir();
        }
        return dbFolder.getAbsolutePath();
    }
    
    private String createRunFolder(){
        String runFolderPath = new SimpleDateFormat(Config.getInstance().get().getRunFolderPrefixDateFormat()).format(new Date()); 
        File runFolder = new File(createDatabaseFolder() + "/" + runFolderPath);
        if( !runFolder.isDirectory() ){
            runFolder.mkdir();
        }
        return runFolder.getAbsolutePath();
    }    
    
    private String getFormattedExecutionTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("mm 'minutes', ss 'seconds', SSS 'milliseconds'");
        String time = sdf.format(this.executionTime);
        return time;
    }
    
}
