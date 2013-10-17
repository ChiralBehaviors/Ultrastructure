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

package com.hellblazer.CoRE.meta.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.hellblazer.CoRE.meta.graph.impl.Graph;

/**
 * @author hparry
 * 
 */
public class NetworkGraphTest {

	private TestNodeImpl SONG_TYPE;
	private TestNodeImpl CHANNEL_TYPE;
	private TestNodeImpl USER_TYPE;

	private TestNodeImpl Tool;
	private TestNodeImpl Disturbed;
	private TestNodeImpl Alan_Parsons_Project;
	private TestNodeImpl Barry_Manilow;

	private TestNodeImpl Pushit;
	private TestNodeImpl FortySixAndTwo;
	private TestNodeImpl Parabola;
	private TestNodeImpl Mistress;
	private TestNodeImpl Hell;
	private TestNodeImpl DontTreadOnMe;
	private TestNodeImpl Ronnie;
	private TestNodeImpl CouldItBeMagic;

	private TestNodeImpl Michael;
	private TestNodeImpl Eris;

	private static final String isA = "is a";
	private static final String contains = "contains";
	private static final String owns = "owns";

	private LinkedList<INode<?>> erisNodes;
	private LinkedList<IEdge<?>> erisEdges;
	
	private LinkedList<INode<?>> michaelsNodes;
	private LinkedList<IEdge<?>> michaelsEdges;


	 @Test
	 public void testUnion() throws CloneNotSupportedException {
		 Graph erisGraph = new Graph(erisNodes, erisEdges);
		 Graph michaelsGraph = new Graph(michaelsNodes, michaelsEdges);
		 erisGraph.union(michaelsGraph);
		 assertTrue(erisGraph.getNodes().contains(CouldItBeMagic));
		 assertTrue(erisGraph.getNodes().contains(Parabola));
		 assertTrue(erisGraph.getNodes().contains(Alan_Parsons_Project));
	
	 }
	 
	 @Test
	 public void testEquals() {
		 TestNodeImpl magic2 = new TestNodeImpl("Could It Be Magic");
		 assertEquals(CouldItBeMagic, magic2);
		 
		 List<INode<?>> list = new LinkedList<INode<?>>();
		 list.add(CouldItBeMagic);
		 assertTrue(list.contains(CouldItBeMagic));
		 assertTrue(list.contains(magic2));
	 }
	
	 @Test
	 public void testIntersection() throws CloneNotSupportedException {
		 Graph erisGraph = new Graph(erisNodes, erisEdges);
		 Graph michaelsGraph = new Graph(michaelsNodes, michaelsEdges);
		 erisGraph.intersection(michaelsGraph);
		 assertTrue(erisGraph.getNodes().contains(CouldItBeMagic));
		 assertFalse(erisGraph.getNodes().contains(Parabola));
		 assertFalse(erisGraph.getNodes().contains(Alan_Parsons_Project));
	 }
	
	@Before
	public void setup() {
		createNodesAndEdges();
		createMichaelsGraph();
		createErisGraph();
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
		DontTreadOnMe = new TestNodeImpl("Don't Tread On Me");
		Ronnie = new TestNodeImpl("Ronnie");
		CouldItBeMagic = new TestNodeImpl("Could It Be Magic");

		// users
		// permanently at odds in the age-old batle of musical tastes
		// also, Manilow can't write an ending for shit
		Michael = new TestNodeImpl("Valentine Michael Smith");
		Eris = new TestNodeImpl("Eris");

	}

	/**
	 * Eris, goddess of discord, instigator of the Trojan war, and lover of
	 * double bass pedals.
	 */
	private void createErisGraph() {
		erisEdges = new LinkedList<IEdge<?>>();

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

		INode<?>[] tempNodes = new INode<?>[]{ SONG_TYPE, CHANNEL_TYPE, USER_TYPE, Tool,
				Disturbed, Barry_Manilow, Pushit, FortySixAndTwo, Parabola,
				Mistress, Hell, CouldItBeMagic, Eris };
		erisNodes = new LinkedList<INode<?>>();
		for (INode<?> node : tempNodes) {
			erisNodes.add(node);
		}

	}
	
	/**
	 * 
	 */
	private void createMichaelsGraph() {
		michaelsEdges = new LinkedList<IEdge<?>>();

		michaelsEdges.add(new TestEdgeImpl(CHANNEL_TYPE, Alan_Parsons_Project, isA));
		michaelsEdges.add(new TestEdgeImpl(CHANNEL_TYPE, Barry_Manilow, isA));
		michaelsEdges.add(new TestEdgeImpl(USER_TYPE, Michael, isA));
		michaelsEdges.add(new TestEdgeImpl(SONG_TYPE, CouldItBeMagic, isA));
		
		michaelsEdges.add(new TestEdgeImpl(Michael, Alan_Parsons_Project, owns));
		michaelsEdges.add(new TestEdgeImpl(Michael, Barry_Manilow, owns));

		michaelsEdges.add(new TestEdgeImpl(Barry_Manilow, CouldItBeMagic, contains));

		INode<?>[] tempNodes = new INode<?>[]{ SONG_TYPE, CHANNEL_TYPE, USER_TYPE,
				Alan_Parsons_Project, Barry_Manilow, CouldItBeMagic, Michael };
		
		michaelsNodes = new LinkedList<INode<?>>();
		for (INode<?> node : tempNodes) {
			michaelsNodes.add(node);
		}
		
	}

}
