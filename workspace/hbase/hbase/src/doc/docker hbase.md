部署Hbase
2.1 Docker环境部署
```
# 拉取镜像
docker pull harisekhon/hbase:1.2
# 创建容器
docker run -d --name hbase -p 2181:2181 -p 16010:16010 -p 16020:16020 -p 16030:16030 harisekhon/hbase:1.2

```

```
http://localhost:16010/master-status#replicationStats
```



### hbase 命令行

docker 部署的Hbase需要通过`docker exec -it hbase /bin/bash` 进入Hbase容器内部执行`hbase shell`命令：

```
docker exec -it hbase /bin/bash
```



```
bash-4.4# hbase shell
```



#### 创建表

```
# 表名first ,一个列族 cf1
create 'first','cf1'
# 表名second ,三个列族 cf1,cf2,cf3
create 'second','cf1','cf2','cf3'

```



```
hbase(main):001:0> create 'first','cf1'
Created table first

Took 1.3426 seconds
=> Hbase::Table - first
hbase(main):002:0>
hbase(main):003:0* create 'second','cf1','cf2','cf3'

Created table second
Took 0.7478 seconds
=> Hbase::Table - second
hbase(main):004:0>
hbase(main):005:0* list
TABLE
first
second
2 row(s)
Took 0.0363 seconds
=> ["first", "second"]
```



参考文档：

https://blog.csdn.net/DreamsArchitects/article/details/125436927

https://blog.csdn.net/qq_43842093/article/details/136124349

http://localhost:16010/master-status#replicationStats
