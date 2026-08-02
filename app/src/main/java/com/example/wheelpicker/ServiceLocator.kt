package com.example.wheelpicker

import android.content.Context
import com.example.wheelpicker.data.OptionRepository

object ServiceLocator {

    @Volatile
    private var repositoryInstance: OptionRepository? = null

    fun repository(context: Context): OptionRepository =
        repositoryInstance ?: synchronized(this) {
            repositoryInstance ?: OptionRepository(context.applicationContext)
                .also { repositoryInstance = it }
        }
}
