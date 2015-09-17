package graphql.validation.rules;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

public class VariableTypeMismatchTest {

    @Test
    public void testNotNull() {
        GraphQLObjectType queryType = newObject().name("helloWorldQuery")
                                                 .field(newFieldDefinition().type(GraphQLString)
                                                                            .name("hello")
                                                                            .argument(newArgument().name("id")
                                                                                                   .type(new GraphQLNonNull(GraphQLString))
                                                                                                   .build())
                                                                            .argument(newArgument().name("name")
                                                                                                   .type(GraphQLString)
                                                                                                   .build())
                                                                            .staticValue("world")
                                                                            .build())
                                                 .build();

        GraphQLSchema schema = GraphQLSchema.newSchema()
                                            .query(queryType)
                                            .build();

        // This works
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("name", "foo");
        ExecutionResult result = new GraphQL(schema).execute("query m($name : String) {hello(id: \"foo\", name: $name) }");

        assertEquals(result.getErrors()
                           .toString(),
                     0, result.getErrors()
                              .size());

        // This fails
        arguments = new HashMap<>();
        arguments.put("id", "foo");
        result = new GraphQL(schema).execute("query m($id : String) {hello(id: $id) }",
                                             (Object) null, arguments);

        assertEquals(result.getErrors()
                           .toString(),
                     0, result.getErrors()
                              .size());

        System.out.println(result.getData());
    }
}
