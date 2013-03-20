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

import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author hhildebrand
 * 
 */
@javax.persistence.Entity
@Table(name = "jar_entry", schema = "sqlj")
public class JarEntry {

    @Id
    private Long   entryId;

    @Basic(fetch = FetchType.LAZY)
    private byte   entryImage;

    private String entryName;

    private Long   jarId;

    /**
     * @return the entryId
     */
    public Long getEntryId() {
        return entryId;
    }

    /**
     * @return the entryImage
     */
    public byte getEntryImage() {
        return entryImage;
    }

    /**
     * @return the entryName
     */
    public String getEntryName() {
        return entryName;
    }

    /**
     * @return the jarId
     */
    public Long getJarId() {
        return jarId;
    }

    /**
     * @param entryId
     *            the entryId to set
     */
    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    /**
     * @param entryImage
     *            the entryImage to set
     */
    public void setEntryImage(byte entryImage) {
        this.entryImage = entryImage;
    }

    /**
     * @param entryName
     *            the entryName to set
     */
    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    /**
     * @param jarId
     *            the jarId to set
     */
    public void setJarId(Long jarId) {
        this.jarId = jarId;
    }
}
