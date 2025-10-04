package com.feldman.tiktek


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import com.feldman.tiktek.ui.navigation.TiktekNavHost
import com.feldman.tiktek.ui.theme.AppTheme
import androidx.compose.runtime.staticCompositionLocalOf
import com.feldman.tiktek.data.repo.TiktekRepository


val LocalRepository = staticCompositionLocalOf<TiktekRepository> { error("Repo not provided") }


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as TiktekApp
        setContent {
            AppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    CompositionLocalProvider(LocalRepository provides app.repository) {
                        TiktekNavHost()
                    }
                }
            }
        }
    }
}