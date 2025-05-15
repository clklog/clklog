// 时间格式化
export function formatTime(seconds) {
  if (seconds) {
    seconds = Math.floor(seconds);
    var hours = Math.floor(seconds / 3600);
    var minutes = Math.floor((seconds - hours * 3600) / 60);
    var seconds = seconds - hours * 3600 - minutes * 60;

    if (hours < 10) {
      hours = "0" + hours;
    }
    if (minutes < 10) {
      minutes = "0" + minutes;
    }
    if (seconds < 10) {
      seconds = "0" + seconds;
    }
    return hours + ":" + minutes + ":" + seconds;
  } else {
    return "--";
  }
}
// 获取当前小时的方式
export function getCurrentHour() {
  const currentDate = new Date();
  let currentHour = currentDate.getHours();

  // 如果当前小时小于 10，则在前面添加 0
  currentHour = currentHour < 10 ? "0" + currentHour : currentHour;

  return currentHour;
}
// 流量概览统一展示数值方式
export function filterOverview(val) {
  // if (val == 0) {
  //   return val;
  // }
  if (!val) {
    return "--";
  } else {
    return val;
  }
}

export function formatTimeTable(seconds) {
  if (seconds) {
    seconds = Math.floor(seconds);
    var hours = Math.floor(seconds / 3600);
    var minutes = Math.floor((seconds - hours * 3600) / 60);
    var seconds = seconds - hours * 3600 - minutes * 60;

    if (hours < 10) {
      hours = "0" + hours;
    }
    if (minutes < 10) {
      minutes = "0" + minutes;
    }
    if (seconds < 10) {
      seconds = "0" + seconds;
    }
    return hours + ":" + minutes + ":" + seconds;
  } else {
    return 0;
  }
}

/**
 * Show plural label if time is plural number
 * @param {number} time
 * @param {string} label
 * @return {string}
 */
function pluralize(time, label) {
  if (time === 1) {
    return time + label;
  }
  return time + label + "s";
}

/**
 * @param {number} time
 */
export function timeAgo(time) {
  const between = Date.now() / 1000 - Number(time);
  if (between < 3600) {
    return pluralize(~~(between / 60), " minute");
  } else if (between < 86400) {
    return pluralize(~~(between / 3600), " hour");
  } else {
    return pluralize(~~(between / 86400), " day");
  }
}

// 时间戳转换为年月日---------
export function timeStampFunc(timestamp) {
  const date = new Date(timestamp);
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, "0");
  const day = date.getDate().toString().padStart(2, "0");
  return `${year}-${month}-${day}`;
}
// 时间戳转换为年月日时分秒
export function formatDateHMS(timestamp) {
  if (!timestamp) {
    return timestamp
  }
  const date = new Date(timestamp);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  const seconds = String(date.getSeconds()).padStart(2, "0");
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}
// 日期转为时间戳
export function dateTimeStringToTimestamp(dateTimeString) {
  // 创建一个 Date 对象，将日期时间字符串作为参数传入
  const date = new Date(dateTimeString);
  // 返回对应的时间戳
  return date.getTime();
}

//获取几周前的日期
function getFormattedDate(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}
export function getMondayOfPreviousWeeks(weeksAgo) {
  const today = new Date();
  const dayOfWeek = today.getDay();
  const daysSinceMonday = (dayOfWeek + 6) % 7;
  const mondayOfPreviousWeeks = new Date(
    today.getTime() - (daysSinceMonday + weeksAgo * 7) * 24 * 60 * 60 * 1000
  );

  return getFormattedDate(mondayOfPreviousWeeks);
}

export function getFirstDayOfMonths(monthsAgo) {
  const today = new Date();
  const firstDayOfPreviousMonths = new Date(
    today.getFullYear(),
    today.getMonth() - monthsAgo,
    1
  );
  return getFormattedDate(firstDayOfPreviousMonths);
}

// 获取今日日期年月日
export function todateFunc() {
  const today = new Date();
  const year = today.getFullYear();
  const month = today.getMonth() + 1;
  const day = today.getDate();
  return `${year}-${month < 10 ? "0" + month : month}-${
    day < 10 ? "0" + day : day
  }`;
}

/**
 * Number formatting
 * like 10000 => 10k
 * @param {number} num
 * @param {number} digits
 */
export function numberFormatter(num, digits) {
  if (!num) {
    return "--";
  }
  const si = [
    { value: 1e18, symbol: "E" },
    { value: 1e15, symbol: "P" },
    { value: 1e12, symbol: "T" },
    { value: 1e9, symbol: "G" },
    { value: 1e6, symbol: "M" },
    { value: 1e3, symbol: "k" },
  ];
  for (let i = 0; i < si.length; i++) {
    if (num >= si[i].value) {
      return (
        (num / si[i].value)
          .toFixed(digits)
          .replace(/\.0+$|(\.[0-9]*[1-9])0+$/, "$1") + si[i].symbol
      );
    }
  }
  return num.toString();
}

// 数据格式识别
export function foramtDataValue(value) {
  if (value === true) {
    return true;
  } else if (value === false) {
    return false;
  } else if (value === "") {
    return "";
  } else if (value === 0) {
    return 0;
  } else if (value === "null" || value === "NULL" || value === "undefined") {
    return "";
  } else {
    return value; // 或者根据需求返回其他默认值
  }
}

// 格式化空间
export function conver(limit) {
  if (limit) {
    var size = "";
    if (limit < 0.1 * 1024) {
      //如果小于0.1KB转化成B
      size = limit.toFixed(2) + "B";
    } else if (limit < 0.1 * 1024 * 1024) {
      //如果小于0.1MB转化成KB
      size = (limit / 1024).toFixed(2) + "KB";
    } else if (limit < 0.1 * 1024 * 1024 * 1024) {
      //如果小于0.1GB转化成MB
      size = (limit / (1024 * 1024)).toFixed(2) + "MB";
    } else {
      //其他转化成GB
      size = (limit / (1024 * 1024 * 1024)).toFixed(2) + "GB";
    }
    var sizestr = size + "";
    var len = sizestr.indexOf(".");
    var dec = sizestr.substr(len + 1, 2);
    if (dec == "00") {
      //当小数点后为00时 去掉小数部分
      return sizestr.substring(0, len) + sizestr.substr(len + 3, 2);
    }
    return sizestr;
  } else {
    return "--";
  }
}

export function percenBracket(s) {
  if (s) {
    return "(" + (s * 100).toFixed(2) + "%" + ")";
  }
  return "--";
}
export function toFixedPoint(s) {
  if (s) {
    return (s * 1).toFixed(2) + "%";
  }
  return 0 + "%";
}
export function percentage(s) {
  if (s) {
    return (s * 100).toFixed(2) + "%";
  }
  return "--";
}
// 占比规则
export function percenTable(s) {
  if (s) {
    return (s * 100).toFixed(2) + "%";
  }
  return 0;
}

export function percenTableNull(s) {
  if (s || s == 0) {
    return (s * 100).toFixed(2) + "%";
  }
  return "";
}
// 平均访问页数
export function averageRules(number) {
  if (number) {
    if (Number.isInteger(number)) {
      return number;
    } else {
      return Math.ceil(number);
    }
  }
  return "--";
}
export function averageRulesTable(number) {
  if (number) {
    if (Number.isInteger(number)) {
      return number;
    } else {
      return Math.ceil(number);
    }
  }
  return 0;
}

/**
 * 10000 => "10,000"
 * @param {number} num
 */
export function toThousandFilter(num) {
  return (+num || 0)
    .toString()
    .replace(/^-?\d+/g, (m) => m.replace(/(?=(?!\b)(\d{3})+$)/g, ","));
}

/**
 * Upper case first char
 * @param {String} string
 */
export function uppercaseFirst(string) {
  return string.charAt(0).toUpperCase() + string.slice(1);
}

// 格式化日期标准格式
export function formatDateYMD(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

export function formatDateToday() {
  const currentDate = new Date();
  const year = currentDate.getFullYear();
  const month = String(currentDate.getMonth() + 1).padStart(2, "0"); // 月份是从0开始计数的，需要加1
  const day = String(currentDate.getDate()).padStart(2, "0");
  const formattedDate = `${year}-${month}-${day}`;
  return formattedDate;
}

// 获取12周的日期
export function get3WeekdaysFromDate(dateString) {
  const currentDate = new Date(dateString);

  // 获取当前日期所在的周几（0表示周日，1表示周一，依次类推）
  const currentDayOfWeek = currentDate.getDay();

  // 计算当前日期距离上一个周一的天数
  const daysUntilPreviousMonday =
    currentDayOfWeek === 0 ? 6 : currentDayOfWeek - 1;

  // 计算上一个周一的日期
  const previousMonday = new Date(currentDate);
  previousMonday.setDate(previousMonday.getDate() - daysUntilPreviousMonday);

  // 计算第三周前的周一日期
  const thirdWeekPreviousMonday = new Date(previousMonday);
  thirdWeekPreviousMonday.setDate(thirdWeekPreviousMonday.getDate() - 77); // 14天为两周

  return thirdWeekPreviousMonday;
}
// 获取前30天的日期
export function getThirtyDaysAgo(dateString) {
  const currentDate = new Date(dateString);

  // 获取当前日期的时间戳
  const currentTimestamp = currentDate.getTime();

  // 计算30天前的时间戳
  const thirtyDaysAgoTimestamp = currentTimestamp - 29 * 24 * 60 * 60 * 1000;

  // 创建30天前的日期对象
  const thirtyDaysAgoDate = new Date(thirtyDaysAgoTimestamp);

  // 获取年、月、日
  const year = thirtyDaysAgoDate.getFullYear();
  const month = (thirtyDaysAgoDate.getMonth() + 1).toString().padStart(2, "0"); // 月份从0开始，需要加1，并确保是两位数
  const day = thirtyDaysAgoDate.getDate().toString().padStart(2, "0"); // 确保日期是两位数

  // 返回格式化后的日期字符串
  return `${year}-${month}-${day}`;
}
// 获取12月前的1号日期
export function getTwelveMonthsAgoFirstDay(dateString) {
  const currentDate = new Date(dateString);

  // 获取当前日期的年份和月份
  const currentYear = currentDate.getFullYear();
  const currentMonth = currentDate.getMonth() + 1; // 月份从0开始，加1以匹配实际月份

  // 计算12个月前的年份和月份
  const twelveMonthsAgoYear = currentYear - 1; // 减1表示12个月前
  const twelveMonthsAgoMonth = currentMonth;

  // 创建12个月前的日期对象，设置为该月的第一天
  const twelveMonthsAgoDate = new Date(
    twelveMonthsAgoYear,
    twelveMonthsAgoMonth - 1,
    1
  );

  // 获取年、月、日
  const year = twelveMonthsAgoDate.getFullYear();
  const month = (twelveMonthsAgoDate.getMonth() + 1)
    .toString()
    .padStart(2, "0"); // 月份从0开始，需要加1，并确保是两位数
  const day = twelveMonthsAgoDate.getDate().toString().padStart(2, "0"); // 确保日期是两位数

  // 返回格式化后的日期字符串
  return `${year}-${month}-${day}`;
}
export function isNoFilter(val) {
  if (val) {
    return "是";
  } else if (val == false) {
    return "否";
  } else {
    return "";
  }
}
export function isNoFilterShow(val) {
  if (val) {
    return "显示";
  } else if (val == false) {
    return "隐藏";
  } else {
    return "";
  }
}
export function formatNumber(number) {
  if (!number && number != 0) {
    return "--";
  } else if (number == 0) {
    return 0;
  }
  if (number < 10000) {
    return number.toString();
  }
  if (number < 100000000) {
    return (number / 10000).toFixed(2) + "万";
  }
  if (number > 100000000) {
    return (number / 100000000).toFixed(2) + "亿";
  }
}

export function filterText(val) {
  switch (val) {
    case "STRING":
      return "文本";
    case "NUMBER":
      return "数值";
    case "DATETIME":
      return "时间";
    case "BOOL":
      return "是非";
    case "LIST":
      return "LIST";
  }
}

export function createUniqueString() {
  var randomBytes = new Uint8Array(4);
  window.crypto.getRandomValues(randomBytes);
  var randomNumber = 0;
  for (var i = 0; i < randomBytes.length; i++) {
    randomNumber += randomBytes[i] * Math.pow(256, i);
  }
  return randomNumber;
}

// 过滤包含http;https;www的url
export function filterUrl(uri) {
  var regExp = /^(http:\/\/|https:\/\/|www\.)/;
  return regExp.test(uri);
}
