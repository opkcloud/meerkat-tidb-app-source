TiDB has powerful HTAP capabilities. TiKV and TiFlash can be configured on different machines to solve the problem of HTAP resource isolation.
## Enable MPP with the following command:
````
ALTER TABLE test.table_name SET TIFLASH REPLICA 1;
````
## Query whether TiDB has been opened and completed
````
SELECT * FROM information_schema.tiflash_replica WHERE TABLE_SCHEMA = 'test' and TABLE_NAME = 'table_name';
````
If ExchangeSender and ExchangeReceiver operators appear in the result, MPP has taken effect.

## The following are the results of this experiment
###The SQL statement used is as follows:
````
SELECT trade_date, AVG(probability) avgProbability FROM test.sentiment_analysis_result_temp
WHERE stock_name = 'GOOG' and the transaction date is between '2015-01-01' and '2015-01-31'
  Group by transaction date Sort by transaction date;
````
### The following is the running statistics of the execution of the statement
* Do not enable MPP: refers to the use of traditional SQL database server
* Enable MPP: TiDB can realize the integration of OLTP and OLAP, and can enhance data analysis capabilities

|Data volume |Disable MPP |Enable MPP |
| ------ | --------- | -------- |
| 100,000 | 60 ms | 20 ms |
| 100,000 | 57 ms | 17 ms |
| 100,000 | 59 ms | 18 ms |
| 100,000 | 61 ms | 19 ms |

It can be seen that enabling MPP is three times faster than not enabling MPP (traditional relational database mode).

## Summary
TiDB can solve a major problem of traditional relational databases: weak data analysis performance, resulting in the need for two sets of data architectures for OLTP and OLAP applications. TiDB integrates the two and builds two sets of data systems, which greatly solves the problem. In order to save application costs, TiDB serverless provides services directly online, allowing developers to use it out of the box, saving worry, time and effort.
