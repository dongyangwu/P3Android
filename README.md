# P3Android
Welcome to our personalized Android world!
P3Android's Architecture
-------------------------------------------------------------
P3Android provides two services, `privacy protect service`(PPS) and `personalization support service`(PSS), for achieving privacy security and universal personalization support, which improve the overall privacy security of Android OS in complementary way. Figure 1 shows the proposed P3Android’s architecture.
![image](https://github.com/dongyangwu/P3Android/blob/master/framework.jpg) <br> 
Figure 1. P3Android’s architecture <br>
Details
-------------------------------------------------------------
P3Android encourages storing user data on mobile device, which makes users regain full control of their privacy data and also declines the pressure of third-party cloud providers. To enable easy application personalization, universal personalization service profiles user’s interests and preferences as personae, such as sportsman or housewife. Personae approximately represent user’s various walks of life, and can be extended through custom classifiers. In order to get user’s interest profile, P3Android leverage the truth that all user data must flow through the operating system, so, there are many excellent places to gather the data which can be trained to classify user’s personae.  <br>  <br> 
To supply universal support, P3Android modified the Android framework upon which apps are built. Though there are so many widgets that can be composed to implement multifarious applications, we only focused on which are frequently used by third-party apps to display their contents. When users turn on the personalization service, these widgets can re-embellish its contents according to the profile created above.<br>  <br> 
Though using personae help to limit information leaks, those legacy applications need not to follow this constraints. Therefore, P3Android proposed a dynamic permission check model, which can decide to grant apps different permissions according to apps’ risk levels. There are three levels (high, medium and low), which is ranked by a Naive Bayes Model. This risk rank process is completely automatic, and needs no artificial participation.<br>  <br> 

#P3Android
Android个性化和隐私保护的系统级支持方案

P3Android框架
---------------
本文提出了一种同时支持Android应用程序个性化和系统级隐私保护的机制P3Android。通过修改Android现有保护机制,P3Android增加了隐私保护模块和个性化支持模块，其框架结构如图1所示，其中虚线框部分是对Android现有保护机制的修改。<br>  <br> 
####(1) 隐私保护模块<br>  <br> 
`隐私保护模块包括应用程序风险评级子模块和策略执行子模块`。<br>
风险评级子模块是一个可以自动评估应用程序风险等级的分类器，它利用机器学习中朴素贝叶斯算法学习恶意应用和良性应用申请的权限信息，然后结合先验知识（对每个权限的敏感程度赋予不同权重）计算应用程序的风险值。在朴素贝叶斯模型中，每个应用程序由向量xi = [xi,1, …, xi,m]表示，其中xi,m表示是否申请m权限（等于0表示未申请该权限，等于1表示申请了该权限），m表示Android系统所有权限的总数。由此可以假设xi符合m重伯努利试验<br> 
![image](https://github.com/dongyangwu/P3Android/blob/master/Bernoulli.JPG)<br> 
其中 为伯努利参数。最后，通过归一化将风险值规约到0到10之间，并将其划分高、一般和低风险三个风险区间。该子模块采用离线训练方式，将风险计算方法整合到框架层的应用程序安装步骤中实现自动化的风险打分功能。<br> 
策略执行子模块通过修改Android的权限审查机制，结合应用程序风险等级实施细粒度的权限访问控制。在本模型中，应用程序的风险等级作为重要因素以指导权限审查机制对不同权限集合的控制粒度。当应用程序请求敏感数据时，相应的保护策略如下：<br> 
* 高风险：对写入敏感数据权限集合提供两种授权提醒，分别是“授予”和“拒绝”；对读取敏感数据权限集合提供三种授权提醒，分别是“授予”、“拒绝”和“仿真”；<br>
* 一般风险：对读取敏感数据权限集合提供三种授权提醒，分别是“授予”、“拒绝”和“仿真”。<br>
* 低风险：直接调用Android自身的权限审查机制。<br>  

其中，“仿真”是指PAPDroid根据应用程序请求的敏感数据的结构而模拟出假的数据，使APPs不会由于缺少相应数据而不能给用户提供服务。另外，读取和写入敏感数据权限集合可从论文[94]中表2列出的26种关键权限分类获得。<br>  <br>
####(2) 个性化支持模块<br>  <br>
`个性化支持模块包括个性化信号提取和角色分类两个子模块`。<br>
个性化信号提取子模块负责提取用于用户角色分类的数据。由于用户角色预测的准确性直接影响到个性化内容显示的友好性，所以要提取的信息需要与用户直接相关。本模型在框架层提取用户的个性化信号，包括用户在社交和即时通讯软件中的聊天信息、SMS信息、Email和HTTP通信等，并利用停词表过滤部分噪音信号。另外，为了避免轮询收集方式对系统性能的损耗，PAPDroid仅在个性化信号产生的时候收集信息。<br><br>
角色分类子模块负责预测用户的兴趣爱好。本模型中，每个角色代表一个SVM分类器，每个分类器的训练数据都是从相关的网站中搜集得到，例如，体育爱好者角色的数据可从“sports.sohu.com”获取。由于不同的角色代表不同的兴趣爱好，所以需要合理的划分用户角色。PAPDroid提供了一个预设的角色集合，通过对个性化信号的预测，最终形成用户角色概要文件(profile)。该profile中为每个角色赋予一个权重，表示用户属于此角色的可能性。<br><br>
PAPDroid修改了Android框架层负责个性化显示的控件，例如ListView，GalleryView等，使其内容可以按照用户概要文件中各个兴趣爱好的权重比例排序显示。这样，应用开发商不需要再通过搜集和分析用户隐私数据来获取用户的兴趣爱好，实现了统一的客户端个性化支持。而且，PAPDroid不仅缓解开发人员对隐私保护的压力，也使得对用户隐私数据的分析和操作只发生在用户设备中，从而有效地解决了隐私泄露的问题。<br><br>
###Personalization APIs with their features
###APIs及其功能对照表
---------------------------
![image](https://github.com/dongyangwu/P3Android/blob/master/APIs_features.JPG) <br><br>

开发指南:
---------------------------
首先获取PSS服务:<br>
      `PersonalizationSupportManager psm = (PersonalizationSupportManager) getSystemService(PSM_SERVICE);`<br>
然后即可通过psm对象调用相应的方法,例如"获取用户最感兴趣的话题":<br>
      `String persona = psm.getTopPersonae();`<br>
即可通过该角色实施具体的个性化方案.
