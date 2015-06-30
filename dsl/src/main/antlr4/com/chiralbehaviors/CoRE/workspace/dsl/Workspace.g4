/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
grammar Workspace;


workspace:
    definition=workspaceDefinition
    (imports=imported)? 
    (agencies = definedAgencies)?
    (attributes = definedAttributes)?
    (intervals = definedIntervals)?
    (locations = definedLocations)?
    (products = definedProducts)?
    (relationships = definedRelationships)?
    (statusCodes = definedStatusCodes)?
    (statusCodeSequencings = definedStatusCodeSequencings)?
    (units = definedUnits)? 
    (sequencingAuthorizations = definedSequencingAuthorizations)?
    (inferences = definedInferences)?
    (protocols = definedProtocols)?
    (metaProtocols=definedMetaProtocols)?
    EOF;


definedAgencies: 'agencies' LB  (attributedExistentialRuleform SC)* (edges)? (facets)? RB;
definedAttributes: 'attributes' LB  (attributeRuleform SC)+ (edges)? (facets)? RB;
definedIntervals: 'intervals' LB  (attributedExistentialRuleform SC)* (edges)? (facets)? RB;
definedLocations: 'locations' LB  (attributedExistentialRuleform SC)* (edges)? (facets)? RB;
definedProducts: 'products' LB  (attributedExistentialRuleform SC)* (edges)? (facets)? RB;
definedRelationships: 'relationships' LB  (relationshipPair SC)+ (edges)? (facets)? RB;
definedStatusCodes: 'status codes' LB  (attributedExistentialRuleform SC)* (edges)? (facets)? RB;
definedUnits: 'units' LB  (unit SC)*  (edges)? (facets)? RB;
definedStatusCodeSequencings: 'status code sequencings' LB (statusCodeSequencingSet)+ (edges)? (facets)? RB;
definedSequencingAuthorizations: 'sequencing auths' LB (selfSequencings)? (parentSequencings)? (siblingSequencings)? (childSequencings)?  RB;
definedInferences: 'inferences' LB (edge)+ RB ;
definedProtocols: 'protocols' LB (protocol)+ RB;
definedMetaProtocols: 'meta protocols' LB (metaProtocol)* RB;

edges: 'edges' LB (edge)+ RB;

facets: 'facets' LB (facet)+ RB;

workspaceDefinition: 
    'workspace:'
    uri =  QuotedText
    name=QuotedText
    (description=QuotedText)?;

    
imported:
    'imports' LB (importedWorkspace)+ RB;
    

importedWorkspace: 
    uri =  QuotedText 
    'as '
    namespace = ObjectName;
    
statusCodeSequencingSet:
    service = qualifiedName
    ':' LB (sequencePair)+ RB;
    
sequencePair:
    first = qualifiedName   
    second = qualifiedName;
   
existentialRuleform:
    workspaceName = ObjectName  
    '=' 
    name = QuotedText 
    (description=QuotedText)?; 
    
    
attributedExistentialRuleform:
    existentialRuleform
    ('attribute values' LB (attributeValue)+ RB)?;
    
attributeRuleform:
    existentialRuleform
    ('iri:' iri = QuotedText)?
    ('type:' type = QuotedText)?
    ('indexed:' indexed = ('true' | 'false'))?
    ('keyed:' keyed = ('true' | 'false'))?
    valueType = ('int' | 'bool' | 'text' | 'binary' | 'numeric' | 'timestamp')
    ('attribute values' LB (attributeValue)+ RB)?; 

unit: 
    existentialRuleform
    datatype = ObjectName
    ('enumerated:' enumerated = Boolean)?
    ('min:' min = Number)?
    ('max:' max = Number)?
    ('attribute values' LB (attributeValue)+ RB)?;
    
attributeValue:
    attribute = qualifiedName 
    ':' value = QuotedText
    (sequenceNumber = Number)?;
    
relationshipPair:
    primary=attributedExistentialRuleform '|' inverse=attributedExistentialRuleform; 

qualifiedName:
    (namespace=ObjectName '::')?
    member=ObjectName;
    
edge:
   parent=qualifiedName
   '.' 
   relationship=qualifiedName 
   '.' 
   child=qualifiedName;
   
parentSequencings:
    'parent' LB (parentSequencing)* RB;

parentSequencing:
    'service:' service=qualifiedName
    'status:' status=qualifiedName
    'parent:' parent=qualifiedName
    'next:' next=qualifiedName
    ('replace: ' replace=('true' | 'false'))?
    ('activeSiblings:' activeSiblings=('true' | 'false'))?
    ('sequence:' sequenceNumber=Number)?;
    
siblingSequencings:
    'sibling' LB (siblingSequencing)* RB;

siblingSequencing:
    'parent:' parent=qualifiedName
    'status:' status=qualifiedName
    'sibling:' sibling=qualifiedName
    'next:' next=qualifiedName
    ('replace: ' replace=('true' | 'false'))?
    ('sequence:' sequenceNumber=Number)?;
    
childSequencings:
    'child' LB (childSequencing)* RB;

childSequencing:
    'parent:' parent=qualifiedName
    'status:' status=qualifiedName
    'child:' child=qualifiedName
    'next:' next=qualifiedName
    ('replace: ' replace=('true' | 'false'))?
    ('sequence:' sequenceNumber=Number)?;
    
selfSequencings:
    'self' LB (selfSequencing)* RB;

selfSequencing:
    'service:' service=qualifiedName
    'status:' status=qualifiedName
    'next:' next=qualifiedName
    ('sequence:' sequenceNumber=Number)?;
    
metaProtocol:
    ('transform:' service=qualifiedName)
    ('service:'serviceType=qualifiedName)? 
    ('product:' product=qualifiedName)? 
    ('from:' from=qualifiedName)? 
    ('to:' to=qualifiedName)? 
    ('quantity:' quantity=Number)?
    ('unit:' quantityUnit=qualifiedName)?
    ('requester:' requester=qualifiedName)? 
    ('assign:' assignTo=qualifiedName)? 
    ('sequence:' Number)?
    ('match:' match = ('stop' | 'continue'))?
    ;
    
protocol:
    matchJob
    '->'
    childJob;
    
matchJob: 
    ('service:' service=qualifiedName) 
    ('product:' product=qualifiedName)? 
    ('from:' from=qualifiedName)? 
    ('to:' to=qualifiedName)? 
    ('quantity:' quantity=Number)?
    ('unit:' quantityUnit=qualifiedName)?
    ('requester:' requester=qualifiedName)? 
    ('assign:' assignTo=qualifiedName)? 
    ('sequence:' sequence=Number)?
    ;
    
childJob: 
    ('service:' service=qualifiedName)? 
    (('children:' childrenRelationship=qualifiedName) | ('product:' product=qualifiedName))? 
    ('from:' from=qualifiedName)? 
    ('to:' to=qualifiedName)? 
    ('quantity:' quantity=Number)?
    ('unit:' (quantityUnit=qualifiedName))?
    ('assign:' assignTo=qualifiedName)? 
    ;

facet:
    classifier = qualifiedName
    '.'
    classification = qualifiedName
    (LB classifiedAttributes RB)?
    (name = QuotedText)?
    (description = QuotedText)?
    ('constraints' LB networkConstraints RB)?
    ;
classifiedAttributes: (qualifiedName)+;
networkConstraints: (constraint)+;
constraint: 
    cardinality = ('zero' | 'one' | 'n')
    childRelationship = qualifiedName
    ('get:' inferredGet = ('inferred' | 'immediate'))?
    ':'
    (anyType = ('*Agency' | '*Attribute' | '*Interval' | '*Location' | '*Product' | '*Relationship' | '*StatusCode' | '*Unit')
    | 
    (authorizedRelationship = qualifiedName '.' authorizedParent = qualifiedName))
    (('named' name = ObjectName) | (methodType = ( 'named by relationship' | 'named by entity')))?
    ('sequence:' sequenceNumber = Number)?
    (LB classifiedAttributes RB)?
    ;

ObjectName: ('A'..'Z' | 'a'..'z')('A'..'Z' | 'a'..'z' | '0'..'9' | '_')+ ;
QuotedText: '"' (' ' | '!' |'#'.. '~')+ '"'; 
Boolean: ('true'|'false');
Number: ('0'..'9')+;
 
WS: (' ' | '\t')+ -> skip;
NL: ('\r'? '\n')+ -> skip;
LB: '{';
RB: '}';
SC: ';'; 
