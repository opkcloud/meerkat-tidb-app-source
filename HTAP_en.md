TiDB is good at HTAP. TiKV and TiFlash can be configured on different machines to solve the problem of HTAP resource isolation.
## 1.Enable MPP(masively parallel processiong) with the following command:
````
ALTER TABLE test.table_name SET TIFLASH REPLICA 1;
````
## 2.Query whether TiDB has been opened and completed
````
SELECT * FROM information_schema.tiflash_replica WHERE TABLE_SCHEMA = 'test' and TABLE_NAME = 'table_name';
````
If ExchangeSender and ExchangeReceiver operators appear in the result, MPP has taken effect.
![image](https://github.com/yuan2006/meerkat-tidb-app-source/assets/37364170/90bfb0b1-0bd4-46ce-af8b-f943a84e1304)


## 3.The following are the results of this experiment
###The SQL statement used is as follows:
````
SELECT trade_date, AVG(probability) avgProbability FROM test.sentiment_analysis_result_temp
WHERE stock_name = 'GOOG' and the transaction date is between '2015-01-01' and '2015-01-31'
  Group by transaction date Sort by transaction date;
````
### 4.The following is the running statistics of the execution of the statement
* Do not enable MPP: refers to the use of traditional SQL database server
* Enable MPP: TiDB can realize the integration of OLTP and OLAP, it is HTAP.

| table data record total count | statement type | task type(cop)  | task type(MPP[TiFlash]) |
| ----------------------------- | -------------- | ----- | ------------ |
| 10w                           | avg + group by | 60ms  | 20ms         |
| 10w                           | avg + group by | 57ms  | 17ms         |
| 10w                           | avg + group by | 59ms  | 18ms         |
| 10w                           | avg + group by | 61ms  | 19ms         |
| 170w                          | avg + group by | 426ms | 20ms         |
| 170w                          | avg + group by | 452ms | 19ms         |
| 170w                          | avg + group by | 414ms | 19ms         |
| 170w                          | avg + group by | 457ms | 20ms         |
| 170w                          | count          | 366ms | 34ms         |
| 170w                          | count          | 288ms | 27ms         |
| 170w                          | count          | 270ms | 27ms         |
| 170w                          | count          | 271ms | 27ms         |

Enabling MPP is three to tweenty times faster than not enabling MPP (traditional relational database mode). 
