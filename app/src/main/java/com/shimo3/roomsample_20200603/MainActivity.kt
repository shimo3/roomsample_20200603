package com.shimo3.roomsample_20200603

import android.content.AbstractThreadedSyncAdapter
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var commentDao: CommentDao
    private lateinit var dataAdapter: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get dao
        val app = application
        if (app is App) {
            commentDao = app.getDb().CommentDao()
        }

        // button
        btnSubmit.setOnClickListener {
                // save data
                SaveTask().execute()

            // clear input
//            etComment.setText("")
        }

        // list view
        resetAdapter()
    }

    private fun resetAdapter() {
        // fetch data
        FetchAsyncTask().execute()
//        dataAdapter = ArrayAdapter<>(this, android.R.layout.simple_list_item_1, comments)
    }

    inner class FetchAsyncTask: AsyncTask<Void, Int, Void>() {
        private lateinit var comments: List<Comment>

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: Void?) {
            val array = ArrayList<String>()

            comments.forEach {
                array.add(it.comment)
            }

            dataAdapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.simple_list_item_1, array.toList())
            lv.adapter = dataAdapter
        }

        override fun doInBackground(vararg params: Void?): Void? {
            comments = commentDao.getAll()

            return null
        }

        override fun onCancelled(result: Void?) {
            super.onCancelled(result)
        }

        override fun onCancelled() {
            super.onCancelled()
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }
    }

    inner class SaveTask: AsyncTask<Void, Int, Void>() {
        private lateinit var comment: String

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: Void?) {
            etComment.setText("")

            resetAdapter()
        }

        override fun doInBackground(vararg params: Void?): Void? {
            commentDao.insert(Comment(id = 0, comment = comment))

            return null
        }

        override fun onCancelled(result: Void?) {
            super.onCancelled(result)
        }

        override fun onCancelled() {
            super.onCancelled()
        }

        override fun onPreExecute() {
            comment = etComment.text.toString()
        }
    }
}