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
package com.facebook.presto.spi.security;

import com.facebook.presto.spi.PrestoException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.facebook.presto.spi.StandardErrorCode.NOT_SUPPORTED;
import static java.util.Objects.requireNonNull;

public class Privilege
{
    public enum PrivilegeType
    {
        SELECT,
        INSERT,
        DELETE
    }

    private final PrivilegeType privilegeType;

    public Privilege(PrivilegeType privilegeType)
    {
        this.privilegeType = requireNonNull(privilegeType, "privilegeType is null");
    }

    public Privilege(String privilegeTypeString)
    {
        switch (privilegeTypeString) {
            case "SELECT":
                privilegeType = PrivilegeType.SELECT;
                break;
            case "INSERT":
                privilegeType = PrivilegeType.INSERT;
                break;
            case "DELETE":
                privilegeType = PrivilegeType.DELETE;
                break;
            default:
                throw new PrestoException(NOT_SUPPORTED, "Unsupported privilege: " + privilegeTypeString);
        }
    }

    public String getTypeString()
    {
        switch (this.privilegeType) {
            case SELECT:
                return "SELECT";
            case INSERT:
                return "INSERT";
            case DELETE:
                return "DELETE";
        }
        return null;
    }

    public static List<Privilege> getAllPrivileges()
    {
        List<Privilege> privileges = new ArrayList<Privilege>();
        for (PrivilegeType value : PrivilegeType.values()) {
            privileges.add(new Privilege(value));
        }
        return privileges;
    }

    public PrivilegeType getPrivilegeType()
    {
        return privilegeType;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(privilegeType);
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
        Privilege o = (Privilege) obj;
        return Objects.equals(privilegeType, o.getPrivilegeType());
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("Privilege{");
        sb.append("privilegeType=").append(privilegeType).append("}");
        return sb.toString();
    }
}
