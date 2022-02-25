###  云原生新时代弄潮儿k8s凭什么在容器化方面独树一帜？

*k8s* 全称 `kubernetes`云原生新时代弄潮儿k8s凭什么在容器化方面独树一帜？

Docker 已经统领云原生半壁江山的情况下，`kubernetes`怎么能异军突起？它有什么特殊的嘛？

![img](https://article.biliimg.com/bfs/article/82610a6f9886a3e125259d7aa45977004e07456f.gif)

### 囧么肥事-胡说八道

![img](https://img-blog.csdnimg.cn/img_convert/921d0c2a190df727c043718f4f9dd700.png)

![img](https://img-blog.csdnimg.cn/img_convert/89461c3e35d5e3a617e727b51519a65d.png)

### Kubernetes 可以为做些什么?

在学习一种新技能之前，囧囧建议不要上去先看各种牛叉的实现，我们需要先搞清楚这个技能是什么？学习了之后能为我们提升什么有用的价值。毕竟在当今技术快速更新迭代的时代，我们没有足够的精力去学习太多没有用的知识去扩展的无用的广度，只有你明白了你需要学习的东西的价值，你才有动力去持续跟进，去剖析，去理解，去爱上它，用它创造价值。

![img](https://img-blog.csdnimg.cn/img_convert/fb2bf843c5bbff47ed9fc8cbc4235a4d.gif)

### Kubernetes 是什么?

**Kubernetes** 这个名字源于希腊语，意为“舵手”或“飞行员”。k8s 这个缩写是因为 k 和 s 之间有八个字符的关系🧐🧐🧐。

**官方解读**

```
Kubernetes 是一个可移植的，可扩展的开源平台。
用于管理容器化的工作负载和服务，方便了声明式配置和自动化。
它拥有一个庞大且快速增长的生态系统。Kubernetes 的服务，支持和工具广泛可用。
```

### 部署演进

![img](https://img-blog.csdnimg.cn/img_convert/59ccd69e9cca2d9266cad4feb3f5ed4b.png)

**传统部署**

```
各个组织机构在物理服务器上运行应用程序。无法为物理服务器中的应用程序定义资源边界，更多的选择是凭借工程师的个人经验去分配资源，也就是无法合理的去规划资源分配，这会导致资源分配问题。
```

**虚拟化部署**

```
虚拟化技术允许你在单个物理服务器的 CPU 上运行多个虚拟机（VM）。允许应用程序在 VM 之间隔离，虚拟化技术能够更好地利用物理服务器上的资源，

相比于传统部署方式，优点是更好的可伸缩性，降低硬件成本。
```

**容器部署**

```
以应用程序为中心的管理
资源隔离的粒度更细腻，比VM要更简约，更容易扩展移植，
容器类似于 VM，但是它们具有被放宽的隔离属性，可以在应用程序之间共享操作系统（OS）。
```

### 为什么需要 Kubernetes，它能做什么?

![img](https://img-blog.csdnimg.cn/img_convert/06cac53d252568babcfd49d0822fe7b7.gif)


**应用容器化**

```
容器是打包和运行应用程序的好方式。

容器化已经做到应用程序能以简单快速的方式发布和更新。
```

线上环境我们应用程序需要持续稳定的运行，不能早上部署好了，下午用户无法使用。

出现故障问题的时候，需要有快速响应的机制去确保不会造成重大影响。

程序是无错的情况下，对应到容器层面，就是我们需要保证容器是正确的，不能因为容器的意外而影响到前线😇。

这意味着我们需要管理运行应用程序的容器，时刻监督着运行情况，并确保容器不会停机。

如果一个容器发生故障，需要快速采取措施，例如重启容器，或者另外启动一个容器去替代它。

![img](https://img-blog.csdnimg.cn/img_convert/003deedcc868d7cd213f54539b71ba50.png)


半夜凌晨两点钟，你在家里呼呼大睡，线上疯狂报警，你不得不赶紧起床解决😭😭😭

![img](https://img-blog.csdnimg.cn/img_convert/7bbddd37f5f9a1c80bb3de83f55175f8.png)


咦，想想都惨呀😭😭😭！

如果交给系统去处理容器问题，会不会更容易？更方便？不再需要你精神高度集中每天注意可能发生的各种意外情况😳？

**所以，k8s来了，它来了，它来了，它带着旋风跑来了！**🧐🧐🧐

Kubernetes 提供了一个可弹性运行分布式系统的框架。

![img](https://img-blog.csdnimg.cn/img_convert/1f7b9275049ac88eba8382c69d243e4f.png)

- **服务发现和负载均衡**

  Kubernetes 可以使用 DNS 名称或自己的 IP 地址公开容器，如果进入容器的流量很大， Kubernetes 可以负载均衡并分配网络流量，从而使部署稳定。

- **存储编排**

  Kubernetes 允许你自动挂载你选择的存储系统，例如本地存储、公共云提供商等。

- **自动部署和回滚**

  你可以使用 Kubernetes 描述已部署容器的所需状态，它可以以受控的速率将实际状态 更改为期望状态。例如，你可以自动化 Kubernetes 来为你的部署创建新容器， 删除现有容器并将它们的所有资源用于新容器。

- **自动完成装箱计算**

  Kubernetes 允许你指定每个容器所需 CPU 和内存（RAM）。当容器指定了资源请求时，Kubernetes 可以做出更好的决策来管理容器的资源。

- **自我修复**

  Kubernetes 重新启动失败的容器、替换容器、杀死不响应用户定义的 运行状况检查的容器，并且在准备好服务之前不将其通告给客户端。

- **密钥与配置管理**

  Kubernetes 允许你存储和管理敏感信息，例如`密码`、`OAuth 令牌`和 `ssh 密钥`。你可以在不重建容器镜像的情况下部署和更新密钥和应用程序配置，也无需在堆栈配置中暴露密钥。

![img](https://img-blog.csdnimg.cn/img_convert/141d4c088568bce480b526f5ac2d361d.png)





----



 **《Kubernetes-企业级容器应用托管》-持续胡说八道**

第一段：推荐阅读：[【云原生新时代弄潮儿k8s凭什么在容器化方面独树一帜？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484066&idx=1&sn=441fcae466eb5b5fba2fa29f007d7c07&chksm=cf31eb74f8466262ccc258fe1d21fbd8d65e73221c211b704d216d5116a15ffcc4f4cacf5b31#rd)

第二段：推荐阅读：[【趁着同事玩游戏偷偷认识k8s一家子补补课】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484077&idx=1&sn=2ba024c0e121f7ac83e7264bdf7b4dff&chksm=cf31eb7bf846626d02c59837a2f903ed848d8e0f117c80af16b364e858005c57849f0bb82e47#rd)

第三段：推荐阅读：[【Kubernetes家族容器小管家Pod在线答疑❓】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484110&idx=1&sn=cae2e84fb16b9fe5d8a7727c20009b3b&chksm=cf31eb18f846620e3dd1b7b8b9008fd5960363bc6bd3de679225ea5e45f9a48e93d210ccd572#rd)

第四段：推荐阅读：[【同事提出个我从未想过的问题，为什么Kubernetes要"多此一举"推出静态Pod概念？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484122&idx=1&sn=4f913c1e30808622e80a386aa6b4bef8&chksm=cf31eb0cf846621a4cf5ba605ec6fe4141b244dd2b8c49311accba15909f426277d643b6aceb#rd)

第五段：推荐阅读：[【探针配置失误，线上容器应用异常死锁后，kubernetes集群未及时响应自愈重启容器？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484133&idx=1&sn=116c23255e688ca1b86197689bcc8b72&chksm=cf31eb33f8466225400e6bfaac74d5d26de91b85e8f475ecbebedfb8ae08ebd9dde91aec1177#rd)

第六段：推荐阅读：[【kubernetes集群之Pod说能不能让我体面的消亡呀？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484143&idx=1&sn=5e764d67105c34bbaa4c851482dbe5cc&chksm=cf31eb39f846622f8c0aa21afd5d33d3928073de71058d59f974c5498bf84da2681cf76582a8#rd)

第七段：推荐阅读：[【k8s家族Pod辅助小能手Init容器认知答疑？】](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484153&idx=1&sn=2d6f43036cf2e4cea5fa2aebc4b67ebf&chksm=cf31eb2ff846623904c34e84943576ccf1714d73e042bdc9a4ce584050caf3fc0a85ff5c8908#rd)

第八段：待更新？推荐休闲阅读：[【囧么肥事】](