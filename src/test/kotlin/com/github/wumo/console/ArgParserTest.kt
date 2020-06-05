package com.github.wumo.console

import kotlin.test.Test

class ArgParserTest {
  
  @Test
  fun parseArg() {
    val args = arrayOf("-n", "myname", "-p", "www", "ddd", "eee", "-type", "msys")
    
    val options = object: ArgParser() {
      val packages: List<String> by option("p")
      val name: String? by option("n")
      
      val ddd by lazy { true }
      val `in`: String? by option("in")
      val dir: Boolean? by option("dir")
      val type: String by option(
        "type",
        "typeof",
        oneOf = setOf("mingw32", "mingw64", "msys"),
        description = "set shell type"
      )
    }
    options.parse(args)
    println(options.name)
    println(options.packages)
    println(options.type)
    options.showUsage()
  }
}