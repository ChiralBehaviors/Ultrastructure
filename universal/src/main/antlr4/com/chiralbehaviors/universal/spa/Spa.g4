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
 grammar Spa;

 spa
 :
     'spa {' name description root frame? route+ '}'
 ;

 name
 :
     'name:' NAME
 ;

 description
 :
     'description:' QuotedText
 ;

 root
 :
     'root:' NAME
 ;

 frame
 :
     'frame:' UUID
 ;

 route
 :
     NAME '{' page '}'
 ;

 page
 :
     name description? title frame? query
     (
         '{'
         (
             NAME '{' create? update? delete? navigate? launch? '}'
         )+ '}'
     )?
 ;

 extract
 :
     '{' extraction+ '}'
 ;

 extraction
 :
     variable ':' Spath
 ;

 title
 :
     QuotedText
 ;

 query
 :
     '`' operationDefinition '`'
 ;

 frameBy
 :
     ' by' Spath
 ;

 action
 :
     frameBy? extract? query 
 ;

 create
 :
     'create' action
 ;

 update
 :
     'update' action
 ;

 delete
 :
     'delete' action
 ;

 launch
 :
     'launch'
     (
         frameBy
         | frame
     )?
     (
         launchBy
         | UUID
     )
 ;

 launchBy
 :
     'by' Spath
 ;

 navigate
 :
     'navigate:' NAME frameBy? extract?
 ;

 operationDefinition
 :
     selectionSet
     | operationType NAME variableDefinitions? directives? selectionSet
 ;

 selectionSet
 :
     '{' selection
     (
         ','? selection
     )* '}'
 ;

 operationType
 :
     'query'
     | 'mutation'
 ;

 selection
 :
     field
     | fragmentSpread
     | inlineFragment
 ;

 field
 :
     fieldName arguments? directives? selectionSet?
 ;

 fieldName
 :
     alias
     | NAME
 ;

 alias
 :
     NAME ':' NAME
 ;

 arguments
 :
     '(' argument
     (
         ',' argument
     )* ')'
 ;

 argument
 :
     NAME ':' valueOrVariable
 ;

 fragmentSpread
 :
     '...' fragmentName directives?
 ;

 inlineFragment
 :
     '...' 'on' typeCondition directives? selectionSet
 ;

 fragmentDefinition
 :
     'fragment' fragmentName 'on' typeCondition directives? selectionSet
 ;

 fragmentName
 :
     NAME
 ;

 directives
 :
     directive+
 ;

 directive
 :
     '@' NAME ':' valueOrVariable
     | '@' NAME
     | '@' NAME '(' argument ')'
 ;

 typeCondition
 :
     typeName
 ;

 variableDefinitions
 :
     '(' variableDefinition
     (
         ',' variableDefinition
     )* ')'
 ;

 variableDefinition
 :
     variable ':' type defaultValue?
 ;

 variable
 :
     '$' NAME
 ;

 defaultValue
 :
     '=' value
 ;

 valueOrVariable
 :
     value
     | variable
 ;

 value
 :
     STRING # stringValue
     | NUMBER # numberValue
     | BOOLEAN # booleanValue
     | array # arrayValue
 ;

 type
 :
     typeName nonNullType?
     | listType nonNullType?
 ;

 typeName
 :
     NAME
 ;

 listType
 :
     '[' type ']'
 ;

 nonNullType
 :
     '!'
 ;

 array
 :
     '[' value
     (
         ',' value
     )* ']'
     | '[' ']'
 ;

 NAME
 :
     [_A-Za-z] [_0-9A-Za-z]*
 ;

 STRING
 :
     '"'
     (
         ESC
         | ~["\\]
     )* '"'
 ;

 BOOLEAN
 :
     'true'
     | 'false'
 ;

 Spath
 :
     (
         '/' NAME
     )+
 ;

 QuotedText
 :
     '"'
     (
         ' '
         | '!'
         | '#' .. '&'
         | '(' .. '~'
     )+ '"'
 ;

 UUID
 :
     HEX8 '-' HEX4 '-' HEX4 '-' HEX4 '-' HEX12
 ;

 HEX12
 :
     HEX4 HEX8
 ;

 HEX4
 :
     HEX HEX HEX HEX
 ;

 HEX8
 :
     HEX4 HEX4
 ;

 fragment
 ESC
 :
     '\\'
     (
         ["\\/bfnrt]
         | UNICODE
     )
 ;

 fragment
 UNICODE
 :
     'u' HEX HEX HEX HEX
 ;

 fragment
 HEX
 :
     [0-9a-fA-F]
 ;

 NUMBER
 :
     '-'? INT '.' [0-9]+ EXP?
     | '-'? INT EXP
     | '-'? INT
 ;

 fragment
 INT
 :
     '0'
     | [1-9] [0-9]*
 ;

 // no leading zeros

 fragment
 EXP
 :
     [Ee] [+\-]? INT
 ;

 // \- since - means "range" inside [...]

 WS
 :
     [ \t\n\r]+ -> skip
 ;
