/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.sql.tree;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.requireNonNull;

public class PrestoPrivilege
        extends Node
{
    public enum Type
    {
        SELECT,
        INSERT,
        DELETE
    }

    private final Type type;

    public PrestoPrivilege(Type type)
    {
        this.type = requireNonNull(type, "type is null");
    }

    public static List<PrestoPrivilege> getAllPrestoPrivileges()
    {
        ImmutableList.Builder<PrestoPrivilege> list = ImmutableList.builder();
        for (Type value : Type.values()) {
            list.add(new PrestoPrivilege(value));
        }
        return list.build();
    }

    public Type getType()
    {
        return type;
    }

    public String getTypeString()
    {
        switch (this.type) {
            case SELECT:
                return "SELECT";
            case INSERT:
                return "INSERT";
            case DELETE:
                return "DELETE";
        }
        return null;
    }

    @Override
    public <R, C> R accept(AstVisitor<R, C> visitor, C context)
    {
        return visitor.visitPrestoPrivilege(this, context);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(type);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null || (getClass() != obj.getClass())) {
            return false;
        }
        PrestoPrivilege o = (PrestoPrivilege) obj;
        return Objects.equals(type, o.type);
    }

    @Override
    public String toString()
    {
        return toStringHelper(this)
                .add("type", type)
                .toString();
    }
}
