package com.mkrlabs.pmisstudent.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mkrlabs.pmisstudent.PMISApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun providesFirebaseAuth (): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    fun providesMainApplicationInstance(@ApplicationContext context: Context): PMISApplication {
        return context as PMISApplication
    }



    @Singleton
    @Provides
    fun providesFirebaseFirestore() : FirebaseFirestore {
        return  FirebaseFirestore.getInstance()
    }





}