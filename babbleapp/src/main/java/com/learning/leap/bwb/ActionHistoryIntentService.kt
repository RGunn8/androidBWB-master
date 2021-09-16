package com.learning.leap.bwb

import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.learning.leap.bwb.ActionHistoryIntentService
import com.learning.leap.bwb.room.BabbleDatabase
import com.learning.leap.bwb.utility.Utility
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * Created by ryanlgunn8 on 5/14/17.
 */
class ActionHistoryIntentService : IntentService("actionHistoryIntentService") {
    private val disposables = CompositeDisposable()
    var context: Context? = null
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    override fun onHandleIntent(intent: Intent?) {
        val disposable = Completable.fromAction {
            val amazonDynamoDBClient = AmazonDynamoDBClient(Utility.getCredientail(context))
            val mapper = DynamoDBMapper(amazonDynamoDBClient)
            val actionHistory = BabbleDatabase.getInstance().actionHistory().getAllActionHistory()
            actionHistory.forEach {
                mapper.save(it)
            }
            BabbleDatabase.getInstance().actionHistory().deleteAllActionHistory()

        }.subscribe({

        }, {
            it.printStackTrace()
        })
        disposables.add(disposable);
    }

    companion object {
        @JvmStatic
        fun startActionHistoryIntent(context: Context) {
            val intent = Intent(context, ActionHistoryIntentService::class.java)
            context.startService(intent)
        }
    }
}