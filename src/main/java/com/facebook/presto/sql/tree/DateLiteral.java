package com.facebook.presto.sql.tree;

import com.google.common.base.Objects;

public class DateLiteral
        extends Literal
{
    private final String value;

    public DateLiteral(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public <R, C> R accept(AstVisitor<R, C> visitor, C context)
    {
        return visitor.visitDateLiteral(this, context);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .addValue(value)
                .toString();
    }
}
