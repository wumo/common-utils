package com.github.wumo.process

import java.io.BufferedReader
import java.io.File

import java.io.InputStream
import java.io.InputStreamReader

object ProcessHelper {
  
  fun run(vararg command: String) {
    val process = ProcessBuilder(*command)
    process.inheritIO().start().waitFor()
  }
  
  fun call(vararg command: String): String {
    val builder = ProcessBuilder(*command)
    builder.redirectErrorStream(true)
    val process: Process = builder.start()
    val input: InputStream = process.inputStream
    val reader = BufferedReader(InputStreamReader(input))
    reader.use {
      return it.readText()
    }
  }
  
  fun File.exec(vararg command: String) {
    val builder = ProcessBuilder(*command)
    builder.directory(this)
    builder.redirectErrorStream(true)
    val process = builder.start()
    val input = process.inputStream
    input.use {
      it.transferTo(System.out)
    }
  }
}