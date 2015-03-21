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
    (units = definedUnits)?
    (edges = definedEdges)?
    EOF;

definedAgencies: 'agencies' LB  (existentialRuleform SC)+ RB;
definedAttributes: 'attributes' LB  (existentialRuleform SC)+ RB;
definedIntervals: 'intervals' LB  (existentialRuleform SC)+ RB;
definedLocations: 'locations' LB  (existentialRuleform SC)+ RB;
definedProducts: 'products' LB  (existentialRuleform SC)+ RB;
definedRelationships: 'relationships' LB  (relationshipPair SC)+ RB;
definedStatusCodes: 'status codes' LB  (existentialRuleform SC)+ RB;
definedUnits: 'units' LB  (existentialRuleform SC)+ RB;
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
    
existentialRuleform:
    workspaceName = ObjectName  
    '=' 
    name = QuotedText 
    (description=QuotedText)?; 

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
 
WS: (' ' | '\t')+ -> skip;
NL: ('\r'? '\n')+ -> skip;
LB: '{';
RB: '}';
SC: ';';