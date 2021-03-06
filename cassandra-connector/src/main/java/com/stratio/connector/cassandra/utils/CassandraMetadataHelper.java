/*
 * Licensed to STRATIO (C) under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  The STRATIO (C) licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.stratio.connector.cassandra.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.datastax.driver.core.DataType;
import com.stratio.crossdata.common.metadata.ColumnType;

/**
 * Cassandra mapping of META column types to their underlying Java
 * class implementations. The following conversions are considered:
 * <table>
 * <tr><th>Cassandra Type</th><th>Cassandra Java class</th><th>META Column Type</th></tr>
 * <tr><td>BIGINT</td><td>Long</td><td>BIGINT</td></tr>
 * <tr><td>BOOLEAN</td><td>Boolean</td>BOOLEAN<td></td></tr>
 * <tr><td>COUNTER</td><td>Long</td>NATIVE<td></td></tr>
 * <tr><td>DOUBLE</td><td>Double</td><td>DOUBLE</td></tr>
 * <tr><td>FLOAT</td><td>Float</td><td>FLOAT</td></tr>
 * <tr><td>INT</td><td>Integer</td><td>INT</td></tr>
 * <tr><td>TEXT</td><td>String</td><td>TEXT</td></tr>
 * <tr><td>VARCHAR</td><td>String</td><td>VARCHAR</td></tr>
 * <tr><td>TEXT</td><td>String</td><td>TEXT</td></tr>
 * </table>
 */
public class CassandraMetadataHelper {

    /**
     * Class logger.
     */
    private static final Logger LOG = Logger.getLogger(CassandraMetadataHelper.class.getName());
    /**
     * Mapping of native datatypes to SQL types.
     */
    private static Map<DataType.Name, String> nativeODBCType = new HashMap<>();
   // private static Map<ColumnType, String> dbType = new HashMap<>();
   // private static Map<ColumnType, Class<?>> dbClass = new HashMap<>();
    private static Map<com.stratio.crossdata.common.metadata.DataType, String> dbType = new HashMap<>();
    private static Map<com.stratio.crossdata.common.metadata.DataType, Class<?>> dbClass = new HashMap<>();
/*
    static {

        dbClass.put(ColumnType.BIGINT, Long.class);
        dbClass.put(ColumnType.BOOLEAN, Boolean.class);
        dbClass.put(ColumnType.DOUBLE, Double.class);
        dbClass.put(ColumnType.FLOAT, Float.class);
        dbClass.put(ColumnType.INT, Integer.class);
        dbClass.put(ColumnType.TEXT, String.class);
        dbClass.put(ColumnType.VARCHAR, String.class);

        dbType.put(ColumnType.BIGINT, "BIGINT");
        dbType.put(ColumnType.BOOLEAN, "BOOLEAN");
        dbType.put(ColumnType.DOUBLE, "DOUBLE");
        dbType.put(ColumnType.FLOAT, "FLOAT");
        dbType.put(ColumnType.INT, "INT");
        dbType.put(ColumnType.TEXT, "TEXT");
        dbType.put(ColumnType.VARCHAR, "VARCHAR");

        nativeODBCType.put(DataType.Name.COUNTER, "SQL_INTEGER");

    }*/

    static {

        dbClass.put(com.stratio.crossdata.common.metadata.DataType.BIGINT, Long.class);
        dbClass.put(com.stratio.crossdata.common.metadata.DataType.BOOLEAN, Boolean.class);
        dbClass.put(com.stratio.crossdata.common.metadata.DataType.DOUBLE, Double.class);
        dbClass.put(com.stratio.crossdata.common.metadata.DataType.FLOAT, Float.class);
        dbClass.put(com.stratio.crossdata.common.metadata.DataType.INT, Integer.class);
        dbClass.put(com.stratio.crossdata.common.metadata.DataType.TEXT, String.class);
        dbClass.put(com.stratio.crossdata.common.metadata.DataType.VARCHAR, String.class);

        dbType.put(com.stratio.crossdata.common.metadata.DataType.BIGINT, "BIGINT");
        dbType.put(com.stratio.crossdata.common.metadata.DataType.BOOLEAN, "BOOLEAN");
        dbType.put(com.stratio.crossdata.common.metadata.DataType.DOUBLE, "DOUBLE");
        dbType.put(com.stratio.crossdata.common.metadata.DataType.FLOAT, "FLOAT");
        dbType.put(com.stratio.crossdata.common.metadata.DataType.INT, "INT");
        dbType.put(com.stratio.crossdata.common.metadata.DataType.TEXT, "TEXT");
        dbType.put(com.stratio.crossdata.common.metadata.DataType.VARCHAR, "VARCHAR");
        dbType.put(com.stratio.crossdata.common.metadata.DataType.LIST, "LIST");

        nativeODBCType.put(DataType.Name.COUNTER, "SQL_INTEGER");

    }

    /**
     * Mapping between Cassandra datatypes and META datatypes.
     */
    private static Map<String, com.stratio.crossdata.common.metadata.DataType> typeMapping = new HashMap<>();

    /**
     * Class constructor.
     */
    public CassandraMetadataHelper() {
        for (Map.Entry<com.stratio.crossdata.common.metadata.DataType, String> entry : dbType.entrySet()) {
            typeMapping.put(entry.getValue(), entry.getKey());
        }
    }

    /**
     * Transform a Cassandra type to Crossdata type.
     * @param dbTypeName The Cassandra column type.
     * @return The Crossdata ColumnType.
     */
    public ColumnType toColumnType(String dbTypeName) {
        ColumnType result;
        if (typeMapping.get(dbTypeName.toUpperCase())!=null) {
            result = new ColumnType(typeMapping.get(dbTypeName.toUpperCase()));
            result.setDBMapping(dbType.get(result), dbClass.get(result));
        }else{
            try {
                DataType.Name cassandraType = DataType.Name.valueOf(dbTypeName.toUpperCase());
                result = new ColumnType(com.stratio.crossdata.common.metadata.DataType.NATIVE);
                result.setDBMapping(cassandraType.name(), cassandraType.asJavaClass());
                result.setODBCType(nativeODBCType.get(cassandraType));
            } catch (IllegalArgumentException iae) {
                LOG.error("Invalid database type: " + dbTypeName, iae);
                result = null;
            }
        }
        return result;
    }

    /**
     * Transform a Cassandra type to Crossdata type.
     * @param dbTypeName The Cassandra column type.
     * @return The Crossdata ColumnType.
     */
    public ColumnType toColumnType(String dbTypeName, String innerType) {
        ColumnType result;
        if (typeMapping.get(dbTypeName.toUpperCase())!=null) {
            result = new ColumnType(typeMapping.get(dbTypeName.toUpperCase()));
            result.setDBCollectionType(toColumnType(innerType));
            result.setDBMapping(dbType.get(result), dbClass.get(result));
        }else{
            try {
                DataType.Name cassandraType = DataType.Name.valueOf(dbTypeName.toUpperCase());
                result = new ColumnType(com.stratio.crossdata.common.metadata.DataType.NATIVE);
                result.setDBMapping(cassandraType.name(), cassandraType.asJavaClass());
                result.setODBCType(nativeODBCType.get(cassandraType));
            } catch (IllegalArgumentException iae) {
                LOG.error("Invalid database type: " + dbTypeName, iae);
                result = null;
            }
        }
        return result;
    }


}
