/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.db.processor;

import org.mule.api.MuleEvent;
import org.mule.module.db.domain.connection.DbConnection;
import org.mule.module.db.domain.executor.BulkQueryExecutor;
import org.mule.module.db.domain.executor.BulkQueryExecutorFactory;
import org.mule.module.db.domain.query.BulkQuery;
import org.mule.module.db.domain.query.QueryTemplate;
import org.mule.module.db.domain.query.QueryType;
import org.mule.module.db.domain.transaction.TransactionalAction;
import org.mule.module.db.resolver.database.DbConfigResolver;
import org.mule.module.db.resolver.query.BulkQueryResolver;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Executes an queries in bulk mode on a database
 * * <p/>
 * Accepted queries are any query with no parameters and returning an update count as result.
 * <p/>
 * Both database and bulk query are resolved, if required, using the {@link org.mule.api.MuleEvent}
 * being processed.
 */
public class BulkUpdateMessageProcessor extends AbstractDbMessageProcessor
{

    private final BulkQueryResolver bulkQueryResolver;
    private final BulkQueryExecutorFactory bulkUpdateExecutorFactory;
    private final List<QueryType> validQueryTypes;

    public BulkUpdateMessageProcessor(DbConfigResolver dbConfigResolver, BulkQueryResolver bulkQueryResolver, BulkQueryExecutorFactory bulkUpdateExecutorFactory, TransactionalAction transactionalAction)
    {
        super(dbConfigResolver, transactionalAction);
        this.bulkQueryResolver = bulkQueryResolver;
        this.bulkUpdateExecutorFactory = bulkUpdateExecutorFactory;

        validQueryTypes = new ArrayList<QueryType>();
        validQueryTypes.add(QueryType.UPDATE);
        validQueryTypes.add(QueryType.INSERT);
        validQueryTypes.add(QueryType.DELETE);
        validQueryTypes.add(QueryType.DDL);
    }

    private void validateQueryTemplates(List<QueryTemplate> queryTemplates)
    {
        for (QueryTemplate queryTemplate : queryTemplates)
        {
            validateQueryType(queryTemplate);
        }
    }

    @Override
    protected Object executeQuery(DbConnection connection, MuleEvent muleEvent) throws SQLException
    {

        MuleEvent eventToUse = resolveSource(muleEvent);

        BulkQuery bulkQuery = bulkQueryResolver.resolve(eventToUse);

        validateQueryTemplates(bulkQuery.getQueryTemplates());

        BulkQueryExecutor bulkUpdateExecutor = bulkUpdateExecutorFactory.create();
        return bulkUpdateExecutor.executeBulkQuery(connection, bulkQuery);
    }

    @Override
    protected List<QueryType> getValidQueryTypes()
    {
        return validQueryTypes;
    }
}
