import { createI18n } from 'vue-i18n'
import zh from './zh.json'
import en from './en.json'
import ja from './ja.json'
import ko from './ko.json'
import es from './es.json'
import fr from './fr.json'
import de from './de.json'
import pt from './pt.json'

const LANGUAGE_KEY = 'tea_language'

export const languages = [
  { code: 'zh', label: '中文' },
  { code: 'en', label: 'English' },
  { code: 'ja', label: '日本語' },
  { code: 'ko', label: '한국어' },
  { code: 'es', label: 'Español' },
  { code: 'fr', label: 'Français' },
  { code: 'de', label: 'Deutsch' },
  { code: 'pt', label: 'Português' },
]

const messages = { zh, en, ja, ko, es, fr, de, pt }

function getStoredLanguage() {
  return uni.getStorageSync(LANGUAGE_KEY) || ''
}

const DEFAULT_LANGUAGE = 'zh'

/** 是否用户在设置页手动选过语言。true 时不再按 IP 自动覆盖。 */
export function hasManualLanguage() {
  return !!getStoredLanguage()
}

function detectFromSystem() {
  // 当前阶段统一使用中文，暂不根据系统语言推断；探测接口失败时也能保证中文。
  return DEFAULT_LANGUAGE
}

// 当前阶段固定中文：启动时忽略本地存储的语言，确保打开就是中文。
// 设置页仍可临时切换语言；需要启用用户语言偏好持久化时，恢复下一行即可。
const initialLanguage = DEFAULT_LANGUAGE
// const initialLanguage = getStoredLanguage() || detectFromSystem()

const i18n = createI18n({
  legacy: false,
  globalInjection: true,
  locale: initialLanguage,
  fallbackLocale: DEFAULT_LANGUAGE,
  messages,
})

/** 设置页手动切换才会调用，写入本地存储。 */
export function setLanguage(code) {
  if (!messages[code]) return
  i18n.global.locale.value = code
  uni.setStorageSync(LANGUAGE_KEY, code)
  document.documentElement.setAttribute('lang', code)
}

/** 按 IP 探测结果设置初始语言，不写存储，用户之后手动选过就不再覆盖。 */
export function applyDetectedLanguage(code) {
  // 当前阶段默认固定中文：忽略后端返回的非中文探测结果，
  // 避免后端未重启或浏览器 Accept-Language 为外语时显示成法语等。
  // 需要启用按 IP 分发时，删掉下面这一行即可。
  if (code !== 'zh') return
  if (hasManualLanguage() || !messages[code]) return
  i18n.global.locale.value = code
  document.documentElement.setAttribute('lang', code)
}

export function currentLanguage() {
  return i18n.global.locale.value
}

export default i18n
