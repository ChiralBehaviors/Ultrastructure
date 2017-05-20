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

import java.io.IOException;
import java.io.InputStream;

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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hellblazer.utils.Utils;

/**
 * @author halhildebrand
 *
 */
public class SpaImporter extends SpaBaseListener {
    static String stripQuotes(String original) {
        return original.substring(1, original.length() - 1);
    }

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
    public void exitNavigate(NavigateContext ctx) {
        Action action = build(ctx);
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
        if (ctx.Spath() == null) {
            launch.setImmediate(ctx.UUID()
                                   .getText());
        } else {
            launch.setLaunchBy(ctx.Spath()
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
            ObjectNode extract = JsonNodeFactory.instance.objectNode();
            ctx.extract()
               .extraction()
               .forEach(c -> extract.put(c.NAME()
                                          .getText(),
                                         c.Spath()
                                          .getText()));
            route.setExtract(extract);
        }
        currentPage.navigate(currentField, route);
        super.enterNavigate(ctx);
    }

    @Override
    public void enterPage(PageContext ctx) {
        currentPage = new Page();
        currentPage.setQuery(getResource(stripQuotes(ctx.query()
                                                        .ResourcePath()
                                                        .getText())));
        currentPage.setName(stripQuotes(ctx.name()
                                           .StringValue()
                                           .getText()));
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
                       .NAME()
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
            ObjectNode extract = JsonNodeFactory.instance.objectNode();
            ac.extract()
              .extraction()
              .forEach(c -> extract.put(c.NAME()
                                         .getText(),
                                        c.Spath()
                                         .getText()));
            action.setExtract(extract);
        }
        action.setQuery(getResource(stripQuotes(ac.query()
                                                  .ResourcePath()
                                                  .getText())));
        return action;
    }

    private Action build(NavigateContext nc) {
        Action action = new Action();
        if (nc.frameBy() != null) {
            action.setFrameBy(nc.frameBy()
                                .Spath()
                                .getText());
        }
        if (nc.extract() != null) {
            ObjectNode extract = JsonNodeFactory.instance.objectNode();
            nc.extract()
              .extraction()
              .forEach(c -> extract.put(c.NAME()
                                         .getText(),
                                        c.Spath()
                                         .getText()));
            action.setExtract(extract);
        }
        return action;
    }

    private String getResource(String path) {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            throw new IllegalStateException(String.format("Invalid resource: %s",
                                                          path));
        }
        try {
            return Utils.getDocument(is);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Cannot read resource: %s",
                                                          path),
                                            e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
    }
}
