package com.example.academiaui.feature_db.converter

import androidx.room.TypeConverter
import java.io.File
import java.nio.file.Path
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date

class ListConverter {
    @TypeConverter
    fun fromString(value: String?): List<String>? {
        return value?.split(";")?.map { it.trim() }
    }

    @TypeConverter
    fun toString(list: List<String>?): String? {
        return list?.joinToString(";")
    }

    @TypeConverter
    fun dateToLong(value: Date?): Long? {
        return value?.time
    }

    @TypeConverter
    fun longToDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    private val formatter = DateTimeFormatter.ISO_LOCAL_TIME

    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? {
        return value?.format(formatter)
    }

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it, formatter) }
    }

    @TypeConverter
    fun fileToString(file: File?): String? {
        return file.toString()
    }

    @TypeConverter
    fun stringToFile(string: String): File? {
        return File(string)
    }

}