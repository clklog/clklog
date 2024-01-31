// 平均访问时长
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

// 获取当前日期年月日------
export function todateFunc() {
  const today = new Date();
  const year = today.getFullYear();
  const month = today.getMonth() + 1;
  const day = today.getDate();
  return `${year}-${month}-${day}`;
}

/**
 * Number formatting
 * like 10000 => 10k
 * @param {number} num
 * @param {number} digits
 */
export function numberFormatter(num, digits) {
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
  return 0;
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

export function formatDateYMD(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

// 当前日期
export function formatDateToday() {
  const currentDate = new Date();
  const year = currentDate.getFullYear();
  const month = String(currentDate.getMonth() + 1).padStart(2, "0"); // 月份是从0开始计数的，需要加1
  const day = String(currentDate.getDate()).padStart(2, "0");
  const formattedDate = `${year}-${month}-${day}`;
  return formattedDate;
}