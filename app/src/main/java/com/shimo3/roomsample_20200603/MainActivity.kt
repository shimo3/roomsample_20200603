package com.shimo3.roomsample_20200603

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
        private const val HANDLER_FETCH_DATA = 1
        private const val HANDLER_REFRESH_DATA = 2
    }

    private lateinit var commentDao: CommentDao
    private lateinit var data: ArrayList<String>
    private lateinit var dataAdapter: ArrayAdapter<String>

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
    }

    private fun initHandler() {
        mHandler = object: Handler(mainLooper) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    HANDLER_FETCH_DATA -> {
                        if (msg.obj is List<*>) {
                            @Suppress("UNCHECKED_CAST")
                            val comments = msg.obj as List<Comment>
                            data = ArrayList<String>()
                            comments.forEach {
                                data.add(it.comment)
                            }

                            dataAdapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.simple_list_item_1, data.toList())
                            lv.adapter = dataAdapter
                        }
                    }
                    HANDLER_REFRESH_DATA -> {
                        etComment.text.clear()

                        if (msg.obj is String) {
                            val comment = msg.obj as String
                            dataAdapter.insert(comment, 0)
                            dataAdapter.notifyDataSetChanged()
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

            mHandler.sendMessage(mHandler.obtainMessage(HANDLER_REFRESH_DATA, comment))
        }.start()
    }

}