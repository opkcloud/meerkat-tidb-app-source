TiDB has powerful HTAP capabilities, TiKV and TiFlash can be deployed on different machines on demand to solve the problem of HTAP resource isolation.
## MPP can be enabled with the following command:
```
ALTER TABLE test.sentiment_analysis_result SET TIFLASH REPLICA 1;
```
## Whether the query has been opened is complete
```
SELECT * FROM information_schema.tiflash_replica WHERE TABLE_SCHEMA = 'test' and TABLE_NAME = 'sentiment_analysis_result';
```

## The following are the results of this experiment
Use the Yes statement as follows:
```
SELECT trade_date, AVG(probability) avgProbability FROM test.sentiment_analysis_result
WHERE stock_name = 'GOOG' and trade_date between '2015-01-01' and '2015-01-31'
  GROUP BY trade_date ORDER by trade_date;
```
The following are the statistics of the time taken to execute this statement:
Do not enable MPP: Use a traditional SQL database server
Enable MPP: TiDB can achieve the integration of OLTP and OLAP

| Data volume | MPP | is not enabled Turn on MPP |
| ------ | --------- | ------- |
| 10w    | 60ms      | 20ms    |
| 10w    | 57ms      | 17ms    |
| 10w    | 59ms      | 18ms    |
| 10w    | 61ms      | 19ms    |

It can be seen that enabling MPP is three times faster than not enabling MPP which is traditional relational database schema.

## Summary
TiDB can solve a major problem of traditional relational databases: weak data analysis performance, resulting in OLTP and OLAP applications requiring two sets of data architecture, while TiDB integrates the two, without building two sets of data systems, greatly saving application data costs, TiDB serverless directly provides services online, so that developers can use it out of the box, save worry and time.
