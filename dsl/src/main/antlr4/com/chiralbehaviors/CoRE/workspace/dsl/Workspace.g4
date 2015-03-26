grammar Workspace;


workspace:
    definition=workspaceDefinition
    (imports=imported)?
    //relationships first so that we can define networks
    (relationships = definedRelationships)?
    (agencies = definedAgencies)?
    (attributes = definedAttributes)?
    (locations = definedLocations)?
    (products = definedProducts)?
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


definedAgencies: 'agencies' LB  (existentialRuleform SC)+ (edges)? RB;
definedAttributes: 'attributes' LB  (existentialRuleform SC)+ (edges)? RB;
definedIntervals: 'intervals' LB  (interval SC)+ (edges)? RB;
definedLocations: 'locations' LB  (existentialRuleform SC)+ (edges)? RB;
definedProducts: 'products' LB  (existentialRuleform SC)+ (edges)? RB;
definedRelationships: 'relationships' LB  (relationshipPair SC)+ (edges)? RB;
definedStatusCodes: 'status codes' LB  (existentialRuleform SC)+ (edges)? RB;
definedStatusCodeSequencings: 'status code sequencings' LB (statusCodeSequencingSet)+ (edges)? RB;
definedUnits: 'units' LB  (unit SC)+  (edges)? RB;
definedSequencingAuthorizations: 'sequencing auths' LB (selfSequencings)? (parentSequencings)? (siblingSequencings)? (childSequencings)?  RB;
definedInferences: 'inferences' LB (edge)+ RB ;
definedProtocols: 'protocols' LB (protocol)+ RB;
definedMetaProtocols: 'meta protocols' LB (metaProtocol)* RB;

edges: 'edges' LB (edge)+ RB;

workspaceDefinition: 
    'workspace:'
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

unit: 
    existentialRuleform
    datatype = ObjectName
    ('enumerated:' enumerated = Boolean)?
    ('min:' min = Number)?
    ('max:' max = Number)?;
    
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
    ('match stop:' ('true' | 'false'))?
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
    ('sequence:' Number)?
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
    ('requester:' requester=qualifiedName)?
    ('attr:' (requesterAttribute=qualifiedName))?
    ('assign:' assignTo=qualifiedName)?
    ('attr:' (assignToAttribute=qualifiedName))?
    ;

ObjectName: 'a'..'z' ('A'..'Z' | 'a'..'z' | '0'..'9' | '_')+ ;
QuotedText: '"'(' ' | '!' |'#'.. '~')+ '"';
Boolean: ('true'|'false');
Number: ('0'..'9')+;
 
WS: (' ' | '\t')+ -> skip;
NL: ('\r'? '\n')+ -> skip;
LB: '{';
RB: '}';
SC: ';';
