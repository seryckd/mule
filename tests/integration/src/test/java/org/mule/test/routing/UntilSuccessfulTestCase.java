/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.routing;

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;
import org.mule.tck.functional.FunctionalTestComponent;

public class UntilSuccessfulTestCase extends FunctionalTestCase
{
    private FunctionalTestComponent ftc;

    @Override
    protected String getConfigResources()
    {
        return "until-successful-test.xml";
    }

    @Override
    protected void doSetUp() throws Exception
    {
        super.doSetUp();
        ftc = getFunctionalTestComponent("target-mp");
    }

    public void testDefaultConfiguration() throws Exception
    {
        final MuleClient client = new MuleClient(muleContext);
        client.dispatch("vm://input-1", "XYZ", null);
        ponderUntilMessageCountReceived(1);
    }

    public void testFullConfiguration() throws Exception
    {
        final MuleClient client = new MuleClient(muleContext);
        final MuleMessage response = client.send("vm://input-2", "XYZ", null);
        assertEquals("ACK", response.getPayloadAsString());
        ponderUntilMessageCountReceived(2);
    }

    public void testRetryOnEndpoint() throws Exception
    {
        final MuleClient client = new MuleClient(muleContext);
        client.dispatch("vm://input-3", "XYZ", null);
        ponderUntilMessageCountReceived(2);
    }

    private void ponderUntilMessageCountReceived(final int expectedCount) throws InterruptedException
    {
        while (ftc.getReceivedMessagesCount() < expectedCount)
        {
            Thread.yield();
            Thread.sleep(100L);
        }
    }
}
