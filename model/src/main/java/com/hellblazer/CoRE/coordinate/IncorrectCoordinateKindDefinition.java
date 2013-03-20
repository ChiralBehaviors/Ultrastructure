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
package com.hellblazer.CoRE.coordinate;

/**
 * @author hhildebrand
 * 
 */

public class IncorrectCoordinateKindDefinition extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public IncorrectCoordinateKindDefinition(CoordinateKind k) {
        super(
              String.format("The CoordinateKind with ID='%s' ('%s') does not have its CoordinateKindDefinition rules properly set.  It is not a valid top-level CoordinateKind because not all its CoordinateKindDefinition rules specify a subordinate CoordinateKind",
                            k.getId(), k.getName()));
    }
}
