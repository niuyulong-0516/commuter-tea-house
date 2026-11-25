// 本地开发默认指向本机后端；部署时替换成 Render 后端域名。
// Vite 通过 import.meta.env.VITE_API_BASE_URL 注入（需在 vite.config.js 定义或直接用环境变量）。
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8090'

export const PAGE_SIZE = 10

// 前端模拟模式：后端未启动时用本地 mock 数据跑通完整流程。
// 连真实后端时改成 false（部署时必须为 false）。
export const MOCK_MODE = import.meta.env.VITE_MOCK_MODE === 'false' ? false : true
