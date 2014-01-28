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

package com.hellblazer.CoRE.meta.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.meta.graph.impl.GraphImpl;
import com.hellblazer.CoRE.product.Product;

/**
 * @author hparry
 * 
 */
public class NetworkGraphTest {

    private TestNodeImpl        SONG_TYPE;
    private TestNodeImpl        CHANNEL_TYPE;
    private TestNodeImpl        USER_TYPE;

    private TestNodeImpl        Tool;
    private TestNodeImpl        Disturbed;
    private TestNodeImpl        Alan_Parsons_Project;
    private TestNodeImpl        Barry_Manilow;

    private TestNodeImpl        Pushit;
    private TestNodeImpl        FortySixAndTwo;
    private TestNodeImpl        Parabola;
    private TestNodeImpl        Mistress;
    private TestNodeImpl        Hell;
    private TestNodeImpl        Ronnie;
    private TestNodeImpl        CouldItBeMagic;

    private TestNodeImpl        Michael;
    private TestNodeImpl        Eris;

    private static final String isA      = "is a";
    private static final String contains = "contains";
    private static final String owns     = "owns";

    private ArrayList<Node<?>>  erisNodes;
    private ArrayList<Edge<?>>  erisEdges;

    private ArrayList<Node<?>>  michaelsNodes;
    private ArrayList<Edge<?>>  michaelsEdges;

    @Before
    public void setup() {
        createNodesAndEdges();
        createMichaelsGraph();
        createErisGraph();
    }

    @Test
    public void testClass() {
        Product p = new Product("myProd");
        printClass(p);
    }

    @Test
    public void testEquals() {
        TestNodeImpl magic2 = new TestNodeImpl("Could It Be Magic");
        assertEquals(CouldItBeMagic, magic2);

        List<Node<?>> list = new ArrayList<Node<?>>();
        list.add(CouldItBeMagic);
        assertTrue(list.contains(CouldItBeMagic));
        assertTrue(list.contains(magic2));
    }

    @Test
    public void testIntersection() throws CloneNotSupportedException {
        GraphImpl erisGraph = new GraphImpl(new ArrayList<Node<?>>(erisNodes),
                                            new ArrayList<Edge<?>>(erisEdges));
        GraphImpl michaelsGraph = new GraphImpl(
                                                new ArrayList<Node<?>>(
                                                                       michaelsNodes),
                                                new ArrayList<Edge<?>>(
                                                                       michaelsEdges));
        erisGraph.intersection(michaelsGraph);
        assertTrue(erisGraph.getNodes().contains(CouldItBeMagic));
        assertFalse(erisGraph.getNodes().contains(Parabola));
        assertFalse(erisGraph.getNodes().contains(Alan_Parsons_Project));
    }

    @Test
    public void testUnion() throws CloneNotSupportedException {
        GraphImpl erisGraph = new GraphImpl(new ArrayList<Node<?>>(erisNodes),
                                            new ArrayList<Edge<?>>(erisEdges));
        GraphImpl michaelsGraph = new GraphImpl(
                                                new ArrayList<Node<?>>(
                                                                       michaelsNodes),
                                                new ArrayList<Edge<?>>(
                                                                       michaelsEdges));
        erisGraph.union(michaelsGraph);
        assertTrue(erisGraph.getNodes().contains(CouldItBeMagic));
        assertTrue(erisGraph.getNodes().contains(Parabola));
        assertTrue(erisGraph.getNodes().contains(Alan_Parsons_Project));

    }

    /**
     * Eris, goddess of discord, instigator of the Trojan war, and lover of
     * double bass pedals.
     */
    private void createErisGraph() {
        erisEdges = new ArrayList<Edge<?>>();

        erisEdges.add(new TestEdgeImpl(CHANNEL_TYPE, Tool, isA));
        erisEdges.add(new TestEdgeImpl(CHANNEL_TYPE, Disturbed, isA));
        erisEdges.add(new TestEdgeImpl(CHANNEL_TYPE, Barry_Manilow, isA));
        erisEdges.add(new TestEdgeImpl(USER_TYPE, Eris, isA));
        erisEdges.add(new TestEdgeImpl(SONG_TYPE, FortySixAndTwo, isA));
        erisEdges.add(new TestEdgeImpl(SONG_TYPE, Parabola, isA));
        erisEdges.add(new TestEdgeImpl(SONG_TYPE, Mistress, isA));
        erisEdges.add(new TestEdgeImpl(SONG_TYPE, Hell, isA));
        erisEdges.add(new TestEdgeImpl(SONG_TYPE, Pushit, isA));
        erisEdges.add(new TestEdgeImpl(SONG_TYPE, Ronnie, isA));
        erisEdges.add(new TestEdgeImpl(SONG_TYPE, CouldItBeMagic, isA));

        erisEdges.add(new TestEdgeImpl(Eris, Tool, owns));
        erisEdges.add(new TestEdgeImpl(Eris, Disturbed, owns));
        erisEdges.add(new TestEdgeImpl(Eris, Barry_Manilow, owns));

        erisEdges.add(new TestEdgeImpl(Tool, FortySixAndTwo, contains));
        erisEdges.add(new TestEdgeImpl(Tool, Pushit, contains));
        erisEdges.add(new TestEdgeImpl(Tool, Parabola, contains));
        erisEdges.add(new TestEdgeImpl(Disturbed, Mistress, contains));
        erisEdges.add(new TestEdgeImpl(Disturbed, Hell, contains));
        erisEdges.add(new TestEdgeImpl(Disturbed, Ronnie, contains));
        erisEdges.add(new TestEdgeImpl(Barry_Manilow, CouldItBeMagic, contains));

        Node<?>[] tempNodes = new Node<?>[] { SONG_TYPE, CHANNEL_TYPE,
                USER_TYPE, Tool, Disturbed, Barry_Manilow, Pushit,
                FortySixAndTwo, Parabola, Mistress, Hell, CouldItBeMagic, Eris };
        erisNodes = new ArrayList<Node<?>>();
        for (Node<?> node : tempNodes) {
            erisNodes.add(node);
        }

    }

    /**
	 * 
	 */
    private void createMichaelsGraph() {
        michaelsEdges = new ArrayList<Edge<?>>();

        michaelsEdges.add(new TestEdgeImpl(CHANNEL_TYPE, Alan_Parsons_Project,
                                           isA));
        michaelsEdges.add(new TestEdgeImpl(CHANNEL_TYPE, Barry_Manilow, isA));
        michaelsEdges.add(new TestEdgeImpl(USER_TYPE, Michael, isA));
        michaelsEdges.add(new TestEdgeImpl(SONG_TYPE, CouldItBeMagic, isA));

        michaelsEdges.add(new TestEdgeImpl(Michael, Alan_Parsons_Project, owns));
        michaelsEdges.add(new TestEdgeImpl(Michael, Barry_Manilow, owns));

        michaelsEdges.add(new TestEdgeImpl(Barry_Manilow, CouldItBeMagic,
                                           contains));

        Node<?>[] tempNodes = new Node<?>[] { SONG_TYPE, CHANNEL_TYPE,
                USER_TYPE, Alan_Parsons_Project, Barry_Manilow, CouldItBeMagic,
                Michael };

        michaelsNodes = new ArrayList<Node<?>>();
        for (Node<?> node : tempNodes) {
            michaelsNodes.add(node);
        }

    }

    /**
	 * 
	 */
    private void createNodesAndEdges() {
        // node supertypes
        SONG_TYPE = new TestNodeImpl("SONG_TYPE");
        CHANNEL_TYPE = new TestNodeImpl("CHANNEL_TYPE");
        USER_TYPE = new TestNodeImpl("USER_TYPE");

        // channels
        // yes, they look like bands. Ever used Pandora? That's how this works
        Tool = new TestNodeImpl("Tool");
        Disturbed = new TestNodeImpl("Disturbed");
        Alan_Parsons_Project = new TestNodeImpl("Alan Parsons Project");
        Barry_Manilow = new TestNodeImpl("Barry Manilow");

        // Songs
        Pushit = new TestNodeImpl("Pushit");
        FortySixAndTwo = new TestNodeImpl("Forty Six & 2");
        Parabola = new TestNodeImpl("Parabola");
        Mistress = new TestNodeImpl("Mistress");
        Hell = new TestNodeImpl("Hell");
        Ronnie = new TestNodeImpl("Ronnie");
        CouldItBeMagic = new TestNodeImpl("Could It Be Magic");

        // users
        // permanently at odds in the age-old batle of musical tastes
        // also, Manilow can't write an ending for shit
        Michael = new TestNodeImpl("Valentine Michael Smith");
        Eris = new TestNodeImpl("Eris");

    }

    private void printClass(Ruleform r) {
        assertEquals(Product.class, r.getClass());
    }

}
