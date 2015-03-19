grammar Workspace;


workspace:
    'workspace:' WS 
    name=QuotedText 
    (WS ':' WS description=QuotedText)?
    NL
    (imports=imported)?
    (agencies='agencies' BlockBegin  (existentialRuleform)+ BlockEnd )?
    (attributes='attributes' BlockBegin  (existentialRuleform)+ BlockEnd )?
    (intervals='intervals' BlockBegin  (existentialRuleform )+ BlockEnd )?
    (locations='locations' BlockBegin  (existentialRuleform)+ BlockEnd )?
    (products='products' BlockBegin  (existentialRuleform)+ BlockEnd )?
    (relationships='relationships' BlockBegin  (relationshipPair)+ BlockEnd )?
    (statusCodes='status codes' BlockBegin  (existentialRuleform)+ BlockEnd )?
    (units='units' BlockBegin  (existentialRuleform)+ BlockEnd )?
    (edges='edges' BlockBegin (edge)+ BlockEnd )?
    EOF;
    
imported:
    'imports' BlockBegin (workspaces = importedWorkspace)+ BlockEnd;
    

importedWorkspace: 
    uri = WS QuotedText WS  'as '
    namespace = ObjectName (NL)*;
    
existentialRuleformDeclaration:
    WS
    workspaceName = ObjectName WS '=' WS
    name = QuotedText 
    (WS ':' WS description=QuotedText)?;

existentialRuleform:
    existentialRuleformDeclaration SC (NL)*;

relationshipPairDeclaration:
    existentialRuleformDeclaration WS '|' existentialRuleformDeclaration;
    
relationshipPair:
    relationshipPairDeclaration SC (NL)*;

qualifiedName:
    (namespace=ObjectName '::')?
    member=ObjectName;
    
edgeDeclaration:
   WS 
   parent=qualifiedName
   '.' 
   relationship=qualifiedName 
   '.' 
   child=qualifiedName;
  
edge:
    edgeDeclaration (NL)+;
    

ObjectName: 'a'..'z' ('A'..'Z' | 'a'..'z' | '0'..'9' | '_')+ ;
QuotedText: '"'(' ' | '!' |'#'.. '~')+ '"';
BlockBegin: WS LB (NL)*;
BlockEnd: RB (NL)*;
Edge: ObjectName'.'ObjectName'.'ObjectName'.';
Int: ('0'..'9')+;
 
WS: (' ' | '\t')+;
NL:  '\r'? '\n';
LB: '{';
RB: '}';
SC: ';';