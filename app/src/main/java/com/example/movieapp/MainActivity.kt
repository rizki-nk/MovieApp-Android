package com.example.movieapp


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.movieapp.data.local.AppDatabase
import com.example.movieapp.data.local.UserSessionManager
import com.example.movieapp.data.repository.FavoriteRepository
import com.example.movieapp.data.repository.HomeExtrasRepositoryImpl
import com.example.movieapp.domain.repository.MovieRepository
import com.example.movieapp.data.repository.ReviewRepository
import com.example.movieapp.data.repository.UserRepository
import com.example.movieapp.domain.usecase.GetMovieByIdUseCase
import com.example.movieapp.domain.usecase.GetMovieNewsUseCase
import com.example.movieapp.domain.usecase.GetMovieTriviaUseCase
import com.example.movieapp.domain.usecase.GetPopularMoviesUseCase
import com.example.movieapp.ui.auth.AuthViewModel
import com.example.movieapp.ui.auth.AuthViewModelFactory
import com.example.movieapp.ui.detail.DetailViewModel
import com.example.movieapp.ui.detail.DetailViewModelFactory
import com.example.movieapp.ui.home.HomeViewModel
import com.example.movieapp.ui.library.LibraryViewModel
import com.example.movieapp.ui.library.LibraryViewModelFactory
import com.example.movieapp.ui.navigation.MovieNavGraph
import com.example.movieapp.ui.theme.MovieAppTheme
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        //1.Initialisasi Database & Repository
        val database = AppDatabase.getDatabase(this)
        val userRepository = UserRepository(database.userDao())
        val sessionManager = UserSessionManager(this)
        val favoriteRepository = FavoriteRepository(database.favoriteDao())
        val reviewRepository = ReviewRepository(database.reviewDao())

        // 2. Inisialisasi UseCases
        val movieRepository: MovieRepository = get()
        val getMovieByIdUseCase = GetMovieByIdUseCase(movieRepository)


        setContent {
            MovieAppTheme {
                val detailViewModel: DetailViewModel = viewModel(
                    factory = DetailViewModelFactory(getMovieByIdUseCase, favoriteRepository, reviewRepository)
                )

                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModelFactory(userRepository, sessionManager)
                )

                val libraryViewModel: LibraryViewModel = viewModel(
                    factory = LibraryViewModelFactory(favoriteRepository, reviewRepository)
                )

                MovieNavGraph(
                    homeViewModel = homeViewModel,
                    detailViewModel = detailViewModel,
                    authViewModel = authViewModel,
                    libraryViewModel = libraryViewModel
                )
            }
        }
    }
}