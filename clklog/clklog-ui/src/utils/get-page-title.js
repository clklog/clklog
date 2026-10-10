import defaultSettings from '@/settings'

const title = defaultSettings.title || 'ClkLog'

export default function getPageTitle(pageTitle) {
  if (pageTitle) {
    return `${title}- ${pageTitle} `
  }
  return `${title}`
}
