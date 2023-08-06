TiDB拥有强大的HTAP能力，TiKV、TiFlash 可按需部署在不同的机器，解决 HTAP 资源隔离的问题。
## 可以通过以下命令开启MPP：
```
ALTER TABLE test.table_name SET TIFLASH REPLICA 1;
```
## 查询是否已开启完成
```
SELECT * FROM information_schema.tiflash_replica WHERE TABLE_SCHEMA = 'test' and TABLE_NAME = 'table_name';
```
如果结果中出现 ExchangeSender 和 ExchangeReceiver 算子，表明 MPP 已生效。

## 以下是本次的实验结果
### 使用的SQL语句如下：
```
SELECT trade_date, AVG(probability) avgProbability FROM test.sentiment_analysis_result_temp  
WHERE stock_name = 'GOOG' and trade_date between '2015-01-01' and '2015-01-31'
  GROUP BY trade_date ORDER by trade_date;
```
### 以下是这个语句执行的耗时统计
* 不开启MPP：是指使用是传统SQL数据库服务器
* 开启MPP：是TiDB能实现OLTP与OLAP的融合，能增强数据分析能力

| 数据量 | 不开启MPP | 开启MPP |
| ------ | --------- | ------- |
| 10w    | 60ms      | 20ms    |
| 10w    | 57ms      | 17ms    |
| 10w    | 59ms      | 18ms    |
| 10w    | 61ms      | 19ms    |

可以看出，开启MPP比不开启MPP（传统关系型数据库模式）快三倍。

## 总结
TiDB能解决传统关系型数据库的一大难题：数据分析性能弱，导致了OLTP和OLAP两类的应用需要两套数据架构，而TiDB则将两者进行了整合，无需搭建两套数据体系，大幅节省了应用数据成本，TiDB serverless直接在线提供服务，让开发者可以开箱即用，省心省时省力。
