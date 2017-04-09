/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chiralbehaviors.CoRE.phantasm;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.kernel.phantasm.test.Channel;
import com.chiralbehaviors.CoRE.kernel.phantasm.test.Person;
import com.chiralbehaviors.CoRE.kernel.phantasm.test.Post;
import com.chiralbehaviors.CoRE.kernel.phantasm.test.User;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;

/**
 * @author hparry
 *
 */
public class YourBasicHumblrPhantasmTest extends AbstractModelTest {

	@Before
	public void loadHumblrWorkspace() throws Exception {
		WorkspaceImporter.manifest(this.getClass().getResourceAsStream("/humblr.wsp"), model);

	}

	@Test
	public void testCreatePost() throws Exception {
		User user = model.construct(User.class, ExistentialDomain.Agency, "UserA", "user A");
		Channel firstChannel = model.construct(Channel.class, ExistentialDomain.Product, "myFirstChannel",
				"channel one");
		user.addChannel(firstChannel);
		Post post1 = model.construct(Post.class, ExistentialDomain.Product, "HelloCleveland", "Hello Cleveland!");
		firstChannel.addPost(post1);

		assertEquals(1, firstChannel.getPosts().size());

	}

	@Test
	public void testFollowers() throws Exception {
		User user = model.construct(User.class, ExistentialDomain.Agency, "UserA", "user A");
		Channel firstChannel = model.construct(Channel.class, ExistentialDomain.Product, "myFirstChannel",
				"channel one");
		user.addChannel(firstChannel);
		Post post1 = model.construct(Post.class, ExistentialDomain.Product, "HelloCleveland", "Hello Cleveland!");
		firstChannel.addPost(post1);

		User userB = model.construct(User.class, ExistentialDomain.Agency, "UserB", "user B");
		userB.addFollows(firstChannel);
		model.flush();
		assertEquals(1, userB.getPosts().size());
	}
	
	@Test
	public void testConflictingConstraints() throws Exception {
		User user = model.construct(User.class, ExistentialDomain.Agency, "UserA", "user A");
		Channel firstChannel = model.construct(Channel.class, ExistentialDomain.Product, "myFirstChannel",
				"channel one");
		user.addChannel(firstChannel);
		Post post1 = model.construct(Post.class, ExistentialDomain.Product, "HelloCleveland", "Hello Cleveland!");
		firstChannel.addPost(post1);

		User userB = model.construct(User.class, ExistentialDomain.Agency, "UserB", "user B");
		userB.addFollows(firstChannel);
		model.flush();
		userB.getPosts();
		assertEquals(1, userB.getPosts().size());
		
		Person personB = model.apply(Person.class, userB);
		personB.addPost(post1);
		
	}
	
	@Test
	public void testApplyFacet() throws Exception {
		User user = model.construct(User.class, ExistentialDomain.Agency, "UserA", "user A");
		Person person = model.apply(Person.class, user);
		User userA = person.cast(User.class);
		assertEquals(user.getRuleform(), userA.getRuleform());
	}

}
