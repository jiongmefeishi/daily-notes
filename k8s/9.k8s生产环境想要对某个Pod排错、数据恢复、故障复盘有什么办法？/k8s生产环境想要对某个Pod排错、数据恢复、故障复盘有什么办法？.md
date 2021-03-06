### k8s生产环境想要对某个Pod排错、数据恢复、故障复盘有什么办法？

**k8s考点灵魂拷问9连击之5**

```
考点之简单描述一下k8s副本集ReplicaSet有什么作用？
```
```
考点之为什么ReplicaSet将取代ReplicationController控制器？
```
```
考点之编写 ReplicaSet 的 spec 有什么需要注意的点？
```
```
考点之k8s集群中创建非模板 Pod 为什么可能会被副本集自动收纳？
```
```
考点之线上预警k8s集群循环创建、删除Pod副本，一直无法稳定指定目标副本数量?

如果排除了是Pod内部发生了故障，从RS角度你猜测可能是什么原因？
```



**上一期，讨论了前面五个考点，感兴趣去可以**[看一看【点我进入传送门】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484188&idx=1&sn=b8b06c4abe2f5d56556235f867ec10d0&chksm=cf31eacaf84663dc0dd330e4b5d183c06a2c92f8a0e42c8c76b30d67e1ba90381f5bc7c5f780&scene=21#wechat_redirect)，好了，接下来继续看本期4个考点!

```
考点之标签Pod和可识别标签副本集ReplicaSet 先后创建顺序不同，会造成什么影响？
```
```
考点之生产环境想要对某个Pod排错、数据恢复、故障复盘有什么办法？
```
```
考点之缩放 RepliaSet 有哪些算法策略？
```
```
考点之如何去影响淘汰策略，设置单独偏好？
```



### 囧么肥事-胡说八道

![img](https://img-blog.csdnimg.cn/img_convert/d79b0525667d64b15d559fc8e573c522.png)

![img](https://img-blog.csdnimg.cn/img_convert/36471271afac7eef6458c896e0fe9ded.png)




### 考点之标签Pod和可识别标签副本集ReplicaSet 先后创建顺序不同，会造成什么影响？

假设给Pod打上的标签是 AA，同时RS标签选择器设置匹配 AA。

分为两种情况

```
### 预设RS标签和副本数量
RS-AA 标签选择器可识别 AA 标签
设置副本15个

### 预设Pod标签
裸Pod-AA 标志标签 AA
```

**第一种：RS已经创建，裸Pod随后创建**

```
情况一：
	副本等于15个，此时创建 Pod-AA
结果：
	新的 裸Pod-AA 会被该 RS-AA 识别
	副本数 > 15，开启平衡机制
	新Pod立即被 RS 终止并实行删除操作
	
	
情况二：
	副本小于15个，此时创建 Pod-AA
结果：
	裸Pod-AA 创建后立即被 RS-AA识别
	副本数 <= 15，开启平衡机制，收管裸Pod
```

第二种：裸Pod先创建，随后创建RS

```
情况一：
	创建了小于等与15个裸Pod-AA，此时创建 RS-AA
结果：
	RS-AA 创建成功后
	发现存在有AA标签的Pod
	将所有的Pod-AA纳入自己管辖范围
	副本数 < 15，开启平衡机制
	由RS-AA继续创建剩余Pod-AA补充够15个
	
	
情况二：
	创建了大于15个裸Pod-AA，此时创建 RS-AA
结果：
	RS-AA 创建成功后
	发现存在有AA标签的Pod
	将所有的Pod-AA纳入自己管辖范围
	副本数 > 15，开启平衡机制
	RS-AA实行删除多余Pod操作
	直到副本数维持在15个
```

结论：无论RS何时创建，一旦创建，会将自己标签选择器能识别到的所有Pod纳入麾下，接管生存权，遵循RS规约定义的有效副本数，去开启平衡机制，维持有效标签Pod的副本数。

总之，RS**尽力保证**系统当前正在运行的`Pod`数**等于期望状态**里指定的`Pod`数目。

如果想要独立创建可生存的裸Pod，一定要检查所有的RS标签选择器的可识别范围，避免自己创建的裸Pod被收纳接管。

![img](https://img-blog.csdnimg.cn/img_convert/0b21481d0657e24db32bfe0a9220fe3f.gif)


### 考点之生产环境想要对某个Pod排错、数据恢复、故障复盘有什么办法？

如果线上发现有些Pod没**有按照我们期望的状态**来进行运行，发生了某些故障，但是其他同类型Pod却没有发生。

这种故障**一般属于不易复现的故障**，只会在某些偶然性的条件下触发故障，但是这个触发条件我们又不清楚，所以我们要专门针对这个故障进行问题排查。

这个时候又**不希望在排查过程中影响服务的正常响应**，那该怎么办呢？

**隔离法**，所谓隔离法，就是将 Pod 从 ReplicaSet 集合中隔离出来，让Pod脱离RS的管控范围，额有点类似赎身。

可以通过改变标签来从 ReplicaSet 的目标集中移除 Pod。 

这种技术可以用来从服务中**去除 Pod**，以便进行排错、数据恢复等。 

以这种方式移除的 Pod 将被**自动替换**（假设副本的数量没有改变）。

通过隔离这个目标Pod，RS会自动补充副本Pod去保证集群的高可用，我们不必担心影响到服务线的正常响应。这时候就可以针对这个目标Pod做排查，研究，里里外外的想干啥，就干啥，嘿嘿😋。

```
班级（标签666班）
老师（RS-666）
学生15个（学生证标签666班）
-----------------------------

每天上课，老师都检查学生证入班
学生1号：学生证-666班，进去
学生2号：学生证-666班，进去
...
学生20号：学生证-666班，进去


某天，学生9号的学生证被人改了999班
学生1号：学生证-666班，进去
学生2号：学生证-666班，进去
...
学生9号：学生证-999班，老师拦住了9号，不许进
...
学生20号：学生证-666班，进去
```

这个老师跟RS一样，很偏激，只认学生证（RS只认标签），不认人。如果改了标签，就认不出了，自己也不会再去接管了。

![img](https://img-blog.csdnimg.cn/img_convert/f6d584ac691e1d4aaf99c227db8b3217.gif)


### 考点之缩放 RepliaSet 有哪些算法策略？

通过更新 `.spec.replicas` 字段，指定目标Pod副本数量，ReplicaSet 可以很轻松的实现缩放。

而且，ReplicaSet 控制器能确保经过缩放完成留下来的Pod数量不仅**符合要求副本数量**，而且Pod是可用，可操作的。

RS扩容不必说，肯定创建新的Pod副本，纳入管理。

至于缩容，降低集合规模时ReplicaSet 控制器会对所有可用的Pods **进行一次权重排序**，剔除最不利于系统高可用，稳健运行的Pod。

其一般性算法如下：

1. 首先优先选择剔除**阻塞（Pending）且不可调度**的 Pods。
2. 如果设置了 `controller.kubernetes.io/pod-deletion-cost` 注解，则注解值较小的优先被剔除。
3. **所处节点**上副本个数较多的 Pod **优先于所处节点上副本较少者**被剔除。
4. 如果 **Pod 的创建时间不同**，**最近创建的 Pod 优先于早前创建的 Pod 被剔除**。 

如果以上**比较结果都相同**，则**随机剔除**。



### 考点之如何去影响淘汰策略，设置单独偏好？

前说了，RS在进行缩容操作时，有自己的一套**淘汰策略**。**根据四种淘汰策略进行权重排序**，去剔除RS认为**不利于系统稳健运行**的Pod。

同一应用的不同 Pods 可能其**利用率是不同的**。在对应用执行缩容操作时，可能希望移除利用率较低的 Pods。

**那么我们怎么做，才能去影响到RS的淘汰机制，保留我们自己认为需要保留的Pod呢？**

前面提到了`controller.kubernetes.io/pod-deletion-cost` 注解值较小的Pod会优先被剔除。

我们可以通过这个注解去影响RS淘汰机制，设置个人保留偏好。

**那么什么是`controller.kubernetes.io/pod-deletion-cost` 注解？**

此注解设置到 Pod 上，取值范围为 `[-2147483647, 2147483647]`，如果注解值非法，API 服务器会拒绝对应的 Pod。

表示从RS中删除Pod所需要花费的开销。

RS认为**删除开销较小的 Pods 比删除开销较高的 Pods 更容易被删除**，更有利于系统的稳健运行。

不过此机制实施仅是尽力而为，并不能保证一定会影响 Pod 的删除顺序。只能说是爱妃给皇上吹枕边风，真正做出决定的还是皇上。

**注意：**

```
此功能特性处于 Beta 阶段，默认被禁用。
通过为 kube-apiserver 和 kube-controller-manager 设置特性门控 PodDeletionCost 开启功能。
```



![img](https://img-blog.csdnimg.cn/img_convert/b8413bf646495a4754fc98c8b1f9b01a.gif)




-----

 **《Kubernetes-企业级容器应用托管》-持续胡说八道**

第一段：推荐阅读：[【云原生新时代弄潮儿k8s凭什么在容器化方面独树一帜？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484066&idx=1&sn=441fcae466eb5b5fba2fa29f007d7c07&chksm=cf31eb74f8466262ccc258fe1d21fbd8d65e73221c211b704d216d5116a15ffcc4f4cacf5b31#rd)

第二段：推荐阅读：[【趁着同事玩游戏偷偷认识k8s一家子补补课】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484077&idx=1&sn=2ba024c0e121f7ac83e7264bdf7b4dff&chksm=cf31eb7bf846626d02c59837a2f903ed848d8e0f117c80af16b364e858005c57849f0bb82e47#rd)

第三段：推荐阅读：[【Kubernetes家族容器小管家Pod在线答疑❓】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484110&idx=1&sn=cae2e84fb16b9fe5d8a7727c20009b3b&chksm=cf31eb18f846620e3dd1b7b8b9008fd5960363bc6bd3de679225ea5e45f9a48e93d210ccd572#rd)

第四段：推荐阅读：[【同事提出个我从未想过的问题，为什么Kubernetes要"多此一举"推出静态Pod概念？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484122&idx=1&sn=4f913c1e30808622e80a386aa6b4bef8&chksm=cf31eb0cf846621a4cf5ba605ec6fe4141b244dd2b8c49311accba15909f426277d643b6aceb#rd)

第五段：推荐阅读：[【探针配置失误，线上容器应用异常死锁后，kubernetes集群未及时响应自愈重启容器？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484133&idx=1&sn=116c23255e688ca1b86197689bcc8b72&chksm=cf31eb33f8466225400e6bfaac74d5d26de91b85e8f475ecbebedfb8ae08ebd9dde91aec1177#rd)

第六段：推荐阅读：[【kubernetes集群之Pod说能不能让我体面的消亡呀？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484143&idx=1&sn=5e764d67105c34bbaa4c851482dbe5cc&chksm=cf31eb39f846622f8c0aa21afd5d33d3928073de71058d59f974c5498bf84da2681cf76582a8#rd)

第七段：推荐阅读：[【k8s家族Pod辅助小能手Init容器认知答疑？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484153&idx=1&sn=2d6f43036cf2e4cea5fa2aebc4b67ebf&chksm=cf31eb2ff846623904c34e84943576ccf1714d73e042bdc9a4ce584050caf3fc0a85ff5c8908#rd)

第八段：推荐阅读：[【k8s初面考点ReplicaSet副本集极限9连击你懂了吗？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484188&idx=1&sn=b8b06c4abe2f5d56556235f867ec10d0&chksm=cf31eacaf84663dc0dd330e4b5d183c06a2c92f8a0e42c8c76b30d67e1ba90381f5bc7c5f780#rd)

第九段：推荐阅读：[【生产环境想要对某个Pod排错、数据恢复、故障复盘有什么办法？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484206&idx=1&sn=631183744568cd7756cb3d747595c479&chksm=cf31eaf8f84663eed04cfdd2c04dd8f1522a3c5bf43cbf4a24dd2e3a335fc4189f1269627e8f#rd)

第十段：推荐阅读：[【跟k8s工作负载Deployments的缘起缘灭】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484214&idx=1&sn=7a0a593abbcd34347351bcc3ecd6785a&chksm=cf31eae0f84663f61600e14108ebd7b0db326d26d1f78ca7686e685c4de4728af80b54de3b4c#rd)

后续未更新？推荐休闲阅读：[【囧么肥事】](https://mp.weixin.qq.com/mp/appmsgalbum?__biz=Mzg3NjU0NDE4NQ==&action=getalbum&album_id=2218140423993212933#wechat_redirect)

