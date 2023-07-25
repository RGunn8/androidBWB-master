package com.learning.leap.bwb.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.migrations.SharedPreferencesMigration
import androidx.datastore.migrations.SharedPreferencesView
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.room.Room
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.auth.CognitoCredentialsProvider
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.learning.leap.bwb.helper.LocalLoadSaveHelper
import com.learning.leap.bwb.room.BabbleDatabase
import com.learning.leap.bwb.utility.Constant
import com.learning.leap.bwb.utility.DynamoDBSingleton
import com.learning.leap.bwb.utility.Utility
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLocalLoadHelper(@ApplicationContext context: Context): LocalLoadSaveHelper =
        LocalLoadSaveHelper(context)

    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("Global", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): BabbleDatabase =
        Room.databaseBuilder(
            context,
            BabbleDatabase::class.java,
            "babbleDB"
        ).build()

    @Provides
    @Singleton
    fun provideCredentials(@ApplicationContext context: Context): CognitoCredentialsProvider =
        CognitoCachingCredentialsProvider(
            context,  /* get the context for the application */
            Constant.CognitoIdentityPoolId,  /* Identity Pool ID */
            Regions.US_EAST_1 /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        )

    @Provides
    @Singleton
    fun provideTransferUtility(
        credentialsProvider: CognitoCredentialsProvider,
        @ApplicationContext context: Context
    ): TransferUtility {
        val mAmazonS3: AmazonS3 = AmazonS3Client(credentialsProvider)
        return TransferUtility(mAmazonS3, context)
    }

    @Provides
    @Singleton
    fun provideAmazonMapper(credentialsProvider: CognitoCredentialsProvider): DynamoDBMapper {
        val amazonDynamoDBClient =
            AmazonDynamoDBClient(credentialsProvider)
        return DynamoDBMapper.builder().dynamoDBClient(amazonDynamoDBClient).build()

    }

    @Provides
    @Singleton
    fun provideFileDir(@ApplicationContext context: Context): File {
        return context.filesDir
    }

    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration(appContext.applicationContext,"Global")),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { appContext.applicationContext.dataStoreFile("DataStore")}
        )

    }
}