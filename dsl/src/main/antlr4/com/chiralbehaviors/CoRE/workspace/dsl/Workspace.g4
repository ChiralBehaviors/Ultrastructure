grammar Workspace;


workspace:
    'workspace:' 
    name=QuotedText 
    (':' description=QuotedText)?
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
    uri =  QuotedText 'as '
    namespace = ObjectName;
    
existentialRuleformDeclaration:
    workspaceName = ObjectName  '=' 
    name = QuotedText 
    (':' description=QuotedText)?;

existentialRuleform:
    existentialRuleformDeclaration SC;

relationshipPairDeclaration:
    existentialRuleformDeclaration '|' existentialRuleformDeclaration;
    
relationshipPair:
    relationshipPairDeclaration SC;

qualifiedName:
    (namespace=ObjectName '::')?
    member=ObjectName;
    
edgeDeclaration:
   parent=qualifiedName
   '.' 
   relationship=qualifiedName 
   '.' 
   child=qualifiedName;
  
edge:
    edgeDeclaration;
    

ObjectName: 'a'..'z' ('A'..'Z' | 'a'..'z' | '0'..'9' | '_')+ ;
QuotedText: '"'(' ' | '!' |'#'.. '~')+ '"';
BlockBegin: LB;
BlockEnd: RB;
Edge: ObjectName'.'ObjectName'.'ObjectName'.';
Int: ('0'..'9')+;
 
WS: (' ' | '\t')+ -> skip;
NL: ('\r'? '\n')+ -> skip;
LB: '{';
RB: '}';
SC: ';';