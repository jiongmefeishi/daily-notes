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


《k8s-企业级容器应用托管》- 持续更新

第一段：认识k8s是什么？推荐阅读：[《云原生新时代弄潮儿k8s凭什么在容器化方面独树一帜？》](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484066&idx=1&sn=441fcae466eb5b5fba2fa29f007d7c07&chksm=cf31eb74f8466262ccc258fe1d21fbd8d65e73221c211b704d216d5116a15ffcc4f4cacf5b31#rd)

第二段：认识k8s家庭成员？推荐阅读：[《趁着同事玩游戏偷偷认识k8s一家子补补课》](https://mp.weixin.qq.com/s?__biz=Mzg3NjU0NDE4NQ==&mid=2247484077&idx=1&sn=2ba024c0e121f7ac83e7264bdf7b4dff&chksm=cf31eb7bf846626d02c59837a2f903ed848d8e0f117c80af16b364e858005c57849f0bb82e47#rd)

第三段：待更新？推荐休闲阅读：[《囧么肥事》](https://mp.weixin.qq.com/mp/appmsgalbum?__biz=Mzg3NjU0NDE4NQ==&action=getalbum&album_id=2218140423993212933#wechat_redirect)

