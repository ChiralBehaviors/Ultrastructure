grammar WorkspaceDsl2;

workspace:
    name=Name NL
    description=Description NL
    imports=LB (URI 'as' Name NL)+ RB NL
    products=LB (ExistentialRuleform NL)+ RB
    locations=LB (ExistentialRuleform NL)+ RB
    relationships=LB (RelationshipPair NL)+ RB
    edges=LB (Edge NL)+ RB
    EOF;
    

ObjectName: '"' ('A'..'Z' | 'a'..'z' | ' ')+ '"' ;
Name: '"' ('A'..'Z' | 'a'..'z' | ' ')+ '"' ;
Description: '"' ('A'..'Z' | 'a'..'z' | ' ')+ '"' ;
URI: '"' ('A'..'Z' | 'a'..'z' | ' ')+ '"' ;
ExistentialRuleform: ObjectName WS '=' WS Name ':' Description SC ;
RelationshipPair: ExistentialRuleform '|' ExistentialRuleform SC ;
Edge: ObjectName'.'ObjectName'.'ObjectName'.';
Int: ('0'..'9')+;

WS: (' ' | '\t')+;
NL:  '\r'? '\n';
LB: '{';
RB: '}';
SC: ';';
