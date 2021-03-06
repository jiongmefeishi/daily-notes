### k8s集群Job负载支持多个Pod可靠并发执行，如何权衡利弊选择适合的并行计算模式？

```
简单聊聊你对工作负载Job的理解？
```

```
Job 支持多个 Pod 可靠的并发执行，如何权衡利弊选择适合的并行计算模式？
```

```
Job控制并行了解吗？为什么线上实际并行性可能比并行性请求略大或略小？
```

![](https://img-blog.csdnimg.cn/img_convert/b9e8202e02613eb00ac3a7a6aa3e707f.gif)

### 囧么肥事-胡说八道

![](https://img-blog.csdnimg.cn/img_convert/3e5829c3ee57651fbcf98cb2d528187e.png)

![](https://img-blog.csdnimg.cn/img_convert/b98416f4d48c4c9d41b43834dd699265.png)



### 简单聊聊你对工作负载Job的理解？

**在说工作负载Job执行原理之前，先了解下为什么会需要Job负载？**

对于`ReplicaSet`、`ReplicationController`等**持久性负载**来说，它们的职责是让Pod保存预期的副本数量，稳定持久运行。

除非主动去更改模板，进行扩缩操作，否则这些Pod一直持久运行，并且**运行的是持久性任务**，比如`Nginx，MySQL`等。

咦？**持久任务**，那么万事万物有相对面，太极分阴阳，同样，任务除了持久任务外，也有非持久任务。



**身边哪些是非持久任务呢？**

我们在日常的工作中经常都会遇到一些需要进行**批量数据处理和分析、或者是根据时间调度的需求**，这些属于**短期性质的任务**。不需要持久运行，仅执行一次就结束。例如进行**数据库跨库同步，热点数据统计分析**等。也可以在特定时间调度单个任务，例如你想调度低活跃周期的任务。

这些执行完成就结束，完成了我们**设定的某个目标就可以终止的**，我们划分为非持久任务。

好了，既然知道了任务划分，而且k8s中`ReplicaSet`、`ReplicationController`等**持久性负载**是保证Pod稳定持久运行，那么对立的，Job负载的职责就是**保证非持久任务在生命周期内达成使命后体面终止**。

需要Job负载，其实就是对持久性负载的补充。

简单来说：“Pod，你去完成你的任务，完成之后我给你个体面的结束”。

**昙花一现**这个成语形容Job负载管理的Pod非常合适。昙花的目标是绽放，绽放完毕后立刻凋零。Job Pod 完成任务，终结，刚刚好😂。

![](https://img-blog.csdnimg.cn/img_convert/e5d676e73fbd4f2ee6358fc1fd1044ff.gif)

**接下来说说Job负载工作原理**

Job 负载会创建一个或者多个 Pods，执行目标任务，在节点硬件失效或者重启情况下将继续重试 Pods 的执行，直到指定数量的 Pods 成功终止。

随着 Pods 成功结束，Job 跟踪记录成功完成的 Pods 个数。 当数量达到指定的成功个数阈值时，任务（即 Job）结束。 

删除 Job 的操作会清除所创建的全部 Pods。 

挂起 Job 的操作会删除 Job 的所有活跃 Pod，直到 Job 被再次恢复执行。



### Job 支持多个 Pod 可靠的并发执行，如何权衡利弊选择适合的并行计算模式？

**并行计算的模式有好多种，每种都有自己的强项和弱点，如何权衡选择？**

讲个小故事，你是团队组长，手底下有三个得力助手，囧囧，大肥，阿三。

某天，有个重要的项目需要派遣一位助手去洽谈，你需要从中选择一位，他们三个各有优缺点，你需要权衡利弊选择一位。

囧囧，优点：工作负责，技能纯熟，技术型人才，缺点，内向

大肥，优点：工作久，经验丰富，为人圆滑，经验型人才，缺点：太傲娇

阿三，优点：社交能力强，能说会道，社交天花板型人才，缺点：技能相对差些

如果你作为组长，这次是跟客户洽谈合作事宜，你会派遣哪位小可爱呢？😁😁😁



好了，说完小故事，接下来话题回到Job负载。

Kubernetes Job 可以用来支持 Pod 的并发执行，但是呢？需要注意的是，Job 对象并非设计为支持需要紧密相互通信的Pod的并发执行，例如科学计算。

Job 对象支持**并发处理一系列相互独立但是又相互关联的工作任务**，例如：文件转码，发送邮件，渲染视频帧等

**延伸：Job有三种执行方式，能简单说说是什么吗？**

```
第一种：非并行Job
```

**特点**😈

通常只启动一个 Pod，除非该 Pod 失败，会启动替代副本，当 Pod 成功终止时，立即视 Job 为完成状态。

简单理解非并行Job，Job启动后，只运行一个Pod，Pod成功运行结束后整个Job也就立刻结束

可以不设置 `spec.completions` 和 `spec.parallelism`。 这两个属性都不设置时，均取默认值 1。

```
第二种：具有确定完成计数的并行Job
```

**特点**😈

Job 用来代表整个任务，当成功的 Pod 个数达到 `.spec.completions` 时，Job 被视为完成

对于 *确定完成计数* 类型的 Job，应该设置 `.spec.completions` 为**所需要的完成个数**，`.spec.completions` 字段设置为非 0 的正数值， 你可以设置 `.spec.parallelism`，也可以不设置，其默认值为 1。

```
第三种：带工作队列的并行Job
```

**特点**😈

多个 Pod 之间必须**相互协调**，或者借助外部服务**确定每个 Pod 要处理哪个工作条目**

对于一个工作队列 Job，不设置 `spec.completions`，默认值为 `.spec.parallelism`，但要将`.spec.parallelism` 设置为一个非负整数。

```
例如，任一 Pod 都可以从工作队列中取走最多 N 个工作条目
每个 Pod 都可以独立确定其它 Pod 是否已完成，进而确定 Job 是否完成

当感知到 Job 中任何一个Pod成功终止，Job负载不再创建新Pod
一旦至少 1 个 Pod 成功完成，并且所有 Pod 都已终止，即可宣告 Job 成功完成
一旦任何 Pod 成功退出，任何其它 Pod 都不应再对此任务执行任何操作或生成任何输出，所有 Pod 都应启动退出过程
```

了解完Job常规执行方式，下面回归正题，**如何权衡利弊选择适合的并行计算模式？**

先看看常见并行模式方案

**第一种模式，从Job负载的角度对比考虑**

单对单

```
每个工作任务分配一个Job负载
```

单对多

```
一个Job负载负责所有工作任务
```

> 对比结果：
>
> 单对单，每个工作任务一个 Job 对象，可以专注任务，但是会给用户带来一些额外的负担，系统需要管理大量的 Job 对象；
>
> 单对多，每个工作任务一个 Job 对象，更适合处理大量工作任务的场景，节约开销；

**第二种模式，从Pod数量对比考虑**

单对单

```
创建与工作任务相等的Pod
```

单对多

```
每个Pod可以处理多个工作任务
```

> 对比结果
>
> 单对单，Pod的数量与工作任务的数量相等，通常不需要对现有代码和容器做较大改动；
>
> 单对多，每个Pod可以处理多个工作任务，更适合处理大量工作任务的场景，节约开销；

**第三种模式，结合队列服务**

> 需要运行一个队列服务
>
> 需要对已有的程序或者容器做修改，以便其可以配合队列工作
>
> 如果是一个已有的程序，改造时可能存在难度
>
> 与之比较，其他方案在修改现有容器化应用以适应需求方面可能更容易一些

![](https://img-blog.csdnimg.cn/img_convert/95fb867bd55fbff4f94fddc963db2a60.gif)

总结：通过三种模式的简单对比，首先在分配Job个数的时候，如果考虑到任务量较小，而且需要专注于每个任务的进行，那么选择单Job，如果任务量较多，考虑节约资源的情况下，选择使用一个Job来处理大量任务。从Pod层次考虑，则要多**分析一下现有应用的代码改动成本和容器的改造成本**，如果改动较大，任务数量还能接收的情况下，那么建议选择单对单，为每个任务分配一个Pod即可，任务执行完毕资源回收。但是如果**任务量巨大，在短期要求完成，资源储备量有限的情况下，建议单对多**，每个Pod处理多个任务。

至于结合队列服务，通常对现有程序的改动量较大，而且队列消费分配上需要根据实际情况进行占比考虑，是否存在顺序消费问题，是否存在一致性问题等等，非特殊业务需要，建议不轻易考虑，成本较高（时间，工作量，改造难度，资源分配等等综合都较高）。



### Job控制并行了解吗？为什么线上实际并行性可能比并行性请求略大或略小？

> Job并行性：指定是同一时刻处于运行状态，处理任务的 Pods 个数。

并行性请求（`.spec.parallelism`）可以设置为任何非负整数。 

```
如果未设置，则默认为 1。 

如果设置为 0，则 Job 相当于启动之后便被暂停，直到设置>0
```

实际并行性（在任意时刻运行状态的 Pods 个数）可能比并行性请求略大或略小， 原因如下：

- 对于确定完成计数 Job，**实际上并行执行的 Pods 个数不会超出剩余未完成的任务数**。
- 对于 *工作队列* Job，有任何 Job 成功结束之后，不会再有新的 Pod 启动。 不过，剩下的 Pods 会继续执行完毕。
- Job 控制器没有来得及作出响应， Pods 个数可能比请求的数目大，也可能小。
- Job 控制器因为任何原因（例如，缺少 `ResourceQuota` 或者没有权限）无法创建 Pods。
- Job 控制器可能会因为之前**同 一 Job 中 Pod 失效次数过多而压制新 Pod 的创建**。
- 当 Pod 处于体面终止进程中，需要一定时间才能停止。


![](https://img-blog.csdnimg.cn/img_convert/477a68dfe25731a86061e9ba62231dcc.gif)

**Kubernetes 推荐学习书**

> Kubernetes权威指南PDF
> 链接:https://pan.baidu.com/s/11huLHJkCeIPZqSyLEoUEmQ 提取码:sa88



k8s系列所有问题更新记录：[GitHub](https://gitee.com/jiongmefeishi/JMFS-Interview-Notebook-Kubernetes) , [Gitee](https://gitee.com/jiongmefeishi/JMFS-Interview-Notebook-Kubernetes)

