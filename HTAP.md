TiDB拥有强大的HTAP能力，
## 可以通过以下命令开启MPP：
```
ALTER TABLE test.table_name SET TIFLASH REPLICA 1;
```
## 查询是否已开启完成
```
SELECT * FROM information_schema.tiflash_replica WHERE TABLE_SCHEMA = 'test' and TABLE_NAME = 'table_name';
```

## 以下是本次的实验结果

### 未开启TiFlash进行一个统计数据查询的结果如下：

### 开启TiFlash后进行统计数据查询的结果如下：

## 总结
TiDB能解决传统关系型数据库的一大难题：数据分析性能弱，导致了OLTP和OLAP两类的应用需要两套数据架构，而TiDB则将两者进行了整合，无需搭建两套数据体系，大幅节省了应用数据成本，TiDB serverless直接在线提供服务，让开发者可以开箱即用，省心省时省力。
