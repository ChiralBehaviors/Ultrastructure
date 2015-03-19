grammar Workspace;


workspace:
    'workspace:' WS_OP 
    name=QuotedText 
    (WS_OP ':' WS_OP description=QuotedText)?
    NL
    (imports=imported)?
    (agencies='agencies' BlockBegin  (existentialRuleform)+ BlockEnd )?
    (attributes='attributes' BlockBegin  (existentialRuleform)+ BlockEnd )?
    (intervals='intervals' BlockBegin  (existentialRuleform )+ BlockEnd )?
    (locations='locations' BlockBegin  (existentialRuleform)+ BlockEnd )?
    (products='products' BlockBegin  (existentialRuleform)+ BlockEnd )?
    (relationships='products' BlockBegin  (relationshipPair)+ BlockEnd )?
    (statusCodes='status codes' BlockBegin  (existentialRuleform)+ BlockEnd )?
    (units='units' BlockBegin  (existentialRuleform)+ BlockEnd )?
    (edges='edges' BlockBegin (edge)+ BlockEnd )?
    EOF;
    
imported:
    'imports' BlockBegin (workspaces = importedWorkspace)+ BlockEnd;
    

importedWorkspace: 
    uri = WS_OP URL WS_OP  'as '
    namespace = ObjectName (NL)*;
    
existentialRuleformDeclaration:
    workspaceName = ObjectName WS_OP '=' WS_OP
    name = QuotedText 
    (WS_OP ':' description=QuotedText)?;

existentialRuleform:
    existentialRuleformDeclaration SC (NL)*;

relationshipPairDeclaration:
    existentialRuleform WS_OP '|' WS_OP existentialRuleform;
    
relationshipPair:
    relationshipPairDeclaration SC (NL)*;

qualifiedName:
    (namespace=ObjectName '::')?
    member=ObjectName;
    
edgeDeclaration:
   qualifiedName '.' qualifiedName '.' qualifiedName;
  
edge:
    edgeDeclaration (NL)+;
    

ObjectName: 'a'..'z' ('A'..'Z' | 'a'..'z' | '0'..'9' | '_')+ ;
QuotedText: '"' ('A'..'Z' | 'a'..'z' | '0'..'9' | WS)+ '"' ;
URL: '"'(' ' | '!' |'#'.. '~')+ '"';
BlockBegin: WS_OP LB WS_OP (NL)*;
BlockEnd: WS_OP RB WS_OP (NL)*;
Edge: ObjectName'.'ObjectName'.'ObjectName'.';
Int: ('0'..'9')+;

WS_OP: (WS)?;
WS: (' ' | '\t')+;
NL:  '\r'? '\n';
LB: '{';
RB: '}';
SC: ';';