package ru.isands.lib.specification.template.enums;

public enum CriteriaOperation {
    EQUAL("::"),
    NOT_EQUAL("!:"),
    IN("::"), //use with multiple values separated by '|'
    NOT_IN("!:"), //use with multiple values separated by '|'
    LIKE("::"), //use with % in value
    NOT_LIKE("!:"), //use with % in value
    LESS("<<"),
    LESS_OR_EQUAL("<:"),
    GREATER(">>"),
    GREATER_OR_EQUAL(">:");

    private final String operator;
    public String operator(){
        return operator;
    }

    CriteriaOperation(String operator) {
        this.operator = operator;
    }
}
