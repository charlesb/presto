-- database: presto; groups: aggregate; tables: orders
select round(cast(covar_pop(o_totalprice, o_orderkey) as numeric), 1) , round(cast(covar_samp(o_totalprice, o_orderkey) as numeric),1) from orders
