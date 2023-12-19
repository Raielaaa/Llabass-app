package com.example.lab_ass_app.di

import android.content.Context
import com.example.lab_ass_app.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    @Named("FirebaseAuth.Instance")
    fun providesFirebaseAuthInstance() = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    @Named("FirebaseFireStore.Instance")
    fun providesFirebaseFireStoreInstance() = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    @Named("FirebaseStorage.Instance")
    fun providesFirebaseStorageInstance() = FirebaseStorage.getInstance().reference

    @Singleton
    @Provides
    @Named("GoogleSignInClient.Instance")
    fun providesGoogleSignInClient(
        @ApplicationContext
        appContext: Context
    ) = GoogleSignIn.getClient(appContext, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(Constants.CLIENT_ID_TOKEN)
        .requestEmail()
        .build()
    )
}