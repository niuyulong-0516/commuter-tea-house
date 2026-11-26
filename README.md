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
- 数据库：本地 H2 文件数据库 / 部署时 PostgreSQL（Render 免费实例）
- 图片：本地 `uploads/` 目录 / 部署时 Cloudflare R2 对象存储

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

## 线上部署

部署目标是：前端静态站点 + 后端服务 + 数据库 + 图片对象存储，全部走免费额度，适合上架前的验证阶段。

### 用到的服务

| 服务 | 用途 | 免费额度说明 |
| --- | --- | --- |
| GitHub | 代码仓库，Render 从这里拉取代码 | 免费私有仓库 |
| Render | 跑 Spring Boot 后端 + 托管 PostgreSQL 数据库 | Web Service 永久免费（15 分钟无访问休眠）；免费 PostgreSQL 创建后 30 天过期，不可免费续期 |
| Cloudflare Pages | 托管前端 H5 静态站点 | 免费，带宽充足 |
| Cloudflare R2 | 存放用户上传的图片 | 10 GB/月存储免费，无出站流量费 |

### 部署前准备

代码里已经写好了两份部署配置，不需要手动改：

- `render.yaml`：Render Blueprint，一键创建后端服务和数据库
- `server/Dockerfile`：后端 Docker 镜像构建
- `app/src/config.js`：前端通过环境变量切换后端地址

环境变量的下发规则：

- 本地开发不配任何环境变量，默认用 H2 + 本地 `uploads/`
- 线上由 Render / Cloudflare Pages 注入环境变量，自动切换到 PostgreSQL + R2

### 第一步：Cloudflare R2（图片存储）

1. 注册 Cloudflare 并进入 R2，首次开通会要求绑卡（仅身份验证，免费额度内不扣费）
2. 创建存储桶，名称填 `commuter-tea`
3. 桶设置 → 「公共开发 URL」→ 点「启用」，得到公开地址，形如 `https://pub-xxxx.r2.dev`
4. 进入「管理 R2 API 令牌」→ 创建令牌：
   - 类型选「账户 API 令牌」（Account API Token）
   - 权限选「对象读和写」（Object Read & Write）
   - 存储桶只勾选 `commuter-tea`
   - TTL 留空（永不过期）
5. 创建后保存 **Access Key ID** 和 **Secret Access Key**（Secret 只显示一次）
6. 在 R2 首页右侧能看到 **Account ID**

记下这 5 个值（后面 Render 要用）：

```text
R2_ENDPOINT      = https://<Account ID>.r2.cloudflarestorage.com
R2_BUCKET        = commuter-tea
R2_ACCESS_KEY    = <Access Key ID>
R2_SECRET_KEY    = <Secret Access Key>
R2_PUBLIC_URL    = https://pub-xxxx.r2.dev
```

注意：`R2_ENDPOINT` 只到域名，末尾不要带 `/commuter-tea`，桶名单独填在 `R2_BUCKET`。

### 第二步：Render（后端 + 数据库）

1. 代码推送到 GitHub 仓库（Render 只支持 GitHub / GitLab / Bitbucket，不支持 Gitee）
2. 用 GitHub 账号登录 https://render.com
3. 打开 https://dashboard.render.com/blueprints → **New Blueprint Instance**
4. 选择 GitHub 仓库 `niuyulong-0516/commuter-tea-house`
5. Render 会自动识别根目录的 `render.yaml`，点 **Apply**
6. 在变量填写页填入第一步的 5 个 R2 变量
7. Render 会自动创建并关联：
   - Web Service `commuter-tea-server`（Docker 构建，端口 8090）
   - PostgreSQL 数据库 `commuter_tea`
   - 数据库连接串由 Blueprint 自动注入，不用手填

后端健康检查地址：`/api/locale/detect`。

### 第三步：Cloudflare Pages（前端 H5）

1. 在 `app/` 目录构建：`npm run build:h5`，产物在 `app/dist/build/h5`
2. Cloudflare Dashboard → Workers & Pages → Create → Pages → 连接 GitHub 仓库
3. 构建配置：
   - 构建命令：`cd app && npm install && npm run build:h5`
   - 构建输出目录：`app/dist/build/h5`
   - Node 版本选 22 以上（见 `app/.nvmrc`）
4. 环境变量（部署前设置，改完要重新构建）：
   ```text
   VITE_API_BASE_URL=https://<你的后端服务名>.onrender.com
   VITE_MOCK_MODE=false
   ```
5. `VITE_MOCK_MODE=false` 才会走真实后端接口，否则一直是 mock 数据

### 部署后注意

- Render 免费 PostgreSQL 创建后 30 天过期，**不能免费续期**：过期后有 14 天宽限期，期间可付费升级保住数据，否则数据被删除；每个账号同时只能有 1 个免费库，可删除旧库后新建（数据不保留）
- Render 免费服务休眠后首次访问会慢（约 30 秒），属正常现象
- Google Play 上架要求后端为 HTTPS，Render 默认提供 `onrender.com` 的 HTTPS 域名，可直接使用
- 图片上传后返回的是 R2 公开 URL，不占用后端磁盘

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
