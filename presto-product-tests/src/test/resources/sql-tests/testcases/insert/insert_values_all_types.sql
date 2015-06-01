-- database: presto; groups: insert; mutable_tables: datatype|created
-- delimiter: |; ignoreOrder: true; 
--!
insert into ${mutableTables.datatype} values(1,2.34567,'a',cast('2014-01-01' as date), cast ('2015-01-01 03:15:16' as timestamp), TRUE);
select * from ${mutableTables.datatype}
--!
1|2.34567|a|2014-01-01|2015-01-01 03:15:16|true|