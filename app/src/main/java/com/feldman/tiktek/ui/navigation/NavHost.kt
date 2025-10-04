package com.feldman.tiktek.ui.navigation


import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.feldman.tiktek.ui.screens.BooksScreen
import com.feldman.tiktek.ui.screens.BookScreen
import com.feldman.tiktek.ui.screens.SolutionScreen


object Routes {
    const val BOOKS = "books"
    const val BOOK_DETAIL = "bookDetail/{bookId}/{bookTitle}"
    const val SOLUTION_VIEWER = "solutionViewer/{imageUrl}"
}


@Composable
fun NavHost() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.BOOKS) {

        composable(Routes.BOOKS) {
            BooksScreen(
                onOpenBook = { id, title ->
                    val safeTitle = java.net.URLEncoder.encode(title, Charsets.UTF_8.name())
                    nav.navigate("bookDetail/$id/$safeTitle")
                },
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.systemBars)
            )
        }

        composable(
            route = Routes.BOOK_DETAIL,
            arguments = listOf(
                navArgument("bookId") { type = NavType.StringType },
                navArgument("bookTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            val titleEnc = backStackEntry.arguments?.getString("bookTitle") ?: ""
            val title = java.net.URLDecoder.decode(titleEnc, Charsets.UTF_8.name())
            BookScreen(
                bookId = bookId,
                title = title,
                onBack = {
                    if (nav.previousBackStackEntry != null) {
                        nav.popBackStack()
                    }
                },
                onOpenSolution = { url ->
                    val encoded = java.net.URLEncoder.encode(url, Charsets.UTF_8.name())
                    nav.navigate("solutionViewer/$encoded")
                },
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.systemBars)
            )
        }

        composable(
            route = Routes.SOLUTION_VIEWER,
            arguments = listOf(
                navArgument("imageUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encoded = backStackEntry.arguments?.getString("imageUrl") ?: ""
            val decodedUrl = java.net.URLDecoder.decode(encoded, Charsets.UTF_8.name())

            SolutionScreen(
                imageUrl = decodedUrl,
                onBack = { nav.navigateUp() },
                modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)
            )
        }

    }

}