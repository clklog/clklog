package com.zcunsoft.clklog.api.utils;

public class StringUtils {

	/**
	* 下划线转驼峰命名HelloWorld --> hello_world
	*/
	public static String toUnderScoreCase(String str) {
	   if (str == null) {
	       return null;
	   }
	   StringBuilder sb = new StringBuilder();
	   // 前置字符是否大写
	   boolean preCharIsUpperCase = true;
	   // 当前字符是否大写
	   boolean curreCharIsUpperCase = true;
	   // 下一字符是否大写
	   boolean nexteCharIsUpperCase = true;
	   for (int i = 0; i < str.length(); i++) {
	       char c = str.charAt(i);
	       if (i > 0) {
	           preCharIsUpperCase = Character.isUpperCase(str.charAt(i - 1));
	       } else {
	           preCharIsUpperCase = false;
	       }

	       curreCharIsUpperCase = Character.isUpperCase(c);

	       if (i < (str.length() - 1)) {
	           nexteCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
	       }

	       if (preCharIsUpperCase && curreCharIsUpperCase && !nexteCharIsUpperCase) {
	           sb.append("_");
	       } else if ((i != 0 && !preCharIsUpperCase) && curreCharIsUpperCase) {
	           sb.append("_");
	       }
	       sb.append(Character.toLowerCase(c));
	   }

	   return sb.toString();
	}
	
}
