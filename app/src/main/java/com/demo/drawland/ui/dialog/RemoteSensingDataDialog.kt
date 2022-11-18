package com.demo.drawland.ui.dialog

import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.demo.base.BaseDialog
import com.demo.drawland.R
import com.demo.drawland.app.AppAdapter
import com.demo.drawland.ui.adapter.SelectAdapter

/**
 * @author: hujw
 * @time: 2022/11/17
 * @desc:
 */
class RemoteSensingDataDialog {

    class Builder(context: Context) : BaseDialog.Builder<Builder>(context) {

        private val recyclerView: RecyclerView? by lazy { findViewById(R.id.mRecyclerView) }
        private val adapter: RemoteSensingAdapter

        init {
            setContentView(R.layout.remote_sensing_data_dialog)
            adapter = RemoteSensingAdapter(getContext())
            recyclerView?.adapter = adapter
        }

        fun setList(data: MutableList<String>): Builder = apply {
            adapter.setData(data)
        }
    }

    class RemoteSensingAdapter(context: Context) : AppAdapter<String>(context) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
            return ViewHolder()
        }

        inner class ViewHolder : AppViewHolder(R.layout.remote_sensing_item) {
            private val titleView: AppCompatTextView? by lazy { findViewById(R.id.tv_title) }
            private val recyclerView: RecyclerView? by lazy { findViewById(R.id.mRecyclerView) }

            private val adapter: SelectAdapter = SelectAdapter(getContext())

            override fun onBindView(position: Int) {
                val mData=mutableListOf<Any>()
                mData.add("七月")
                mData.add("八月")
                adapter.setData(mData)
                titleView?.text = getItem(position)
                recyclerView?.adapter = adapter

            }


        }
    }
}