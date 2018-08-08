/**
 * Copyright (c) 2018 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 *  This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.workspace.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.meta.workspace.json.workflow.ChildSequencing;
import com.chiralbehaviors.CoRE.meta.workspace.json.workflow.MetaProtocol;
import com.chiralbehaviors.CoRE.meta.workspace.json.workflow.ParentSequencing;
import com.chiralbehaviors.CoRE.meta.workspace.json.workflow.Protocol;
import com.chiralbehaviors.CoRE.meta.workspace.json.workflow.SelfSequencing;
import com.chiralbehaviors.CoRE.meta.workspace.json.workflow.SiblingSequencing;

/**
 * @author halhildebrand
 *
 */
public class JsonWorkspace {
    public List<FacetApplication>         applications;
    public Map<String, ChildSequencing>   childSequences   = new HashMap<>();
    public String                         description;
    public List<Edge>                     edges            = new ArrayList<>();
    public Map<String, Existential>       existentials     = new HashMap<>();
    public Map<String, Facet>             facets;
    public List<Import>                   imports          = new ArrayList<>();
    public Map<String, Inference>         inferences       = new HashMap<>();
    public Map<String, MetaProtocol>      metaProtocols    = new HashMap<>();
    public String                         name;
    public Map<String, ParentSequencing>  parentSequences  = new HashMap<>();
    public Map<String, Protocol>          protocols        = new HashMap<>();
    public Map<String, SelfSequencing>    selfSequences    = new HashMap<>();
    public Map<String, SiblingSequencing> siblingSequences = new HashMap<>();
    public String                         uri;
    public int                            version;
}