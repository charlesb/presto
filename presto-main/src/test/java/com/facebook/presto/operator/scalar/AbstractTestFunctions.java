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
package com.facebook.presto.operator.scalar;

import com.facebook.presto.Session;
import com.facebook.presto.metadata.FunctionListBuilder;
import com.facebook.presto.metadata.Metadata;
import com.facebook.presto.metadata.SqlFunction;
import com.facebook.presto.spi.ErrorCodeSupplier;
import com.facebook.presto.spi.PrestoException;
import com.facebook.presto.spi.type.SqlDecimal;
import com.facebook.presto.spi.type.Type;
import com.facebook.presto.sql.analyzer.SemanticErrorCode;
import com.facebook.presto.sql.analyzer.SemanticException;

import java.util.List;

import static com.facebook.presto.spi.StandardErrorCode.AMBIGUOUS_FUNCTION_CALL;
import static com.facebook.presto.spi.StandardErrorCode.INVALID_CAST_ARGUMENT;
import static com.facebook.presto.spi.StandardErrorCode.INVALID_FUNCTION_ARGUMENT;
import static com.facebook.presto.spi.type.DecimalType.createDecimalType;
import static com.facebook.presto.type.UnknownType.UNKNOWN;
import static java.lang.String.format;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public abstract class AbstractTestFunctions
{
    protected FunctionAssertions functionAssertions;

    protected AbstractTestFunctions()
    {
        functionAssertions = new FunctionAssertions();
    }

    protected AbstractTestFunctions(Session session)
    {
        functionAssertions = new FunctionAssertions(session);
    }

    protected void assertFunction(String projection, Type expectedType, Object expected)
    {
        functionAssertions.assertFunction(projection, expectedType, expected);
    }

    protected void assertDecimalFunction(String statement, SqlDecimal expectedResult)
    {
        assertFunction(statement,
                createDecimalType(expectedResult.getPrecision(), expectedResult.getScale()),
                expectedResult);
    }

    protected void assertInvalidFunction(String projection, Type expectedType, String message)
    {
        try {
            assertFunction(projection, expectedType, null);
            fail("Expected to throw an INVALID_FUNCTION_ARGUMENT exception with message " + message);
        }
        catch (PrestoException e) {
            assertEquals(e.getErrorCode(), INVALID_FUNCTION_ARGUMENT.toErrorCode());
            assertEquals(e.getMessage(), message);
        }
    }

    protected void assertInvalidFunction(String projection, String message)
    {
        try {
            evaluateInvalid(projection);
            fail("Expected to throw an INVALID_FUNCTION_ARGUMENT exception with message " + message);
        }
        catch (PrestoException e) {
            assertEquals(e.getErrorCode(), INVALID_FUNCTION_ARGUMENT.toErrorCode());
            assertEquals(e.getMessage(), message);
        }
    }

    protected void assertInvalidFunction(String projection, SemanticErrorCode expectedErrorCode)
    {
        try {
            evaluateInvalid(projection);
            fail(format("Expected to throw %s exception", expectedErrorCode));
        }
        catch (SemanticException e) {
            assertEquals(e.getCode(), expectedErrorCode);
        }
    }

    protected void assertInvalidFunction(String projection, ErrorCodeSupplier expectedErrorCode)
    {
        try {
            evaluateInvalid(projection);
            fail(format("Expected to throw %s exception", expectedErrorCode.toErrorCode()));
        }
        catch (PrestoException e) {
            assertEquals(e.getErrorCode(), expectedErrorCode.toErrorCode());
        }
    }

    protected void assertInvalidCast(String projection)
    {
        try {
            evaluateInvalid(projection);
            fail("Expected to throw an INVALID_CAST_ARGUMENT exception");
        }
        catch (PrestoException e) {
            assertEquals(e.getErrorCode(), INVALID_CAST_ARGUMENT.toErrorCode());
        }
    }

    protected void assertInvalidCast(String projection, String message)
    {
        try {
            evaluateInvalid(projection);
            fail("Expected to throw an INVALID_CAST_ARGUMENT exception");
        }
        catch (PrestoException e) {
            assertEquals(e.getErrorCode(), INVALID_CAST_ARGUMENT.toErrorCode());
            assertEquals(e.getMessage(), message);
        }
    }

    protected void assertAmbiguousCall(String projection, String message)
    {
        try {
            evaluateInvalid(projection);
            fail("Expected to throw an AMBIGUOUS_FUNCTION_CALL exception");
        }
        catch (PrestoException e) {
            assertEquals(e.getErrorCode(), AMBIGUOUS_FUNCTION_CALL.toErrorCode());
            assertEquals(e.getMessage(), message);
        }
    }

    protected void registerScalar(Class<?> clazz)
    {
        Metadata metadata = functionAssertions.getMetadata();
        List<SqlFunction> functions = new FunctionListBuilder(metadata.getTypeManager())
                .scalar(clazz)
                .getFunctions();
        metadata.getFunctionRegistry().addFunctions(functions);
    }

    protected void assertEvaluates(String projection, Type expectedType)
    {
        functionAssertions.tryEvaluate(projection, expectedType);
    }

    private void evaluateInvalid(String projection)
    {
        // type isn't necessary as the function is not valid
        functionAssertions.assertFunction(projection, UNKNOWN, null);
    }
}
