package com.demo.drawland.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hjq.toast.ToastUtils
import com.demo.base.BaseAdapter
import com.demo.drawland.R
import com.demo.drawland.app.AppAdapter

/**
 * @author: hujw
 * @time: 2022/11/17
 * @desc:
 */
 class SelectAdapter(context: Context) : AppAdapter<Any>(context), BaseAdapter.OnItemClickListener {

    /** 最小选择数量 */
    private var minSelect = 1

    /** 最大选择数量 */
    private var maxSelect = Int.MAX_VALUE

    /** 选择对象集合 */
    private val selectSet: HashMap<Int, Any> = HashMap()

    init {
        setOnItemClickListener(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder()
    }

    fun setSelect(vararg positions: Int) {
        for (position in positions) {
            selectSet[position] = getItem(position)
        }
        notifyDataSetChanged()
    }

    fun setMaxSelect(count: Int) {
        maxSelect = count
    }

    fun setMinSelect(count: Int) {
        minSelect = count
    }

    fun getMinSelect(): Int {
        return minSelect
    }

    fun setSingleSelect() {
        setMaxSelect(1)
        setMinSelect(1)
    }

    fun isSingleSelect(): Boolean {
        return maxSelect == 1 && minSelect == 1
    }

    fun getSelectSet(): HashMap<Int, Any> {
        return selectSet
    }

    /**
     * [BaseAdapter.OnItemClickListener]
     */
    override fun onItemClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
        if (selectSet.containsKey(position)) {
            // 当前必须不是单选模式才能取消选中
            if (!isSingleSelect()) {
                selectSet.remove(position)
                notifyItemChanged(position)
            }
        } else {
            if (maxSelect == 1) {
                selectSet.clear()
                notifyDataSetChanged()
            }
            if (selectSet.size < maxSelect) {
                selectSet[position] = getItem(position)
                notifyItemChanged(position)
            } else {
                ToastUtils.show(String.format(getString(R.string.select_max_hint)!!, maxSelect))
            }
        }
    }

    inner class ViewHolder : AppViewHolder(R.layout.select_item) {

        private val textView: TextView? by lazy { findViewById(R.id.tv_select_text) }
        private val checkBox: CheckBox? by lazy { findViewById(R.id.tv_select_checkbox) }

        override fun onBindView(position: Int) {
            textView?.text = getItem(position).toString()
            checkBox?.isChecked = selectSet.containsKey(position)
            if (maxSelect == 1) {
                checkBox?.isClickable = false
            } else {
                checkBox?.isEnabled = false
            }
        }
    }
}
