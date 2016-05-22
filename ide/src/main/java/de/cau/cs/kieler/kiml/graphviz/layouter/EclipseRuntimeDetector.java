/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2015 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.kiml.graphviz.layouter;

/**
 * Provides a way to check if the Eclipse platform is running or not.
 * 
 * @author Jan Koehnlein
 */
final class EclipseRuntimeDetector {

    /**
     * Private constructor to prevent instantiation.
     */
    private EclipseRuntimeDetector() {
        // Not intended to be instantiated
    }

    /**
     * Checks if the Eclipse platform is running or not. This is basically
     * equivalent to calling {@code Platform.isRunning()}, but doesn't depend on
     * the
     * 
     * @return {@code true} if Eclipse is running, {@code false} otherwise.
     */
    static boolean isEclipseRunning() {
        return false;
    }

}
