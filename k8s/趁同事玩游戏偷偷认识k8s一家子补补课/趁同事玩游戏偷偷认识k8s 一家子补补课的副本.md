### 偷偷认识k8s 一家子补补课



![开工啦](/Users/tao/Desktop/daily-notes/k8s/趁同事玩游戏偷偷认识k8s一家子补补课/开工啦.gif)

Kubernetes集群这个大家庭在容器化时代能够新军崛起，要感谢其众多可靠稳定，工作认真负责的优质**成员**。

这些兄弟姐妹们为集群提供`故障转移`和`高可用性`，保证k8s 可以跨多主机运行，集群跨多个节点运行。



### k8s兄弟姐妹

**先进入k8s组件成员学习讨论环节**

![22020901jpg](/Users/tao/Desktop/daily-notes/k8s/趁同事玩游戏偷偷认识k8s一家子补补课/22020901jpg.jpg)

![22020902](/Users/tao/Desktop/daily-notes/k8s/趁同事玩游戏偷偷认识k8s一家子补补课/22020902.jpg)

### k8s家庭成员

![k8s家庭](/Users/tao/Desktop/daily-notes/k8s/趁同事玩游戏偷偷认识k8s一家子补补课/k8s家庭.jpg)



 一个k8s集群由**master节点**和**worker节点**组成。**看看k8s官方组件图**：

![k8s](https://d33wubrfki0l68.cloudfront.net/2475489eaf20163ec0f54ddc1d92aa8d4c87c96b/e7c81/images/docs/components-of-kubernetes.svg)

- Master Node：是集群的控制中心、网关和中枢枢纽，是k8s家庭里的大家长，主要作用：暴露API接口，跟踪其他服务器的健康状态、以最优方式调度负载，以及编排其他组件之间的通信。
- 单个的Master节点可以完成所有的功能，生产环境中需要部署多个Master节点，组成Cluster，避免单点故障问题带来的服务失效和数据丢失。
- Worker Node：工作节点，遵从大家长指令，兢兢业业的创建和销毁Pod对象，调整网络规则进行合理路由和流量转发。
- 生产环境中，Worker Node节点可以有N个。



![金钱豹](/Users/tao/Desktop/daily-notes/k8s/趁同事玩游戏偷偷认识k8s一家子补补课/金钱豹.jpg)











