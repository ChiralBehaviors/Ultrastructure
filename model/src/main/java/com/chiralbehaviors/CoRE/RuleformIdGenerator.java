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
package com.chiralbehaviors.CoRE;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;

/**
 * @author hhildebrand
 * 
 */
public class RuleformIdGenerator extends ObjectIdGenerator<String> {

	private static final long serialVersionUID = 1L;
	private final Class<? extends Ruleform> scope;

	public RuleformIdGenerator() {
		this(Ruleform.class);
	}

	/**
	 * @param scope
	 */
	public RuleformIdGenerator(Class<? extends Ruleform> scope) {
		super();
		this.scope = scope;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fasterxml.jackson.annotation.ObjectIdGenerator#canUseFor(com.fasterxml
	 * .jackson.annotation.ObjectIdGenerator)
	 */
	@Override
	public boolean canUseFor(ObjectIdGenerator<?> gen) {
		return gen.getClass() == getClass() && gen.getScope() == scope;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fasterxml.jackson.annotation.ObjectIdGenerator#forScope(java.lang
	 * .Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ObjectIdGenerator<String> forScope(Class<?> scope) {
		return this.scope == scope ? this : new RuleformIdGenerator(
				(Class<? extends Ruleform>) scope);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fasterxml.jackson.annotation.ObjectIdGenerator#generateId(java.lang
	 * .Object)
	 */
	@Override
	public String generateId(Object forPojo) {
		return String.format("%s-%s", forPojo.getClass().getSimpleName(),
				((Ruleform) forPojo).getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fasterxml.jackson.annotation.ObjectIdGenerator#getScope()
	 */
	@Override
	public Class<?> getScope() {
		return scope;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fasterxml.jackson.annotation.ObjectIdGenerator#key(java.lang.Object)
	 */
	@Override
	public IdKey key(Object key) {
		return new IdKey(getClass(), scope, key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fasterxml.jackson.annotation.ObjectIdGenerator#newForSerialization
	 * (java.lang.Object)
	 */
	@Override
	public ObjectIdGenerator<String> newForSerialization(Object context) {
		return new RuleformIdGenerator(scope);
	}
}
