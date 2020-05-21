package com.wjx.android.smalltools.module.currentactivity

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.interfaces.OnInvokeView
import com.wjx.android.smalltools.R
import com.wjx.android.smalltools.adapter.HistoryAdapter
import com.wjx.android.smalltools.bean.HistoryData
import com.wjx.android.smalltools.module.App
import com.wjx.android.smalltools.module.activity.MainActivity

/**
 * Created with Android Studio.
 * Description:
 * @author: Wangjianxian
 * @CreateDate: 2020/5/15 13:25
 */
object FloatWindowManager {

    private var hasInit = false
    private var windowRecycleView: RecyclerView? = null
    private val windowAdapter by lazy { HistoryAdapter() }

    fun hasInit() = hasInit

    fun init(context: Activity) {
        hasInit = true
        EasyFloat.with(context)
            .setShowPattern(ShowPattern.ALL_TIME)
            .setSidePattern(SidePattern.DEFAULT)
            .setGravity(Gravity.TOP)
            // 启动前台Service
            .startForeground(true, myNotification(context))
            .setLayout(R.layout.window_history, OnInvokeView { view ->
                windowRecycleView = view.findViewById(R.id.windowRecycleView)
                windowRecycleView?.let {
                    it.layoutManager = LinearLayoutManager(context)
                    it.adapter = windowAdapter
                }
            }).show()

    }

    /**
     * 自定义的通知栏消息，可根据业务需要进行配置
     */
    private fun myNotification(context: Activity): Notification = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
            // 创建消息渠道
            val channel =
                NotificationChannel("SmallTools", "系统悬浮窗", NotificationManager.IMPORTANCE_MIN)
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            Notification.Builder(context, "SmallTools")
                .setCategory(Notification.CATEGORY_SERVICE)
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ->
            Notification.Builder(context)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setPriority(Notification.PRIORITY_MIN)

        else -> Notification.Builder(context)
    }
        .setAutoCancel(true)
        .setOngoing(true)
        .setContentTitle("Box 正在运行")
//        .setContentText("浮窗从未如此简单……")
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentIntent(
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        .build()


    fun addItem(historyBean: HistoryData) {
        windowAdapter.addData(0, historyBean)
        windowRecycleView?.scrollToPosition(0)
//        windowAdapter.notifyDataSetChanged()
    }

    fun show() {
        EasyFloat.showAppFloat(App.CONTEXT)
    }

    fun hide() {
        EasyFloat.hideAppFloat(App.CONTEXT)
    }

    fun clearView() {
        windowAdapter.data.clear()
        windowAdapter.notifyDataSetChanged()
    }
}