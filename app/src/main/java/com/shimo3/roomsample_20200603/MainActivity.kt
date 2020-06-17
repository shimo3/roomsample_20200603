package com.shimo3.roomsample_20200603

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
        private const val HANDLER_FETCH_DATA = 1
        private const val HANDLER_REFRESH_DATA = 2
        private const val HANDLER_DELETE_DATA = 3
    }

    private lateinit var commentDao: CommentDao
    private lateinit var dataAdapter: SimpleAdapter
    private lateinit var data: ArrayList<Map<String, String>>

    private lateinit var mHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize handler
        initHandler()

        // get dao
        val app = application
        if (app is App) {
            commentDao = app.getDb().CommentDao()
        }

        // button
        btnSubmit.setOnClickListener {
            // save data
            append()

        }

        // list view
        initAdapter()

        // ListView
        lv.setOnItemLongClickListener { parent, view, position, id ->
            val ret = false

            val r = data.get(id.toInt())

            AlertDialog.Builder(this).apply {
                title = "確認"
                setMessage("ID:${r["id"]}のデータを削除しますか？")
                setPositiveButton("削除") { _, _ ->
                    Thread {
                        commentDao.deleteById(r["id"]?.toInt()!!)
                        mHandler.sendMessage(mHandler.obtainMessage(HANDLER_DELETE_DATA, r))
                    }.start()
                }
                setNegativeButton("キャンセル") { _, _ -> }
            }.show()

            ret
        }
    }

    private fun initHandler() {
        mHandler = object: Handler(mainLooper) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    HANDLER_FETCH_DATA -> {
                        if (msg.obj is List<*>) {
                            @Suppress("UNCHECKED_CAST")
                            val comments = msg.obj as List<Comment>
                            data = ArrayList<Map<String, String>>()
                            comments.forEach {
                                val r = HashMap<String, String>()
                                r["id"] = it.id.toString()
                                r["comment"] = it.comment

                                data.add(r)
                            }

//                            dataAdapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.simple_list_item_1, data.toList())
                            dataAdapter = SimpleAdapter(
                                            this@MainActivity, data,
                                            R.layout.my_simple_list_item_2,
                                            arrayOf("id", "comment"), intArrayOf(R.id.text1, R.id.text2))
                            lv.adapter = dataAdapter
                        }
                    }
                    HANDLER_REFRESH_DATA -> {
                        etComment.text.clear()

                        if (msg.obj is Comment) {
//                            val comment = msg.obj as String
//                            dataAdapter.insert(comment, 0)
                            val comment = msg.obj as Comment
                            val r = HashMap<String, String>()
                            r["id"] = comment.id.toString()
                            r["comment"] = comment.comment

                            data.add(0, r)
                            dataAdapter.notifyDataSetChanged()
                            Toast.makeText(this@MainActivity, "データの追加ができたよ", Toast.LENGTH_LONG).show()
                        }
                    }
                    HANDLER_DELETE_DATA -> {
                        if (msg.obj is HashMap<*, *>) {
                            @Suppress("UNCHECKED_CAST")
                            val r = msg.obj as HashMap<String, String>
                            data.remove(r)
                            dataAdapter.notifyDataSetChanged()
                            Toast.makeText(this@MainActivity, "データを削除したよ", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        Thread {
            val comments = commentDao.getAll()

            mHandler.sendMessage(mHandler.obtainMessage(HANDLER_FETCH_DATA, comments))
        }.start()
    }

    private fun append() {
        val comment: String = etComment.text.toString()
        Thread {
            commentDao.insert(Comment(0, comment))
            val commentObj = commentDao.getLastInserted()

            mHandler.sendMessage(mHandler.obtainMessage(HANDLER_REFRESH_DATA, commentObj))
        }.start()
    }

}