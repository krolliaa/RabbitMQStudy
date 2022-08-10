# 消息中间件是什么？

消息队列就是消息中间件，消息很好理解你可以理解为信息，那中间件呢？中间件是提供软件和软件之间连接的软件，以便于软件各个部件之间的沟通。**在咖啡店买咖啡这个例子中，微信就是消息中间件。发送方和接收方就是被消息中间件连接的**

# 为什么要使用消息中间件呢？

**同步直接调用 ---> 异步直接调用 ---> 消息中间件**

先来理解下什么是同步直接调用？请看下图：

![](https://img-blog.csdnimg.cn/8c2fd1aed0bc4e4ab161b2b73f6f4ac0.png)

- 可以看到同步直接调用的整个业务调用链非常长，这就会让用户等待的**时间**也变得很**长**。
- 而且**中途**只要有一个环节出现了**故障**就会导致**整个业务瘫痪**掉。这跟初中学习的串联没什么太大区别。
- 并且如果**业务处于高峰期**，此时某一个业务的处理能力比较**拉跨**的话就会导致**整个系统都很拉跨**。

为了解决同步直接调用的这些处理时间长、局部故障导致全局故障，业务高峰期业务能力弱导致整个系统都弱的问题，就得到了**异步直接调用**的解决方案。

![](https://img-blog.csdnimg.cn/a45dfa601fb34ceeb9a0fd6f795dbf16.png)

可以看到异步直接调用只要用户做了操作，就立马响应转换为“处理中”这么一种效果，新建一个异步线程，然后再去处理调用各个组件。异步直接调用解决了：

1. 业务调用链虽然还是挺长，但是解决了用户等待时间长的问题
2. 部分组件故障会导致整个业务瘫痪，可能业务调用链里面的组件还是会发生故障，但我用户的操作返回的结果是没问题的
3. 业务高峰期没有缓冲，这个也解决了，因为用户每次操作都会从线程池中拿一个新的异步线程

**<font color="red">但是异步直接调用这样又带来了新的问题：即如果业务高峰期非常非常爆满的话，那么线程池中的异步线程终究是不够用的，就会导致内存爆满。</font>**

所以就需要一个能够处理高并发的，速度非常快的这么一种东西，它每次有任务一来我就自动快速的去完成这个任务，实现高效率操作。找来找去发现消息中间件是最最合适的。所以我们再将异步直接调用调用改造成了围绕消息中间件打造的这么一种模式：

![](https://img-blog.csdnimg.cn/a21f573f87ff4c30b28ad8fd74b81cbe.png)

![](https://img-blog.csdnimg.cn/32279f40627945efb760a3b81dc4380b.png)

通过这幅图我们就可以感受到消息中间件的妙处了：

- 整个业务调用链很短，都是交给消息中间件去做，用户等待的时间也很短。
- 某个组件故障不会导致整个业务瘫痪
- 业务高峰期还有缓冲
- 业务高峰期时不会产生大量的异步线程【之前在异步直接调用可以发现异步线程非常的长，这种非常长时间的异步线程是很容被`KILL`掉的所以一定要防止这种长时的异步线程出现，比如非常长的`sleep`】

# 消息中间件的作用

- 异步处理
- 系统解耦
- 流量削峰和流控
- 消息广播：一个服务可以发送给多个服务，类似于微信的一人群发
- 消息收集：日志收集系统
- 最终一致性：对方什么时候处理是对方的事情但是存在于消息中间的消息一定会被对方处理掉【即时间问题但是一定可以处理】

总结：**消息中间件就是软件和软件之间发送消息的软件**，消息中间件最大的作用就是**异步消息**、**系统解耦**，此外还有**流量削峰**、**流量控制**、**消息收集**、**消息广播**等特点。

# `RabbitMQ`高性能的原因

- 感受下安卓和苹果的速度，`RabbitMQ`是`erlang`语言开发的，一门专门为**交换机软件开发诞生的编程语言**。
- 适用于分布式系统，面向并发的。
- 而且`erlang`是虚拟机解释运行的可以跨平台部署
- `erlang`的进程间上下文切换效率远高于`C`语言
- 并且有着和`Socket`原生一样的延迟。

`RabbitMQ`饿了么美团中国银行工商银行这些互联网、金融行业有着广泛的应用。

# `RabbitMQ`的底层原理 --- `AMQP`协议

- 协议就是规范，而`AMQP`协议就是`RabbitMQ`的规范，规定了`RabbitMQ`的对外接口。
- 学习`RabbitMQ`本质就是学习`AMQP`协议。

生产者生产消息然后贴上`Routing Key`路由键，这个路由键就相当于快递的收件地址`Queue`。这个快递到快递分拨中心的路程我们称其为`Connection`连接。因为去往快递分拨中心肯定不止一个快递，也许有好多好多个快递正在赶往快递分拨中心，这一条条到快递分拨中心的我们称其为`Channel`叫做一条条信道。然后快递就到快递分拨中心这里，快递分拨中心在消息中间件里头叫做`exchange`就是交换机的意思，到了快递分拨中心以后，快递分拨中心要将快递分拨中心跟收件地址进行绑定不然快递员不知道从哪里拿快递，然后快递员就可以快递到收件地址`Queue`可以是蜂巢，也可以是菜鸟驿站，然后消费者就可以从菜鸟驿站中取出快递拿来消费了。消费者拿快递的通道跟生产者的一样，去往队列的路有好多条，而且此时肯定同时也有好多消费者正在前往拿快递的路上，所以就会有多个信道，这些信道放到一块就组成了连接。【连接其实就是`TCP`连接】

整个快递系统起主导作用的就是消息中间件，这里称呼为`Message Broker`用于接收和分发快递。因为一个`Broker`快递系统忙不过来，所以这个大大的`Message Broker`就创建了好多`Virtual Host`，就是虚拟`Broker`，将多个单元隔开。

- 整个快递系统最核心的组件就是：快递分拨中心 ---> `Exchange`交换机 ---> 它承担了非常重要的功能即`RabbitMQ`的核心功能 ---> **路由转发**

## 作业

使用`draw.io`绘制`AMQP`协议结构图。

![](https://img-blog.csdnimg.cn/9f0e5d464ed5452a89cf95226dd8ad50.png)

# `RabbitMQ`的心脏

`RabbitMQ`的整个核心就是`Exchange`交换机。

- `Exchange`是`AMQP`协议和`RabbitMQ`的**核心组件**。
- `Exchange`的功能是根据**路由键**和**绑定关系**为消息提供路由，将消息转发至相应的队列。

- `Exchange`一共有四种类型：`Direct Topic Fanout Headers`，以前三种为主第四种使用地非常少。

## `Direct Exchange`【直连模式】

- `Message`中的路由键`Routing Key`【快件地址】如果和绑定的菜鸟驿站`Binding Key`是一致的，那么【快递分拨中心】`Exchange`直接将`Message`发送到对应的队列【菜鸟驿站】`Queue`中。

  ![](https://img-blog.csdnimg.cn/13c100680a1f479b85df8138fc797b52.png)

## `Fanout Exchange`【扇形模式】

- 每个分发到`Fanout Exchange`的消息`Message`会将消息分发到所有绑定队列`Queue`上。也就是说会发送到所有这个交换机快递分拨中心绑定的消息队列上。

  ![](https://img-blog.csdnimg.cn/3c5aae51c7b64cb7b6f51ee1ea5a2bbf.png)

  

## `Topic Exchange`【主题模式】

- 根据`Routing Key`以及通配规则【可以跟`Binding Key + #`匹配任意单词】，如果没有通配规则则跟直连模式一样，同时还可以跟`*`匹配一个单词。

## 总结

**<font color="red">推荐：模拟的`RabbitMQ`网站 ---> `http://tryrabbitmq.com/`</font>**

- `AMQP`协议直接决定了`RabbitMQ`的内部结构和外部行为
- 对于发送者来说，它只关系自己的消息有没有发送到特定的【快递分拨中心】交换机`Exchange`中
- 消息通过`Exchange`路由后，到达具体的消息队列
- 消费者将消息从监听的队列中取走

站在`Exchange`交换机这个快递分拨中心的角度上来，我只关心要发送的快递要发到哪里去，来寄快递的人告诉我这个快递要寄到哪里去，然后我就让指定的快递员按指定快递路线去发就可以了。所以**路由键**`Routing Key`就是告诉快递分拨中心我要发到哪里去，而**绑定关系**`Binding Key`就是快递分拨中心指定的快递线路。【**简而言之交换机`Exchange`：不关心怎么来，只关心到哪去**】

**所以说交换机`Exchange`的作用就是：<font color="red">根据路由键和绑定关系为消息提供路由，将消息转发到相应的队列中。</font>**

## 作业

作业：在`tryrabbitmq.com`网站中，搭建`Direct Fanout Topic`三种模式的发布订阅模型。

第一种：`Direct`直连模式 ---> 张三和李四购买了咖啡，直连模式会发送到`1号丰巢快递柜`，张三李四轮询消费。

![](https://img-blog.csdnimg.cn/bbbbb550e8c84ceb9bb2ee3bdfc014d2.png)

第二种：`Fanout`广播模式 ---> 奶茶店老板豪气十足，每个人都送`1`杯饮料，美团乐坏了。

![](https://img-blog.csdnimg.cn/27e9a634acda43fa93d69c003fe015b6.png)

第三种：`Topic`主题模式 --->

- 张三无论热的冰的咖啡都可以，糖分无所谓，老板做啥他喝啥，所以张三的绑定关系绑定了个`*.coffee`
- 李四只喝热咖啡，糖分无所谓，所以李四的绑定关系绑定了个`hot.coffee`
- 王五只喝冰咖啡，糖分无所谓，所以李四的绑定关系绑定了个`cold.coffee`

奶茶店老板不定时送咖啡，可以看到当老板送热咖啡的时候，张三和李四可以收到，当送冰咖啡的时候张三和王五可以收到，看来不挑食可以得到的更多哈哈

![](https://img-blog.csdnimg.cn/b62fac4d47f14594831f9f3c55d14090.png)

某天李四想减肥了，所以不想喝甜的咖啡了，于是李四喝咖啡的标准就是：`hot.unsweet.coffee`。

王五这个人呢就很喜欢吃甜食她也不介意，但是她还是想喝冰的，于是她喝咖啡的标准就成了：`cold.sweet.coffee`

张三这个人还是一贯的不挑食不介意，冰的热的还是甜的她都可以，她就喜欢喝咖啡，没别的要求哈哈

![](https://img-blog.csdnimg.cn/b3ef7e8a0e894b9ea81d4e66f1e8615e.png)

这里张三`#.coffee`，这里的`#`只有在主题模式才有，表示匹配任意多个单词。

- 当有冷的甜的咖啡的时候张三跟王五可以收到。
- 当有热的苦的咖啡的时候张三跟李四可以收到。

# `RabbitMQ`快速安装

`Windows`：

- [RabbitMQ Erlang Version Requirements — RabbitMQ](https://www.rabbitmq.com/which-erlang.html)查找版本映射关系
- [Index - Erlang/OTP](https://www.erlang.org/)下载安装`Erlang`
- [Installing on Windows — RabbitMQ](https://www.rabbitmq.com/install-windows.html)下载安装`RabbitMQ`

配置环境变量，然后直接在`cmd`中输入`rabbitmq-server`即可 ---> 这里的版本是`3.10.7`如果是老一点的版本，可能需要启动插件才可以在网页版中查看`RabbitMQ`消息队列。 ---> `http://localhost:15672`

```shell
rabbitmq-plugins --help
rabbitmq-plugins list 
rabbitmq-plugins rabbitmq_management
```

该网页有六大模块：**概览、连接、信道、交换机、队列、管理**

![](https://img-blog.csdnimg.cn/7f69607850f14ac08da133260c3cbb28.png)

在`RabbitMQ`里头给予了一个默认的交换机即：`AMQP default`，它本质也是直连模式但是简化了直连模式的`Binding Key`，它默认`Binding Key`就是队列名。所以在这个直连模式下，你要实现发布订阅，你的路由键直接跟队列名同名即可。

## 作业

使用`RabbitMQ`管控台配置：直连模式、广播模式、主题模式

- 我们创建三种类型的交换机，交换机是负责给通过路由键和绑定关系给消息提供路由的，分别为：美团直连模式、美团广播模式、美团话题模式。

  ![](https://img-blog.csdnimg.cn/380795f57fc84014ab8fd77642641330.png)

- 然后创建三个队列，队列存储的是消息内容，等待消费者的消费，分别为：1号丰巢快递柜、2号丰巢快递柜、3号丰巢快递柜。

  ![](https://img-blog.csdnimg.cn/3792179435f94af2bd6a52af5a81e2da.png)

1. 测试直连模式：

   针对三个队列绑定关系为：`hot.coffee *.coffee cold.coffee`

   发送消息的路由键为：`hot.coffee cold.coffee`，发送，观察队列情况：

   ![](https://img-blog.csdnimg.cn/3792179435f94af2bd6a52af5a81e2da.png)

   可以看到在直连模式下，消息发送到哪个队列，路由键跟绑定关系是绝对紧密相连的。

2. 测试广播模式：

   针对三个队列绑定关系为：`hot.coffee *.coffee cold.coffee`

   只发送1个消息，路由键为：`hot.coffee`，发送，观察队列情况：

   ![](https://img-blog.csdnimg.cn/84e58285faff41ab803dc36ad1bf9302.png)

   可以看到虽然只发送了一个消息而且路由键为`hot.coffee`而只有1号丰巢快递柜的绑定关系跟这个路由键相匹配，但是所有队列都收到了这个消息，这就是广播模式的发布订阅模型。

3. 测试话题模式：

   针对三个队列绑定关系为：`hot.coffee *.coffee cold.coffee`

   只发送1个消息，路由键为：`hot.coffee`，发送，观察队列情况：

   ![](https://img-blog.csdnimg.cn/2418516c68c04f088c1efe7e91c069d7.png)

   可以看到1号跟2号收到了，但是3号没有收到，奇怪，2号的绑定关系不是`*.coffee`吗？这就是话题模式的精髓所在，它可以通过通配符的方式匹配上路由从而使队列接收到消息。

# `RabbitMQ`命令行工具

- 想看什么就`List`什么
- 想清空什么就`Purge`什么
- 想删除什么就`Delete`什么
- 一切问题记得使用`--help`

查看相关：	

```powershell
查看状态：rabbitmqctl status
查看连接：rabbitmqctl list_connections
查看信道：rabbitmqctl list_channels
查看交换机：rabbitmqctl list_exchanges
查看绑定：rabbitmqctl list_bindings
查看队列：rabbitmqctl list_queues
查看消费者：rabbitmqctl list_consumers
```

队列相关：

```powershell
查看队列：rabbitmqctl list_queues
删除队列：rabbitmqctl delete_queue
清空队列：rabbitmqctl purge_queue
```

用户相关：

```powershell
新建用户：rabbitmqctl add_user
修改用户密码：rabbitmqctl change_password
删除用户：rabbitmqctl delete_user
查看用户：rabbitmqctl list_users
设置用户角色：rabbitmqctl set_user_tags
```

应用相关：

```powershell
启动应用：rabbitmqctl start_app
关闭应用保留（暂停）Erlang虚拟机：rabbitmqctl stop_app
关闭应用并关闭Erlang虚拟机：rabbitmqctl stop
```

集群相关：

```powershell
加入集群：rabbitmqctl join_cluster
离开集群：rabbitmqctl reset
```

镜像队列相关：

```powershell
设置镜像队列：rabbitmqctl sync_queue
取消镜像队列：rabbitmqctl cancel_sync_queue
```

# 总结

- `RabbitMQ`高性能的原因：

  1. 从内看：`Erlang`进程间上下文切换的效率远高于`C Java`，使得`RabbitMQ`的并发能力非常强悍

  2. 从外看：网络性能有着跟原生`Socket`一样的延迟，使得`RabbitMQ`网络`IO`性能极高

- `RabbitMQ`的底层协议`AMQP`协议

- `RabbitMQ`的核心`Exchange`

# `RabbitMQ`消息交换的关键是什么？

**使用`RabbitMQ`本质上就是在使用`AMQP`协议**，`AMQP`协议定义了`RabbitMQ`的内部结构和外部行为。

**在`RabbitMQ`中其消息转换流程如下：**

- 发送者不能将消息直接发送到队列中，而需要发送给交换机
- 交换机通过路由键和绑定关系给消息提供路由，将消息转发到指定队列
- 消费者从队列中取走消息

**合理的交换机和队列设置：**

- 交换机的数量不能过多，一般来说同一个业务，或者同一类业务使用同一个交换机。
- 合理设置队列数量，一般来说一个微服务监听一个队列或者一个微服务的一个业务监听一个队列。
- 合理配置交换机类型，是同`Topic`模式的时候仔细设置绑定关系`Binding Key`以及路由键`Routing Key`

**尽量使用自动化配置：**

- 将创建交换机、队列的操作固化在应用代码中，这样就可以更高效且不易出错免去了复杂的运维操作
- 一般来说，交换机由双方同时声明，队列由接收方声明并配置绑定关系
- 交换机/队列的参数一定要由双方开发团队确认，否则重复声明时若参数不一致会导致生命失败

所以关键就是：**根据`AMQP`协议合理配置交换机和队列并且尽可能的时自动化部署交换机和队列。**

包括：交换机类型、交换机数量、队列数量、双方同时声明交换机跟队列

## 作业

**为什么`AMQP`要设计出`Exchange`消息流转机制呢？**

我是这样想的：

1. 设计出交换机消息流转机制，生产者跟消费者就不用关心转发的事情了，生产者只负责消费，消费者只负责消费，而有关消息转发的事情就交给`Exchange`来做，这样做可以很大程度上使**系统解耦**。
2. 同时生产者生产完毕之后就可以不管消费者有没有去消费了，那么生产者的任务就完成了，它就可以继续干他的事情了，这就产生了**异步消息处理**的好处。大大省去了等待时间。
3. 然后就是关于生产者生产的消息要到哪个地方【这里叫做队列】去被哪个消费者所消费，如果没有`Exchange`交换机去流转消息，那这些工作总得有人去做的，此时当然就是生产者来做了，这样肯定就会降低生产者的效率，而有了交换机可以去做流转，还可以做到**消息广播**跟**消息收集**，将消息分配到符合规则条件的队列中，并且还能做一些记录日志的工作。
4. 最后就是关于流量的问题了，生产者一直负责生产，消费者一直负责消费，假设一下现在突然有了非常非常哒的流量，那么势必生产者会在一瞬间生产大量的消息，此时消费者的消费能力可能是挡不住的，作为生产者和消费者的中间着`Exchange`此时就可以发挥巨大的作用了。它可以针对这巨大的流量做**流量削峰**跟**流量控制**。

所以我猜`AMQP`正是综合以上种种优点才设计出了`Exchange`消息流转机制，它有用：**<font color="red">系统解耦、异步处理、流量削峰、流量控制、消息广播、消息收集</font>**等等好处。

# 什么是微服务架构？

微服务架构就是将应用程序构建为松耦合可以独立部署的一组服务。

- 所谓服务就是一个单一的，可以独立部署的软件组件，实现了一些有用的功能。
- 所谓松耦合就是封装了服务的实现细节，要使用时直接通过`API`调用接口。

那么如何将一个应用程序拆分成一个个的微服务呢？

- 拆分方法一：你可以根据**系统操作**进行微服务拆分，比如查询服务拆成一块，添加服务拆成一块
- 拆分方法二：你可以根据**业务能力**进行微服务拆分，比如订单相关、骑手相关、结算相关等。**【推荐】**
- 拆分方法三：你可以根据**子域**进行微服务拆分

# 实战使用`RabbitMQ`

## 需求分析

- 一个外卖后端系统用户可以在线下单外卖
- 用户在线下单后可以实时查询订单进度
- 系统可以承受短时间内大量并发请求【饭点时间请求会突然变大】

## 架构设计

- 使用微服务系统，组件之间充分解耦

  【你下单 ----------> 商家接单 ----------> 骑手前往、拿外卖、送外卖 ----------> 结算 ----------> 积分】

  - 订单的获取和履行 ----------> 订单微服务

  - 供应商和产品管理 ----------> 商家微服务

  - 送餐、骑手管理 ----------> 骑手微服务

  - 记账和结算 ----------> 结算微服务

  - 积分管理 ----------> 积分微服务

    ![](https://img-blog.csdnimg.cn/77f533ede7964adaa185dcb7c1d66267.png)

- 使用消息中间件，解耦业务逻辑

- 使用数据库，持久化业务数据

整个业务流程如下：

![](https://img-blog.csdnimg.cn/32279f40627945efb760a3b81dc4380b.png)

## 接口设计

- 新建订单接口
- 查询订单接口

## 微服务数据库相关设计

- 每个微服务使用自己的数据库/数据表
- 不要使用共享数据进行通信
- 不要使用外键，对于数据量非常少的表慎用索引【非常消耗资源】

![](https://img-blog.csdnimg.cn/4bf660d106624bb0ae5a984464501e1b.png)

`Sql`语句如下【例子】：

```sql
create table `deliveryman` (
	`id` int NOT NULL AUTO_INCREMENT COMMENT `骑手ID`,
    `name` VARCHAR(36) DEFAULT NULL COMMENT `名称`,
    `status` VARCHAR(36) DEFAULT NULL COMMENT `状态`,
    `date` DATETIME DEFAULT NULL COMMENT `时间`
)ENGINE=InnoDB AUTO_INCREMENT=5 CHARACTER SET=utf-8;
```

**搭建订单数据库：`order_detail.sql`**

```sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `status` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
  `address` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单地址',
  `account_id` int(0) NULL DEFAULT NULL COMMENT '用户id',
  `product_id` int(0) NULL DEFAULT NULL COMMENT '产品id',
  `deliveryman_id` int(0) NULL DEFAULT NULL COMMENT '骑手id',
  `settlement_id` int(0) NULL DEFAULT NULL COMMENT '结算id',
  `reward_id` int(0) NULL DEFAULT NULL COMMENT '积分奖励id',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `date` datetime(0) NULL DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 403 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
```

**搭建商家数据库：`restaurant.sql`**

```sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `restaurant`;
CREATE TABLE `restaurant`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '餐厅id',
  `name` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `address` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址',
  `status` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
  `settlement_id` int(0) NULL DEFAULT NULL COMMENT '结算id',
  `date` datetime(0) NULL DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

INSERT INTO `restaurant` VALUES (1, 'qeqwe', '2weqe', 'OPEN', 1, '2020-05-06 19:19:39');

SET FOREIGN_KEY_CHECKS = 1;
```

**搭建产品数据库：`product.sql`**

```sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '产品id',
  `name` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `price` decimal(9, 2) NULL DEFAULT NULL COMMENT '单价',
  `restaurant_id` int(0) NULL DEFAULT NULL COMMENT '地址',
  `status` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
  `date` datetime(0) NULL DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

INSERT INTO `product` VALUES (2, 'eqwe', 23.25, 1, 'AVALIABLE', '2020-05-06 19:19:04');

SET FOREIGN_KEY_CHECKS = 1;
```

**搭建骑手数据库：`deliveryman.sql`**

```sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `deliveryman`;
CREATE TABLE `deliveryman`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '骑手id',
  `name` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `status` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
  `date` datetime(0) NULL DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

INSERT INTO `deliveryman` VALUES (1, 'wangxiaoer', 'AVALIABLE', '2020-06-10 20:30:17');

SET FOREIGN_KEY_CHECKS = 1;
```

**搭建结算数据库：`settlement.sql`**

```sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `settlement`;
CREATE TABLE `settlement`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '结算id',
  `order_id` int(0) NULL DEFAULT NULL COMMENT '订单id',
  `transaction_id` int(0) NULL DEFAULT NULL COMMENT '交易id',
  `amount` decimal(9, 2) NULL DEFAULT NULL COMMENT '金额',
  `status` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
  `date` datetime(0) NULL DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1168 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
```

**搭建积分数据库：`reward.sql`**

```sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `reward`;
CREATE TABLE `reward`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '奖励id',
  `order_id` int(0) NULL DEFAULT NULL COMMENT '订单id',
  `amount` decimal(9, 2) NULL DEFAULT NULL COMMENT '积分量',
  `status` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
  `date` datetime(0) NULL DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
```

## 搭建环境

`pom.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.2</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.kk</groupId>
    <artifactId>RabbitMQStudy</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>RabbitMQStudy</name>
    <description>RabbitMQStudy</description>
    <properties>
        <java.version>1.8</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.2.2</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
            <version>2.7.2</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

