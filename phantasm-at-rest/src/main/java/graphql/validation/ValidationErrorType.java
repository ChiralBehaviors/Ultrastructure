package graphql.validation;

public enum ValidationErrorType {

    BadValueForDefaultArg, DefaultForNonNullArgument, FieldsConflict,
    FieldUndefined, FragmentCycle, FragmentTypeConditionInvalid,
    InlineFragmentTypeConditionInvalid, InvalidFragmentType, InvalidSyntax,
    MisplacedDirective, MissingDirectiveArgument, MissingFieldArgument,
    NonInputTypeOnVariable, SubSelectionNotAllowed, SubSelectionRequired,
    UnboundVariable, UndefinedFragment, UndefinedVariable, UnknownArgument,
    UnknownDirective, UnknownType, UnusedFragment, UnusedVariable,
    VariableTypeMismatch, WrongType

}
