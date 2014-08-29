/*
 * Licensed to STRATIO (C) under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  The STRATIO (C) licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.stratio.connector.cassandra.engine;


import com.datastax.driver.core.Session;
import com.stratio.connector.cassandra.CassandraExecutor;
import com.stratio.connector.cassandra.statements.CreateCatalogStatement;
import com.stratio.connector.cassandra.statements.CreateTableStatement;
import com.stratio.connector.cassandra.statements.DropCatalogStatement;
import com.stratio.connector.cassandra.statements.DropTableStatement;
import com.stratio.meta.common.connector.IMetadataEngine;
import com.stratio.meta.common.exceptions.ExecutionException;
import com.stratio.meta.common.exceptions.UnsupportedException;
import com.stratio.meta2.common.data.CatalogName;
import com.stratio.meta2.common.data.ClusterName;
import com.stratio.meta2.common.data.ColumnName;
import com.stratio.meta2.common.data.TableName;
import com.stratio.meta2.common.metadata.CatalogMetadata;
import com.stratio.meta2.common.metadata.TableMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jjlopez on 26/08/14.
 */
public class CassandraMetadataEngine implements IMetadataEngine {

    private Map<String, Session> sessions;
    private Session session = null;

    public CassandraMetadataEngine(Map<String, Session> sessions) {
        this.sessions = sessions;
    }

    @Override
    public void createCatalog(ClusterName targetCluster, CatalogMetadata catalogMetadata)
        throws UnsupportedException, ExecutionException {
        session = sessions.get(targetCluster);
        String catalogName = catalogMetadata.getName().getQualifiedName();
        Map<String, Object> catalogOptions = catalogMetadata.getOptions();

        CreateCatalogStatement catalogStatement =
            new CreateCatalogStatement(catalogName, true, catalogOptions.toString());
        CassandraExecutor.execute(catalogStatement.toString(), session);

    }

    @Override
    public void createTable(ClusterName targetCluster, TableMetadata tableMetadata)
        throws UnsupportedException, ExecutionException {
        session = sessions.get(targetCluster);
        String tableName = tableMetadata.getName().getQualifiedName();
        Map<String, Object> tableOptions = tableMetadata.getOptions();
        List<String> primaryKey = (List<String>) tableOptions.get("primaryKey");
        List<String> clusterKey = (List<String>) tableOptions.get("clusterKey");


        Map<ColumnName, com.stratio.meta2.common.metadata.ColumnMetadata> tableColumns =
            tableMetadata.getColumns();
        Map<String, String> columnWithType = new HashMap<String, String>();
        for (ColumnName key : tableColumns.keySet()) {
            com.stratio.meta2.common.metadata.ColumnMetadata column =
                (com.stratio.meta2.common.metadata.ColumnMetadata) tableColumns.get(key);
            columnWithType
                .put(column.getColumnType().getStandardType(), column.getName().getName());
        }

        CreateTableStatement tableStatement =
            new CreateTableStatement(tableName, columnWithType, primaryKey, clusterKey, 0, 0);
        CassandraExecutor.execute(tableStatement.toString(), session);

    }

    @Override
    public void dropCatalog(ClusterName targetCluster, CatalogName name)
        throws UnsupportedException, ExecutionException {
        session = sessions.get(targetCluster);
        DropCatalogStatement catalogStatement = new DropCatalogStatement(name.getName(), true);
        CassandraExecutor.execute(catalogStatement.toString(), session);
    }

    @Override
    public void dropTable(ClusterName targetCluster, TableName name)
        throws UnsupportedException, ExecutionException {
        session = sessions.get(targetCluster);
        DropTableStatement tableStatement = new DropTableStatement(name.getQualifiedName(), true);
        CassandraExecutor.execute(tableStatement.toString(), session);

    }
}