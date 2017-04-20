/**
 * Copyright (c) 2017 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.universal;

import static com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspacePresentation.stripQuotes;

import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.universal.spa.SpaBaseListener;
import com.chiralbehaviors.CoRE.universal.spa.SpaParser.ActionContext;
import com.chiralbehaviors.CoRE.universal.spa.SpaParser.CreateContext;
import com.chiralbehaviors.CoRE.universal.spa.SpaParser.DeleteContext;
import com.chiralbehaviors.CoRE.universal.spa.SpaParser.FieldActionContext;
import com.chiralbehaviors.CoRE.universal.spa.SpaParser.LaunchContext;
import com.chiralbehaviors.CoRE.universal.spa.SpaParser.NavigateContext;
import com.chiralbehaviors.CoRE.universal.spa.SpaParser.PageContext;
import com.chiralbehaviors.CoRE.universal.spa.SpaParser.RouteContext;
import com.chiralbehaviors.CoRE.universal.spa.SpaParser.SpaContext;
import com.chiralbehaviors.CoRE.universal.spa.SpaParser.UpdateContext;

/**
 * @author halhildebrand
 *
 */
public class SpaImporter extends SpaBaseListener {
    private String    currentField;
    private Page      currentPage;
    private final Spa spa = new Spa();

    @Override
    public void enterCreate(CreateContext ctx) {
        Action action = build(ctx.action());
        currentPage.create(currentField, action);
    }

    @Override
    public void enterDelete(DeleteContext ctx) {
        Action action = build(ctx.action());
        currentPage.delete(currentField, action);
    }

    @Override
    public void enterFieldAction(FieldActionContext ctx) {
        currentField = ctx.NAME()
                          .getText();
    }

    @Override
    public void enterLaunch(LaunchContext ctx) {
        Launch launch = new Launch();
        if (ctx.frame() == null && ctx.frameBy() != null) {
            launch.setFrameBy(ctx.frameBy()
                                 .Spath()
                                 .getText());
        } else if (ctx.frame() != null) {
            launch.setFrame(ctx.frame()
                               .getText());
        }
        if (ctx.launchBy() == null) {
            launch.setImmediate(ctx.UUID()
                                   .getText());
        } else {
            launch.setLaunchBy(ctx.launchBy()
                                  .Spath()
                                  .getText());
        }
    }

    @Override
    public void enterNavigate(NavigateContext ctx) {
        Route route = new Route();
        route.setPath(ctx.NAME()
                         .getText());
        if (ctx.frameBy() != null) {
            route.setFrameBy(ctx.frameBy()
                                .Spath()
                                .getText());
        }
        if (ctx.extract() != null) {
            route.setExtract(ctx.extract()
                                .extraction()
                                .stream()
                                .collect(Collectors.toMap(k -> k.NAME()
                                                                .getText(),
                                                          v -> v.Spath()
                                                                .getText())));
        }
        super.enterNavigate(ctx);
    }

    @Override
    public void enterPage(PageContext ctx) {
        currentPage = new Page();
        currentPage.setTitle(ctx.title()
                                .getText());
        if (ctx.description() != null) {
            currentPage.setDescription(stripQuotes(ctx.description()
                                                      .StringValue()
                                                      .getText()));
        }
        if (ctx.frame() != null) {
            currentPage.setFrame(ctx.frame()
                                    .getText());
        }
    }

    @Override
    public void enterSpa(SpaContext ctx) {
        spa.setName(stripQuotes(ctx.name()
                                   .StringValue()
                                   .getText()));
        spa.setDescription(stripQuotes(ctx.description()
                                          .StringValue()
                                          .getText()));
        spa.setRoot(ctx.root()
                       .getText());
    }

    @Override
    public void enterUpdate(UpdateContext ctx) {
        Action action = build(ctx.action());
        currentPage.update(currentField, action);
    }

    @Override
    public void exitRoute(RouteContext ctx) {
        spa.route(ctx.NAME()
                     .getText(),
                  currentPage);
        currentPage = null;
    }

    public Spa getSpa() {
        return spa;
    }

    private Action build(ActionContext ac) {
        Action action = new Action();
        if (ac.frameBy() != null) {
            action.setFrameBy(ac.frameBy()
                                .Spath()
                                .getText());
        }
        if (ac.extract() != null) {
            action.setExtract(ac.extract()
                                .extraction()
                                .stream()
                                .collect(Collectors.toMap(k -> k.NAME()
                                                                .getText(),
                                                          v -> v.Spath()
                                                                .getText())));
        }
        return action;
    }
}
