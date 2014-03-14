/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.db.result.statement;

import org.mule.module.db.domain.autogeneratedkey.AutoGeneratedKeyStrategy;
import org.mule.module.db.domain.query.QueryTemplate;
import org.mule.module.db.domain.connection.DbConnection;
import org.mule.module.db.result.resultset.ResultSetHandler;

import java.sql.Statement;

/**
 * Processes {@link Statement} results in streaming mode
 */
public class IteratorStatementResultHandler implements StatementResultHandler
{

    private final ResultSetHandler resultSetHandler;

    public IteratorStatementResultHandler(ResultSetHandler resultSetHandler)
    {
        this.resultSetHandler = resultSetHandler;
    }

    @Override
    public Object processStatement(DbConnection connection, Statement statement, QueryTemplate queryTemplate, AutoGeneratedKeyStrategy autoGeneratedKeyStrategy)
    {
        StatementResultIteratorFactory statementResultIteratorFactory = connection.getStatementResultIteratorFactory(resultSetHandler);

        return statementResultIteratorFactory.create(connection, statement, queryTemplate, autoGeneratedKeyStrategy);
    }
}
