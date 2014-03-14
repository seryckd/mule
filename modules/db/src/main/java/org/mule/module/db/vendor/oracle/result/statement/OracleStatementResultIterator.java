/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.db.vendor.oracle.result.statement;

import org.mule.module.db.domain.query.QueryTemplate;
import org.mule.module.db.domain.connection.DbConnection;
import org.mule.module.db.domain.autogeneratedkey.AutoGeneratedKeyStrategy;
import org.mule.module.db.result.resultset.ResultSetHandler;
import org.mule.module.db.result.statement.StatementResultIterator;

import java.sql.Statement;

/**
 * Defines a {@link StatementResultIterator} for Oracle databases
 */
public class OracleStatementResultIterator extends StatementResultIterator
{

    public OracleStatementResultIterator(DbConnection connection, Statement statement, QueryTemplate queryTemplate, AutoGeneratedKeyStrategy autoGeneratedKeyStrategy, ResultSetHandler resultSetHandler)
    {
        super(connection, statement, queryTemplate, autoGeneratedKeyStrategy, resultSetHandler);
    }

    @Override
    protected boolean retrieveAutoGeneratedKeys()
    {
        // Auto generated keys must be processed always, otherwise they will be detected as a resultSet
        // but the result won't be correctly processed
        return true;
    }
}
