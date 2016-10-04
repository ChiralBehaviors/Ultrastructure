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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.sun.javafx.scene.control.skin.ListViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;

/**
 * @author hhildebrand
 *
 */
public class ListViewWithVisibleRowCount<T> extends ListView<T> {

    public static class ListViewSkinX<T> extends ListViewSkin<T> {
        public static class MyFlow<M extends IndexedCell<?>>
                extends VirtualFlow<M> {

            protected double getPrefLength(int rowsPerPage) {
                double sum = 0.0;
                int rows = rowsPerPage; //Math.min(rowsPerPage, getCellCount());
                for (int i = 0; i < rows; i++) {
                    sum += getCellLength(i);
                }
                return sum;
            }

        }

        public ListViewSkinX(ListViewWithVisibleRowCount<T> listView) {
            super(listView);
            registerChangeListener(listView.visibleRowCountProperty(),
                                   "VISIBLE_ROW_COUNT");
            handleControlPropertyChanged("VISIBLE_ROW_COUNT");
        }

        @Override
        protected double computePrefHeight(double width, double topInset,
                                           double rightInset,
                                           double bottomInset,
                                           double leftInset) {
            // super hard-codes to 400 .. doooh
            return getFlowPrefHeight(getVisibleRowCount());
        }

        @Override
        protected VirtualFlow<ListCell<T>> createVirtualFlow() {
            return new MyFlow<>();
        }

        @SuppressWarnings("rawtypes")
        protected double getFlowPrefHeight(int rows) {
            double height = 0;
            if (flow instanceof MyFlow) {
                height = ((MyFlow) flow).getPrefLength(rows);
            } else {
                for (int i = 0; i < rows && i < getItemCount(); i++) {
                    height += invokeFlowCellLength(i);
                }
            }
            return height + snappedTopInset() + snappedBottomInset();

        }

        @Override
        protected void handleControlPropertyChanged(String p) {
            super.handleControlPropertyChanged(p);
            if ("VISIBLE_ROW_COUNT".equals(p)) { 
                getSkinnable().refresh();
            }
        }

        protected double invokeFlowCellLength(int index) {
            double height = 1.0;
            Class<?> clazz = VirtualFlow.class;
            try {
                Method method = clazz.getDeclaredMethod("getCellLength",
                                                        Integer.TYPE);
                method.setAccessible(true);
                return ((double) method.invoke(flow, index));
            } catch (NoSuchMethodException | SecurityException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                e.printStackTrace();
            }
            return height;
        }

        /**
         * Returns the visibleRowCount value of the table.
         */
        private int getVisibleRowCount() {
            return ((ListViewWithVisibleRowCount<T>) getSkinnable()).visibleRowCountProperty()
                                                                    .get();
        }
    }

    private IntegerProperty visibleRowCount = new SimpleIntegerProperty(this,
                                                                        "visibleRowCount",
                                                                        10);

    public IntegerProperty visibleRowCountProperty() {
        return visibleRowCount;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ListViewSkinX<>(this);
    }
}
