-- database: presto; groups: aggregate; tables: orders
select round(covar_pop(o_totalprice, o_orderkey), 4) , round(covar_samp(o_totalprice, o_orderkey),4) from orders
