# 访问页面路径清理

## 背景知识

当我们访问一个网站时，在任一页面上点击链接、按钮等会访问另一个页面，如此往复，形成访问链。

每个页面有一个路径，路径分配到对应处理程序，即为路由。路由，最早出现在后端，因为早期的网页都是服务端渲染的。随着前端功能的进化，出现了前端路由。

不论是后端路由还是前端路由，都应当有合理的组织逻辑。

## 合理的路由

现今主流，我们用`/`作为层次分级，每进一级代表更细粒度。

以一个店铺网站为例：

- `/` 最顶层，页面展示本店铺的品牌形象和各板块导航。
- `/shop` 商品陈列模块，页面展示一些热卖商品、优惠推荐等。
- `/shop/category/<category_id>` 商品分类模块，页面展示特定品类下的商品集。
- `/shop/product/<product_id>` 商品详情模块，页面展示特定商品的细节信息。
- `/order` 订单模块，页面展示待付款订单、待收货订单。
- `/order/<order_id>` 订单详情模块，页面展示特定订单的细节信息。
- `/cart` 购物车模块，页面展示添加在购物车中的商品清单，总价等。
- `/account` 账户模块，页面展示账户信息，包括头像、昵称、积分等。
- `/contact` 联系模块，页面展示如何与店铺取得联系的方法。

### 前端hash模式路由

对于前端路由，需要特别注意基于`hash`模式的情形。路径类似是这样的：

- `https://exmaple.com/#/`
- `https://exmaple.com/#/shop`
- `https://exmaple.com/#/shop/category/<category_id>`
- `https://exmaple.com/#/shop/product/<product_id>`
- `https://exmaple.com/#/order`
- `https://exmaple.com/#/order/<order_id>`
- `https://exmaple.com/#/account`
- `https://exmaple.com/#/contact`

在实现良好的情况下，不会有问题，路径的层次关系是一致的。

如果是带有搜索参数的情况，路径类似这样：

- `https://exmaple.com/#/shop/search?keyword=abc&price_max=100`

我们可以将`?keyword=abc&price_max=100`分离出去，留下的部分则是符合标准的路径。

对于下面的情况，则需要避免：

- `https://exmaple.com/?r=0.1234#/shop` 通过添加随机参数，强制从服务器获取未缓存页面资源。
- `https://exmaple.com/?token=abcd#/shop` 将外部跳转过来的链接参数，持续保留。
- `https://exmaple.com/?token=abcd#/shop?keyword=abc&price_max=100` 在技术层面，这是完全合法的，但一定会给人带来困惑。

这样不仅不美观，而且导致页面路径无法有效归类。后续处理程序需要花费大量精力去补救。

### 前端history模式路由

另外一种前端路由则是基于`history`模式，从形成的路径来看，和后端路由基本是相同的。路径类似是这样的：

- `https://exmaple.com/`
- `https://exmaple.com/shop`
- `https://exmaple.com/shop/category/<category_id>`
- `https://exmaple.com/shop/product/<product_id>`
- `https://exmaple.com/order`
- `https://exmaple.com/order/<order_id>`
- `https://exmaple.com/account`
- `https://exmaple.com/contact`

在实现良好的情况下，不会有问题，路径的层次关系是一致的。

如果是带有搜索参数的情况，路径类似这样：

- `https://exmaple.com/shop/search?keyword=abc&price_max=100`

我们可以将`?keyword=abc&price_max=100`分离出去，留下的部分则是符合标准的路径。

由于自身的技术上的限制，不会出现两处搜索参数。

以上两种模式，可以看出前端history模式路由，路径更简洁，可能存在的混乱情况更少。技术上来说，前端hash模式路由通用性更广，而前端history模式路由需要服务器端支持。实践上来说，前端history模式路由几乎没有什么障碍，甚至对象存储之类的服务也可以支持。

## 处理程序和补救措施

对于采集到的日志，在入库之前通常需要做一些处理。

这里仅限于访问日志范畴，进行讨论。

### 路径信息提取

URL通常由以下部分组成，通过正则表达式可以容易提取：

`scheme://host:port/path?query`

通常以`host:port`定义站点集，`/path`则是日志分析的路径，`query`则属于补充信息，可以采用KV模式拆分。

对于前端hash模式路由，则稍有不同：

`scheme://host:port/app#fragment?query`

通常以`host:port/app`定义站点集(应用)，`fragment`则是日志分析的路径，`query`则属于补充信息，可以采用KV模式拆分。

对于`/shop/product/1`这样的路径，一般在日志处理阶段，转化为`/shop/product/<product_id>`。

### 补救措施

如果客户端实现不良，可以处理阶段补救，当然最好是在源头解决。

- 可能会出现路径大小写不统一，可以在处理阶段统一为小写。
- 可能会出现路径连续`//`，可以更新为`/`。
- 可能会同时出现路径`/shop`、`/shop/`，一般意义上是相同页面，可以统一为`/shop`。