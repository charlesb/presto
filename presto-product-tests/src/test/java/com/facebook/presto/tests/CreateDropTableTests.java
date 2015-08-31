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
package com.facebook.presto.tests;

import com.facebook.presto.tests.ImmutableTpchTablesRequirements.ImmutableNationTable;
import com.facebook.presto.tests.utils.PrestoDDLUtils.Table;
import com.teradata.tempto.ProductTest;
import com.teradata.tempto.Requires;
import org.testng.annotations.Test;

import static com.facebook.presto.tests.TestGroups.CREATE_DROP_TABLE;
import static com.facebook.presto.tests.utils.PrestoDDLUtils.createPrestoTable;
import static com.teradata.tempto.assertions.QueryAssert.assertThat;
import static com.teradata.tempto.query.QueryExecutor.query;
import static java.lang.String.format;

@Requires(ImmutableNationTable.class)
public class CreateDropTableTests
        extends ProductTest
{
    @Test(groups = CREATE_DROP_TABLE)
    public void shouldCreateTableAsSelect()
            throws Exception
    {
        String tableName = "create_table_as_select";
        try (Table table = createPrestoTable(tableName, "CREATE TABLE %s AS SELECT * FROM nation")) {
            assertThat(query(format("SELECT * FROM %s", table.getNameInDatabase()))).hasRowsCount(25);
        }
    }

    @Test(groups = CREATE_DROP_TABLE)
    public void shouldCreateTableAsEmptySelect()
            throws Exception
    {
        String tableName = "create_table_as_empty_select";
        try (Table table = createPrestoTable(tableName, "CREATE TABLE %s AS SELECT * FROM nation WHERE 0 is NULL")) {
            assertThat(query(format("SELECT * FROM %s", table.getNameInDatabase()))).hasRowsCount(0);
        }
    }

    @Test(groups = CREATE_DROP_TABLE)
    public void shouldCreateTable()
            throws Exception
    {
        String tableName = "create_table_nation";
        try (Table table = createPrestoTable(tableName, "CREATE TABLE %s " +
                "(n_nationkey BIGINT, n_name VARCHAR, n_regionkey BIGINT, n_comment VARCHAR )")) {
            query(format("INSERT INTO %s SELECT * FROM nation", table.getNameInDatabase()));
            assertThat(query(format("SELECT * FROM %s", table.getNameInDatabase()))).hasRowsCount(25);
        }
    }

    @Test(groups = CREATE_DROP_TABLE)
    public void shouldCreateTableIfNotExists()
            throws Exception
    {
        String tableName = "nation";
        Table table = createPrestoTable(tableName, "CREATE TABLE IF NOT EXISTS %s (n_nationkey BIGINT, n_name VARCHAR, n_regionkey BIGINT, n_comment VARCHAR )");
        assertThat(query(format("SELECT * FROM %s", table.getNameInDatabase()))).hasRowsCount(25);
    }

    @Test(groups = CREATE_DROP_TABLE)
    public void shouldDropTableIfNotExists()
            throws Exception
    {
        String tableName = "drop_table_nation";
        Table table = createPrestoTable(tableName, "CREATE TABLE %s (n_nationkey BIGINT, n_name VARCHAR, n_regionkey BIGINT, n_comment VARCHAR )");
        table.executeDropTable();
        query(format("DROP TABLE IF EXISTS %s", table.getNameInDatabase()));
    }
}
