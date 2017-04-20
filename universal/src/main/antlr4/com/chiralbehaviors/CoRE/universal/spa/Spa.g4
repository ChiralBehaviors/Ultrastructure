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

 tokens {
     HIDDEN
 }

 spa
 :
     'spa' '{' name description root frame? route+ '}' EOF
 ;

 name
 :
     'name:' StringValue
 ;

 description
 :
     'description:' StringValue
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
         fieldAction+
     )?
 ;

 fieldAction
 :
     NAME '{' create? update? delete? navigate? launch? '}'
 ;

 extract
 :
     '{' extraction+ '}'
 ;

 extraction
 :
     NAME ':' Spath
 ;

 title
 :
     'title: ' StringValue
 ;

 query
 :
     'query:' Spath
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
     'create:' action
 ;

 update
 :
     'update:' action
 ;

 delete
 :
     'delete:' action
 ;

 launch
 :
     'launch:'
     (
         frameBy
         | frame
     )?
     (
         Spath
         | UUID
     )
 ;

 navigate
 :
     'navigate:' NAME frameBy? extract?
 ;

 NAME
 :
     [_A-Za-z] [_0-9A-Za-z]*
 ;

 StringValue
 :
     '"'
     (
         ~( ["\\\n\r\u2028\u2029] )
         | EscapedChar
     )* '"'
 ;

 fragment
 EscapedChar
 :
     '\\'
     (
         ["\\/bfnrt]
         | Unicode
     )
 ;

 fragment
 Unicode
 :
     'u' Hex Hex Hex Hex
 ;

 fragment
 Hex
 :
     [0-9a-fA-F]
 ;

 // --------------- IGNORED ---------------

 Ignored
 :
     (
         Whitspace
         | LineTerminator
         | Comment
     ) -> channel ( HIDDEN )
 ;

 fragment
 Comment
 :
     '#' ~[\n\r\u2028\u2029]*
 ;

 fragment
 LineTerminator
 :
     [\n\r\u2028\u2029]
 ;

 fragment
 Whitspace
 :
     [\t\u000b\f\u0020\u00a0]
 ;

 Spath
 :
     (
         '/' NAME
     )+
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
     Hex Hex Hex Hex
 ;

 HEX8
 :
     HEX4 HEX4
 ;
