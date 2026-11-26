<template>
  <view
    class="app-icon"
    :style="{
      width: size + 'rpx',
      height: size + 'rpx',
      backgroundImage: iconUrl,
    }"
  />
</template>

<script>
// 统一 24x24 线条风格的矢量图标，用 data URI 作背景图，H5 与小程序都支持。
const paths = {
  tea: '<path d="M4 8h12v6a6 6 0 0 1-6 6 6 6 0 0 1-6-6V8z"/><path d="M16 9h2a2 2 0 0 1 0 4h-2"/><path d="M8 2.5v2.5M12 2v3"/>',
  rail: '<rect x="5" y="3" width="14" height="14" rx="3"/><path d="M5 10h14"/><circle cx="9" cy="13.5" r="1"/><circle cx="15" cy="13.5" r="1"/><path d="M8 17l-2 4M16 17l2 4"/>',
  bus: '<rect x="4" y="4" width="16" height="13" rx="2"/><path d="M4 11h16"/><circle cx="8" cy="18" r="2"/><circle cx="16" cy="18" r="2"/>',
  lang: '<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3c3 3.5 3 14.5 0 18M12 3c-3 3.5-3 14.5 0 18"/>',
  user: '<circle cx="12" cy="8" r="4"/><path d="M4 21c0-4 3.6-6.5 8-6.5s8 2.5 8 6.5"/>',
  menu: '<path d="M4 6h16M4 12h16M4 18h16"/>',
  history: '<path d="M3 12a9 9 0 1 0 3-6.7L3 8"/><path d="M3 3v5h5"/><path d="M12 7v5l3 2"/>',
  star: '<path d="m12 3 2.8 5.7 6.2.9-4.5 4.4 1.1 6.2L12 17.3 6.4 20.2l1.1-6.2L3 9.6l6.2-.9z"/>',
  edit: '<path d="M12 20h9"/><path d="M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4z"/>',
  message: '<path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>',
  logout: '<path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><path d="m16 17 5-5-5-5"/><path d="M21 12H9"/>',
  heart: '<path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21l7.7-7.5 1.1-1.1a5.5 5.5 0 0 0 0-7.8z"/>',
  bookmark: '<path d="M6 3h12a1 1 0 0 1 1 1v17l-7-4-7 4V4a1 1 0 0 1 1-1z"/>',
  trash: '<path d="M4 7h16M10 11v6M14 11v6M6 7l1 13a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1l1-13M9 7V4h6v3"/>',
  image: '<rect x="3" y="4" width="18" height="16" rx="2"/><circle cx="8.5" cy="9" r="1.5"/><path d="m4 17 5-5 4 4 3-3 4 4"/>',
}

export default {
  props: {
    name: { type: String, required: true },
    size: { type: Number, default: 40 },
    color: { type: String, default: '#ffffff' },
  },
  computed: {
    iconUrl() {
      const body = paths[this.name] || paths.tea
      const svg = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="${this.color}" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${body}</svg>`
      return `url("data:image/svg+xml,${encodeURIComponent(svg)}")`
    },
  },
}
</script>

<style>
.app-icon {
  flex: none;
  background-position: center;
  background-repeat: no-repeat;
  background-size: contain;
}
</style>
