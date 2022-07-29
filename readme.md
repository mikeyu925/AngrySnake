# Angry Snake

> SpringBoot + MyBatis-Plus + MySql + JavaScript





### 游戏介绍

一款可以与玩家/AI **实时对战的回合制游戏**，采用**前后端分离**实现，在每回合你可以控制你的snake朝着某个方向进行下一步移动来打败对方的snake！

游戏判决规则：

- 对方蛇撞墙 或者 撞到了你的蛇 或者 撞到了自己  ===> You Win!
- 两条蛇同时相撞 ===>  Dogfall



### MyBatis-Plus

一个 [MyBatis (opens new window)](https://www.mybatis.org/mybatis-3/)的增强工具，在 MyBatis 的基础上只做增强不做改变，为简化开发、提高效率而生。有着无侵入、损耗小、内置代码生成器等优势。

> 用MyBatis-Plus 完成 SpringBoot 对接 Mysql 数据库



### JSON Web Token(JWT)

定义了一种紧凑的、自包含的方式，用于作为JSON对象在各方之间安全地传输信息。该信息可以被验证和信任，因为它是数字签名的。

> 由于前后端分离，前端和后端会有跨域的问题，因此用JWT验证会更容易一些。





## 用户登录

数据库表 user表设计：

|  id  | username | password |   photo    |
| :--: | :------: | :------: | :--------: |
|  1   |   fish   |  xxxxxx  | 存的是链接 |

后端相关API：

- /user/account/token/：验证用户名密码，验证成功后返回jwt token（令牌）
- /user/account/info/：根据令牌返回用户信息
- /user/account/register/：注册账号

>  退出登录：直接在前端删除 token 即可



**用户登录持久化**：

将token存储在浏览器的localStorage里

```js
localStorage.setItem("jwt_token", resp.token);
```

浏览器刷新进入到登录页面时：

- 如果已经登录了
  - 获取浏览器里存储的token
  - 更新token
  - 利用token从云端获取信息
    - 成功：跳转至首页