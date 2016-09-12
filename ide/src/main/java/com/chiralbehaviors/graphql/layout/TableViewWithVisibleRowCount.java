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

import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Skin;
import javafx.scene.control.TableView;

/**
 * @author hhildebrand
 *
 */
public class TableViewWithVisibleRowCount<T> extends TableView<T> {

    private IntegerProperty visibleRowCount = new SimpleIntegerProperty(this,
                                                                        "visibleRowCount",
                                                                        10);

    public IntegerProperty visibleRowCountProperty() {
        return visibleRowCount;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TableViewSkinX<>(this);
    }

    /**
     * Skin that respects table's visibleRowCount property.
     */
    public static class TableViewSkinX<T> extends TableViewSkin<T> {

        public TableViewSkinX(TableViewWithVisibleRowCount<T> tableView) {
            super(tableView);
            registerChangeListener(tableView.visibleRowCountProperty(),
                                   "VISIBLE_ROW_COUNT");
            handleControlPropertyChanged("VISIBLE_ROW_COUNT");
        }

        @Override
        protected void handleControlPropertyChanged(String p) {
            super.handleControlPropertyChanged(p);
            if ("VISIBLE_ROW_COUNT".equals(p)) {
                needCellsReconfigured = true;
                getSkinnable().requestFocus();
            }
        }

        /**
         * Returns the visibleRowCount value of the table.
         */
        private int getVisibleRowCount() {
            return ((TableViewWithVisibleRowCount<T>) getSkinnable()).visibleRowCountProperty()
                                                                     .get();
        }

        /**
         * Calculates and returns the pref height of the for the given number of
         * rows.
         * 
         * If flow is of type MyFlow, queries the flow directly otherwise
         * invokes the method.
         */
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

        /**
         * Overridden to compute the sum of the flow height and header
         * prefHeight.
         */
        @Override
        protected double computePrefHeight(double width, double topInset,
                                           double rightInset,
                                           double bottomInset,
                                           double leftInset) {
            // super hard-codes to 400 .. doooh
            double prefHeight = getFlowPrefHeight(getVisibleRowCount());
            return prefHeight + getTableHeaderRow().prefHeight(width);
        }

        /**
         * Reflectively invokes protected getCellLength(i) of flow.
         * 
         * @param index
         *            the index of the cell.
         * @return the cell height of the cell at index.
         */
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
         * Overridden to return custom flow.
         */
        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        protected VirtualFlow createVirtualFlow() {
            return new MyFlow();
        }

        /**
         * Extended to expose length calculation per a given # of rows.
         */
        @SuppressWarnings("rawtypes")
        public static class MyFlow extends VirtualFlow {

            protected double getPrefLength(int rowsPerPage) {
                double sum = 0.0;
                int rows = rowsPerPage; //Math.min(rowsPerPage, getCellCount());
                for (int i = 0; i < rows; i++) {
                    sum += getCellLength(i);
                }
                return sum;
            }

        }

    }
}