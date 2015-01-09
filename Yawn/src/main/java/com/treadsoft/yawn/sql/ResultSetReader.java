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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple helper that reads a result set into a List (rows) of Maps (keyed by 
 * column name)
 * @author jchida
 */
public class ResultSetReader {
    
    private ResultSet rs;
    private List<Map> rows;
    private YawnLogger logger;
    private List<String> columnNames = new ArrayList<String>();
    
    public ResultSetReader(java.sql.ResultSet resultSet, YawnLogger logger){
        this.logger = logger;
        this.rs = resultSet;
        // read all rows into a List of Maps, each map is keyed by column name
        try{
            this.rows = readRows();
        } catch( SQLException e ){
            e.printStackTrace();
        }
    }
    
    private List<Map> readRows() throws SQLException {
        ResultSetMetaData metadata = rs.getMetaData();
        //store column names;
        for (int i = 1; i < metadata.getColumnCount() + 1; i++) {
            String columnName = metadata.getColumnName(i);    
            this.columnNames.add(columnName);
        }
        Map<String, Object> columns = null;
        List<Map> rows = new ArrayList<Map>(); 
        while( this.rs.next() ){
            columns = new HashMap<String, Object>();
            for (int i = 1; i < columnNames.size() + 1; i++) {
                columns.put(columnNames.get(i-1), rs.getObject(i));
            }
            rows.add(columns);
        }
        return rows;
    }
    
    public List<Map> getRows(){
        return this.rows;
    }
    
    public void writeToCsv() throws IOException {
        File output = new File(this.logger.getRunFolderPath() + "/output.csv");
        FileWriter fw = new FileWriter(output);
        //insert a blank like so that a first column of ID does not cause a SYLK 
        //format error in Excel
        fw.write("\n");
        //write header
        for (int i = 0; i < columnNames.size(); i++) {
            fw.write(columnNames.get(i));
            fw.write(",");
        }
        fw.write("\n");
        //write rows
        for(Map m : rows) {
            for (int i = 0; i < columnNames.size(); i++) {
                Object value = m.get(columnNames.get(i));
                if(value != null){
                    fw.write(value.toString());
                }else{
                    fw.write("");
                }
                fw.write(",");
            }
            fw.write("\n");
        }
        fw.close();
    }
}
