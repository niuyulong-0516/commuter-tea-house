# 通勤茶馆

给通勤一族准备的路上茶馆：分享上班路上的新鲜事，也把堵车、加班、挤地铁的槽点倒一倒。

当前是第一版 MVP，包含：

- 出行方式：轨道交通 / 公共交通。用户有偏好，帖子也有类型；首页按偏好进入，并可随时切换查看
- 账号体系：强制登录，注册填用户名和密码，成功后生成 6 位顺序茶客编号
- 信息流：全部 / 新鲜事 / 吐槽，支持最新、热门排序，并按出行类型筛选
- 发帖：文字 + 最多 9 张图片，自动带上当前出行类型
- 匿名发布：不展示茶客昵称
- 帖子详情：查看全文、图片预览、路过人数
- 多语言：中文、英文、日文、韩文、西班牙文、法文、德文、葡萄牙文，当前默认固定中文，可在设置页切换（按 IP 分发逻辑保留但暂未启用）

暂未包含：点赞、评论、头像上传、失物招领专区、消息通知。

## 技术栈

- 前端：uni-app（Vue 3 + Vite），一套代码可输出 H5、微信小程序、App
- 后端：Java 17 + Spring Boot 3.3 + Spring Data JPA
- 数据库：H2 文件数据库（本地零配置，后续可换 MySQL）
- 图片：本地 `uploads/` 目录，通过 `/uploads/**` 静态访问

## 目录结构

```
commuter-tea-house/
├── app/                      # uni-app 前端
│   ├── src/pages/type-select # 出行方式选择（启动页）
│   ├── src/pages/auth        # 注册 / 登录
│   ├── src/pages/index       # 信息流
│   ├── src/pages/publish     # 发帖
│   ├── src/pages/detail      # 帖子详情
│   ├── src/api               # 接口封装
│   ├── src/i18n              # 8 种语言文件与 i18n 初始化
│   ├── src/pages/settings    # 设置页（语言、出行方式、退出）
│   ├── src/components        # 通用组件（矢量图标）
│   └── src/utils             # 请求、登录态、时间格式化
└── server/                   # Spring Boot 后端
    ├── src/main/java/com/commutertea
    │   ├── user              # 用户、注册登录
    │   ├── post              # 帖子
    │   ├── file              # 图片上传
    │   └── common            # 出行类型、登录拦截器、异常处理
    └── src/main/resources/application.yml
```

## 本地启动

前端接口依赖后端，需要开两个终端窗口，先启动后端，再启动前端。

### 1. 终端一：后端

要求 JDK 17。项目自带 Maven Wrapper，不需要全局安装 Maven。当前电脑已把 JDK 17 放在用户目录下，可以直接启动：

```bash
cd server
export JAVA_HOME="$HOME/.local/toolchains/jdk-17.0.21.1+2/Contents/Home"
./mvnw spring-boot:run
```

JDK 在其他位置时，把 `JAVA_HOME` 换成对应路径即可。

看到 `Started CommuterTeaApplication` 表示启动成功：

- 接口地址：`http://localhost:8090`
- H2 控制台：`http://localhost:8090/h2-console`（JDBC URL 填 `jdbc:h2:file:./data/commuter-tea`，账号 `sa`，密码留空）
- 数据库文件：`server/data/`
- 上传图片：`server/uploads/`

第一次启动会自动写入 8 条演示帖子和一个默认体验账号：

- 用户名：`demo`
- 密码：`123456`
- 出行偏好：轨道交通

想重置数据，停止服务后删除 `server/data/` 和 `server/uploads/` 即可。默认账号只在不存在时创建，不会覆盖你后续改过的密码。

> 数据库结构有变更（新增用户表、帖子增加出行类型）。新字段可空、兼容旧数据；如果想从干净的演示数据开始，删除 `server/data/` 再启动。

### 2. 终端二：前端

要求 Node.js 22.12 以上，项目里提供了 `.nvmrc`（当前为 24）。当前 shell 默认是 Node 16，需要先切到项目版本：

```bash
cd app
source ~/.nvm/nvm.sh && nvm use
npm install
npm run dev:h5
```

`npm install` 只需第一次执行。浏览器打开终端里提示的地址（默认 `http://localhost:5173`），就能看到信息流；5173 被占用时 Vite 会自动换端口，以终端实际打印的地址为准。

首次打开会先选出行方式（轨道交通 / 公共交通），然后注册（用户名 + 密码）；注册成功直接进入首页，并显示 6 位茶客编号。已注册用户可在登录页切换账号，首页右上角可以「切换出行」或「退出」。

后端必须先启动，否则页面能打开，但列表为空或提示「网络不太顺畅」。

后端地址默认是 `http://localhost:8090`，配置在 `app/src/config.js`。

真机调试时 `localhost` 指向手机自己，需要改成电脑的局域网 IP：

```bash
# app/.env.development
VITE_API_BASE_URL=http://192.168.1.10:8090
```

微信小程序还需要在 `app/src/manifest.json` 填入小程序 AppID，并在微信公众平台配置合法请求域名。

## 打包

```bash
cd app
npm run build:h5        # 输出 app/dist/build/h5
npm run build:mp-weixin # 输出 app/dist/build/mp-weixin
```

App 安装包建议用 HBuilderX 打开 `app/` 目录后云打包。

## 接口

除注册、登录外，所有接口都要在请求头带 `X-Auth-Token`（注册或登录后返回）。未登录返回 401，前端会自动回到入口页。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/register` | 注册，入参 `username`/`password`/`transportType`（`RAIL` 或 `BUS`），返回用户信息与 token |
| POST | `/api/auth/login` | 登录，入参 `username`/`password`，返回用户信息与 token |
| GET | `/api/auth/me` | 当前登录用户 |
| POST | `/api/auth/logout` | 退出登录 |
| PUT | `/api/auth/transport-type` | 修改出行偏好，入参 `transportType` |
| GET | `/api/posts?category=ALL&transportType=RAIL&sort=latest&page=0&size=10` | 信息流，`category` 可选 `ALL`/`FRESH`/`RANT`，`transportType` 可选 `ALL`/`RAIL`/`BUS`，`sort` 可选 `latest`/`hot` |
| GET | `/api/posts/{id}` | 帖子详情，同时增加路过人数 |
| POST | `/api/posts` | 发帖，出行类型取当前用户的偏好 |
| POST | `/api/uploads/images` | 上传图片，表单字段名 `file` |
| GET | `/api/locale/detect` | 探测默认语言（免登录），当前固定返回 `{"language":"zh"}` |

发帖请求示例：

```json
{
  "content": "今天地铁上遇到一件挺暖的事。",
  "category": "FRESH",
  "anonymous": true,
  "images": ["/uploads/2026/11/xxxx.png"]
}
```

请求头带上 `X-Auth-Token`，前端已经自动处理。

## 多语言

界面文案统一放在 `app/src/i18n/<语言码>.json`，支持 `zh`/`en`/`ja`/`ko`/`es`/`fr`/`de`/`pt` 八种。

- 当前阶段默认固定中文：前端初始语言直接取中文；首次进入调用 `GET /api/locale/detect`（免登录），后端也固定返回 `zh`，即使接口失败也不影响中文显示。按 IP / `Accept-Language` 分发的逻辑保留在后端 `IpLocaleService`，需要时恢复其块注释即可启用。
- 用户在设置页可临时切换语言（写入本地 `tea_language`），但当前阶段启动时强制中文，刷新后回到中文；需要启用语言偏好持久化时，恢复 `app/src/i18n/index.js` 中被注释的 `initialLanguage` 行即可。
- 语言入口统一放在首页右上角的「设置」→ 设置页；类型选择页、注册登录页不再有语言按钮。
- 后端错误提示仍返回中文 `message`，由 `app/src/utils/request.js` 的 `errorKeyMap` 按原文映射到对应语言，后端文案变动时同步改这张表即可。

## 下一版可以做什么

- 点赞、评论，让信息流有互动
- 头像上传
- 失物招领专区：丢失 / 拾到、线路和站点筛选
- 举报、屏蔽和内容审核
- 图片迁移到对象存储，数据库换 MySQL
