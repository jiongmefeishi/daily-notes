### k8s初面考点ReplicaSet副本集极限9连击你懂了吗？

**k8s考点灵魂拷问9连击**

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



![](https://img-blog.csdnimg.cn/img_convert/09932184de031d5e9783f69f497a2103.gif)

### 囧么肥事-胡说八道

![](https://img-blog.csdnimg.cn/img_convert/23c293759d7fddbf59c0324d6b45e930.png)

![](https://img-blog.csdnimg.cn/img_convert/c8c7b64c21f16e4714fd63062ea862c8.png)





### 考点之k8s副本集ReplicaSet有什么作用？

`ReplicaSet`的主要作用是控制副本数量的，这里的每一个副本就是一个`Pod`，`ReplicaSet`它是用来确保我们有指定数量的`Pod`副本正在运行的`Kubernetes`控制器，这里为了方便后面统一把`ReplicaSet`简称 RS。

**进一步说什么是控制副本数量？**

RS确保Pod以你指定的副本数运行，即如果有容器异常退出，会自动创建新的 Pod 来替代，而异常多出来的容器也会自动回收。

假设k8s集群中，你想要运行10个Pod，如果这时候有4个Pod发生故障，异常退出，那么RS会自动创建新的4个Pod来替代发生故障的4个Pod。

RS**尽力保证**系统当前正在运行的`Pod`数**等于期望状态**里指定的`Pod`数目。

你想要10个，那么RS就尽可能保证在任何时候都给你提供10个，没有就创建，多了就删除。

**总之，ReplicaSet 尽可能确保任何时间都有指定数量的 Pod 副本在运行。**

### 考点之为什么ReplicaSet将取代ReplicationController控制器？

`ReplicationController`控制器（简称为RC）。

在之前旧版本的k8s中，使用的是RC控制器实现了k8s集群的高可用性，它跟现在的RS控制器作用类似，作用是，确保Pod以指定的副本数运行。

ReplicaSet继承了RC的功能，并实现了扩展，主要突出扩展是更强大的**标签选择能力** ，即selector。

**进一步说什么是标签选择能力？**

`ReplicaSet`会通过标签选择器（`Label-Selecto`r）管理所有被打上与选择器匹配的标签的容器。

**下面通过一段拟人对白，来理解什么是标签选择：**

RS说：”嘿嘿，我要管理被打上 A，AA，AAA标签的Pod，都不许跑，听我指挥，排队站好，🧍🏻立正，向前看！“

Pod-001说：”我被打上了BBB标签，我才不归你管呢！“

Pod-002说：”我被打上了AA标签，快来接管我吧，我准备好了“

Pod-003说：”呜呜，我想独立，我不想被RS管，我要做一个自由自在的孩子，但是不幸的是，我被打上了A标签，RS给我管的紧紧的，我失去了自由，我好可怜呀“

`ReplicationController`自己也有标签选择能力，但是它只能选择包含某个标签的匹配Pod；

ReplicaSet的选择器在基础上增加了允许匹配缺少某个标签的pod，或包含特定标签名的Pod；

**举个例子**

两组Pod，env标签分别是`production`和`devel`

> Pod-A     env=production
>
> Pod-B     env=devel

RC 只能匹配其中的Pod-A或者Pod-B中的一个；

RS 则可以同时可以匹配并将它们视为一个大组，无论标签env的值具体是什么（env=*）,都可以标签名来进行匹配；

![](https://img-blog.csdnimg.cn/img_convert/5b5522ef70c7db3aa263b1243984b55e.png)



### 考点之编写 ReplicaSet 的 spec 有什么需要注意的点？

类似其他`Kubernetes API` 对象，RS也需要指定 `apiVersion`、`kind`、和 `metadata` 字段。

1. 对于 `ReplicaSets` 而言，其 `kind` 始终是 ReplicaSet。
2. `ReplicaSet` 对象的名称必须是合法的 DNS 子域名
3. 属性`.spec.template` 是一个Pod 模版， 要求设置标签，注意不要将标签与其他控制器的`标签选择器重叠`
4. 属性`.spec.template.spec.restartPolicy` 指定模板的重启策略 ，允许的取值是 `Always`
5. 属性`.spec.selector` 字段是一个`标签选择器` 用来筛选匹配标签的Pod归属
6. 在 ReplicaSet 中，`.spec.template.metadata.labels` 的值必须与 `spec.selector` 值 相匹配，否则该配置会被 API 拒绝。



### 考点之k8s集群中创建非模板 Pod 为什么可能会被副本集自动收纳？

前面提到了，RS采用了最新的标签选择能力，通过指定`.spec.selector`标签选择器，不仅可根据标签值，甚至连标签名一致都可以进行匹配。

首先如果采用Pod模板创建Pod，会被指定标签，**RS会根据标签自动接管Pod**。

**再来看看非模板**

非模板创建，其实就是直接创建裸的 Pods。

**为什么可能会被副本集RS自动接管？**

除非在创建裸Pod的时候，你确保这些裸的 Pods 并不包含可能与你的某个 ReplicaSet 的`.spec.selector`相匹配的标签。

在创建裸Pods前，必须完全排除跟任何RS有可能相同的标签，否则，RS认为你创建的Pod 就是要指定给自己接管的。



### 考点之线上预警k8s集群循环创建、删除Pod副本，一直无法稳定指定目标副本数量，排除了是Pod内部发生了故障，从RS角度你猜测可能是什么原因？

首先理解一下问题，循环创建Pod副本？

RS一直在正常工作，维持Pod副本数量，缺少就创建，多了就删除。问题来了，一直创建，然后又删除，却不能稳定Pod副本数量？

看下这个**循环过程**

```
RS指定Pod副本数量10个

当前副本7个
RS检测不够10个
RS开启平衡机制，创建2个维持稳定

再检测发现 15个
RS开启平衡机制，删除5个维持稳定

再检测发现13个
RS开启平衡机制，删除3个维持稳定

再检测发现9个
RS开启平衡机制，增加1个维持稳定

再检测发现10个
无需稳定

再检测发现8个...
再检测发现18个...
```

总之，RS检测副本数量，不是比10个多，就是比10少，**一直难以维持10个有效副本**。

既然排除了是Pod内部故障问题，那么从RS角度进行可能分析，可以初步判定是多个RS标签选择器规则重复导致的。

**分析初步判定原因**

`ReplicaSet`会通过标签选择器（Label-Selector）管理所有带有与选择器匹配的标签的容器。

创建`Pod`时，它会认为所有`Pod` 是一样的，是无状态的，所以在创建顺序上不会有先后之分。

使用相同的标签选择器创建多个`ReplicaSet`，则多个RS无法识别哪个Pod是自己创建的，都会认为是归属于自己管理的Pod。

例如

```
第一个 RS-A，指定副本数量 10
标签选择器可以匹配 env=xxx

RS-A生成10个Pod标签为 env=xxx
一组Pod：
Pod-1(env=xxx)
Pod-2(env=xxx)
Pod-3(env=xxx)
...
...
Pod-10(env=xxx)


这时候创建了一个RS-B

第二个 RS-B，指定副本数量 25
标签选择器和 RS-A 相同
标签选择器可以匹配 env=xxx


因为选择器匹配一样
RS-B 匹配到了RS-A创建的10个Pod
RS-B 发现Pod-x（env=xxx）数量不够25

RS-B 继续创建额外的10个

Pod-11(env=xxx)
Pod-12(env=xxx)
Pod-13(env=xxx)
...
...

此时RS-A 发现自己匹配的Pod > 10
它认为是自己创建多了
启动平衡机制

删除超过 10 个的额外Pod
删除 Pod-Xi(env=xxx)

而RS-B 发现自己匹配的Pod < 25
就启动平衡机制
创建 Pod-Xi(env=xxx)



就这样
一个不停的创建
一个不停的删除

最终总是无法满足稳定数量的 10 和 25
```

双方的当前状态始终**不等于期望状态，这就会引发问题**，因此确保`ReplicaSet`标签选择器的唯一性这一点很重要。

-----

本期暂时讨论上述5点，下期完成下面4点
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

![](https://img-blog.csdnimg.cn/img_convert/ffe082865e945fb41a498ad0dbdcf6a9.gif)




----



 **《Kubernetes-企业级容器应用托管》-持续胡说八道**

第一段：推荐阅读：[【云原生新时代弄潮儿k8s凭什么在容器化方面独树一帜？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484066&idx=1&sn=441fcae466eb5b5fba2fa29f007d7c07&chksm=cf31eb74f8466262ccc258fe1d21fbd8d65e73221c211b704d216d5116a15ffcc4f4cacf5b31#rd)

第二段：推荐阅读：[【趁着同事玩游戏偷偷认识k8s一家子补补课】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484077&idx=1&sn=2ba024c0e121f7ac83e7264bdf7b4dff&chksm=cf31eb7bf846626d02c59837a2f903ed848d8e0f117c80af16b364e858005c57849f0bb82e47#rd)

第三段：推荐阅读：[【Kubernetes家族容器小管家Pod在线答疑❓】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484110&idx=1&sn=cae2e84fb16b9fe5d8a7727c20009b3b&chksm=cf31eb18f846620e3dd1b7b8b9008fd5960363bc6bd3de679225ea5e45f9a48e93d210ccd572#rd)

第四段：推荐阅读：[【同事提出个我从未想过的问题，为什么Kubernetes要"多此一举"推出静态Pod概念？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484122&idx=1&sn=4f913c1e30808622e80a386aa6b4bef8&chksm=cf31eb0cf846621a4cf5ba605ec6fe4141b244dd2b8c49311accba15909f426277d643b6aceb#rd)

第五段：推荐阅读：[【探针配置失误，线上容器应用异常死锁后，kubernetes集群未及时响应自愈重启容器？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484133&idx=1&sn=116c23255e688ca1b86197689bcc8b72&chksm=cf31eb33f8466225400e6bfaac74d5d26de91b85e8f475ecbebedfb8ae08ebd9dde91aec1177#rd)

第六段：推荐阅读：[【kubernetes集群之Pod说能不能让我体面的消亡呀？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484143&idx=1&sn=5e764d67105c34bbaa4c851482dbe5cc&chksm=cf31eb39f846622f8c0aa21afd5d33d3928073de71058d59f974c5498bf84da2681cf76582a8#rd)

第七段：推荐阅读：[【k8s家族Pod辅助小能手Init容器认知答疑？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484153&idx=1&sn=2d6f43036cf2e4cea5fa2aebc4b67ebf&chksm=cf31eb2ff846623904c34e84943576ccf1714d73e042bdc9a4ce584050caf3fc0a85ff5c8908#rd)

第八段：推荐阅读：[【k8s初面考点ReplicaSet副本集极限9连击你懂了吗？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484188&idx=1&sn=b8b06c4abe2f5d56556235f867ec10d0&chksm=cf31eacaf84663dc0dd330e4b5d183c06a2c92f8a0e42c8c76b30d67e1ba90381f5bc7c5f780#rd)

第九段：待更新？推荐休闲阅读：[【囧么肥事】](https://mp.weixin.qq.com/mp/appmsgalbum?__biz=Mzg3NjU0NDE4NQ==&action=getalbum&album_id=2218140423993212933#wechat_redirect)

