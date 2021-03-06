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
package com.facebook.presto.operator.aggregation.state;

import com.facebook.presto.util.array.ObjectBigArray;

import java.math.BigInteger;

import static java.util.Objects.requireNonNull;

public class BigIntegerStateFactory
        implements AccumulatorStateFactory<BigIntegerState>
{
    @Override
    public BigIntegerState createSingleState()
    {
        return new SingleBigIntegerState();
    }

    @Override
    public Class<? extends BigIntegerState> getSingleStateClass()
    {
        return SingleBigIntegerState.class;
    }

    @Override
    public BigIntegerState createGroupedState()
    {
        return new GroupedBigIntegerState();
    }

    @Override
    public Class<? extends BigIntegerState> getGroupedStateClass()
    {
        return GroupedBigIntegerState.class;
    }

    public static class GroupedBigIntegerState
            extends AbstractGroupedAccumulatorState
            implements BigIntegerState
    {
        private final ObjectBigArray<BigInteger> bigIntegers = new ObjectBigArray<>();

        @Override
        public void ensureCapacity(long size)
        {
            bigIntegers.ensureCapacity(size);
        }

        @Override
        public BigInteger getBigInteger()
        {
            return bigIntegers.get(getGroupId());
        }

        @Override
        public void setBigInteger(BigInteger value)
        {
            requireNonNull(value, "value is null");
            bigIntegers.set(getGroupId(), value);
        }

        @Override
        public long getEstimatedSize()
        {
            return bigIntegers.sizeOf();
        }
    }

    public static class SingleBigIntegerState
            implements BigIntegerState
    {
        public static final int BIG_INTEGER_APPROX_SIZE = 4 * 16 + 64;
        private BigInteger bigInteger;

        @Override
        public BigInteger getBigInteger()
        {
            return bigInteger;
        }

        @Override
        public void setBigInteger(BigInteger bigInteger)
        {
            this.bigInteger = bigInteger;
        }

        @Override
        public long getEstimatedSize()
        {
            if (bigInteger == null) {
                return 0;
            }
            return BIG_INTEGER_APPROX_SIZE;
        }
    }
}
