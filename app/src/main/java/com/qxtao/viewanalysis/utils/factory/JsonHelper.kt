package com.qxtao.viewanalysis.utils.factory

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken

import java.lang.reflect.Type

fun Any.toJson(): String? {
    return try {
        JsonHelper.toJson(this)
    } catch (t: Throwable) {
        null
    }
}

inline fun <reified T> String.fromJson(): T? {
    return JsonHelper.fromJson(this, object : TypeToken<T>() {}.type)
}

object JsonHelper {
    val gson = Gson()

    fun toJson(o: Any): String {

        return toJson(
            gson,
            o
        )
    }

    fun toJson(gson: Gson, o: Any?): String {
        return if (o == null) {
            ""
        } else gson.toJson(o)
    }

    fun fromJson(json: Any): JsonElement? {
        return fromJson(json, JsonElement::class.java)
    }

    fun <T> fromJson(json: Any, tClass: Class<T>): T? {
        return fromJson(
            gson,
            json,
            tClass
        )
    }

    fun <T> fromJson(json: Any?, tClass: Type): T? {
        try {
            if (json == null) {
                return null
            }
            return if (json is JsonElement) {
                gson.fromJson<T>(json as JsonElement?, tClass)
            } else gson.fromJson<T>(json.toString(), tClass)

        } catch (t: Throwable) {

        }

        return null
    }

    fun <T> fromJson(gson: Gson, json: Any?, tClass: Class<T>): T? {
        try {
            if (json == null) {
                return null
            }
            return if (json is JsonElement) {
                gson.fromJson(json as JsonElement?, tClass)
            } else gson.fromJson(json.toString(), tClass)
        } catch (_: Throwable) {

        }

        return null
    }
}
