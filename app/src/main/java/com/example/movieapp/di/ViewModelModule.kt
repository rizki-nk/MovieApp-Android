package com.example.movieapp.di

import com.example.movieapp.domain.usecase.GetMovieByIdUseCase
import com.example.movieapp.domain.usecase.GetMovieNewsUseCase
import com.example.movieapp.domain.usecase.GetMovieTriviaUseCase
import com.example.movieapp.domain.usecase.GetPopularMoviesUseCase
import com.example.movieapp.domain.usecase.SearchMovieUseCase
import com.example.movieapp.ui.detail.DetailViewModel
import com.example.movieapp.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    factory { GetPopularMoviesUseCase(get()) }
    factory { GetMovieNewsUseCase(get()) }
    factory { GetMovieTriviaUseCase(get()) }
    factory { SearchMovieUseCase(get()) }
    factory { GetMovieByIdUseCase(get()) }

    // Daftarkan HomeViewModel yang akan memakai data dari Repository/Use Case
    viewModel {
        HomeViewModel(
            getPopularMoviesUseCase = get(),
            getMovieNewsUseCase = get(),
            getMovieTriviaUseCase = get(),
            searchMovieUseCase = get()
        )
    }

    // Daftarkan DetailViewModel
    viewModel {
        DetailViewModel(
            getMovieByIdUseCase = get(),
            favoriteRepository = get(),
            reviewRepository = get()
        )
    }
}