/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
grammar Workspace;


workspace:
    definition=workspaceDefinition
    (imports=imported)? 
    (agencies = definedAgencies)?
    (attributes = definedAttributes)?
    (locations = definedLocations)?
    (products = definedProducts)?
    (relationships = definedRelationships)?
    (statusCodes = definedStatusCodes)?
    (statusCodeSequencings = definedStatusCodeSequencings)?
    (units = definedUnits)?
    //intervals refer to units and therefore must be parsed afterwards
    (intervals = definedIntervals)?
    (sequencingAuthorizations = definedSequencingAuthorizations)?
    (inferences = definedInferences)?
    (protocols = definedProtocols)?
    (metaProtocols=definedMetaProtocols)?
    EOF;


definedAgencies: 'agencies' LB  (existentialRuleform SC)+ (edges)? (classifiedAttributes)? RB;
definedAttributes: 'attributes' LB  (attributeRuleform SC)+ (edges)? (classifiedAttributes)? RB;
definedIntervals: 'intervals' LB  (interval SC)+ (edges)? (classifiedAttributes)? RB;
definedLocations: 'locations' LB  (existentialRuleform SC)+ (edges)? (classifiedAttributes)? RB;
definedProducts: 'products' LB  (existentialRuleform SC)+ (edges)? (classifiedAttributes)? RB;
definedRelationships: 'relationships' LB  (relationshipPair SC)+ (edges)? (classifiedAttributes)? RB;
definedStatusCodes: 'status codes' LB  (existentialRuleform SC)+ (edges)? (classifiedAttributes)? RB;
definedStatusCodeSequencings: 'status code sequencings' LB (statusCodeSequencingSet)+ (edges)? (classifiedAttributes)? RB;
definedUnits: 'units' LB  (unit SC)+  (edges)? (classifiedAttributes)? RB;
definedSequencingAuthorizations: 'sequencing auths' LB (selfSequencings)? (parentSequencings)? (siblingSequencings)? (childSequencings)?  RB;
definedInferences: 'inferences' LB (edge)+ RB ;
definedProtocols: 'protocols' LB (protocol)+ RB;
definedMetaProtocols: 'meta protocols' LB (metaProtocol)* RB;

edges: 'edges' LB (edge)+ RB;

classifiedAttributes: 'classified attributes' LB (classifiedAttribute)+ RB;

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
    
interval:
    existentialRuleform
    ('start: ' start = Number
        startUnit = qualifiedName
    )?
    ('duration: ' duration = Number
        durationUnit = qualifiedName
    )?;
    
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
    
attributeRuleform:
    existentialRuleform
    valueType = ('int' | 'bool' | 'text' | 'binary' | 'numeric' | 'timestamp')
    ('attribute values' LB (attributeValue)+ RB)?; 

unit: 
    existentialRuleform
    datatype = ObjectName
    ('enumerated:' enumerated = Boolean)?
    ('min:' min = Number)?
    ('max:' max = Number)?;
    
attributeValue:
    attribute = qualifiedName 
    ':' value = QuotedText
    (sequenceNumber = Number)?;
    
relationshipPair:
    primary=existentialRuleform '|' inverse=existentialRuleform; 

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
    ('attr:' serviceAttribute=qualifiedName)?
    ('product:' product=qualifiedName)?
    ('attr:' productAttribute=qualifiedName)?
    ('from:' from=qualifiedName)?
    ('attr:' (fromAttribute=qualifiedName))?
    ('to:' to=qualifiedName)?
    ('attr:' (toAttribute=qualifiedName))?
    ('quantity:' quantity=Number)?
    ('unit:' quantityUnit=qualifiedName)?
    ('requester:' requester=qualifiedName)?
    ('attr:' requesterAttribute=qualifiedName)?
    ('assign:' assignTo=qualifiedName)?
    ('attr:' assignToAttribute=qualifiedName)?
    ('sequence:' Number)?
    ('match:' match = ('stop' | 'continue'))?
    ;
    
protocol:
    matchJob
    '->'
    childJob;
    
matchJob: 
    ('service:' service=qualifiedName)
    ('attr:' serviceAttribute=qualifiedName)?
    ('product:' product=qualifiedName)?
    ('attr:' productAttribute=qualifiedName)?
    ('from:' from=qualifiedName)?
    ('attr:' (fromAttribute=qualifiedName))?
    ('to:' to=qualifiedName)?
    ('attr:' (toAttribute=qualifiedName))?
    ('quantity:' quantity=Number)?
    ('unit:' quantityUnit=qualifiedName)?
    ('requester:' requester=qualifiedName)?
    ('attr:' requesterAttribute=qualifiedName)?
    ('assign:' assignTo=qualifiedName)?
    ('attr:' assignToAttribute=qualifiedName)?
    ('sequence:' sequence=Number)?
    ;
    
childJob: 
    ('service:' service=qualifiedName)?
    ('attr:' (serviceAttribute=qualifiedName))?
    (('children:' childrenRelationship=qualifiedName) | ('product:' product=qualifiedName))?
    ('attr:' (productAttribute=qualifiedName))?
    ('from:' from=qualifiedName)?
    ('attr:' (fromAttribute=qualifiedName))?
    ('to:' to=qualifiedName)?
    ('attr:' (toAttribute=qualifiedName))?
    ('quantity:' quantity=Number)?
    ('unit:' (quantityUnit=qualifiedName))?
    ('assign:' assignTo=qualifiedName)?
    ('attr:' (assignToAttribute=qualifiedName))?
    ;

classifiedAttribute:
    classification = qualifiedName
    '.'
    classifier = qualifiedName 
    authorized = qualifiedName
    (defaultValue = QuotedText)?
    (sequenceNumber = Number)?
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
