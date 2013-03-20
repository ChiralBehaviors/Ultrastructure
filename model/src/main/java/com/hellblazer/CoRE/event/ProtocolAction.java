/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.hellblazer.CoRE.event;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Ruleform;

/**
 * @author hhildebrand
 * 
 */
@javax.persistence.Entity
@Table(name = "protocol_action", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "protocol_action_id_seq", sequenceName = "protocol_action_id_seq")
public class ProtocolAction extends Ruleform {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "protocol_action_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    @ManyToOne
    @JoinColumn(name = "protocol")
    private Protocol          protocol;

    @Enumerated(EnumType.ORDINAL)
    private ActionType        action;

    private String            name;

    @Column(name = "sequence_number")
    private Integer           sequenceNumber   = 1;

    @ManyToOne
    @JoinColumn(name = "jar_entry")
    private JarEntry          jarEntry;

    @Column(name = "static_method")
    private String            staticMethod;

    @ManyToOne
    @JoinColumn(name = "attribute")
    private Protocol          attribute;

    @Column(name = "binary_value")
    private byte[]            binaryValue;

    @Column(name = "boolean_value")
    private Boolean           booleanValue;

    @Column(name = "integer_value")
    private Integer           integerValue;

    @Column(name = "numeric_value")
    private BigDecimal        numericValue;

    @Column(name = "text_value")
    private String            textValue;

    @Column(name = "timestamp_value")
    private Timestamp         timestampValue;

    /**
     * @return the action
     */
    public ActionType getAction() {
        return action;
    }

    /**
     * @return the attribute
     */
    public Protocol getAttribute() {
        return attribute;
    }

    /**
     * @return the binaryValue
     */
    public byte[] getBinaryValue() {
        return binaryValue;
    }

    /**
     * @return the booleanValue
     */
    public Boolean getBooleanValue() {
        return booleanValue;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * @return the integerValue
     */
    public Integer getIntegerValue() {
        return integerValue;
    }

    /**
     * @return the jarEntry
     */
    public JarEntry getJarEntry() {
        return jarEntry;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the numericValue
     */
    public BigDecimal getNumericValue() {
        return numericValue;
    }

    /**
     * @return the protocol
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * @return the sequenceNumber
     */
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * @return the staticMethod
     */
    public String getStaticMethod() {
        return staticMethod;
    }

    /**
     * @return the textValue
     */
    public String getTextValue() {
        return textValue;
    }

    /**
     * @return the timestampValue
     */
    public Timestamp getTimestampValue() {
        return timestampValue;
    }

    /**
     * @param action
     *            the action to set
     */
    public void setAction(ActionType action) {
        this.action = action;
    }

    /**
     * @param attribute
     *            the attribute to set
     */
    public void setAttribute(Protocol attribute) {
        this.attribute = attribute;
    }

    /**
     * @param binaryValue
     *            the binaryValue to set
     */
    public void setBinaryValue(byte[] binaryValue) {
        this.binaryValue = binaryValue;
    }

    /**
     * @param booleanValue
     *            the booleanValue to set
     */
    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param integerValue
     *            the integerValue to set
     */
    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    /**
     * @param jarEntry
     *            the jarEntry to set
     */
    public void setJarEntry(JarEntry jarEntry) {
        this.jarEntry = jarEntry;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param numericValue
     *            the numericValue to set
     */
    public void setNumericValue(BigDecimal numericValue) {
        this.numericValue = numericValue;
    }

    /**
     * @param protocol
     *            the protocol to set
     */
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    /**
     * @param sequenceNumber
     *            the sequenceNumber to set
     */
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * @param staticMethod
     *            the staticMethod to set
     */
    public void setStaticMethod(String staticMethod) {
        this.staticMethod = staticMethod;
    }

    /**
     * @param textValue
     *            the textValue to set
     */
    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    /**
     * @param timestampValue
     *            the timestampValue to set
     */
    public void setTimestampValue(Timestamp timestampValue) {
        this.timestampValue = timestampValue;
    }

}
