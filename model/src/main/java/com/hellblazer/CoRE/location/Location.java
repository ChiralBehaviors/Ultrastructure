/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hellblazer.CoRE.location;

import static com.hellblazer.CoRE.location.Location.FIND_ATTRIBUTE_AUTHORIZATIONS;
import static com.hellblazer.CoRE.location.Location.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS;
import static com.hellblazer.CoRE.location.Location.FIND_CLASSIFIED_ATTRIBUTE_VALUES;
import static com.hellblazer.CoRE.location.Location.FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS;
import static com.hellblazer.CoRE.location.Location.LOCATION_NAME;
import static com.hellblazer.CoRE.location.LocationMetaRule.CONTEXT_META_RULES;
import static com.hellblazer.CoRE.location.LocationRelationship.RULES;
import static com.hellblazer.CoRE.location.LocationRelationship.TARGET_CONTEXTS;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.NameSearchResult;
import com.hellblazer.CoRE.attribute.Attributable;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.entity.Entity;
import com.hellblazer.CoRE.entity.EntityLocation;
import com.hellblazer.CoRE.kernel.WellKnownObject;
import com.hellblazer.CoRE.meta.Model;
import com.hellblazer.CoRE.meta.NetworkedModel;
import com.hellblazer.CoRE.meta.models.ModelImpl;
import com.hellblazer.CoRE.network.Networked;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * General idea of a location or address; where some resource, entity or event
 * can be found in a variety of spaces
 * 
 */
@javax.persistence.Entity
@Table(name = "location", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "location_id_seq", sequenceName = "location_id_seq")
@NamedQueries({
               @NamedQuery(name = "location" + Model.FIND_BY_NAME_SUFFIX, query = "select l from Location l where l.name = :name"),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_VALUES, query = "SELECT "
                                                                            + "  attrValue "
                                                                            + "FROM "
                                                                            + "       LocationAttribute attrValue, "
                                                                            + "       LocationAttributeAuthorization auth, "
                                                                            + "       LocationNetwork network "
                                                                            + "WHERE "
                                                                            + "        auth.authorizedAttribute = attrValue.attribute AND "
                                                                            + "        network.relationship = auth.classification AND "
                                                                            + "        network.child = auth.classifier AND"
                                                                            + "        attrValue.location = :ruleform AND "
                                                                            + "        auth.classification = :classification AND "
                                                                            + "        auth.classifier = :classifier "),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS, query = "select la from LocationAttributeAuthorization la "
                                                                                    + "WHERE la.classification = :classification "
                                                                                    + "AND la.classifier = :classifier"),
               @NamedQuery(name = FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS, query = "select la from LocationAttributeAuthorization la "
                                                                                 + "WHERE la.groupingResource = :groupingResource"),
               @NamedQuery(name = FIND_ATTRIBUTE_AUTHORIZATIONS, query = "select la from LocationAttributeAuthorization la "
                                                                         + "WHERE la.classification = :classification "
                                                                         + "AND la.classifier = :classifier "
                                                                         + "AND la.groupingResource = :groupingResource"),
               @NamedQuery(name = LOCATION_NAME, query = "SELECT la.name FROM Location la WHERE la.id = :id") })
@NamedNativeQueries({
// ?1 = :queryString, ?2 = :numberOfMatches
@NamedNativeQuery(name = "location" + Model.NAME_SEARCH_SUFFIX, query = "SELECT id, name, description FROM ruleform.existential_name_search('location', ?1, ?2)", resultClass = NameSearchResult.class) })
public class Location extends ExistentialRuleform implements
        Networked<Location, LocationNetwork>, Attributable<LocationAttribute> {
    private static final Logger    LOG                                      = LoggerFactory.getLogger(Location.class);
    private static final long      serialVersionUID                         = 1L;
    public static final String     FIND_ATTRIBUTE_AUTHORIZATIONS            = "location.findAttributeAuthorizations";
    public static final String     FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS = "location"
                                                                              + NetworkedModel.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
    public static final String     FIND_CLASSIFIED_ATTRIBUTE_VALUES         = "location.findClassifiedAttributes";
    public static final String     FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS    = "location.findGroupedAttributeAuthorizations";
    public static final String     LOCATION_NAME                            = "location.getName";
    public static final String     NAME_SEARCH                              = "location"
                                                                              + Model.NAME_SEARCH_SUFFIX;
    public static final String     FIND_BY_ID                               = "location.findById";
    public static final String     FIND_BY_NAME                             = "location.findByName";

    //bi-directional many-to-one association to LocationAttribute
    @OneToMany(mappedBy = "location")
    @JsonIgnore
    private Set<LocationAttribute> attributes;

    @ManyToOne
    @JoinColumn(name = "context")
    private LocationContext        context;

    //bi-directional many-to-one association to EntityLocation
    @OneToMany(mappedBy = "location")
    @JsonIgnore
    private Set<EntityLocation>    entities;

    @Id
    @GeneratedValue(generator = "location_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                   id;

    //bi-directional many-to-one association to LocationNetwork
    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<LocationNetwork>   networkByChild;

    //bi-directional many-to-one association to LocationNetwork
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<LocationNetwork>   networkByParent;

    public Location() {
    }

    /**
     * @param id
     */
    public Location(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public Location(Resource updatedBy) {
        super(updatedBy);
    }

    /**
     * @param name
     */
    public Location(String name) {
        super(name);
    }

    /**
     * @param name
     * @param updatedBy
     */
    public Location(String name, Resource updatedBy) {
        super(name, updatedBy);
    }

    /**
     * @param name
     * @param description
     */
    public Location(String name, String description) {
        super(name, description);
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     */
    public Location(String name, String description, LocationContext context,
                    Resource updatedBy) {
        this(name, description, updatedBy);
        this.context = context;
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     */
    public Location(String name, String description, Resource updatedBy) {
        super(name, description, updatedBy);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#addChildRelationship(com.hellblazer.CoRE.NetworkRuleform)
     */
    @Override
    public void addChildRelationship(LocationNetwork relationship) {
        relationship.setChild(this);
        networkByChild.add(relationship);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#addParentRelationship(com.hellblazer.CoRE.NetworkRuleform)
     */
    @Override
    public void addParentRelationship(LocationNetwork relationship) {
        relationship.setParent(this);
        networkByParent.add(relationship);
    }

    @Override
    public Location clone() {
        Location clone = (Location) super.clone();
        clone.attributes = null;
        clone.networkByChild = null;
        clone.networkByParent = null;
        clone.entities = null;
        return clone;
    }

    /**
     * <p>
     * Finds all Entities located at Locations related to a query Location by
     * the given Relationship. Briefly, this is what happens:
     * </p>
     * <ol>
     * <li>A user-given Location (whether it exists in the database already, or
     * is constructed "on-the-fly" based on previous user input; it just has to
     * have all its Attributes set) is given. This is the query Location. The
     * user is interested in finding out what else is around this Location.</li>
     * <li>The user indicates what kind of Locations they are interested in
     * finding by specifying <code>targetContext</code>. As an example, if the
     * user has a Location representing genomic coordinates in the 18th draft of
     * the human genome, and they are interested in finding other Locations in
     * the 18th draft that are related to their query Location, their
     * <code>targetContext</code> would be "Human Genome Draft 18 Coordinate".
     * If they were interested in translating to coordinates in the 17th draft,
     * on the other hand, they would specify "Human Genome Draft 17 Coordinate",
     * and so on.</li>
     * <li>The Relationship given is used to complete a subject-predicate-object
     * triple relating the query Location to the result set Locations. For
     * instance, if the user wants to find all genomic locations that contain
     * their query location, they would specify the Relationship
     * "overlap-contained-by". The query Location is always the subject of this
     * triple.</li>
     * <li>With this information, we can consult the
     * {@link com.hellblazer.CoRE.ruleform.LocationMetaRule} and
     * {@link com.hellblazer.CoRE.ruleform.LocationRelationship} ruleforms to
     * retrieve related Locations. However, we do not just return these Location
     * objects. Instead, we also return the Entities located at those Locations,
     * as well as the coordinates of the Locations.</li>
     * </ol>
     * 
     * @param query
     *            A Location
     * @param relationship
     *            the Relationship that holds between the Location you have and
     *            the Locations you want
     * @param targetContext
     *            the kind of Locations to be found
     * @return a List of Object[]; the first element of each array will be a
     *         Entity located at one of the Locations found, the second will be
     *         the actual Location, and the rest of the elements are the
     *         coordinates of that Location. This will vary depending on the
     *         targetContext.
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findEntitiesAtRelatedLocations(EntityManager em,
                                                         final Relationship relationship,
                                                         final LocationContext targetContext) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = builder.createQuery(Object[].class);

        criteriaQuery.from(Location.class);
        criteriaQuery.from(EntityLocation.class);

        new ArrayList<Selection<?>>();
        new ArrayList<Predicate>();

        /*
         * Use this List to store the names (with table aliases!) of the objects
         * you want to appear in the SELECT clause of the query. Since it's a
         * list, the objects will appear in the SELECT clause in the order they
         * are added.
         */
        List<String> selectColumns = new ArrayList<String>();
        selectColumns.add("bl.entity"); // want the related Entity
        selectColumns.add("bl.location"); // toss in the Location object as well

        /*
         * This string starts out the FROM clause of the query. The aliases
         * defined here should be used throughout this method when referring to
         * properties of these objects.
         * 
         * In this particular case, we will define the join condition below in
         * the "whereClauses" data structure... if we didn't, we'd get a
         * Cartesian join, which is definitely not what we want!
         */
        String fromClause = "from Location as l, EntityLocation as bl";

        /*
         * This list is used to store conditions that will be used to JOIN other
         * tables into the query. The keywords "join" and "and" should not be
         * part of any string in this List; they should be association names and
         * aliases, such as "l.attributes as la_1".
         * 
         * The join conditions should be added to the whereClauses list.
         */
        List<String> joinClauses = new ArrayList<String>();

        /*
         * This list is used to store conditions that will be used in the WHERE
         * clause. As with the joinClauses list, the "where" and "and" keywords
         * should not appear anywhere; just give the condition, such as
         * "alias1.property = alias2.property".
         */
        List<String> whereClauses = new ArrayList<String>();

        /*
         * This is the condition to link the Location and EntityLocation
         * tables from fromClause above.
         */
        whereClauses.add("bl.location.id = l.id");

        whereClauses.add("l.context.id = " + targetContext.getId());

        //----------------------------------------------------------------------
        /*
         * This might be inefficient from a database querying perspective, but
         * it's quick and dirty (to code!). We want to show all the coordinates
         * of the Locations directly in the result query, so we need to step
         * through each of the permitted Attributes of the targetContext and add
         * them to our query.
         */

        Set<ContextAttribute> targetAttributes = targetContext.getAttributes();
        for (ContextAttribute lca : targetAttributes) {
            Attribute a = lca.getAttribute();
            String alias = "c_" + lca.getSequenceNumber(); // c_1, c_2, etc. (for "coordinate 1", "coordinate 2")
            LOG.debug(String.format("Attribute %s is %s",
                                    lca.getSequenceNumber(), a.getName()));

            joinClauses.add("l.attributes as " + alias);
            whereClauses.add(alias + ".attribute.id = " + a.getId());

            String property = null;

            //TODO: Factor this out into a method... it's used below as well, and will pop up again as the application is developed
            if (a.getValueType().equals("entity")) {
                property = alias + "." + "entityValue";
            } else if (a.getValueType().equals("integer")) {
                property = alias + "." + "integerValue";
            } else if (a.getValueType().equals("text")) {
                property = alias + "." + "textValue";
            } else if (a.getValueType().equals("resource")) {
                property = alias + "." + "resourceValue";
            } else {
                //TODO: make this a bit more robust
                throw new RuntimeException(
                                           "Could not determine the consideration in which to look to find a value of type \""
                                                   + a.getValueType()
                                                   + " in the LocationContextAttribute ruleform.");
            }

            selectColumns.add(property);
        }

        //----------------------------------------------------------------------
        /*
         * Now that we've set up our query data structures, we can begin
         * processing.
         * 
         * First, we need to examine the relevant Location MetaRules in order to
         * determine what mapped Entity value to use to retrieve the
         * appropriate Location Relationship rules.
         */
        //TODO: We need to pull this query out into a DAO at some point.
        List<LocationMetaRule> locationMetaRules = em.createNamedQuery(CONTEXT_META_RULES,
                                                                       LocationMetaRule.class).setParameter("context",
                                                                                                            getContext()).getResultList();

        int numberOfMetaRules = locationMetaRules.size();
        LOG.debug(String.format("Found %s Location MetaRules",
                                numberOfMetaRules));

        /*
         * We'll use this boolean flag to note whether or not we found any
         * applicable Location Relationship rules.
         */
        boolean foundRules = false;

        /*
         * According to the Location MetaRules we find, this will store the
         * reference to the mapped Entity we will use to look up our Location
         * Relationship rules.
         */
        Entity mappedEntity = null;

        /*
         * The fact that the check condition for this "for" loop is "i <= size"
         * rather than "i < size" is NOT a typo. Read on for why this is.
         * 
         * DO _NOT_ CHANGE IT!
         */
        for (int i = 0; i <= numberOfMetaRules; i++) {

            if (i != numberOfMetaRules) {
                /*
                 * We have metaRules to examine... first we must get the
                 * Attribute Mask from the metaRule.
                 */
                LocationMetaRule lmr = locationMetaRules.get(i);
                Attribute mask = lmr.getAttributeMask();

                //log.debug("Location MetaRule: " + lmr);

                /*
                 * Once we have the mask, we then look through the attributes of
                 * our query Location to find the same attribute. If we find it,
                 * then we set mappedEntity to be the same as the value of
                 * that attribute.
                 * 
                 * We iterate through the list of attributes rather than
                 * querying the database directly because the query Location
                 * might not already exist in the database! In such a case, the
                 * Location has been assembled "on-the-fly" from user input,
                 * most likely in the web application interface. If we're
                 * dealing with a Location that *is* already stored in the
                 * database, then this will work on it as well. Since Locations
                 * are only expected to have a handful of attributes, this isn't
                 * going to be inefficient.
                 */
                for (LocationAttribute la : getAttributes()) {
                    LOG.debug(String.format("The Attribute is: %s",
                                            la.getAttribute().getName()));
                    LOG.debug(String.format("The metaRule attribute is: %s",
                                            mask.getName()));

                    if (mask.equals(la.getAttribute())) {
                        LOG.debug("Found a match!");
                        mappedEntity = la.getEntityValue();

                        /*
                         * If we find something, then we don't need to look for
                         * any more matches; a Location can only ever have one
                         * value for any Attribute (according to the definition
                         * of the Location Attribute ruleform).
                         */
                        break;
                    } else {
                        LOG.debug(String.format("No match between location's attribute %s and metaRule attribute %s",
                                                la.getAttribute().getName(),
                                                mask.getName()));
                    }
                }
            } else {
                /*
                 * If our index variable is equal to the size of the list, then
                 * we have gone through all the potential metarules without
                 * finding any applicable ones. If this is the case, then we
                 * will fall back on the special "(ANY)" Entity to match the
                 * general case.
                 */
                LOG.debug(String.format("using the %s", WellKnownObject.ANY));
                mappedEntity = new ModelImpl(em).find(WellKnownObject.ANY,
                                                      Entity.class);
            }

            //------------------------------------------------------------------
            /*
             * At this point we should have a valid mapped Entity. We now
             * have all the information we need to start looking for Location
             * Relationship rules.
             */
            LOG.debug(String.format("Mapped Entity is: %s", mappedEntity));
            LOG.debug("Retrieving Location Relationship rules");

            // TODO: We need to pull this query out into a DAO at some point.
            List<LocationRelationship> locationRelationshipRules = em.createNamedQuery(RULES,
                                                                                       LocationRelationship.class).setParameter("context",
                                                                                                                                getContext()).setParameter("relationship",
                                                                                                                                                           relationship).setParameter("targetContext",
                                                                                                                                                                                      targetContext).setParameter("mappedEntityValue",
                                                                                                                                                                                                                  mappedEntity).getResultList();

            /*
             * If we found Location Relationship rules then those will be used
             * to generate our query. We will cease processing following that
             * (we don't need to try to generate queries using subsequent
             * metaRules, for instance).
             * 
             * If, on the other hand, there are no Location Relationship rules
             * for these parameters, then we need to move on to the next
             * available metaRule and query again.
             */
            if (!locationRelationshipRules.isEmpty()) {
                LOG.debug(String.format("Found some Location Relationship rules for %s / %s / %s / %s",
                                        new Object[] { getContext().getName(),
                                                relationship.getName(),
                                                targetContext.getName(),
                                                mappedEntity.getName() }));
                foundRules = true;
            } else {
                LOG.debug(String.format("Didn't find any Location Relationship rules for %s / %s / %s / %s",
                                        new Object[] { getContext().getName(),
                                                relationship.getName(),
                                                targetContext.getName(),
                                                mappedEntity.getName() }));
                continue;
            }

            //------------------------------------------------------------------
            /*
             * Now that we have some Location Relationship rules to examine, we
             * can begin building our HQL query.
             */
            for (LocationRelationship rule : locationRelationshipRules) {
                /*
                 * We will build up a query by creating successive joins, one
                 * for each Attribute. In order to do this, each join should
                 * have it's own unique alias, which we generate using the
                 * sequence number of the Location Relationship rule, which is
                 * guaranteed to be unique among the rules retrieved.
                 */
                String newAlias = "la_" + rule.getSequenceNumber(); // la_1, la_2, la_3, etc.
                LOG.debug(String.format("Alias is %s", newAlias));

                /* 
                 * These joins are all on the "attributes" collection of the query Location.
                 */
                String joinClause = "l.attributes as " + newAlias;
                String whereClause = newAlias + ".attribute.id = "
                                     + rule.getLocation2Attribute().getId();

                // Record these clauses for constructing the HQL query later on
                joinClauses.add(joinClause);
                LOG.debug(String.format("Added join: %s", joinClause));
                whereClauses.add(whereClause);
                LOG.debug(String.format("Added join where: %s", whereClause));

                /*
                 * Here we iterate through the attributes of our query Location
                 * in order to generate the appropriate WHERE condition, as
                 * specified by the Location Relationship rule.
                 */
                for (LocationAttribute la : getAttributes()) {
                    if (la.getAttribute().equals(rule.getLocation1Attribute())) {

                        Attribute a = la.getAttribute();
                        String clause = null;
                        String property = null;

                        /*
                         * Since an Attribute (in general) can be one of several
                         * potential datatypes, we need to handle each case
                         * individually.
                         */
                        // TODO: break these types into an Enumeration?
                        // TODO: throw an exception if getOperator() returns null
                        if (a.getValueType().equals("entity")) {
                            property = newAlias + "." + "entityValue";
                            clause = la.getEntityValue().getId()
                                     + " "
                                     + rule.getAttributeRelationship().getOperator()
                                     + " " + property + ".id";
                        } else if (a.getValueType().equals("integer")) {
                            property = newAlias + "." + "integerValue";
                            clause = la.getIntegerValue()
                                     + " "
                                     + rule.getAttributeRelationship().getOperator()
                                     + " " + property;
                        } else if (a.getValueType().equals("text")) {
                            property = newAlias + "." + "textValue";
                            clause = "'"
                                     + la.getTextValue()
                                     + "' "
                                     + rule.getAttributeRelationship().getOperator()
                                     + " " + property;
                        } else if (a.getValueType().equals("resource")) {
                            property = newAlias + "." + "resourceValue";
                            clause = la.getResourceValue().getId()
                                     + " "
                                     + rule.getAttributeRelationship().getOperator()
                                     + " " + property + ".id";
                        } else {
                            //TODO: make this a bit more robust
                            throw new RuntimeException(
                                                       "Could not determine the consideration in which to look to find a value of type \""
                                                               + a.getValueType()
                                                               + " in the Location Attribute ruleform.");
                        }

                        LOG.debug(String.format("Added 'where' clause: %s",
                                                clause));
                        whereClauses.add(clause);

                        // Once we've found the right Attribute, we don't need to check the rest.
                        break;
                    }
                }
            }

            /*
             * If we found Location Relationship rules and thus can generate an
             * HQL query, then we don't need to examine any more metaRules.
             */
            if (foundRules) {
                break;
            }
        }

        //----------------------------------------------------------------------
        /*
         * If we've gotten to this point, then we can either generate our HQL
         * query, or else we didn't find any applicable Location Relationship
         * rules.
         */

        if (foundRules) {
            /*
             * We can now go about generating a valid HQL query based on the
             * information taken from our Location Relationshp rules.
             * 
             * Start it all off with a "select".
             */
            StringBuffer hqlQuery = new StringBuffer();
            hqlQuery.append("select ");

            /*
             * Add all the objects to the select clause. We'll add a comma and
             * space after all but the last one (we'll add a newline after that
             * one for readability purposes).
             */
            for (int i = 0; i < selectColumns.size(); i++) {
                hqlQuery.append(selectColumns.get(i));
                if (i != selectColumns.size() - 1) {
                    hqlQuery.append(", ");
                } else {
                    hqlQuery.append("\n");
                }
            }

            // SELECT is done, now add FROM
            hqlQuery.append(fromClause).append("\n");

            // add all the joined tables
            for (String join : joinClauses) {
                hqlQuery.append("join ").append(join).append("\n");
            }

            /*
             * Add all the where conditions. If it's the first one, add the
             * "where" keyword in front; all others get "and" prepended. Each
             * condition gets its own line.
             */
            for (int i = 0; i < whereClauses.size(); i++) {
                if (i == 0) {
                    hqlQuery.append("where ");
                } else {
                    hqlQuery.append("  and ");
                }
                hqlQuery.append(whereClauses.get(i)).append("\n");
            }

            /*
             * At this point, we have generated a valid HQL query representing
             * the information in the Location Relationship rules. We can now
             * execute this query against the database and return the results.
             */
            LOG.debug(String.format("Complete query: %s", hqlQuery));
            return em.createQuery(hqlQuery.toString()).getResultList();
        } else {
            /* 
             * If we didn't find any Location Relationship rules, then just return an empty list.
             */
            LOG.debug("Didn't find any Location Relationship rules... here, have an empty list");
            return new ArrayList<Object[]>();
        }
    }

    @Override
    public Set<LocationAttribute> getAttributes() {
        return attributes;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.Attributable#getAttributeType()
     */
    @Override
    public Class<LocationAttribute> getAttributeType() {
        return LocationAttribute.class;
    }

    public List<LocationContext> getAvailableTargetContexts(EntityManager em) {
        return em.createNamedQuery(TARGET_CONTEXTS, LocationContext.class).setParameter("context",
                                                                                        this).getResultList();
    }

    public LocationContext getContext() {
        return context;
    }

    public Set<EntityLocation> getEntities() {
        return entities;
    }

    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.Networked#getImmediateChildren()
     */
    @Override
    public List<LocationNetwork> getImmediateChildren(EntityManager em) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#getNetworkByChild()
     */
    @Override
    public Set<LocationNetwork> getNetworkByChild() {
        return networkByChild;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#getNetworkByParent()
     */
    @Override
    public Set<LocationNetwork> getNetworkByParent() {
        return networkByParent;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.Networked#link(com.hellblazer.CoRE.network.Relationship, com.hellblazer.CoRE.network.Networked, com.hellblazer.CoRE.resource.Resource, javax.persistence.EntityManager)
     */
    @Override
    public void link(Relationship r, Location child, Resource updatedBy,
                     Resource inverseSoftware, EntityManager em) {
        LocationNetwork link = new LocationNetwork(this, r, child, updatedBy);
        em.persist(link);
        LocationNetwork inverse = new LocationNetwork(child, r.getInverse(),
                                                      this, inverseSoftware);
        em.persist(inverse);
    }

    @Override
    public void setAttributes(Set<LocationAttribute> locationAttributes) {
        attributes = locationAttributes;
    }

    public void setContext(LocationContext locationContext) {
        context = locationContext;
    }

    public void setEntities(Set<EntityLocation> entityLocations) {
        entities = entityLocations;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#setNetworkByChild(java.util.Set)
     */
    @Override
    public void setNetworkByChild(Set<LocationNetwork> theNetworkByChild) {
        networkByChild = theNetworkByChild;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#setNetworkByParent(java.util.Set)
     */
    @Override
    public void setNetworkByParent(Set<LocationNetwork> theNetworkByParent) {
        networkByParent = theNetworkByParent;
    }

}