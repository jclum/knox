/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.gateway.descriptor.xml;

import org.apache.hadoop.gateway.descriptor.ClusterDescriptor;
import org.apache.hadoop.gateway.descriptor.FilterDescriptor;
import org.apache.hadoop.gateway.descriptor.FilterParamDescriptor;
import org.apache.hadoop.gateway.descriptor.ResourceDescriptor;
import org.apache.hadoop.gateway.descriptor.ClusterDescriptorExporter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.Writer;

public class XmlClusterDescriptorExporter implements ClusterDescriptorExporter, XmlClusterDescriptorTags {

  @Override
  public String getFormat() {
    return "xml";
  }

  @Override
  public void store( ClusterDescriptor descriptor, Writer writer ) throws IOException {
    try {
      DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = builderFactory.newDocumentBuilder();
      Document document = builder.newDocument();
      document.setXmlStandalone( true );

      Element cluster = document.createElement( CLUSTER );
      document.appendChild( cluster );

      for( ResourceDescriptor resource : descriptor.resources() ) {
        cluster.appendChild( createResource( document, resource ) );
      }

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      transformerFactory.setAttribute( "indent-number", 2 );
      Transformer transformer = transformerFactory.newTransformer();
      //transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
      transformer.setOutputProperty( OutputKeys.STANDALONE, "yes" );
      transformer.setOutputProperty( OutputKeys.INDENT, "yes" );

      StreamResult result = new StreamResult( writer );
      DOMSource source = new DOMSource(document);
      transformer.transform( source, result );

    } catch( ParserConfigurationException e ) {
      throw new IOException( e );
    } catch( TransformerException e ) {
      throw new IOException( e );
    }
  }

  private static Element createResource( Document dom, ResourceDescriptor resource ) {
    Element element = dom.createElement( RESOURCE );

    addTextElement( dom, element, RESOURCE_SOURCE, resource.source() );
    addTextElement( dom, element, RESOURCE_TARGET, resource.target() );

    for( FilterDescriptor filter : resource.filters() ) {
      element.appendChild( createFilter( dom, filter ) );
    }

    return element;
  }

  private static Element createFilter( Document dom, FilterDescriptor filter ) {
    Element element = dom.createElement( FILTER );

    addTextElement( dom, element, FILTER_ROLE, filter.role() );
    addTextElement( dom, element, FILTER_IMPL, filter.impl() );

    for( FilterParamDescriptor param : filter.params() ) {
      element.appendChild( createFilterParam( dom, param ) );
    }

    return element;
  }

  private static Element createFilterParam( Document dom, FilterParamDescriptor param ) {
    Element element = dom.createElement( FILTER_PARAM );
    addTextElement( dom, element, FILTER_PARAM_NAME, param.name() );
    addTextElement( dom, element, FILTER_PARAM_VALUE, param.value() );
    return element;
  }

  private static void addTextElement( Document doc, Element parent, String tag, String text ) {
    if( text != null ) {
      Element element = doc.createElement( tag );
      element.appendChild( doc.createTextNode( text ) );
      parent.appendChild( element );
    }
  }

}
