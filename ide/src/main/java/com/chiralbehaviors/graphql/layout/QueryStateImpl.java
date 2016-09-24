/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.graphql.layout;

public class QueryStateImpl implements QueryState {
    private String data;
    private String operationName;
    private String query;
    private String source;
    private String targetURL;

    private String variables;

    @Override
    public String getData() {
        return data;
    }
    @Override
    public String getOperationName() {
        return operationName;
    }

    @Override
    public String getQuery() {
        return query;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public String getTargetURL() {
        return targetURL;
    }

    @Override
    public String getVariables() {
        return variables;
    }

    @Override
    public void setData(String data) {
        this.data = data;
    }

    @Override
    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    @Override
    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public void setTargetURL(String targetURL) {
        this.targetURL = targetURL;
    }

    @Override
    public void setVariables(String variables) {
        this.variables = variables;
    }

}