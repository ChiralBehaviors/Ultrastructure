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
package com.chiralbehaviors.CoRE.network;

import com.chiralbehaviors.CoRE.ExistentialRuleform;

/**
 * An Aspect is the classification of an networked ruleform within a network.
 * The network relation is A relationship B, where "relationship" is the
 * aspect's classification and "B" is the aspect's classifier.
 * 
 * @author hhildebrand
 * 
 */
public class Aspect<RuleForm extends ExistentialRuleform<RuleForm, ?>> {
	private final Relationship classification;
	private final RuleForm classifier;

	/**
	 * @param classification
	 * @param classifier
	 */
	public Aspect(Relationship classification, RuleForm classifier) {
		this.classifier = classifier;
		this.classification = classification;
	}

	public Relationship getClassification() {
		return classification;
	}

	public RuleForm getClassifier() {
		return classifier;
	}

	@Override
	public String toString() {
		return "Aspect [classification=" + classification + ", classifier="
				+ classifier + "]";
	}
}
