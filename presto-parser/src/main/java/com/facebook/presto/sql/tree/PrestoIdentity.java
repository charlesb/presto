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

import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.requireNonNull;

public class PrestoIdentity
        extends Node
{
    private final QualifiedName name;

    public PrestoIdentity(QualifiedName name)
    {
        this.name = requireNonNull(name, "user/role is null");
    }

    public QualifiedName getName()
    {
        return name;
    }

    @Override
    public <R, C> R accept(AstVisitor<R, C> visitor, C context)
    {
        return visitor.visitNode(this, context);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name);
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
        PrestoIdentity o = (PrestoIdentity) obj;
        return Objects.equals(name, o.name);
    }

    @Override
    public String toString()
    {
        return toStringHelper(this)
                .add("name", name)
                .toString();
    }
}