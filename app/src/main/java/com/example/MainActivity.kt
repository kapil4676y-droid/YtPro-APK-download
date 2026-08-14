package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.AppDatabase
import com.example.data.repository.YtProRepository
import com.example.ui.components.ArchitectureDeploymentGuideDialog
import com.example.ui.components.ChannelAvatar
import com.example.ui.components.CreateMediaBottomSheet
import com.example.ui.components.CreatePlaylistDialog
import com.example.ui.components.GoogleSignInDialog
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.PayoutRequestDialog
import com.example.ui.components.PlaylistSelectorDialog
import com.example.ui.components.ReportContentDialog
import com.example.ui.components.YtProLogo
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.ChannelScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.ShortsScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.screens.SubscriptionsScreen
import com.example.ui.screens.UploadScreen
import com.example.ui.screens.WatchScreen
import com.example.ui.theme.YtProIndigo
import com.example.ui.theme.YtProRose
import com.example.ui.theme.YTProTheme
import com.example.ui.viewmodel.YtProViewModel
import com.example.ui.viewmodel.YtProViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = YtProRepository(database)
        val viewModelFactory = YtProViewModelFactory(repository)
        setContent {
            YTProTheme {
                val viewModel: YtProViewModel = viewModel(factory = viewModelFactory)
                YtProApp(viewModel = viewModel)
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Shorts : Screen("shorts", "Shorts", Icons.Filled.Bolt, Icons.Outlined.Bolt)
    object Create : Screen("create", "Create", Icons.Filled.Add, Icons.Filled.Add)
    object Subscriptions : Screen("subscriptions", "Subscriptions", Icons.Filled.Subscriptions, Icons.Outlined.Subscriptions)
    object Library : Screen("library", "You", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)

    object Studio : Screen("studio", "Studio", Icons.Filled.VideoLibrary, Icons.Outlined.VideoLibrary)
    object Admin : Screen("admin", "Admin", Icons.Filled.VideoLibrary, Icons.Outlined.VideoLibrary)
    object Search : Screen("search", "Search", Icons.Filled.Search, Icons.Filled.Search)
    object Channel : Screen("channel/{channelId}", "Channel", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle) {
        fun createRoute(channelId: String) = "channel/$channelId"
    }
}

@Composable
fun YtProApp(viewModel: YtProViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val activeVideo by viewModel.activeVideo.collectAsState()
    val isMiniPlayer by viewModel.isMiniPlayer.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val reportDialogState by viewModel.reportDialogState.collectAsState()
    val showPlaylistSelector by viewModel.showPlaylistSelector.collectAsState()
    val showCreatePlaylistDialog by viewModel.showCreatePlaylistDialog.collectAsState()
    val showPayoutDialog by viewModel.showPayoutRequestDialog.collectAsState()
    val showDeploymentDocs by viewModel.showDeploymentDocs.collectAsState()
    val showGoogleSignInDialog by viewModel.showGoogleSignInDialog.collectAsState()
    val showCreateBottomSheet by viewModel.showCreateBottomSheet.collectAsState()

    val unreadNotifs = notifications.count { !it.isRead }

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Shorts,
        Screen.Create,
        Screen.Subscriptions,
        Screen.Library
    )

    val currentRoute = currentDestination?.route
    val isWatchOverlayVisible = activeVideo != null && !isMiniPlayer

    // Handle Android system Back button when watch player is full screen
    BackHandler(enabled = isWatchOverlayVisible) {
        viewModel.minimizeToMiniPlayer()
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                // Hide top app bar when in shorts full screen or watch overlay is active
                if (currentRoute != Screen.Shorts.route && !isWatchOverlayVisible) {
                    YtProTopAppBar(
                        unreadCount = unreadNotifs,
                        userAvatar = currentUser.avatarUrl,
                        onSearchClick = { navController.navigate(Screen.Search.route) },
                        onNotificationsClick = { navController.navigate(Screen.Subscriptions.route) },
                        onProfileClick = { navController.navigate(Screen.Library.route) }
                    )
                }
            },
            bottomBar = {
                if (!isWatchOverlayVisible) {
                    Column {
                        // Miniplayer floating bar directly above navigation bar
                        AnimatedVisibility(
                            visible = activeVideo != null && isMiniPlayer,
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                        ) {
                            activeVideo?.let { vid ->
                                MiniPlayerBar(
                                    video = vid,
                                    isPlaying = isPlaying,
                                    onPlayPause = { viewModel.togglePlayPause() },
                                    onClick = { viewModel.expandMiniPlayer() },
                                    onClose = { viewModel.closePlayer() }
                                )
                            }
                        }

                        NavigationBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("bottom_nav_bar"),
                            containerColor = Color(0xF20F0F0F),
                            tonalElevation = 0.dp
                        ) {
                            bottomNavItems.forEach { screen ->
                                val isSelected = currentDestination?.route == screen.route
                                val isCreateTab = screen == Screen.Create

                                if (isCreateTab) {
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Surface(
                                                modifier = Modifier.size(42.dp),
                                                shape = CircleShape,
                                                color = Color.Transparent
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(
                                                            Brush.linearGradient(
                                                                listOf(Color(0xFFA084E8), Color(0xFF6F1E51))
                                                            ),
                                                            CircleShape
                                                        )
                                                        .border(1.dp, Color(0x4DFFFFFF), CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Add,
                                                        contentDescription = "Upload Video",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                            }
                                        },
                                        label = null
                                    )
                                } else {
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                                contentDescription = screen.title
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = screen.title,
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color(0xFFA084E8),
                                            selectedTextColor = Color(0xFFA084E8),
                                            unselectedIconColor = Color(0xFFAAAAAA),
                                            unselectedTextColor = Color(0xFFAAAAAA),
                                            indicatorColor = Color(0x26A084E8)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToShorts = { navController.navigate(Screen.Shorts.route) },
                        onNavigateToChannel = { chId -> navController.navigate(Screen.Channel.createRoute(chId)) }
                    )
                }

                composable(Screen.Shorts.route) {
                    ShortsScreen(
                        viewModel = viewModel,
                        onNavigateToChannel = { chId -> navController.navigate(Screen.Channel.createRoute(chId)) }
                    )
                }

                composable(Screen.Create.route) {
                    UploadScreen(
                        viewModel = viewModel,
                        onUploadCompleted = { uploadedVideo ->
                            navController.navigate(Screen.Home.route)
                            viewModel.playVideo(uploadedVideo)
                        }
                    )
                }

                composable(Screen.Subscriptions.route) {
                    SubscriptionsScreen(
                        viewModel = viewModel,
                        onNavigateToChannel = { chId -> navController.navigate(Screen.Channel.createRoute(chId)) }
                    )
                }

                composable(Screen.Library.route) {
                    LibraryScreen(
                        viewModel = viewModel,
                        onNavigateToStudio = { navController.navigate(Screen.Studio.route) },
                        onNavigateToAdmin = { navController.navigate(Screen.Admin.route) },
                        onNavigateToChannel = { chId -> navController.navigate(Screen.Channel.createRoute(chId)) }
                    )
                }

                composable(Screen.Studio.route) {
                    StudioScreen(viewModel = viewModel)
                }

                composable(Screen.Admin.route) {
                    AdminScreen(viewModel = viewModel)
                }

                composable(Screen.Search.route) {
                    SearchScreen(
                        viewModel = viewModel,
                        onNavigateToChannel = { chId -> navController.navigate(Screen.Channel.createRoute(chId)) }
                    )
                }

                composable(
                    route = Screen.Channel.route,
                    arguments = listOf(navArgument("channelId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val channelId = backStackEntry.arguments?.getString("channelId") ?: ""
                    ChannelScreen(
                        channelId = channelId,
                        viewModel = viewModel,
                        onNavigateToShorts = { navController.navigate(Screen.Shorts.route) }
                    )
                }
            }
        }

        // Full Screen Video Player Overlay
        AnimatedVisibility(
            visible = isWatchOverlayVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            activeVideo?.let { currentVid ->
                WatchScreen(
                    video = currentVid,
                    viewModel = viewModel,
                    onNavigateToChannel = { chId ->
                        viewModel.minimizeToMiniPlayer()
                        navController.navigate(Screen.Channel.createRoute(chId))
                    }
                )
            }
        }

        // Global Dialog Hosts
        if (reportDialogState != null) {
            val rep = reportDialogState!!
            ReportContentDialog(
                targetType = rep.targetType,
                targetId = rep.targetId,
                targetTitle = rep.targetTitle,
                onDismiss = { viewModel.closeReportDialog() },
                onSubmitReport = { reason, details ->
                    viewModel.submitReport(reason, details)
                }
            )
        }

        if (showPlaylistSelector != null) {
            val vidId = showPlaylistSelector!!
            PlaylistSelectorDialog(
                videoId = vidId,
                viewModel = viewModel,
                onDismiss = { viewModel.showPlaylistSelector.value = null }
            )
        }

        if (showCreatePlaylistDialog) {
            CreatePlaylistDialog(
                onDismiss = { viewModel.showCreatePlaylistDialog.value = false },
                onCreate = { title, isPrivate ->
                    viewModel.createPlaylist(title, isPrivate = isPrivate)
                    viewModel.showCreatePlaylistDialog.value = false
                }
            )
        }

        if (showPayoutDialog) {
            PayoutRequestDialog(
                onDismiss = { viewModel.showPayoutRequestDialog.value = false },
                onSubmit = { amount, method, dest ->
                    viewModel.requestPayout(amount, method, dest)
                    viewModel.showPayoutRequestDialog.value = false
                }
            )
        }

        if (showDeploymentDocs) {
            ArchitectureDeploymentGuideDialog(
                onDismiss = { viewModel.showDeploymentDocs.value = false }
            )
        }

        if (showGoogleSignInDialog) {
            GoogleSignInDialog(
                currentUser = currentUser,
                onDismiss = { viewModel.showGoogleSignInDialog.value = false },
                onSignIn = { email, name, avatar ->
                    viewModel.loginWithGoogle(email, name, avatar)
                },
                onSwitchAccount = { user ->
                    viewModel.switchAccount(user)
                }
            )
        }

        if (showCreateBottomSheet) {
            CreateMediaBottomSheet(
                onDismiss = { viewModel.showCreateBottomSheet.value = false },
                onNavigateToUpload = { navController.navigate(Screen.Create.route) },
                onNavigateToShorts = { navController.navigate(Screen.Shorts.route) }
            )
        }
    }
}

@Composable
fun YtProTopAppBar(
    unreadCount: Int,
    userAvatar: String,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = Color(0xF20F0F0F),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x14FFFFFF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            YtProLogo()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier.testTag("top_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFFF1F1F1),
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier.testTag("top_notifications_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadCount > 0) {
                                Badge(
                                    containerColor = Color(0xFFA084E8),
                                    contentColor = Color.White
                                ) { Text("$unreadCount") }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = Color(0xFFF1F1F1),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .padding(start = 2.dp)
                        .clickable { onProfileClick() }
                        .testTag("top_profile_avatar")
                ) {
                    ChannelAvatar(avatarUrl = userAvatar, size = 34.dp)
                }
            }
        }
    }
}
