package com.time.cat.helper

import org.joda.time.DateTime

fun DateTime.seconds() = (millis / 1000).toInt()
