package com.github.wumo.os

import com.google.gradle.osdetector.OsDetector

object OS {
  private val osDetector = OsDetector()
  val os: String = osDetector.os
  val arch: String = osDetector.arch
  val classifier: String = osDetector.classifier
  
  const val windows: String = "windows"
  const val linux: String = "linux"
  const val osx: String = "osx"
  
  const val x86_32: String = "x86_32"
  const val x86_64: String = "x86_64"
  
  val isWindows: Boolean = os == windows
  val isLinux: Boolean = os == linux
  val isOSX: Boolean = os == osx
  
  val is32bit: Boolean = arch == x86_32
  val is64bit: Boolean = arch == x86_64
}