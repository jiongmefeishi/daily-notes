### 趁着同事玩游戏偷偷认识k8s一家子补补课


![开工啦](https://img-blog.csdnimg.cn/img_convert/a5e65e7b6cdc4ae89fd2ebc2a9a87f96.gif)

Kubernetes集群这个大家庭在容器化时代能够新军崛起，要感谢其众多可靠稳定，工作认真负责的优质**成员**。

这些兄弟姐妹们为集群提供`故障转移`和`高可用性`，保证k8s 可以跨多主机运行，集群跨多个节点运行。



### k8s兄弟姐妹

**先进入k8s组件成员学习讨论环节**

![img](https://img-blog.csdnimg.cn/img_convert/e28dc4dae5a685fb719a36bca408e5fe.png)


![img](https://img-blog.csdnimg.cn/img_convert/d523ca373b2aca4996a8b0a79b6ccdf1.png)


### k8s家庭成员

![img](https://img-blog.csdnimg.cn/img_convert/f0e5b01a0bc3ce42c977dcb8af569c64.png)



 一个k8s集群由**master节点**和**worker节点**组成。**看看k8s官方组件图**：

![img](https://img-blog.csdnimg.cn/img_convert/69a1b1a4aa6dc38f2dd6637c22f23874.png)

- **Master Node**：是集群的控制中心、网关和中枢枢纽，是k8s家庭里的大家长，主要作用：暴露API接口，跟踪其他服务器的健康状态、以最优方式调度负载，以及编排其他组件之间的通信。
- 单个的Master节点可以完成所有的功能，生产环境中需要部署多个Master节点，组成Cluster，避免**单点故障**问题带来的服务失效和数据丢失。
- **Worker Node**：工作节点，遵从大家长指令，兢兢业业的创建和销毁Pod对象，**调整网络规则进行合理路由和流量转发**。
- 生产环境中，Worker Node节点可以有N个。


![发财豹](https://img-blog.csdnimg.cn/img_convert/09f3ed620abcfc086aed80ff292e075c.png)

-----

**Kubernetes 推荐学习书**

> Kubernetes权威指南PDF
> 链接:https://pan.baidu.com/s/11huLHJkCeIPZqSyLEoUEmQ 提取码:sa88



k8s系列所有问题更新记录：[GitHub](https://gitee.com/jiongmefeishi/JMFS-Interview-Notebook-Kubernetes)

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

