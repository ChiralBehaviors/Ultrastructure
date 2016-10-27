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

package com.chiralbehaviors.graphql.layout.explorer;

public class QueryState {
    private String data;
    private String operationName;
    private String query;
    private String selection;
    private String targetURL;
    private String variables;

    public QueryState() {
    }

    public QueryState(QueryState state) {
        targetURL = state.getTargetURL();
        query = state.getQuery();
        variables = state.getVariables();
        operationName = state.getOperationName();
        selection = state.getSelection();
        data = state.getData();
    }

    public QueryState(String targetURL, String query, String variables,
                      String operationName, String source, String data) {
        this.targetURL = targetURL;
        this.query = query;
        this.variables = variables;
        this.operationName = operationName;
        this.selection = source;
        this.data = data;
    }

    public void clear() {
        targetURL = null;
        query = null;
        variables = null;
        operationName = null;
        selection = null;
        data = null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        QueryState other = (QueryState) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        if (operationName == null) {
            if (other.operationName != null)
                return false;
        } else if (!operationName.equals(other.operationName))
            return false;
        if (query == null) {
            if (other.query != null)
                return false;
        } else if (!query.equals(other.query))
            return false;
        if (selection == null) {
            if (other.selection != null)
                return false;
        } else if (!selection.equals(other.selection))
            return false;
        if (targetURL == null) {
            if (other.targetURL != null)
                return false;
        } else if (!targetURL.equals(other.targetURL))
            return false;
        if (variables == null) {
            if (other.variables != null)
                return false;
        } else if (!variables.equals(other.variables))
            return false;
        return true;
    }

    public String getData() {
        return data;
    }

    public String getOperationName() {
        return operationName;
    }

    public String getQuery() {
        return query;
    }

    public String getSelection() {
        return selection;
    }

    public String getTargetURL() {
        return targetURL;
    }

    public String getVariables() {
        return variables;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result
                 + ((operationName == null) ? 0 : operationName.hashCode());
        result = prime * result + ((query == null) ? 0 : query.hashCode());
        result = prime * result + ((selection == null) ? 0 : selection.hashCode());
        result = prime * result
                 + ((targetURL == null) ? 0 : targetURL.hashCode());
        result = prime * result
                 + ((variables == null) ? 0 : variables.hashCode());
        return result;
    }

    public void initializeFrom(QueryState state) {
        targetURL = state.getTargetURL();
        query = state.getQuery();
        variables = state.getVariables();
        operationName = state.getOperationName();
        selection = state.getSelection();
        data = state.getData();
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setSelection(String source) {
        this.selection = source;
    }

    public void setTargetURL(String targetURL) {
        this.targetURL = targetURL;
    }

    public void setVariables(String variables) {
        this.variables = variables;
    }

    @Override
    public String toString() {
        return String.format("QueryState [data=%s, operationName=%s, query=%s, selection=%s, targetURL=%s, variables=%s]",
                             data, operationName, query, selection, targetURL,
                             variables);
    }
}