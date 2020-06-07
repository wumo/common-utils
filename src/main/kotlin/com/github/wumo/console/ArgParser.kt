package com.github.wumo.console

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClassifier
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.typeOf

open class ArgParser {
  private val requiredOptions = mutableSetOf<String>()
  private val argValues = mutableMapOf<String, Option<*>>()

  inner class Option<T>(
    internal val name: String,
    internal val alias: String?,
    internal val description: String? = null,
    internal val varargs: Boolean,
    internal val required: Boolean,
    internal val defaultValue: T?,
    internal val oneOf: Set<T>?,
    internal val stringConvert: (String)->Any
  ): ReadOnlyProperty<ArgParser, T> {

    init {
      check(name !in argValues) { "option $name is already defined!" }
      argValues[name] = this
      if(alias != null) {
        check(alias !in argValues) { "option $alias is already defined!" }
        argValues[alias] = this
      }
      if(required)
        requiredOptions.add(name)
    }

    internal val values = mutableListOf<Any>()

    override fun getValue(thisRef: ArgParser, property: KProperty<*>): T {
      check(values.isNotEmpty() || !required) { "required option $name doesn't have value" }
      if(values.isEmpty()) return null as T
      return (if(varargs) values else values[0]) as T
    }

    internal fun process(str: String) {
      check(values.isEmpty() || varargs) { "more than value for option $name!" }
      val value = stringConvert(str)
      check(oneOf == null || (value as T) in oneOf) { "option $name value should be one of $oneOf" }

      values.add(value)
    }

    internal fun clear() {
      values.clear()
    }

    override fun toString(): String {
      return buildString {
        append("-$name ")
        if(alias != null)
          append("--$alias ")
        if(description != null)
          append(description)
      }
    }
  }

  @OptIn(ExperimentalStdlibApi::class)
  val converters = mapOf<KClassifier, (String)->Any>(
    typeOf<Byte>().classifier!! to { s: String-> s.toByte() },
    typeOf<Short>().classifier!! to { s: String-> s.toShort() },
    typeOf<Int>().classifier!! to { s: String-> s.toInt() },
    typeOf<Long>().classifier!! to { s: String-> s.toLong() },
    typeOf<Float>().classifier!! to { s: String-> s.toFloat() },
    typeOf<Double>().classifier!! to { s: String-> s.toDouble() },
    typeOf<String>().classifier!! to { s: String-> s },
    typeOf<Char>().classifier!! to { s: String->
      check(s.length == 1) { "longer size to fit one char" }
      s[0]
    },
    typeOf<Boolean>().classifier!! to { s: String-> s.toBoolean() }
  )

  @OptIn(ExperimentalStdlibApi::class)
  inline fun <reified T> option(
    name: String,
    alias: String? = null,
    defaultValue: T? = null,
    oneOf: Set<T>? = null,
    description: String? = null
  ): Option<T> {
    val type = typeOf<T>()
    val classifier = type.classifier ?: error("Not supported type $type")
    val conveter = if(classifier == typeOf<List<*>>().classifier) {
      val subType = type.arguments[0].type?.classifier
      converters[subType] ?: error("Not supported type $type")
    } else
      converters[classifier] ?: error("Not supported type $type")
    return Option(
      name, alias, description,
      T::class.isSubclassOf(List::class), !type.isMarkedNullable,
      defaultValue, oneOf, conveter
    )
  }

  fun showUsage() {
    println("Options:")
    HashSet(argValues.values).forEach {
      println("\t$it")
    }
  }

  val argPattern = Regex("""(?:-|--)(\w+)""")

  fun parse(args: Array<String>) {
    try {
      val required = HashSet<String>(requiredOptions)
      var i = 0
      while(i < args.size)
        argPattern.matchEntire(args[i++])?.also {
          val (name) = it.destructured
          val option = argValues[name] ?: error("Bad option")
          check(i < args.size) { "not value for option $name" }
          option.process(args[i++])
          if(option.required)
            required.remove(option.name)
          if(option.varargs)
            while(i < args.size && argPattern.matchEntire(args[i]) == null)
              option.process(args[i++])
        } ?: error("Bad option!")

      check(required.isEmpty()) {
        buildString {
          append("Missing values for options:")
          appendln(required.joinToString(",") { it })
        }
      }
    } catch(e: Exception) {
      showUsage()
      throw e
    }
  }
}