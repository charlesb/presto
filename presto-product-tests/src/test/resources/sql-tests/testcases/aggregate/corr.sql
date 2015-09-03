-- database: presto; groups: mytest; tables: orders
select round(corr(o_totalprice, o_orderkey),6) from orders
