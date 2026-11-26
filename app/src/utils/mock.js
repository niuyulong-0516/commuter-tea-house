// 前端 mock 数据：后端未启动时用本地数据跑通完整流程，由 config.js 的 MOCK_MODE 控制。
// 连真实后端时把 MOCK_MODE 改成 false 即可，页面代码无需改动。

const DEMO_USER = {
  id: '10001',
  username: 'demo',
  transportType: 'RAIL',
  token: 'mock-token-demo',
  createdAt: '2026-01-01T00:00:00',
  avatar: '',
  nickname: '茶客小明',
  gender: 'SECRET',
  bio: '上班路上，坐下来聊两句',
}

const minutesAgo = (minutes) => new Date(Date.now() - minutes * 60 * 1000).toISOString()

// 占位图用 SVG data URI，完全离线可用，避免依赖外部图片服务。
const placeholder = (text) =>
  `data:image/svg+xml,${encodeURIComponent(
    `<svg xmlns="http://www.w3.org/2000/svg" width="600" height="400"><rect width="100%" height="100%" fill="#dbe8df"/><text x="50%" y="50%" fill="#2f6b4f" font-size="36" text-anchor="middle" dominant-baseline="middle">${text}</text></svg>`
  )}`

let posts = [
  {
    id: 1,
    content: '今天早班地铁居然有座位，终于能安静喝杯茶再上班了。',
    category: 'FRESH',
    transportType: 'RAIL',
    anonymous: false,
    authorName: '茶客小林',
    images: [placeholder('车窗外的清晨')],
    createdAt: minutesAgo(6),
    viewCount: 18,
  },
  {
    id: 2,
    content: '通勤两小时真的会消耗掉对生活的一半热情，有没有同款伙伴聊聊你们怎么熬过来的？',
    category: 'RANT',
    transportType: 'RAIL',
    anonymous: true,
    authorName: '',
    images: [],
    createdAt: minutesAgo(45),
    viewCount: 32,
  },
  {
    id: 3,
    content: '换到公交之后发现沿路风景还挺好，分享一张今天拍到的晚霞。',
    category: 'FRESH',
    transportType: 'BUS',
    anonymous: false,
    authorName: '阿哲',
    images: [placeholder('公交上的晚霞')],
    createdAt: minutesAgo(180),
    viewCount: 9,
  },
  {
    id: 4,
    content: '换乘通道能不能装个自动扶梯，每天爬楼梯真的累了。',
    category: 'RANT',
    transportType: 'BUS',
    anonymous: false,
    authorName: '通勤老王',
    images: [],
    createdAt: minutesAgo(1500),
    viewCount: 25,
  },
]

let nextId = posts.length + 1

// 互动状态与评论存在内存里，刷新页面会重置（仅用于无后端时的演示）。
const interactions = new Set() // 元素形如 `${postId}:${type}`
let comments = [
  { id: 1, postId: 1, userId: '10002', authorName: '茶客小周', content: '同款！早班有座就是一天好心情的开始。', createdAt: minutesAgo(4) },
]
let nextCommentId = 2

// 浏览历史、草稿存在内存里，刷新页面会重置（仅用于无后端时的演示）。
let historyIds = [1, 3]
let drafts = [
  {
    id: 1, userId: DEMO_USER.id, content: '今天地铁上遇到一件有意思的事……',
    category: 'FRESH', anonymous: false, images: [], updatedAt: minutesAgo(120),
  },
]
let nextDraftId = 2

const ok = (data, ms = 200) => new Promise((resolve) => setTimeout(() => resolve(data), ms))
const fail = (message, ms = 200) =>
  new Promise((resolve, reject) => setTimeout(() => reject(new Error(message)), ms))

function parseQuery(url) {
  const result = {}
  const index = url.indexOf('?')
  if (index < 0) return result
  for (const part of url.slice(index + 1).split('&')) {
    const [key, value = ''] = part.split('=')
    result[decodeURIComponent(key)] = decodeURIComponent(value)
  }
  return result
}

export function mockRequest({ url, method = 'GET', data }) {
  const path = url.split('?')[0]
  const query = parseQuery(url)

  const agreementMatch = path.match(/^\/api\/agreements\/(terms|privacy)$/)
  if (agreementMatch) {
    const code = agreementMatch[1]
    return ok({
      code,
      content: code === 'terms'
        ? '通勤茶馆 用户协议\n\n欢迎使用通勤茶馆。本应用是一个面向通勤人群的轻社区。\n\n一、服务说明\n本应用提供内容浏览、发布、点赞、收藏、评论等功能。\n\n二、用户行为规范\n请遵守法律法规，不得发布违法违规内容。\n\n三、联系我们\n如需帮助，请通过应用内反馈渠道或邮件联系我们。'
        : '通勤茶馆 隐私协议\n\n我们重视你的隐私。本协议说明本应用如何收集、使用和保护你的个人信息。\n\n一、我们收集的信息\n账号资料、发布内容、互动数据及反馈信息。\n\n二、信息的使用\n用于提供服务、改进体验及保障账号安全。\n\n三、信息安全\n我们采用合理措施保护你的信息。\n\n四、联系我们\n如有疑问，请通过应用内反馈或邮件联系我们。',
      updatedAt: '2026-11-25T00:00:00',
    })
  }

  if (path === '/api/locale/detect') return ok({ language: 'zh' })

  if (path === '/api/feedback' && method === 'POST') {
    if (!data || !data.description || !data.description.trim()) {
      return fail('问题描述不能为空')
    }
    return ok({
      id: Date.now(),
      description: data.description.trim(),
      images: data.images || [],
      whatsapp: data.whatsapp || '',
      createdAt: new Date().toISOString(),
    })
  }

  if (path === '/api/auth/login' && method === 'POST') {
    if (data && data.username === 'demo' && data.password === '123456') return ok(DEMO_USER)
    return fail('用户名或密码不正确')
  }

  if (path === '/api/auth/register' && method === 'POST') {
    if (!data || !data.username || data.username.length < 3) return fail('用户名长度 3-32 位')
    if (!data.password || data.password.length < 6) return fail('密码长度 6-64 位')
    return ok({
      ...DEMO_USER,
      id: String(nextId++),
      username: data.username,
      transportType: data.transportType || 'RAIL',
      token: `mock-token-${data.username}`,
    })
  }

  if (path === '/api/auth/me') return ok(DEMO_USER)
  if (path === '/api/auth/logout') return ok(null)

  if (path === '/api/auth/transport-type' && method === 'PUT') {
    return ok({ ...DEMO_USER, transportType: data && data.transportType })
  }

  if (path === '/api/auth/profile' && method === 'PUT') {
    Object.assign(DEMO_USER, data || {})
    return ok({ ...DEMO_USER })
  }

  // 背景图历史（mock 用内存数组，重启页面即重置）
  let mockBackgrounds = []
  if (path === '/api/auth/backgrounds') {
    if (method === 'GET') return ok({ current: DEMO_USER.backgroundImage || '', items: mockBackgrounds })
    if (method === 'POST') {
      const url = (data && data.url || '').trim()
      if (url && !mockBackgrounds.some((item) => item.url === url)) {
        mockBackgrounds = [{ id: Date.now(), url, createdAt: new Date().toISOString() }, ...mockBackgrounds]
      }
      DEMO_USER.backgroundImage = url
      return ok({ current: url, items: mockBackgrounds })
    }
    if (method === 'DELETE') {
      DEMO_USER.backgroundImage = ''
      return ok({ current: '', items: mockBackgrounds })
    }
  }
  const bgSelectMatch = path.match(/^\/api\/auth\/backgrounds\/(\d+)$/)
  if (bgSelectMatch && method === 'PUT') {
    const item = mockBackgrounds.find((b) => b.id === Number(bgSelectMatch[1]))
    if (item) DEMO_USER.backgroundImage = item.url
    return ok({ current: DEMO_USER.backgroundImage || '', items: mockBackgrounds })
  }

  if (path.startsWith('/api/library/')) {
    const rest = path.slice('/api/library/'.length)
    const historyMatch = rest.match(/^history\/(\d+)$/)
    if (historyMatch && method === 'POST') {
      const id = Number(historyMatch[1])
      historyIds = [id, ...historyIds.filter((item) => item !== id)]
      return ok(null)
    }
    if (rest === 'history') {
      return ok(posts.filter((post) => historyIds.includes(post.id)))
    }
    if (rest === 'my-posts') {
      return ok(posts.filter((post) => post.authorName === DEMO_USER.nickname))
    }
    if (rest === 'drafts') {
      if (method === 'GET') return ok(drafts)
      if (method === 'POST') {
        const draft = {
          id: nextDraftId++, userId: DEMO_USER.id,
          content: (data && data.content) || '',
          category: (data && data.category) || 'FRESH',
          anonymous: !!(data && data.anonymous),
          images: (data && data.images) || [],
          updatedAt: new Date().toISOString(),
        }
        drafts.unshift(draft)
        return ok(draft)
      }
    }
    const draftMatch = rest.match(/^drafts\/(\d+)$/)
    if (draftMatch) {
      const id = Number(draftMatch[1])
      const index = drafts.findIndex((item) => item.id === id)
      if (index < 0) return fail('草稿不存在')
      if (method === 'GET') return ok(drafts[index])
      if (method === 'PUT') {
        drafts[index] = {
          ...drafts[index],
          content: (data && data.content) || '',
          category: (data && data.category) || 'FRESH',
          anonymous: !!(data && data.anonymous),
          images: (data && data.images) || [],
          updatedAt: new Date().toISOString(),
        }
        return ok(drafts[index])
      }
      if (method === 'DELETE') {
        drafts.splice(index, 1)
        return ok(null)
      }
    }
    return fail('请求失败，请稍后再试')
  }

  if (path === '/api/banners') {
    const hot = posts.slice().sort((a, b) => b.viewCount - a.viewCount).slice(0, 5)
    return ok(hot.map((post) => ({
      postId: post.id,
      content: post.content,
      image: post.images && post.images.length ? post.images[0] : null,
    })))
  }

  if (path === '/api/posts/favorites') {
    const ids = Array.from(interactions)
      .filter((key) => key.endsWith(':FAVORITE'))
      .map((key) => Number(key.split(':')[0]))
    return ok(posts.filter((post) => ids.includes(post.id)))
  }

  const socialMatch = path.match(/^\/api\/posts\/(\d+)\/(interactions|like|favorite|comments)$/)
  if (socialMatch) {
    const postId = Number(socialMatch[1])
    const action = socialMatch[2]
    const post = posts.find((item) => item.id === postId)
    if (!post) return fail('这杯茶暂时找不到了')

    if (action === 'interactions') {
      return ok({
        liked: interactions.has(`${postId}:LIKE`),
        favorited: interactions.has(`${postId}:FAVORITE`),
        likeCount: Array.from(interactions).filter((key) => key === `${postId}:LIKE`).length + (post.likeCount || 0),
        favoriteCount: Array.from(interactions).filter((key) => key === `${postId}:FAVORITE`).length + (post.favoriteCount || 0),
        commentCount: comments.filter((comment) => comment.postId === postId).length,
      })
    }

    if (action === 'like' || action === 'favorite') {
      const type = action === 'like' ? 'LIKE' : 'FAVORITE'
      const key = `${postId}:${type}`
      let active
      if (interactions.has(key)) {
        interactions.delete(key)
        active = false
      } else {
        interactions.add(key)
        active = true
      }
      const count = Array.from(interactions).filter((item) => item.endsWith(`:${type}`) && Number(item.split(':')[0]) === postId).length
        + (post[`${action}Count`] || 0)
      return ok({ active, count })
    }

    if (action === 'comments') {
      if (method === 'GET') {
        const page = Number(query.page || 0)
        const size = Number(query.size || 20)
        const items = comments.filter((comment) => comment.postId === postId)
        return ok({
          items: items.slice(page * size, page * size + size),
          page,
          size,
          total: items.length,
          hasMore: (page + 1) * size < items.length,
        })
      }
      if (method === 'POST') {
        const comment = {
          id: nextCommentId++,
          postId,
          userId: DEMO_USER.id,
          authorName: DEMO_USER.nickname,
          content: (data && data.content || '').trim(),
          createdAt: new Date().toISOString(),
        }
        comments.unshift(comment)
        return ok(comment)
      }
    }
  }

  if (path === '/api/posts' && method === 'GET') {
    let items = posts.slice()
    if (query.category && query.category !== 'ALL') {
      items = items.filter((post) => post.category === query.category)
    }
    if (query.transportType && query.transportType !== 'ALL') {
      items = items.filter((post) => post.transportType === query.transportType)
    }
    if (query.sort === 'hot') items.sort((a, b) => b.viewCount - a.viewCount)

    const page = Number(query.page || 0)
    const size = Number(query.size || 10)
    return ok({
      items: items.slice(page * size, page * size + size),
      page,
      size,
      total: items.length,
      hasMore: (page + 1) * size < items.length,
    })
  }

  if (path === '/api/posts' && method === 'POST') {
    const post = {
      id: nextId++,
      content: (data && data.content) || '',
      category: (data && data.category) || 'FRESH',
      transportType: DEMO_USER.transportType,
      anonymous: !!(data && data.anonymous),
      authorName: data && data.anonymous ? '' : DEMO_USER.username,
      images: (data && data.images) || [],
      createdAt: new Date().toISOString(),
      viewCount: 0,
    }
    posts.unshift(post)
    return ok(post)
  }

  if (path === '/api/posts/search' && method === 'GET') {
    const keyword = (query.keyword || '').trim().toLowerCase()
    const page = Number(query.page || 0)
    const size = Number(query.size || 10)
    const items = keyword
      ? posts.filter((post) => post.content.toLowerCase().includes(keyword))
      : []
    return ok({
      items: items.slice(page * size, page * size + size),
      page,
      size,
      total: items.length,
      hasMore: (page + 1) * size < items.length,
    })
  }

  const detailMatch = path.match(/^\/api\/posts\/(\d+)$/)
  if (detailMatch && method === 'GET') {
    const post = posts.find((item) => String(item.id) === detailMatch[1])
    if (!post) return fail('这杯茶暂时找不到了')
    return ok({ ...post, viewCount: post.viewCount + 1 })
  }

  return fail('请求失败，请稍后再试')
}

export function mockUploadImage() {
  return ok({ url: placeholder('新图片') })
}
