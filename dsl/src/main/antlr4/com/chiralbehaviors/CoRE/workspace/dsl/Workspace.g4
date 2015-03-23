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
    (edges = definedEdges)?
    EOF;

definedAgencies: 'agencies' LB  (existentialRuleform SC)+ RB;
definedAttributes: 'attributes' LB  (existentialRuleform SC)+ RB;
definedIntervals: 'intervals' LB  (interval SC)+ RB;
definedLocations: 'locations' LB  (existentialRuleform SC)+ RB;
definedProducts: 'products' LB  (existentialRuleform SC)+ RB;
definedRelationships: 'relationships' LB  (relationshipPair SC)+ RB;
definedStatusCodes: 'status codes' LB  (existentialRuleform SC)+ RB;
definedStatusCodeSequencings: 'status code sequencings' LB (statusCodeSequencingSet)+ RB;
definedUnits: 'units' LB  (unit SC)+ RB;
definedEdges: 'edges' LB (edge)+ RB;
    
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
    

ObjectName: 'a'..'z' ('A'..'Z' | 'a'..'z' | '0'..'9' | '_')+ ;
QuotedText: '"'(' ' | '!' |'#'.. '~')+ '"';
Boolean: ('true'|'false');
Number: ('0'..'9')+;
 
WS: (' ' | '\t')+ -> skip;
NL: ('\r'? '\n')+ -> skip;
LB: '{';
RB: '}';
SC: ';';