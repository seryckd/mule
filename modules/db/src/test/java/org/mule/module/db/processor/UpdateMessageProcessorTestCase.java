/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.db.processor;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.module.db.domain.autogeneratedkey.NoAutoGeneratedKeyStrategy;
import org.mule.module.db.domain.connection.DbConnection;
import org.mule.module.db.domain.connection.DbConnectionFactory;
import org.mule.module.db.domain.executor.QueryExecutor;
import org.mule.module.db.domain.param.QueryParam;
import org.mule.module.db.domain.query.Query;
import org.mule.module.db.domain.query.QueryTemplate;
import org.mule.module.db.domain.query.QueryType;
import org.mule.module.db.domain.transaction.TransactionalAction;
import org.mule.tck.size.SmallTest;

import java.util.Collections;

import org.junit.Ignore;
import org.junit.Test;

@Ignore("Re-add query validation")
@SmallTest
public class UpdateMessageProcessorTestCase
{

    @Test
    public void testAcceptsValidQuery() throws Exception
    {
        // Implement
    }

    @Test
    public void testRejectsNonSupportedSql() throws Exception
    {
        DbConnectionFactory dbConnectionFactory = mock(DbConnectionFactory.class);

        for (QueryType type : QueryType.values())
        {
            QueryTemplate queryTemplate = new QueryTemplate("UNUSED SQL TEXT", type, Collections.<QueryParam>emptyList());

            if (type != QueryType.UPDATE && type != QueryType.STORE_PROCEDURE_CALL)
            {
                try
                {
                    Query query = new Query(queryTemplate, null);
                    new UpdateMessageProcessor(null, null, null, null, null);
                    fail("UpdateMessageProcessor should accept UPDATE query only");
                }
                catch (IllegalArgumentException expected)
                {
                }
            }
        }
    }

    @Test
    public void testCommitsWorkIfNoTransactionDefined() throws Exception
    {
        DbConnection connection = mock(DbConnection.class);
        DbConnectionFactory dbConnectionFactory = mock(DbConnectionFactory.class);
        when(dbConnectionFactory.createConnection(TransactionalAction.JOIN_IF_POSSIBLE)).thenReturn(connection);
        QueryExecutor updateExecutor = mock(QueryExecutor.class);

        UpdateMessageProcessor processor = new UpdateMessageProcessor(null, null, null, null, null);
        MuleEvent event = mock(MuleEvent.class);
        MuleMessage muleMessage = mock(MuleMessage.class);
        when(event.getMessage()).thenReturn(muleMessage);

        processor.process(event);
        verify(dbConnectionFactory, times(1)).releaseConnection(connection);
        verify(updateExecutor, times(1)).execute(eq(connection), any(Query.class), any(NoAutoGeneratedKeyStrategy.class));
    }
}
