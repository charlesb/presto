-- database: presto; groups: aggregate; tables: orders
select round(covar_pop(o_totalprice, o_orderkey), 1) , round(covar_samp(o_totalprice, o_orderkey),1) from orders
