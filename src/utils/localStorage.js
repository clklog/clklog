/**
 * localStorage 工具类
 */

// 存储数据
export const setLocalStorage = (key, value) => {
  if (typeof value === 'object') {
    value = JSON.stringify(value)
  }
  localStorage.setItem(key, value)
}

// 获取数据
export const getLocalStorage = (key) => {
  const value = localStorage.getItem(key)
  try {
    return JSON.parse(value)
  } catch (e) {
    return value
  }
}

// 删除数据
export const removeLocalStorage = (key) => {
  localStorage.removeItem(key)
}

// 清空所有数据
export const clearLocalStorage = () => {
  localStorage.clear()
} 