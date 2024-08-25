package com.example.transpose.di


import com.example.transpose.data.repository.local_file.LocalFileRepository
import com.example.transpose.data.repository.local_file.LocalFileRepositoryImpl
import com.example.transpose.data.repository.newpipe.NewPipeRepository
import com.example.transpose.data.repository.newpipe.NewPipeRepositoryImpl
import com.example.transpose.data.repository.suggestion_keyword.SuggestionKeywordRepository
import com.example.transpose.data.repository.suggestion_keyword.SuggestionKeywordRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindSuggestionKeywordRepository(
        suggestionKeywordRepositoryImpl: SuggestionKeywordRepositoryImpl
    ): SuggestionKeywordRepository

    @Binds
    @Singleton
    abstract fun bindNewPipeRepository(
        newPipeRepositoryImpl: NewPipeRepositoryImpl
    ): NewPipeRepository

    @Binds
    @Singleton
    abstract fun bindLocalFileRepository(
        localFileRepositoryImpl: LocalFileRepositoryImpl
    ): LocalFileRepository
}