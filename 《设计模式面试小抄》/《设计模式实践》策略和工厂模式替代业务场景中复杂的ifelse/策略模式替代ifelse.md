### 《策略模式替换ifelse》

![0](https://mmbiz.qpic.cn/mmbiz_png/2GfhQ2H7oTEAjuuGFQ7uyWswjNib3Tf49vcibeQS8MNosqf9JH5NoxyaDaYuZeMZKPiaD9Ks1JWgWk9aHwadycZlA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

我是肥哥，一名不专业的面试官！

我是囧囧，一名积极找工作的小菜鸟！

> **囧囧表示：小白面试最怕的就是面试官问的知识点太笼统，自己无法快速定位到关键问题点！！！**

----



**本期主要面试考点**

```
面试官考点之如何用设计模式替换业务场景中复杂的ifelse？
```

----

![1](https://mmbiz.qpic.cn/mmbiz_jpg/2GfhQ2H7oTGPZ97KQVxqRqsSwE0avysNib9icQJpTkPfA7HxqDCibdPuumJUtLhk5bUEEFpzp3AoqT5uonrgtdaBA/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

![2](https://mmbiz.qpic.cn/mmbiz_jpg/2GfhQ2H7oTGPZ97KQVxqRqsSwE0avysNb13eFOibGWLYFzNGebd35VGVm2fGMyCMTTG2ocDxsx5qyBGXDpqJf3A/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)



**VIP类型**

```
import java.util.Objects;

/**
 * @author: 欢迎关注喂信公猪号：囧么肥事
 * @date: 2021/12/16
 * @email: jiongmefeishi@163.com
 *
 * 会员类型
 */
public enum VIPEnums {

    GOLD(1, "黄金会员"),
    STAR(2, "星钻会员"),
    SPORTS(3, "体育会员"),
    FUN_VIP(4, "FUN会员");

    private final int code;
    private final String desc;

    VIPEnums(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static VIPEnums getByCode(Integer code) {
        for (VIPEnums s : VIPEnums.values()) {
            if (Objects.equals(s.getCode(), code)) {
                return s;
            }
        }
        return null;
    }
}
```

**VIP实体**

```
/**
 * @author: 欢迎关注喂信公猪号：囧么肥事
 * @date: 2021/12/16
 * @email: jiongmeifeishi@163.com
 *
 * vip
 */
public class VIP {

    private VIPEnums vipType;

    // TODO VIP 其他属性 id, name ...

    public VIP() {
    }

    public VIP(VIPEnums vipType) {
        this.vipType = vipType;
    }

    public VIPEnums getVipType() {
        return vipType;
    }

    public void setVipType(VIPEnums vipType) {
        this.vipType = vipType;
    }
}
```

**if-else 模式**

```
// if-else 模式
public class App {
    public static void main( String[] args ) {

        // 黄金会员
        VIP vip = new VIP(VIPEnums.GOLD);

        if (vip.getVipType().getCode() == VIPEnums.GOLD.getCode()) {
            // TODO 黄金会员权益
        } else if (vip.getVipType().getCode() == VIPEnums.STAR.getCode()) {
            // TODO 星钻会员权益
        } else if (vip.getVipType().getCode() == VIPEnums.SPORTS.getCode()) {
            // TODO 体育会员权益
        } else if (vip.getVipType().getCode() == VIPEnums.FUN_VIP.getCode()) {
            // TODO FUN会员权益
        } else {
            // TODO 其他会员...
        }

    }
}
```

### 策略模式

**VIP策略接口**

```
/**
 * @author: 欢迎关注喂信公猪号：囧么肥事
 * @date: 2021/12/16
 * @email: jiongmefeishi@163.com
 * VIP 策略接口
 */
public interface VIPStrategy {

    // VIP 具备的权益
    void equity();
}

```

**策略接口具体实现类-黄金会员**

```
/**
 * @author: 欢迎关注喂信公猪号：囧么肥事
 * @date: 2021/12/16
 * @email: jiongmefeishi@163.com
 *
 * 策略接口具体实现类-黄金会员
 */
public class GoldVIPStrategyImpl implements VIPStrategy {

    @Override
    public void equity() {
        // TODO 黄金会员具备的具体权益
    }
}
```

**策略接口具体实现类-星钻会员**

```
/**
 * @author: 欢迎关注喂信公猪号：囧么肥事
 * @date: 2021/12/16
 * @email: jiongmefeishi@163.com
 *
 * 策略接口具体实现类-星钻会员
 */
public class StarVIPStrategyImpl implements VIPStrategy {

    @Override
    public void equity() {
        // TODO 星钻会员具备的具体权益
    }
}

```

**策略接口具体实现类-体育会员**

```
/**
 * @author: 欢迎关注喂信公猪号：囧么肥事
 * @date: 2021/12/16
 * @email: jiongmefeishi@163.com
 *
 * 策略接口具体实现类-体育会员
 */
public class SportsVIPStrategyImpl implements VIPStrategy {

    @Override
    public void equity() {
        // TODO 体育会员具备的具体权益
    }
}
```

**策略接口具体实现类-FUN会员**

```
/**
 * @author: 欢迎关注喂信公猪号：囧么肥事
 * @date: 2021/12/16
 * @email: jiongmefeishi@163.com
 *
 * 策略接口具体实现类-FUN会员
 */
public class FunVIPStrategyImpl implements VIPStrategy {

    @Override
    public void equity() {
        // TODO FUN会员具备的具体权益
    }
}
```

**策略上下文类**

```
/**
 * @author: 欢迎关注喂信公猪号：囧么肥事
 * @date: 2021/12/16
 * @email: jiongmefeishi@163.com
 *
 * 策略上下文类( vip 策略接口的持有者)
 */
public class VIPStrategyContext {

    private VIPStrategy vipStrategy;

    // 设置VIP策略
    public void setVipStrategy(VIPStrategy vipStrategy) {
        this.vipStrategy = vipStrategy;
    }

    // 执行 VIP 权益
    public void handle() {
        if (vipStrategy != null) {
            vipStrategy.equity();
        }
    }
}
```

**策略工厂**

```
/**
 * @author: 欢迎关注喂信公猪号：囧么肥事
 * @date: 2021/12/16
 * @email: jiongmefeishi@163.com
 *
 * VIP策略工厂
 */
public class VIPStrategyFactory {

    private VIPStrategyFactory() {
    }

    public static VIPStrategy getVipStrategy(VIP vip) {
        VIPStrategy vipStrategy = null;
        
        if (vip.getVipType().getCode() == VIPEnums.GOLD.getCode()) {
            // 黄金会员策略实现类
            vipStrategy = new GoldVIPStrategyImpl();
        } else if (vip.getVipType().getCode() == VIPEnums.STAR.getCode()) {
            // 星钻会员策略实现类
            vipStrategy = new StarVIPStrategyImpl();
        } else if (vip.getVipType().getCode() == VIPEnums.SPORTS.getCode()) {
            // 体育会员策略实现类
            vipStrategy = new SportsVIPStrategyImpl();
        } else if (vip.getVipType().getCode() == VIPEnums.FUN_VIP.getCode()) {
            // FUN会员策略实现类
            vipStrategy = new FunVIPStrategyImpl();
        } else {
            // 其他会员...
        }

        return vipStrategy;
    }
}
```

**模拟会员登录获取权益**

```
/**
 * @author: 欢迎关注喂信公猪号：囧么肥事
 * @date: 2021/12/16
 * @email: jiongmefeishi@163.com
 *
 * 模拟会员登录获取权益
 */
public class TestStrategy {

    public static void main(String[] args) {

        // 黄金会员
        VIP vip = new VIP(VIPEnums.GOLD);

        // 策略上下文，执行者
        VIPStrategyContext context = new VIPStrategyContext();

        // 根据会员类型，获取会员具体策略，获取黄金会员策略
        VIPStrategy strategy = VIPStrategyFactory.getVipStrategy(vip);

        // 绑定给执行者
        context.setVipStrategy(strategy);

        // 执行黄金会员的策略，黄金权益
        context.handle();
    }
}
```

我们知道， 策略模式的本身设计出来的目的是封装一系列的算法，这些算法都具有共性，可以相互替换，算法独立于使用它的客户端独立变化，客户端不需要了解关注算法的具体实现，客户端仅仅依赖于策略接口 。



通过使用策略模式和工厂模式结合，是不是感觉变得高大上起来了呢？😇

当然了，最主要的是程序的扩展来说更方便了一些，更符合开闭原则，开放扩展，关闭修改。无论新增多少种新类型的会员，每个人只需要去继承策略接口，实现新会员应有的权益即可。



**注意**，虽然利于扩展，但是策略模式的缺点也很明显，策略工厂在创建具体的策略实现类的时候，还是书写大量的 if-else 去进行判断，如图 

![缺点](https://cdn.nlark.com/yuque/0/2021/png/10374809/1639657614062-cd0d9bd1-8149-462a-9b94-cd7f66d02a24.png?x-oss-process=image%2Fresize%2Cw_1314%2Climit_0)



有小伙伴就说了这和不使用策略模式和工厂模式似乎差不多？？？

抽出一个方法或者封装成一个对象去调用岂不是更简单？？？

![抽调](https://cdn.nlark.com/yuque/0/2021/png/10374809/1639657890219-73bfae2a-d90e-41e8-9f8b-e2252c21bb5f.png?x-oss-process=image%2Fresize%2Cw_1298%2Climit_0)



接下来，我们就说说**如何优化策略工厂**。



首先，我们的工厂，是根据当前传入的用户的会员类型，判断后，返回相应的策略实现类，那么可以借助集合来存储实现类，会员类型作为 key，将所有的会员策略都注册到 map 中。需要注意的是，日常开发基于Spring进行bean管理，上面需要创建的策略类，当然都是希望被 **Spring 动态托管**，而不是我们自己去一个个的new 出实例。



**问题是，如何去实现策略类通过spring进行托管注册?**

Spring种提供的`InitializingBean`接口，这个接口为Bean提供了属性初始化后的处理方法，它只包括`afterPropertiesSet`方法，凡是继承该接口的类，在`bean`的属性初始化后都会执行该方法。我们利用此方法把`Spring`通过`IOC`创建出来的Bean注册`Map `中。



**改造策略工厂**

```

import org.example.model.VIP;
import org.example.strategy.VIPStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: 欢迎关注喂信公猪号：囧么肥事
 * @date: 2021/12/16
 * @email: jiongmefeishi@163.com
 * <p>
 * VIP策略工厂
 */
public class VIPStrategyFactory {

    // 存储策略类实例
    public static Map<Integer, VIPStrategy> strategyMap = new ConcurrentHashMap<>();

    private VIPStrategyFactory() {
    }

    public static VIPStrategy getVipStrategy(VIP vip) {
        if (vip == null) {
            return null;
        }
        return strategyMap.get(vip.getVipType().getCode());
    }
}
```

**改造策略类**，在bean属性初始化后，将实例对象注册到工厂类中的 map

以黄金会员为例：

```
import org.example.factory.VIPStrategyFactory;
import org.example.model.VIPEnums;
import org.example.strategy.VIPStrategy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * @author: 欢迎关注喂信公猪号：囧么肥事
 * @date: 2021/12/16
 * @email: jiongmefeishi@163.com
 *
 * 策略接口具体实现类-黄金会员
 */
@Service
public class GoldVIPStrategyImpl implements VIPStrategy, InitializingBean {

    @Override
    public void equity() {
        // TODO 黄金会员具备的具体权益
        System.out.println("黄金会员具备的具体权益");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        VIPStrategyFactory.strategyMap.put(VIPEnums.GOLD.getCode(), new GoldVIPStrategyImpl());
    }
}
```

通过策略模式、工厂模式以及Spring的`InitializingBean`接口，算是解决了大量的if else，后续新VIP出现也更容易扩展，当然了，这里只是对于设计模式思想的一个简单的示例，实际应用开发中，还是要根据具体的业务场景灵活变通。有需要的小伙伴也可以自己手动模拟一些场景，比如奶茶店各种奶茶新品等等。如果想用囧囧的示例，可公猪号上回复220110 自行导入示例运行即可。



注意：学习软件设计原则，千万不能形成强迫症。当碰到业务复杂的场景时，需要随机应变。

学习设计原则是学习设计模式的基础。在实际开发过程中，并不是一定要求所有代码都遵循设计原则，而是要综合考虑人力、时间、成本、质量，不刻意追求完美，要在适当的场景遵循设计原则。这体现的是一种平衡取舍，可以帮助我们设计出更加优雅的代码结构。



**设计模式其实也是一门艺术。设计模式源于生活，不要为了套用设计模式而使用设计模式。**

![img](https://img-blog.csdnimg.cn/img_convert/ffa4e69bc51e8e8ac20e6ad189887497.gif)

喜欢的小伙伴，欢迎点赞收藏关注

![3](https://mmbiz.qpic.cn/mmbiz_png/2GfhQ2H7oTH4MWyq9zq3JibfFP3VKDn6EewpW7fmTMuKbK08XzpZrdVS2JYu3an0ynicFcAMt0TAmnJMN4w3IpRQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)
