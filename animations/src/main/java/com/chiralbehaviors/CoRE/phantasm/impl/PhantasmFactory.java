/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.phantasm.impl;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.PhantasmBase;

/**
 * @author hhildebrand
 *
 */
public class PhantasmFactory {

    public <T extends PhantasmBase<RuleForm>, RuleForm extends ExistentialRuleform<RuleForm, ?>> T acquire(Model model,
                                                                                                           RuleForm ruleform,
                                                                                                           Class<T> phantasm) {
        return null;
    }

    public <T, RuleForm extends ExistentialRuleform<RuleForm, ?>> T construct(Model model,
                                                                              Class<RuleForm> ruleform,
                                                                              String name,
                                                                              String description,
                                                                              Agency updatedBy,
                                                                              Class<T> phantasm,
                                                                              Class<?>... mixins) {
        return null;
    }
}
