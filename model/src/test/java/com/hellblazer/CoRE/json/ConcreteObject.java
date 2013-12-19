/**
 * Copyright (C) 2013 Halloran Parry. All rights reserved.
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
package com.hellblazer.CoRE.json;

import com.hellblazer.CoRE.agency.Agency;


public class ConcreteObject extends Agency {
    String value;

    public ConcreteObject(long id, String name, AbstractObject ref,
                          String value) {
        setId(id);
//        this.name = name;
//        this.ref = ref;
        this.value = value;
    }
    
    public ConcreteObject() {
        
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String v) {
        value = v;
    }

}