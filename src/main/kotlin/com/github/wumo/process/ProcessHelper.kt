package com.github.wumo.process

import com.github.wumo.os.OS
import java.io.BufferedReader
import java.io.File

import java.io.InputStream
import java.io.InputStreamReader

object ProcessHelper {
  fun exec(cmd: String, workDir: File? = null) {
    val dir = workDir ?: File(".")
    if(OS.isWindows)
      exec(dir, "cmd", "/c", "call $cmd")
    else
      exec(dir, "/bin/bash", "-c", cmd)
  }
  
  fun eval(cmd: String, workDir: File? = null): String {
    val dir = workDir ?: File(".")
    return if(OS.isWindows)
      call(dir, "cmd", "/c", "call $cmd")
    else
      call(dir, "/bin/bash", "-c", cmd)
  }
  
  fun call(workDir: File, vararg command: String): String {
    val builder = ProcessBuilder(*command)
    builder.directory(workDir)
    builder.redirectErrorStream(true)
    val process: Process = builder.start()
    val input: InputStream = process.inputStream
    val reader = BufferedReader(InputStreamReader(input))
    reader.use {
      return it.readText()
    }
  }
  
  fun exec(workDir: File, vararg command: String) {
    val builder = ProcessBuilder(*command)
    builder.directory(workDir)
    builder.redirectErrorStream(true)
    val process = builder.start()
    val input = process.inputStream
    input.use {
      it.transferTo(System.out)
    }
    check(process.exitValue() == 0) { "error exec $command" }
  }
}