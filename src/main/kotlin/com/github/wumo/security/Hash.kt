package com.github.wumo.security

import java.security.MessageDigest.getInstance

object Hash {
  fun String.md5() =
    StringBuilder(32).apply {
      //优化过的 md5 字符串生成算法
      getInstance("MD5")
        .digest(toByteArray()).forEach {
          val value = it.toInt() and 0xFF
          val high = value / 16
          val low = value % 16
          append(if(high <= 9) '0' + high else 'a' - 10 + high)
          append(if(low <= 9) '0' + low else 'a' - 10 + low)
        }
    }.toString()
}

