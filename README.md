# cotton_database
这个项目是以爬取全球棉花供需数据为基础，棉花的月度供需平衡表，以此分析棉花期货市场的走势。
## 代码暂时不再维护
由于软件框架过于老旧。基本需要重写了
部分关键数据源过期
服务器已经不再运行
数据基本丢失

该只存储代码用
一个悲伤的故事~~~~~~


## 对棉花期货有兴趣的朋友可以看下思路

### 数据思路
基本思路是收集各国的年度供需平衡表和月度细分数据包括：  
月度产量  
月度出口量进口量  
月度商业库存和工业库存  
公式如下： 上一个月的库存 + 本月产量 + 本月进口量 - 本月出口量 - 本月表观消费量 = 本月末库存  
由于表观消费很难确定，所以一般来说变成  
公式如下： 上一个月的库存 + 本月产量 + 本月进口量 - 本月出口量 - 本月库存  =   本月表观消费量  
这样你就可以做出月度的供需平衡表，和历年的供需平衡表进行比较  

### 分析架构应该分为几个维度
时间      月度、年度  
供需情况  供需平衡表 ， 进出口，产量（天气，运输，政策），消费，库存，  
市场情绪  投机资金量  
重要事件  黑天鹅，政府干预，天气  
  
由于期货市场驱动价格的因子非常不固定，不同时间段作用价格的因子不一样。需要在上述的维度中需找到供需因子中，真正发挥作用的因子是非常困难的，很多时候就是有钱说了算~~~~~~ 又是非常悲伤的故事~~~
  
# 结语：因此期货的长期价格方向是可以达到一个共识的。但是具体的价格是不能预测的。交易和分析其实是2马事情，猜对未来，不代表能赚钱。

