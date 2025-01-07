package com.example.todolist.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.todolist.dataclass.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tasks.db" // 数据库名称
        private const val DATABASE_VERSION = 1 // 数据库版本
        private const val TABLE_NAME = "tasks" // 表名

        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
        private const val COLUMN_ATTACHMENT = "attachment"
        private const val COLUMN_DUE_DATE = "dueDate"
        private const val COLUMN_IMPORTANCE = "importance"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_TAGS = "tags"
        private const val COLUMN_IS_COMPLETED = "isCompleted"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_NAME (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_TITLE TEXT, " +
                    "$COLUMN_CONTENT TEXT, " +
                    "$COLUMN_ATTACHMENT TEXT, " +
                    "$COLUMN_DUE_DATE INTEGER, " +
                    "$COLUMN_IMPORTANCE INTEGER, " +
                    "$COLUMN_CATEGORY TEXT, " +
                    "$COLUMN_TAGS TEXT, " +
                    "$COLUMN_IS_COMPLETED INTEGER)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // 插入任务
    fun insertTask(task: Task): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_CONTENT, task.content)
            put(COLUMN_ATTACHMENT, task.attachment)
            put(COLUMN_DUE_DATE, task.dueDate)
            put(COLUMN_IMPORTANCE, task.importance)
            put(COLUMN_CATEGORY, task.category)
            put(COLUMN_TAGS, task.tags.joinToString(","))
            put(COLUMN_IS_COMPLETED, if (task.isCompleted) 1 else 0)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    // 查询所有任务
    fun getAllTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
                val attachment = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTACHMENT))
                val dueDate = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE))
                val importance = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMPORTANCE))
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                val tags = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAGS)).split(",")
                val isCompleted =
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1

                val task = Task(
                    id,
                    title,
                    content,
                    attachment,
                    dueDate,
                    importance,
                    category,
                    tags,
                    isCompleted
                )
                tasks.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return tasks
    }

    // 查询当天任务
    fun getTodayTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = readableDatabase

        // 获取今天的日期字符串，例如 "2024-11-07"
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // 查询当天的数据
        val query = "SELECT * FROM $TABLE_NAME WHERE strftime('%Y-%m-%d', $COLUMN_DUE_DATE / 1000, 'unixepoch') = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(todayDate))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
                val attachment = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTACHMENT))
                val dueDate = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE))
                val importance = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMPORTANCE))
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                val tags = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAGS))
                    .split(",").map { it.trim() }
                val isCompleted =
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1

                val task = Task(
                    id,
                    title,
                    content,
                    attachment,
                    dueDate,
                    importance,
                    category,
                    tags,
                    isCompleted
                )
                tasks.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return tasks
    }

    // 根据id查询任务
    fun getTaskById(taskId: Long): Task? {
        val db = readableDatabase
        var task: Task? = null
        val cursor = db.query(
            TABLE_NAME,
            null,  // 查询所有列
            "$COLUMN_ID = ?",
            arrayOf(taskId.toString()),
            null,
            null,
            null
        )
        // 拿到任务数据了，直接创建对象返回，不用再循环游标了
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
            val attachment = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTACHMENT))
            val dueDate = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE))
            val importance = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMPORTANCE))
            val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
            val tags = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAGS))
                .split(",").map { it.trim() }
            val isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1

            // 创建 Task 对象
            task = Task(
                id = id,
                title = title,
                content = content,
                attachment = attachment,
                dueDate = dueDate,
                importance = importance,
                category = category,
                tags = tags,
                isCompleted = isCompleted
            )
        }
        cursor.close()
        db.close()
        return task
    }

    fun getTitlesByDate(dueDate: Long): List<String> {
        Log.e("error", dueDate.toString())
        val db = readableDatabase
        val titles = mutableListOf<String>()
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_TITLE),  // 只查询 title 列
            "$COLUMN_DUE_DATE = ?",
            arrayOf(dueDate.toString()),
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            titles.add(title)
        }

        cursor.close()
        db.close()
        return titles
    }

    fun getTitlesAndContentsByDate(dueDate: Long): List<Pair<String, String>> {
        Log.e("error", dueDate.toString())
        val db = readableDatabase
        val titlesAndContents = mutableListOf<Pair<String, String>>()
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_TITLE, COLUMN_CONTENT),  // 查询 title 和 content 列
            "$COLUMN_DUE_DATE = ?",
            arrayOf(dueDate.toString()),
            null,
            null,
            null
        )
        while (cursor.moveToNext()) {
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
            titlesAndContents.add(Pair(title, content))
        }
        cursor.close()
        db.close()
        return titlesAndContents
    }

    // 查询之前未完成的任务
    fun getIncompleteTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = readableDatabase

        // 获取今天的日期字符串，例如 "2024-11-07"
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // 查询当天之前（不包括当天），未完成的任务数据
        val query = "SELECT * FROM $TABLE_NAME WHERE " +
                "strftime('%Y-%m-%d', $COLUMN_DUE_DATE / 1000, 'unixepoch') < ? and $COLUMN_IS_COMPLETED = 0"
        val cursor: Cursor = db.rawQuery(query, arrayOf(todayDate))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
                val attachment = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTACHMENT))
                val dueDate = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE))
                val importance = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMPORTANCE))
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                val tags = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAGS))
                    .split(",").map { it.trim() }
                val isCompleted =
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1

                val task = Task(
                    id,
                    title,
                    content,
                    attachment,
                    dueDate,
                    importance,
                    category,
                    tags,
                    isCompleted
                )
                tasks.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return tasks
    }

    // 更新任务
    fun updateTask(task: Task): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_CONTENT, task.content)
            put(COLUMN_ATTACHMENT, task.attachment)
            put(COLUMN_DUE_DATE, task.dueDate)
            put(COLUMN_IMPORTANCE, task.importance)
            put(COLUMN_CATEGORY, task.category)
            put(COLUMN_TAGS, task.tags.joinToString(","))
            put(COLUMN_IS_COMPLETED, if (task.isCompleted) 1 else 0)
        }
        return db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(task.id.toString()))
    }

    fun updateTasksDueDate(tasks: List<Task>) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 8)  // 设置小时为 0
        calendar.set(Calendar.MINUTE, 0)       // 设置分钟为 0
        calendar.set(Calendar.SECOND, 0)       // 设置秒为 0
        calendar.set(Calendar.MILLISECOND, 0)  // 设置毫秒为 0
        val todayDate = calendar.timeInMillis
        val db = this.writableDatabase
        tasks.forEach { task ->
            val values = ContentValues().apply {
                put(COLUMN_DUE_DATE, todayDate)
            }
            db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(task.id.toString()))
        }
        db.close()
    }

    fun markTasksAsCompleted(tasks: List<Task>) {
        val db = this.writableDatabase
        val sql = "UPDATE $TABLE_NAME SET $COLUMN_IS_COMPLETED = 1 WHERE $COLUMN_ID IN (${tasks.map { "?" }.joinToString(",")})"

        db.execSQL(sql, tasks.map { it.id }.toTypedArray())
        db.close()
    }

    // 删除任务
    fun deleteTask(taskId: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(taskId.toString()))
    }

}
