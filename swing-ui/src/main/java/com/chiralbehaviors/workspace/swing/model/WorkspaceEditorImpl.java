/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.workspace.swing.model;

import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.workspace.Workspace;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceEditorImpl implements WorkspaceEditor {
    @SuppressWarnings("unused")
    private final Model     model;
    @SuppressWarnings("unused")
    private final Workspace workspace;

    public WorkspaceEditorImpl(Model model, Workspace workspace) {
        this.model = model;
        this.workspace = workspace;
    }
}
