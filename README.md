# P3Android
Welcome to our personalized Android world!
P3Android's Architecture
-------------------------------------------------------------
P3Android provides two services, `privacy protect service`(PPS) and `personalization support service`(PSS), for achieving privacy security and universal personalization support, which improve the overall privacy security of Android OS in complementary way. Figure 1 shows the proposed P3Android’s architecture. 

![image](https://github.com/dongyangwu/P3Android/blob/master/framework.jpg) <br> <br>

<center>Figure 1. P3Android’s architecture<center>  
 
Details
-------------------------------------------------------------
P3Android encourages storing user data on mobile device, which makes users regain full control of their privacy data and also declines the pressure of third-party cloud providers. To enable easy application personalization, universal personalization service profiles user’s interests and preferences as personae, such as sportsman or housewife. Personae approximately represent user’s various walks of life, and can be extended through custom classifiers. In order to get user’s interest profile, P3Android leverage the truth that all user data must flow through the operating system, so, there are many excellent places to gather the data which can be trained to classify user’s personae.  <br>  <br> 
To supply universal support, P3Android modified the Android framework upon which apps are built. Though there are so many widgets that can be composed to implement multifarious applications, we only focused on which are frequently used by third-party apps to display their contents. When users turn on the personalization service, these widgets can re-embellish its contents according to the profile created above.<br>  <br> 
Though using personae help to limit information leaks, those legacy applications need not to follow this constraints. Therefore, P3Android proposed a dynamic permission check model, which can decide to grant apps different permissions according to apps’ risk levels. There are three levels (high, medium and low), which is ranked by a Naive Bayes Model. This risk rank process is completely automatic, and needs no artificial participation.<br>  <br> 


APIs which P3Android provides for personalization service support
---------------------------
![image](https://github.com/dongyangwu/P3Android/blob/master/APIs_features.JPG) <br><br>

Guide for developers which want to use P3Android services and APIs:
---------------------------
1, get an instance of Personalization Support Service Manager. e.g.<br>
`PersonalizationSupportManager psm = (PersonalizationSupportManager) getSystemService(PSM_SERVICE);`<br>

2, call the methods which PSS provides. e.g. to get the favorite topic of the user:<br>
`String persona = psm.getTopPersonae();`<br>

3, then provide specific personalizaiton function of your own for users.

