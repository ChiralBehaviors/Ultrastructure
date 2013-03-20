package com.hellblazer.CoRE.location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.collections.iterators.ReverseListIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hellblazer.CoRE.coordinate.Coordinate;
import com.hellblazer.CoRE.coordinate.CoordinateAttribute;
import com.hellblazer.CoRE.coordinate.CoordinateBundle;
import com.hellblazer.CoRE.coordinate.CoordinateKind;
import com.hellblazer.CoRE.coordinate.CoordinateKindDefinition;
import com.hellblazer.CoRE.coordinate.IncorrectCoordinateKindDefinition;
import com.hellblazer.CoRE.entity.Entity;
import com.hellblazer.CoRE.entity.EntityLocationNetwork;

/**
 * Convenience class used to facilitate the calculation of nested location
 * information. Wraps several bits of information that needs to be passed around
 * in the nested location calculation (basically because Java doesn't allow
 * multiple return values).
 * 
 * @author hhildebrand
 * 
 */
public class LocationCalculator {
    private static class LocationInformation {

        /**
         * Stores all the CoordinateAttribute rules that are accumulated in the
         * process of computing nested locations.
         */
        private LinkedList<CoordinateAttribute> accumulatedCoordinates = new LinkedList<CoordinateAttribute>();

        /**
         * The current Coordinate being processed.
         */
        private final Coordinate                baseCoordinate;

        /**
         * The contextual Entity to which <code>baseCoordinate</code> is
         * relative.
         */
        private final Entity                    currentContext;

        /**
         * The index in <code>definitions</code> at which the current definition
         * rule the calculation is working on is located.
         */
        private final int                       definitionIndex;                                               // Defaults to 0

        /**
         * Constructor sets all pertinent information. This should be the one
         * used the first time through a recursion, since it does not take
         * accumulated coordinates as a parameter. For that, see
         * {@link #LocationCalculator(Coordinate, Entity, int, List)}.
         * 
         * @param baseCoordinate
         *            the Coordinate being nested
         * @param currentContext
         *            the Entity that <code>base</code> is relative to
         * @param definitionIndex
         *            the index of the next definition rule to use
         */
        public LocationInformation(Coordinate baseCoordinate,
                                   Entity currentContext, int definitionIndex) {
            this.baseCoordinate = baseCoordinate;
            this.currentContext = currentContext;
            this.definitionIndex = definitionIndex;
        }

        /**
         * Copies accumulated coordinates in addition to setting other pertinent
         * information. This constructor should be used in deeper recursions to
         * allow the passing on of accumulated coordinates to deeper levels.
         * 
         * @param baseCoordinate
         *            the Coordinate being nested
         * @param currentContext
         *            the Entity that <code>base</code> is relative to
         * @param definitionIndex
         *            the index of the next definition rule to use
         * @param accumulatedCoordinates
         *            all coordinate information accumulated so far. The
         *            contents of this list are copied into a new List, and in
         *            the same order.
         */
        public LocationInformation(Coordinate baseCoordinate,
                                   Entity currentContext,
                                   int definitionIndex,
                                   List<CoordinateAttribute> accumulatedCoordinates) {
            this(baseCoordinate, currentContext, definitionIndex);

            for (CoordinateAttribute ca : accumulatedCoordinates) {
                accumulatedCoordinates.add(ca);
            }
        }

        /**
         * Adds all the CoordinateAttribute rules from <code>attributes</code>
         * to the accumulated CoordinateAttribute rules. Items from
         * <code>attributes</code> are added to the front of the list. So, if
         * you have already accumulated a list like <code>( A B C )</code>, and
         * you want to add a list <code>( D E )</code>, you will end up with
         * <code>( D E A B C )</code>.
         * 
         * @param attributes
         *            the rules to be added
         */
        @SuppressWarnings("unchecked")
        public void addCoordinates(List<CoordinateAttribute> attributes) {
            for (Iterator<CoordinateAttribute> i = new ReverseListIterator(
                                                                           attributes); i.hasNext();) {
                accumulatedCoordinates.addFirst(i.next());
            }
        }

        public List<CoordinateAttribute> getAccumulatedCoordinates() {
            return accumulatedCoordinates;
        }

        public Coordinate getBaseCoordinate() {
            return baseCoordinate;
        }

        public Entity getCurrentContext() {
            return currentContext;
        }

        public int getDefinitionIndex() {
            return definitionIndex;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            String format = "Info[ baseCoordinate='%s' currentContext='%s' numberAccumulatedCoordinates='%s', currentRule='%s']";
            return String.format(format, baseCoordinate, currentContext,
                                 accumulatedCoordinates.size(), definitionIndex);
        }
    }

    private static final Logger            LOG = LoggerFactory.getLogger(LocationCalculator.class);

    private List<CoordinateBundle>         coordinates;

    /**
     * The rules that comprise the definition of the kind of coordinates you
     * want to calculate. This is an instance variable because the recursive
     * calculations need to be able to access this information at all levels,
     * and carrying around through each invocation was judged "ugly".
     * 
     * It gets (re-)initialized each time that
     * {@link #accumulateCoordinates(LocationCalculator.LocationInformation)} is
     * called.
     */
    private List<CoordinateKindDefinition> definitions;

    /**
     * The original CoordinateKind passed in to the calculation. Should be a
     * valid top-level CoordinateKind.
     */
    private CoordinateKind                 originalKind;

    /**
     * Retrieves the fully-specified coordinates of a given kind for a given
     * Entity.
     * 
     * @param entity
     *            the Entity you want the location of
     * @param kind
     *            the kind of locations you want
     * @return a List of Lists of CoordinateAttributes, since a Entity may have
     *         multiple locations of a specified kind (e.g. many gene copies in
     *         a genome). We return lists of CoordinateAttributes and not
     *         Coordinates, since we use Coordinates to represent very granular
     *         concepts. For example, we don't have full-blown genomic
     *         coordinates for everything (consisting of Chromosome, Strand,
     *         Start, Stop), but rather a particular gene may have a Linear
     *         Range-type of Coordinate (consisting of only Start and Stop)
     *         relative to a strand of a chromosome. This method reads rules in
     *         the database to build up complete, top-level locations. Since
     *         storing both granular and high-level information in the database
     *         would be redundant, we just pass back the CoordinateAttribute
     *         information that effectively refers to no Coordinate. The
     *         attribute and value of the CoordinateAttributes is the key
     *         information.
     * 
     * @throws IncorrectCoordinateKindDefinition
     *             if <code>kind</code> is not defined completely in terms of
     *             subordinate CoordinateKinds
     */
    public List<CoordinateBundle> getFullCoordinates(EntityManager em,
                                                     Entity entity,
                                                     CoordinateKind kind) {

        // We need to set up the "global" information for the calculation each time we run it
        initialize(kind);

        List<EntityLocationNetwork> locationRules = new ArrayList<EntityLocationNetwork>();

        /*
         * This is handy because it lets us use the same CoordinateKind to
         * resolve locations for Entities at various levels. For example, if
         * a Genomic Coordinate is defined in terms of Genome, Chromosome,
         * Strand, and Linear Range, then we can re-use it for Entities
         * representing specific chromosomes or strands. They won't have a
         * Linear Range coordinate, so we can skip over that and move
         * progressively through the definition rules until we find something
         * that sticks.
         */
        int index = 0;
        for (; locationRules.isEmpty() && index < definitions.size(); index++) {
            // This is the kind of coordinate we need to retrieve first
            CoordinateKind firstOne = definitions.get(index).getSubordinateCoordinateKind();

            /*
             * Now we'll find all the location rules that pertain to this
             * Entity and have coordinates of the desired type.
             */
            locationRules = entity.getLocationRules(em, firstOne);
        }

        /*
         * If we get through all the definition rules and still don't find
         * anything, this for loop gets skipped anyway.
         */
        for (EntityLocationNetwork rule : locationRules) {
            /*
             * We now set up the information needed to recursively compute all
             * the nested location information. We start with the current
             * Coordinate, also noting what Entity it is relative to. The
             * index of the next definition rule to examine is carried along to
             * determine when the recursion can stop.
             */
            LocationInformation info = new LocationInformation(
                                                               rule.getCoordinate(),
                                                               rule.getContextualEntity(),
                                                               index);
            List<LocationInformation> finalInfo = accumulateCoordinates(em,
                                                                        info);
            for (LocationInformation i : finalInfo) {
                coordinates.add(new CoordinateBundle(
                                                     i.getAccumulatedCoordinates(),
                                                     i.getCurrentContext()));
            }
        }

        return coordinates;
    }

    /**
     * Recursively accumulates nested coordinate information.
     * 
     * @param info
     *            the LocationInformation object containing the information
     *            needed for the current invocation
     * @return a List of LocationInformation objects representing all nested
     *         coordinates below the current level
     */
    private List<LocationInformation> accumulateCoordinates(EntityManager em,
                                                            LocationInformation info) {

        // This will collect the information gathered from recursive invocations of this method
        List<LocationInformation> infos = new ArrayList<LocationInformation>();

        LOG.info(String.format("AccumulatingCoordinates for %s", info));
        if (info.getBaseCoordinate().getKind().getNestable()) {
            LOG.debug(String.format("Nestable for %s", info));

            List<EntityLocationNetwork> locationRules = info.getCurrentContext().getLocationRules(em,
                                                                                                  info.getBaseCoordinate().getKind());

            if (!locationRules.isEmpty()) {
                // we can still do some nesting
                for (EntityLocationNetwork rule : locationRules) {

                    /*
                     * Here's where the magic happens. We take the base
                     * coordinate and another coordinate that it is nested
                     * inside to calculate a new coordinate that is the same as
                     * the base coordinate, but relative to the context of the
                     * other coordinate.
                     */
                    Coordinate newCoordinate = info.getBaseCoordinate().nestCoordinates(em,
                                                                                        rule.getCoordinate());

                    /*
                     * Assemble the new information and iterate again.
                     * 
                     * We're not changing the definition list index since we
                     * know we're still nesting, and shouldn't advance to the
                     * next kind of coordinate in the definition list.
                     * 
                     * We also pass along the coordinate information we've
                     * accumulated so far.
                     */
                    LocationInformation newInfo = new LocationInformation(
                                                                          newCoordinate,
                                                                          rule.getContextualEntity(),
                                                                          info.getDefinitionIndex(),
                                                                          info.getAccumulatedCoordinates());

                    // Time to recur and save the results
                    infos.addAll(accumulateCoordinates(em, newInfo));
                }

                return infos;
            }
        } // end nestable test

        /*
         * At this point, we know that this isn't a nestable kind of coordinate,
         * or that there is no further nesting possible for this kind of
         * coordinate. Thus, we can add the attributes of this coordinate to the
         * ones accumulated so far.
         * 
         * Technically speaking, we could just pass
         * "info.getBaseCoordinate().getAttributes()" to info.addCoordinates,
         * but we need to call the DAO method in order to ensure that they are
         * ordered properly. See the JavaDoc for
         * CoordinateDAO#getOrderedAttributes(Coordinate) for more information.
         * 
         */
        info.addCoordinates(info.getBaseCoordinate().getOrderedAttributes(em));

        /*
         * Now we find the next set(s) of coordinates for current context
         * Entity.
         * 
         * We need to make sure we're not trying to access beyond the end of our
         * definition list.
         */
        List<EntityLocationNetwork> newRules = new ArrayList<EntityLocationNetwork>();
        if (info.getDefinitionIndex() != definitions.size()) {
            CoordinateKind kind = definitions.get(info.getDefinitionIndex()).getSubordinateCoordinateKind();

            Entity currentContext = info.getCurrentContext();
            LOG.debug(String.format("Fetching rules for %s and %s",
                                    currentContext.getName(), kind.getName()));
            newRules = currentContext.getLocationRules(em, kind);
        }

        /*
         * If we've already processed all the kinds of coordinates in our
         * definitions list, or there are no available EntityLocationNetwork
         * rules to process, we can quit. Otherwise, we've got more recurrences
         * to make.
         */
        // TODO: Might we need to test if newRules is empty, but we're NOT at
        // the end of the definitions list (i.e. there should be more rules in
        // the database?) That might be an error situation...
        if (newRules.isEmpty()) {
            LOG.debug(String.format("No further things to accumulate! %s", info));
            // We're at the end, so pass along what we've accumulated so far.
            infos.add(info);
            return infos;
        }

        // Continue accumulating coordinate information
        for (EntityLocationNetwork rule : newRules) {

            // Increment the definition index to move on to the next one
            LocationInformation newInfo = new LocationInformation(
                                                                  rule.getCoordinate(),
                                                                  rule.getContextualEntity(),
                                                                  info.getDefinitionIndex() + 1,
                                                                  info.getAccumulatedCoordinates());

            // recur, and pass back what we find.
            infos.addAll(accumulateCoordinates(em, newInfo));
        }

        return infos;
    }

    /**
     * Initializes various "global" information needed throughout the recursive
     * calculation of coordinate values. This is not a constructor, because it's
     * conceivable that you might want to run multiple calculations. This is the
     * first thing that gets called when a nesting calculation is performed.
     * This <em>could</em> be moved into a constructor, but then you'd have to
     * know that once you use the object to make a calculation, you can't use it
     * again. This way seems easier on the programmer.
     * 
     * @param kind
     *            a CoordinateKind whose definition rules are used to drive the
     *            calculation.
     */
    private void initialize(CoordinateKind kind) {

        originalKind = kind;

        definitions = new ArrayList<CoordinateKindDefinition>();
        /*
         * We need to reverse the order of our definition rules. In the
         * database, they are stored in a general-to-specific order; for example
         * Genome, Chromosome, Strand, Linear Range.
         *
         * However, when calculating the location of a Entity such as a gene,
         * we need to work upwards from most specific to general. By reversing
         * the list, we allow users to represent rules in the way that makes
         * sense to them.
         */
        Collection<? extends CoordinateKindDefinition> definitionRules = kind.getDefinitionRules();
        List<CoordinateKindDefinition> rules = new ArrayList<CoordinateKindDefinition>(
                                                                                       definitionRules.size());
        rules.addAll(definitionRules);
        Collections.reverse(rules);
        for (CoordinateKindDefinition rule : rules) {
            if (rule.getSubordinateCoordinateKind() == null) {
                throw new IncorrectCoordinateKindDefinition(originalKind);
            }
            definitions.add(rule);
        }

        coordinates = new ArrayList<CoordinateBundle>();
    }
}