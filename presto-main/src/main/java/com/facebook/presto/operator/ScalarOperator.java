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
package com.facebook.presto.operator;

import com.facebook.presto.spi.Page;
import com.facebook.presto.spi.PrestoException;
import com.facebook.presto.spi.type.Type;
import com.google.common.collect.ImmutableList;

import java.util.List;

import static com.facebook.presto.spi.StandardErrorCode.SUBQUERY_MULTIPLE_ROWS;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

public class ScalarOperator
        implements Operator
{
    public static class ScalarOperatorFactory
            implements OperatorFactory
    {
        private final int operatorId;
        private final Type type;
        private boolean closed;

        public ScalarOperatorFactory(int operatorId, Type type)
        {
            this.operatorId = operatorId;
            this.type = requireNonNull(type, "type is null");
        }

        @Override
        public List<Type> getTypes()
        {
            return ImmutableList.of(type);
        }

        @Override
        public Operator createOperator(DriverContext driverContext)
        {
            checkState(!closed, "Factory is already closed");
            OperatorContext operatorContext = driverContext.addOperatorContext(operatorId, ScalarOperator.class.getSimpleName());
            return new ScalarOperator(operatorContext, type);
        }

        @Override
        public void close()
        {
            closed = true;
        }
    }

    private final OperatorContext operatorContext;
    private final Type type;
    private boolean finishing = false;
    private Page page;
    private int rowsCount = 0;

    public ScalarOperator(OperatorContext operatorContext, Type type)
    {
        this.operatorContext = requireNonNull(operatorContext, "operatorContext is null");
        this.type = requireNonNull(type, "types is null");
    }

    @Override
    public OperatorContext getOperatorContext()
    {
        return operatorContext;
    }

    @Override
    public List<Type> getTypes()
    {
        return ImmutableList.of(type);
    }

    @Override
    public void finish()
    {
        finishing = true;
    }

    @Override
    public boolean isFinished()
    {
        return finishing && page == null;
    }

    @Override
    public boolean needsInput()
    {
        return !finishing;
    }

    @Override
    public void addInput(Page page)
    {
        requireNonNull(page, "page is null");
        checkState(this.page == null, "Operator contains non yet processed page");
        this.page = page;
    }

    @Override
    public Page getOutput()
    {
        try {
            if (page != null && page.getPositionCount() > 0) {
                rowsCount += page.getPositionCount();
                if (rowsCount > 1) {
                    throw new PrestoException(SUBQUERY_MULTIPLE_ROWS, "Scalar sub-query has returned multiple rows");
                }
            }
            return page;
        }
        finally {
            page = null;
        }
    }
}
