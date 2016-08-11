/*
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.netconf.sal.connect.netconf;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Set;
import org.junit.Test;
import org.opendaylight.controller.config.util.xml.XmlUtil;
import org.opendaylight.netconf.sal.connect.netconf.schema.mapping.BaseSchema;
import org.opendaylight.netconf.sal.connect.util.RemoteDeviceId;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.netconf.monitoring.rev101004.NetconfState;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.netconf.monitoring.rev101004.netconf.state.Schemas;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.data.api.schema.ContainerNode;
import org.opendaylight.yangtools.yang.data.impl.codec.xml.XmlUtils;
import org.opendaylight.yangtools.yang.data.impl.schema.transform.ToNormalizedNodeParser;
import org.opendaylight.yangtools.yang.data.impl.schema.transform.dom.parser.DomToNormalizedNodeParserFactory;
import org.opendaylight.yangtools.yang.model.api.ContainerSchemaNode;
import org.opendaylight.yangtools.yang.model.api.DataSchemaNode;
import org.opendaylight.yangtools.yang.model.api.SchemaContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class NetconfStateSchemasTest {

    @Test
    public void testCreate() throws Exception {
        final SchemaContext schemaContext = BaseSchema.BASE_NETCONF_CTX_WITH_NOTIFICATIONS.getSchemaContext();
        final DataSchemaNode schemasNode =
                ((ContainerSchemaNode) schemaContext
                        .getDataChildByName(NetconfState.QNAME)).getDataChildByName(Schemas.QNAME);

        final Document schemasXml = XmlUtil.readXmlToDocument(getClass().getResourceAsStream("/netconf-state.schemas.payload.xml"));
        final ToNormalizedNodeParser<Element, ContainerNode, ContainerSchemaNode> containerNodeParser = DomToNormalizedNodeParserFactory.getInstance(XmlUtils.DEFAULT_XML_CODEC_PROVIDER, schemaContext, false).getContainerNodeParser();
        final ContainerNode compositeNodeSchemas = containerNodeParser.parse(Collections.singleton(schemasXml.getDocumentElement()), (ContainerSchemaNode) schemasNode);
        final NetconfStateSchemas schemas = NetconfStateSchemas.create(new RemoteDeviceId("device", new InetSocketAddress(99)), compositeNodeSchemas);

        final Set<QName> availableYangSchemasQNames = schemas.getAvailableYangSchemasQNames();
        assertEquals(73, availableYangSchemasQNames.size());

        assertThat(availableYangSchemasQNames,
                hasItem(QName.create("urn:TBD:params:xml:ns:yang:network-topology", "2013-07-12", "network-topology")));
    }
}
