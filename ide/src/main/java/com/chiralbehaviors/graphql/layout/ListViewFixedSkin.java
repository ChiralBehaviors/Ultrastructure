package com.chiralbehaviors.graphql.layout;

import java.util.Set;

import com.sun.javafx.scene.control.skin.ListViewSkin;

import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Region;

public class ListViewFixedSkin<T> extends ListViewSkin<T> {
    // <editor-fold defaultstate="collapsed" desc="Fields">
    private ListViewFixed<T> listView;
    private ScrollBar        scrollBarHorizontal;
    private ScrollBar        scrollBarVertical;
    private boolean          fillWidthCache;
    private double           prefWidthCache;
    private Region           placeholderRegion;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructors">
    public ListViewFixedSkin(ListViewFixed<T> listView) {
        super(listView);

        this.listView = listView;

        registerChangeListener(listView.fillWidthProperty(), "FILL_WIDTH");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods">
    private void updateFillWidth() {
        if (scrollBarHorizontal != null && scrollBarVertical != null
            && fillWidthCache != listView.isFillWidth()) {
            if (listView.isFillWidth() && !fillWidthCache) {
                scrollBarHorizontal.visibleProperty()
                                   .addListener(this::updateCellsPrefWidth);
                scrollBarVertical.visibleProperty()
                                 .addListener(this::updateCellsPrefWidth);
            } else {
                scrollBarHorizontal.visibleProperty()
                                   .removeListener(this::updateCellsPrefWidth);
                scrollBarVertical.visibleProperty()
                                 .removeListener(this::updateCellsPrefWidth);
            }

            fillWidthCache = listView.isFillWidth();
        }
    }

    private void updateCellsPrefWidth(Observable o) {
        final Insets insets = getSkinnable().getInsets();
        final double prefWidth = getSkinnable().getWidth() + insets.getLeft()
                                 + insets.getRight()
                                 - scrollBarVertical.getWidth();

        if (prefWidth != prefWidthCache) {
            for (int i = 0; i < flow.getCellCount(); i++) {
                final IndexedCell<T> cell = flow.getCell(i);

                if (!cell.isEmpty()) {
                    cell.setPrefWidth(prefWidth);
                }
            }

            prefWidthCache = prefWidth;
        }
    }

    private boolean showingPlaceHolder() {
        checkState();

        if (getItemCount() == 0) {
            if (placeholderRegion == null) {
                updatePlaceholderRegionVisibility();

                final Object obj = getChildren().get(getChildren().size() - 1);
                if (obj instanceof Node && ((Region) obj).getStyleClass()
                                                         .contains("placeholder")) {
                    placeholderRegion = (Region) obj;
                }
            }

            if (placeholderRegion != null) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if ("FILL_WIDTH".equals(p)) {
            updateFillWidth();
        }
    }

    @Override
    protected double computePrefHeight(double width, double topInset,
                                       double rightInset, double bottomInset,
                                       double leftInset) {
        if (showingPlaceHolder()) {
            return super.computePrefHeight(width, topInset, rightInset,
                                           bottomInset, leftInset);
        } else {
            double computedHeight = topInset + bottomInset;

            for (int i = 0; i < flow.getCellCount(); i++) {
                final IndexedCell<T> cell = flow.getCell(i);

                if (!cell.isEmpty()) {
                    computedHeight += cell.getHeight();
                }
            }

            return computedHeight;
        }
    }

    @Override
    protected double computePrefWidth(double height, double topInset,
                                      double rightInset, double bottomInset,
                                      double leftInset) {
        double computedWidth = 0;

        if (showingPlaceHolder()) {
            computedWidth += placeholderRegion.getLayoutBounds()
                                              .getWidth();
        } else {
            for (int i = 0; i < flow.getCellCount(); i++) {
                final IndexedCell<T> cell = flow.getCell(i);

                if (!cell.isEmpty() && cell.getWidth() > computedWidth) {
                    computedWidth = cell.getWidth();
                }
            }

            if (scrollBarVertical != null && scrollBarVertical.isVisible()) {
                computedWidth += scrollBarVertical.getWidth();
            }
        }

        if (computedWidth != 0) {
            return computedWidth + leftInset + rightInset;
        } else {
            return super.computePrefWidth(height, topInset, rightInset,
                                          bottomInset, leftInset);
        }
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);

        if (scrollBarHorizontal == null || scrollBarVertical == null) {
            final Set<Node> nodes = getSkinnable().lookupAll(".scroll-bar");

            nodes.stream()
                 .forEach((node) -> {
                     if (node instanceof ScrollBar) {
                         final ScrollBar scrollBar = (ScrollBar) node;

                         if (scrollBar.getOrientation() == Orientation.HORIZONTAL) {
                             scrollBarHorizontal = scrollBar;
                         } else {
                             scrollBarVertical = scrollBar;
                         }
                     }
                 });

            updateFillWidth();
        }
    }

    @Override
    public void dispose() {
        if (fillWidthCache) {
            scrollBarHorizontal.visibleProperty()
                               .removeListener(this::updateCellsPrefWidth);
            scrollBarVertical.visibleProperty()
                             .removeListener(this::updateCellsPrefWidth);
        }

        listView = null;

        super.dispose();
    }
    // </editor-fold>
}