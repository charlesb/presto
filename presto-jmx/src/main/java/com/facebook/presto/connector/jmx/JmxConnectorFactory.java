/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.connector.jmx;

import com.facebook.presto.spi.ConnectorHandleResolver;
import com.facebook.presto.spi.NodeManager;
import com.facebook.presto.spi.connector.Connector;
import com.facebook.presto.spi.connector.ConnectorFactory;
import com.google.common.base.Throwables;
import com.google.inject.Injector;
import io.airlift.bootstrap.Bootstrap;

import javax.management.MBeanServer;

import java.util.Map;

import static io.airlift.configuration.ConfigBinder.configBinder;
import static java.util.Objects.requireNonNull;

public class JmxConnectorFactory
        implements ConnectorFactory
{
    private final MBeanServer mbeanServer;
    private final NodeManager nodeManager;

    public JmxConnectorFactory(MBeanServer mbeanServer, NodeManager nodeManager)
    {
        this.mbeanServer = requireNonNull(mbeanServer, "mbeanServer is null");
        this.nodeManager = requireNonNull(nodeManager, "nodeManager is null");
    }

    @Override
    public String getName()
    {
        return "jmx";
    }

    @Override
    public ConnectorHandleResolver getHandleResolver()
    {
        return new JmxHandleResolver();
    }

    @Override
    public Connector create(String connectorId, Map<String, String> config)
    {
        try {
            Bootstrap app = new Bootstrap(
                    binder -> {
                        configBinder(binder).bindConfig(JmxConnectorConfig.class);
                    }
            );

            Injector injector = app.strictConfig()
                    .doNotInitializeLogging()
                    .setRequiredConfigurationProperties(config)
                    .initialize();

            JmxConnectorConfig jmxConfig = injector.getInstance(JmxConnectorConfig.class);

            return new JmxConnector(
                    connectorId,
                    mbeanServer,
                    nodeManager,
                    jmxConfig.getDumpTables(),
                    jmxConfig.getDumpPeriod(),
                    jmxConfig.getEvictionLimit());
        }
        catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}
