/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
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
package com.chiralbehaviors.CoRE.meta.graph;

/**
 * @author hparry
 *
 */

public class TestEdgeImpl implements Edge<String> {

    private TestNodeImpl parent;
    private TestNodeImpl child;
    private String       edge;

    public TestEdgeImpl(String parent, String child, String edge) {
        this.parent = new TestNodeImpl(parent);
        this.child = new TestNodeImpl(child);
        this.edge = edge;
    }

    public TestEdgeImpl(TestNodeImpl parent, TestNodeImpl child, String edge) {
        this.parent = parent;
        this.child = child;
        this.edge = edge;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.graph.IEdge#getChild()
     */
    @Override
    public Node<String> getChild() {
        return child;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.graph.IEdge#getEdgeObject()
     */
    @Override
    public String getEdgeObject() {
        return edge;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.graph.IEdge#getParent()
     */
    @Override
    public Node<String> getParent() {
        return parent;
    }

}
