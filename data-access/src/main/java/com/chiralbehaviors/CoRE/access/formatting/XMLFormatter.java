/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package com.chiralbehaviors.CoRE.access.formatting;

import static com.chiralbehaviors.CoRE.access.resource.Constants.ATTR_KEY_TYPE;
import static com.chiralbehaviors.CoRE.access.resource.Constants.ATTR_MEMBER_TYPE;
import static com.chiralbehaviors.CoRE.access.resource.Constants.ATTR_NAME;
import static com.chiralbehaviors.CoRE.access.resource.Constants.ATTR_TYPE;
import static com.chiralbehaviors.CoRE.access.resource.Constants.ATTR_VALUE_TYPE;
import static com.chiralbehaviors.CoRE.access.resource.Constants.ATTR_VERSION;
import static com.chiralbehaviors.CoRE.access.resource.Constants.ELEMENT_DESCRIPTION;
import static com.chiralbehaviors.CoRE.access.resource.Constants.ELEMENT_INSTANCE;
import static com.chiralbehaviors.CoRE.access.resource.Constants.ELEMENT_URI;
import static com.chiralbehaviors.CoRE.access.resource.Constants.JEST_INSTANCE_XSD;
import static com.chiralbehaviors.CoRE.access.resource.Constants.MIME_TYPE_XML;
import static com.chiralbehaviors.CoRE.access.resource.Constants.NULL_VALUE;
import static com.chiralbehaviors.CoRE.access.resource.Constants.ROOT_ELEMENT_MODEL;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.openjpa.lib.util.Localizer;
import org.apache.openjpa.meta.ClassMetaData;
import org.apache.openjpa.meta.ValueMetaData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Marshals a root instance and its persistent closure as an XML element. The
 * closure is resolved against the persistence context that contains the root
 * instance. The XML document adheres to the <code>jest-instance.xsd</code>
 * schema.
 * 
 * @author Pinaki Poddar
 * 
 */
public class XMLFormatter {

    public static final Schema           _xsd;
    private static final DocumentBuilder _builder;
    private static final Transformer     _transformer;
    protected static Localizer           _loc       = Localizer.forPackage(XMLFormatter.class);
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat(
                                                                           "MMM dd, yyyy");

    static {
        try {
            _builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            _transformer = TransformerFactory.newInstance().newTransformer();
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            InputStream xsd = XMLFormatter.class.getResourceAsStream(JEST_INSTANCE_XSD);
            _xsd = factory.newSchema(new StreamSource(xsd));

            _transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            _transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
                                           "no");
            _transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            _transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
            _transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            _transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
                                           "2");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Encodes the given meta-model into a new XML document according to JEST
     * Domain XML Schema.
     * 
     * @param model
     *            a persistent domain model. Must not be null.
     */
    public Document encode(Metamodel model) {
        Element root = newDocument(ROOT_ELEMENT_MODEL);
        for (ManagedType<?> t : model.getManagedTypes()) {
            encodeManagedType(t, root);
        }
        return root.getOwnerDocument();
    }

    public String getMimeType() {
        return MIME_TYPE_XML;
    }

    /**
     * Create a new document with the given tag as the root element.
     * 
     * @param rootTag
     *            the tag of the root element
     * 
     * @return the document element of a new document
     */
    public Element newDocument(String rootTag) {
        Document doc = _builder.newDocument();
        Element root = doc.createElement(rootTag);
        doc.appendChild(root);
        String[] nvpairs = new String[] { "xmlns:xsi",
                XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
                // "xsi:noNamespaceSchemaLocation", JEST_INSTANCE_XSD,
                ATTR_VERSION, "1.0", };
        for (int i = 0; i < nvpairs.length; i += 2) {
            root.setAttribute(nvpairs[i], nvpairs[i + 1]);
        }
        return root;
    }

    public void write(Document doc, OutputStream out) throws IOException {
        try {
            _transformer.transform(new DOMSource(doc), new StreamResult(out));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public Document writeOut(Metamodel model, String title, String desc,
                             String uri, OutputStream out) throws IOException {
        Document doc = encode(model);
        decorate(doc, title, desc, uri);
        write(doc, out);
        return doc;
    }

    private void encodeManagedType(ManagedType<?> type, Element parent) {
        Document doc = parent.getOwnerDocument();
        Element root = doc.createElement(type.getPersistenceType().toString().toLowerCase());
        parent.appendChild(root);
        root.setAttribute(ATTR_NAME, type.getJavaType().getSimpleName());
        List<Attribute<?, ?>> attributes = MetamodelHelper.getAttributesInOrder(type);
        for (Attribute<?, ?> a : attributes) {
            String tag = MetamodelHelper.getTagByAttributeType(a);

            Element child = doc.createElement(tag);
            root.appendChild(child);
            child.setAttribute(ATTR_TYPE, typeOf(a.getJavaType()));
            if (a instanceof PluralAttribute) {
                if (a instanceof MapAttribute) {
                    child.setAttribute(ATTR_KEY_TYPE,
                                       typeOf(((MapAttribute<?, ?, ?>) a).getKeyJavaType()));
                    child.setAttribute(ATTR_VALUE_TYPE,
                                       typeOf(((MapAttribute<?, ?, ?>) a).getBindableJavaType()));
                } else {
                    child.setAttribute(ATTR_MEMBER_TYPE,
                                       typeOf(((PluralAttribute<?, ?, ?>) a).getBindableJavaType()));
                }
            }
            child.setTextContent(a.getName());
        }
    }

    Document decorate(Document doc, String title, String desc, String uri) {
        Element root = doc.getDocumentElement();
        Element instance = (Element) root.getElementsByTagName(ELEMENT_INSTANCE).item(0);
        Element uriElement = doc.createElement(ELEMENT_URI);
        uriElement.setTextContent(uri == null ? NULL_VALUE : uri);
        Element descElement = doc.createElement(ELEMENT_DESCRIPTION);
        descElement.setTextContent(desc == null ? NULL_VALUE : desc);
        root.insertBefore(uriElement, instance);
        root.insertBefore(descElement, instance);
        return doc;
    }

    String typeOf(Class<?> cls) {
        return cls.getSimpleName();
    }

    String typeOf(ClassMetaData meta) {
        return meta.getDescribedType().getSimpleName();
    }

    String typeOf(ValueMetaData vm) {
        if (vm.getTypeMetaData() == null) {
            return typeOf(vm.getType());
        }
        return typeOf(vm.getTypeMetaData());
    }

    void validate(Document doc) throws Exception {
        Validator validator = _xsd.newValidator();
        validator.validate(new DOMSource(doc));
    }
}
