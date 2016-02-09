### 安装zookeeper:

```shell
brew install zookeeper
cat /usr/local/etc/zookeeper/zoo.cfg
zkServer start
```

### 安装kafka:

```shell
brew install kafka
cat  /usr/local/etc/kafka/zookeeper.properties
kafka-server-start /usr/local/etc/kafka/server.properties
```

### 创建topic:

```shell
kafka-topics --create --topic kafkatopic --replication-factor 1 --partitions 1 --zookeeper localhost:2181
```

### 在一个终端启动生产者:

```shell
kafka-console-producer --broker-list localhost:9092 --sync --topic kafkatopic
```

### 在另一个终端启动消费者:

```shell
kafka-console-consumer --zookeeper localhost:2181 --topic kafkatopic --from-beginning
```

此时,生成者在终端输入消息, 消费者便能接收到。