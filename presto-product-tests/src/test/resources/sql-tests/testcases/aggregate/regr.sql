-- database: presto; groups: mytest; tables: orders
select round(regr_slope(o_totalprice, o_custkey), 4) , round(regr_intercept(o_totalprice, o_orderkey), 4) from orders
