package com.zcunsoft.clklog.analysis.utils

import com.alibaba.fastjson.JSON

object CommonUtils {

  /**
   * 判断字符串是否是json格式
   *
   * @param content
   * @return
   */
  def isJson(content: String): Boolean = {
    if (content.isEmpty) return false
    var isJsonObject = true
    var isJsonArray = true
    try JSON.parseObject(content)
    catch {
      case e: Throwable =>
        isJsonObject = false
    }
    try JSON.parseArray(content)
    catch {
      case e: Throwable =>
        isJsonArray = false
    }
    if (!isJsonObject && !isJsonArray) { //不是json格式
      return false
    }
    true
  }

  /**
   * 判断参数的格式是否为“yyyyMMdd”格式的合法日期字符串
   *
   * @param str
   */
  def isValidDate(str: String): Boolean = {
    try {
      if (str != null && str.nonEmpty && (str.length == 8)) { // 闰年标志
        var isLeapYear = false
        val year = str.substring(0, 4)
        val month = str.substring(4, 6)
        val day = str.substring(6, 8)
        val vYear = year.toInt
        // 判断年份是否合法
        if (vYear < 1900 || vYear > 2200) return false
        // 判断是否为闰年
        if (vYear % 4 == 0 && vYear % 100 != 0 || vYear % 400 == 0) isLeapYear = true
        // 判断月份
        // 1.判断月份
        if (month.startsWith("0")) {
          val units4Month = month.substring(1, 2)
          val vUnits4Month = units4Month.toInt
          if (vUnits4Month == 0) return false
          if (vUnits4Month == 2) { // 获取2月的天数
            val vDays4February = day.toInt
            if (isLeapYear) if (vDays4February > 29) return false
            else if (vDays4February > 28) return false
          }
        }
        else { // 2.判断非0打头的月份是否合法
          val vMonth = month.toInt
          if (vMonth != 10 && vMonth != 11 && vMonth != 12) return false
        }
        // 判断日期
        // 1.判断日期
        if (day.startsWith("0")) {
          val units4Day = day.substring(1, 2)
          val vUnits4Day = units4Day.toInt
          if (vUnits4Day == 0) return false
        }
        else { // 2.判断非0打头的日期是否合法
          val vDay = day.toInt
          if (vDay < 10 || vDay > 31) return false
        }
        return true
      } else return false

    } catch {
      case e: Exception =>
        e.printStackTrace()
        return false
    }

  }

  /**
   * 判断字符串否是数字
   *
   * @param s
   * @return
   */
  def judgeIsNumber(s: String): Boolean = {
    val regex = """^\d{3}(.*)""".r
    s match {
      case regex(_*) => true
      case _ => false
    }
  }

  /**
   * 判断IP是否符合IPV4
   *
   * @param s
   * @return
   */
  def validIP(s: String): Boolean = {
    val regex = """((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.){3}(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])(?::(?:[0-9]|[1-9][0-9]{1,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5]))?""".r
    s match {
      case regex(_*) => true
      case _ => false
    }
  }

}
