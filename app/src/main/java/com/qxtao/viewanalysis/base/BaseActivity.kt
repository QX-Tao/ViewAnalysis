@file:Suppress("MemberVisibilityCanBePrivate")

package com.qxtao.viewanalysis.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding


abstract class BaseActivity<VB : ViewBinding>(open val block:(LayoutInflater)->VB) : AppCompatActivity(), View.OnClickListener {

    protected val binding by lazy{ block(layoutInflater) }
    private lateinit var _context: Context
    protected val mContext get() = _context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        /**
         * Hide Activity title bar
         * 隐藏系统的标题栏
         */
        supportActionBar?.hide()
        _context = this
        /**
         * Init children
         * 装载子类
         */
        bindViews()
        initViews()
        initEvents()
        addListener()
        onCreate()
    }


    /**
     * Callback [onCreate] method
     *
     * 回调 [onCreate] 方法
     */
    abstract fun onCreate()

    /**
     * Callback [bindViews] method
     *
     * 回调 [bindViews] 方法
     */
    protected open fun bindViews(){}

    /**
     * Callback [initViews] method
     *
     * 回调 [initViews] 方法
     */
    protected abstract fun initViews()

    /**
     * Callback [initEvents] method
     *
     * 回调 [initEvents] 方法
     */
    protected open fun initEvents(){}

    /**
     * Callback [addListener] method
     *
     * 回调 [addListener] 方法
     */
    protected abstract fun addListener()

    /**
     * show short toast
     *
     * Toast通知
     */
    protected fun showShortToast(str: String) {
        Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show()
    }

    /**
     * show long toast
     *
     * Toast通知
     */
    protected fun showLongToast(str: String) {
        Toast.makeText(mContext, str, Toast.LENGTH_LONG).show()
    }

}
