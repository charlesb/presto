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
package com.facebook.presto.client;

import com.facebook.presto.spi.type.TypeSignature;
import com.facebook.presto.spi.type.TypeSignatureParameter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.concurrent.Immutable;

import java.util.Objects;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@Immutable
public class ClientTypeSignatureParameter
{
    private final Optional<ClientTypeSignature> typeSignature;
    private final Optional<Long> longLiteral;

    public ClientTypeSignatureParameter(TypeSignatureParameter typeParameterSignature)
    {
        Optional<TypeSignature> typeSignature = typeParameterSignature.getTypeSignature();
        if (typeSignature.isPresent()) {
            this.typeSignature = Optional.of(new ClientTypeSignature(typeSignature.get()));
        }
        else {
            this.typeSignature = Optional.empty();
        }
        longLiteral = typeParameterSignature.getLongLiteral();
    }

    @JsonCreator
    public ClientTypeSignatureParameter(
            @JsonProperty("typeSignature") Optional<ClientTypeSignature> typeSignature,
            @JsonProperty("longLiteral") Optional<Long> longLiteral)
    {
        checkArgument(typeSignature.isPresent() ^ longLiteral.isPresent(), "Only one of the typeSignature and longLiteral must be set");
        this.typeSignature = typeSignature;
        this.longLiteral = longLiteral;
    }

    @JsonProperty
    public Optional<ClientTypeSignature> getTypeSignature()
    {
        return typeSignature;
    }

    @JsonProperty
    public Optional<Long> getLongLiteral()
    {
        return longLiteral;
    }

    @Override
    public String toString()
    {
        if (typeSignature.isPresent()) {
            return typeSignature.get().toString();
        }
        else {
            return longLiteral.get().toString();
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ClientTypeSignatureParameter other = (ClientTypeSignatureParameter) o;

        return Objects.equals(this.typeSignature, other.typeSignature) &&
                Objects.equals(this.longLiteral, other.longLiteral);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(typeSignature, longLiteral);
    }
}
