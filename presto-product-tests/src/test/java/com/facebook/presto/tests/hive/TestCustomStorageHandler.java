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
package com.facebook.presto.tests.hive;

import com.teradata.tempto.ProductTest;
import com.teradata.tempto.Requirement;
import com.teradata.tempto.RequirementsProvider;
import com.teradata.tempto.Requires;
import com.teradata.tempto.configuration.Configuration;
import org.testng.annotations.Test;

import static com.facebook.presto.tests.TestGroups.HIVE_CONNECTOR;
import static com.facebook.presto.tests.TestGroups.HIVE_CONNECTOR_014;
import static com.facebook.presto.tests.hive.JsonTableDefinition.SIMPLE_JSON_TABLE;
import static com.teradata.tempto.assertions.QueryAssert.Row.row;
import static com.teradata.tempto.assertions.QueryAssert.assertThat;
import static com.teradata.tempto.fulfillment.table.TableRequirements.immutableTable;
import static com.teradata.tempto.query.QueryExecutor.query;

final class JsonRequirements
        implements RequirementsProvider
{
    @Override
    public Requirement getRequirements(Configuration configuration)
    {
        return immutableTable(SIMPLE_JSON_TABLE);
    }
}

public class TestCustomStorageHandler extends ProductTest
{
    /***
     * create hive table that uses custom json storage handler (binary from www.congiu.net/hive-json-serde/1.3/cdh5/json-serde-1.3-jar-with-dependencies.jar)
     * assert content of select * from hive-json-table via presto
     */

    @Requires(JsonRequirements.class)
    @Test(groups = {HIVE_CONNECTOR, HIVE_CONNECTOR_014})
    public void testSelectFromJsonTable()
    {
        assertThat(query("SELECT * " +
                "FROM simple_json_table")).containsOnly(
                row(
                        "hello!",
                        true,
                        123.456));
    }
}
