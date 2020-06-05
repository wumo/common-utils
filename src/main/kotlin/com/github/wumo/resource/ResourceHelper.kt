package com.github.wumo.resource

import java.nio.charset.Charset
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths

object ResourceHelper {
  
  fun toPath(resource: String): Path {
    val uri = Thread.currentThread().contextClassLoader
      .getResource(resource).toURI()
    return if (uri.scheme == "jar") {
      val fs = FileSystems.newFileSystem(uri, emptyMap<String, Any>())
      fs.getPath(resource)
    } else {
      Paths.get(uri)
    }.toAbsolutePath()
  }
  
  fun getInputStream(resource: String) = Thread.currentThread().contextClassLoader
    .getResourceAsStream(resource.replace('\\', '/'))
  
  fun readAll(resource: String, charset: Charset = Charset.defaultCharset()): String {
    getInputStream(resource).use {
      return it!!.readBytes().toString(Charset.defaultCharset())
    }
  }
}