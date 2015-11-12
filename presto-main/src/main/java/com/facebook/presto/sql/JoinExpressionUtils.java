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

package com.facebook.presto.sql;

import com.facebook.presto.sql.tree.ComparisonExpression;
import com.facebook.presto.sql.tree.Expression;
import com.facebook.presto.sql.tree.LikePredicate;
import com.facebook.presto.sql.tree.NotExpression;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;

public class JoinExpressionUtils
{
    private JoinExpressionUtils()
    {
    }

    public static boolean isSupportedJoinConjunct(Expression conjunct)
    {
        return isComparisionExpression(conjunct) || isLikeExpression(conjunct);
    }

    private static boolean isComparisionExpression(Expression conjunct)
    {
        return conjunct instanceof ComparisonExpression;
    }

    private static boolean isLikeExpression(Expression conjunct)
    {
        return conjunct instanceof LikePredicate || (conjunct instanceof NotExpression && ((NotExpression) conjunct).getValue() instanceof LikePredicate);
    }

    public static Expression getLeftExpressionFromJoinConjunct(Expression conjunct)
    {
        checkState(isSupportedJoinConjunct(conjunct), "conjunct form not supported: %s", conjunct);
        if (conjunct instanceof ComparisonExpression) {
            return ((ComparisonExpression) conjunct).getLeft();
        }
        else if (conjunct instanceof LikePredicate) {
            return ((LikePredicate) conjunct).getValue();
        }
        else {
            return ((LikePredicate) ((NotExpression) conjunct).getValue()).getValue();
        }
    }

    public static Expression getRightExpressionFromJoinConjunct(Expression conjunct)
    {
        checkState(isSupportedJoinConjunct(conjunct), "conjunct form not supported: %s", conjunct);
        if (conjunct instanceof ComparisonExpression) {
            return ((ComparisonExpression) conjunct).getRight();
        }
        else if (conjunct instanceof LikePredicate) {
            return ((LikePredicate) conjunct).getPattern();
        }
        else {
            return ((LikePredicate) ((NotExpression) conjunct).getValue()).getPattern();
        }
    }

    public static Optional<Expression> getLikeEscapeFromJoinConjunct(Expression conjunct)
    {
        if (conjunct instanceof LikePredicate) {
            return Optional.ofNullable(((LikePredicate) conjunct).getEscape());
        }
        else if (conjunct instanceof NotExpression && ((NotExpression) conjunct).getValue() instanceof LikePredicate) {
            return Optional.ofNullable(((LikePredicate) ((NotExpression) conjunct).getValue()).getEscape());
        }
        else {
            return Optional.empty();
        }
    }
}
