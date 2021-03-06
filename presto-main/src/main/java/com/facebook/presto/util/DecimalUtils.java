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
package com.facebook.presto.util;

import com.facebook.presto.spi.PrestoException;
import com.facebook.presto.spi.StandardErrorCode;
import com.facebook.presto.spi.type.LongDecimalType;
import com.facebook.presto.spi.type.ShortDecimalType;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.facebook.presto.spi.type.LongDecimalType.MAX_DECIMAL_UNSCALED_VALUE;
import static com.facebook.presto.spi.type.LongDecimalType.MIN_DECIMAL_UNSCALED_VALUE;
import static java.lang.Math.abs;

public final class DecimalUtils
{
    private DecimalUtils() {}

    public static boolean overflows(long value, int precision)
    {
        return abs(value) >= ShortDecimalType.tenToNth(precision);
    }

    public static boolean overflows(BigInteger value, int precision)
    {
        return value.abs().compareTo(LongDecimalType.tenToNth(precision)) >= 0;
    }

    public static boolean overflows(BigInteger value)
    {
        return value.compareTo(MAX_DECIMAL_UNSCALED_VALUE) > 0 || value.compareTo(MIN_DECIMAL_UNSCALED_VALUE) < 0;
    }

    public static void checkOverflow(BigDecimal value)
    {
        checkOverflow(value.unscaledValue());
    }

    public static void checkOverflow(BigInteger value)
    {
        if (overflows(value)) {
            // todo determine correct ErrorCode.
            throw new PrestoException(StandardErrorCode.INVALID_FUNCTION_ARGUMENT, "DECIMAL result exceeds 38 digits: " + value);
        }
    }
}
