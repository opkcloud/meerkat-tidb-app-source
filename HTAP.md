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

## 结论：TiFlash使得TiDB的数据分析性能大幅提升。
