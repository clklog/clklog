export function percentage(s) {
  if (s) {
    return (s * 100).toFixed(2) + "%";
  }
  return "--";
}

export function percent(s) {
  if (s) {
    return (s * 100).toFixed(2);
  }
  return "--";
}
//平均访问页数计算规则
// 小数点后四位数不是0000直接+1，是0000的，直接取整（即为向上取整）
export function averageRules(num) {
  if (num && num % 1 !== 0) {
    const numAfterDot = num.toString().split(".")[1];
    if (!numAfterDot || parseInt(numAfterDot.substr(0, 4)) !== 0) {
      // num = parseFloat((num + 0.0001).toFixed(4));
      num = parseInt(num + 1);
    } else {
      num = parseInt(num);
    }
  }
  return num;
}
