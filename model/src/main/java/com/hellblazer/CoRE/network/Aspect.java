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
package com.hellblazer.CoRE.network;

/**
 * An Aspect is the classification of an networked ruleform within a network.
 * The network relation is A relationship B, where "relationship" is the
 * aspect's classification and "B" is the aspect's classifier.
 * 
 * @author hhildebrand
 * 
 */
public class Aspect<RuleForm extends Networked<RuleForm, ?>> {
    private final Relationship classification;
    private final RuleForm     classifier;

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
