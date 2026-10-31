package com.example.movieapp.di

import com.example.movieapp.BuildConfig
import com.example.movieapp.data.remote.news.NewsApiService
import com.example.movieapp.data.remote.tmdb.ApiService
import com.example.movieapp.data.remote.trivia.TriviaApiService
import com.example.movieapp.data.repository.HomeExtrasRepositoryImpl
import com.example.movieapp.data.repository.MovieRepositoryImpl
import com.example.movieapp.data.repository.NewsRepositoryImpl
import com.example.movieapp.data.repository.TriviaRepositoryImpl
import com.example.movieapp.domain.repository.HomeExtrasRepository
import com.example.movieapp.domain.repository.MovieRepository
import com.example.movieapp.domain.repository.NewsRepository
import com.example.movieapp.domain.repository.TriviaRepository
import com.example.movieapp.ui.news.NewsViewModel
import com.example.movieapp.ui.trivia.TriviaViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import org.koin.androidx.viewmodel.dsl.viewModel
import retrofit2.create

val appModule = module {
    // 1. Logging Interceptor untuk memantau request/response API di Logcat
    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    // 2. OkHttpClient Client
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // 3. Retrofit Instance
    single {
        Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // 4. MovieRepository dengan menyuplai API Key secara aman dari BuildConfig
    single<MovieRepository> {
        MovieRepositoryImpl(
            apiService = get(),
            apiKey = BuildConfig.TMDB_API_KEY
        )
    }

    single<HomeExtrasRepository> {
        HomeExtrasRepositoryImpl()
    }


    // 5. [BARU] Konfigurasi untuk NewsAPI
    // Retrofit Instance khusus NewsAPI dengan Base URL yang berbeda

    single(named("NewsRetrofit")) {
        Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 6. [BARU] NewsApiService yang dibuat menggunakan NewsRetrofit
    single<NewsApiService> {
        get<Retrofit>(named("NewsRetrofit")).create(NewsApiService::class.java)
    }

    // 7. [BARU] NewsRepository diikat ke NewsRepositoryImpl
    single<NewsRepository> {
        NewsRepositoryImpl(
            apiService = get()
        )
    }

    //Pendaftaran NewsViewModel ke Koin
    viewModel {
        NewsViewModel(newsRepository = get())
    }

    // 1. Retrofit Instance khusus OpenTDB
    single(named("TriviaRetrofit")) {
        Retrofit.Builder()
            .baseUrl("https://opentdb.com/")
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 2. TriviaApiService menggunakan TriviaRetrofit
    single<TriviaApiService> {
        get<Retrofit>(named("TriviaRetrofit")).create(TriviaApiService::class.java)
    }

    // 3. TriviaRepository diikat ke TriviaRepositoryImpl (dengan menyuplai TriviaApiService)
    single<TriviaRepository> {
        TriviaRepositoryImpl(triviaApiService = get())
    }

    // 4. Pendaftaran TriviaViewModel
    viewModel{
        TriviaViewModel(triviaRepository = get())
    }



}