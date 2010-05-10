/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.tcp.i18n;

import org.mule.config.i18n.Message;
import org.mule.config.i18n.MessageFactory;
import org.mule.transport.tcp.TcpConnector;

import java.net.URI;

public class TcpMessages extends MessageFactory
{
    private static final TcpMessages factory = new TcpMessages();
    
    private static final String BUNDLE_PATH = getBundlePath(TcpConnector.TCP);

    public static Message failedToBindToUri(URI uri)
    {
        return factory.createMessage(BUNDLE_PATH, 1, uri);
    }

    public static Message failedToCloseSocket()
    {
        return factory.createMessage(BUNDLE_PATH, 2);
    }

    public static Message failedToInitMessageReader()
    {
        return factory.createMessage(BUNDLE_PATH, 3);
    }

    public static Message invalidStreamingOutputType(Class c)
    {
        return factory.createMessage(BUNDLE_PATH, 4, c.getName());
    }

    public static Message pollingReceiverCannotbeUsed()
    {
        return factory.createMessage(BUNDLE_PATH, 5);
    }
}


