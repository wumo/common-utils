package com.github.wumo.date

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object DateUtils {
  val Asia_Shanghai_Zone = ZoneId.of("Asia/Shanghai")
  
  fun now(zoneID: ZoneId = ZoneId.systemDefault()): ZonedDateTime {
    return Instant.now().atZone(zoneID)
  }
  
  fun ZonedDateTime.format(format: String): String {
    return format(DateTimeFormatter.ofPattern(format))
  }
  
  fun dateFrom(
    string: String, format: String,
    zoneID: ZoneId = ZoneId.systemDefault()
  ): ZonedDateTime {
    return LocalDateTime.parse(
      string,
      DateTimeFormatter.ofPattern(format)
    ).atZone(zoneID)
  }
}