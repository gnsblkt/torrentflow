package app.torrentflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TorrentFlowTheme { TorrentFlowApp() } }
    }
}

@Composable
private fun TorrentFlowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF80CBC4),
            secondary = Color(0xFF90CAF9),
            background = Color(0xFF0B0F14),
            surface = Color(0xFF151B23)
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TorrentFlowApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Library", "Transfers", "About")
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("TorrentFlow", fontWeight = FontWeight.Bold)
                        Text("Android companion", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.LibraryBooks, contentDescription = null) },
                    label = { Text("Library") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.CloudDownload, contentDescription = null) },
                    label = { Text("Transfers") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text("About") }
                )
            }
        }
    ) { padding ->
        when (selectedTab) {
            0 -> LibraryScreen(Modifier.padding(padding))
            1 -> EmptyScreen(
                title = "No active transfers",
                detail = "Transfers will appear here when the verified catalogue and native transfer engine are available.",
                modifier = Modifier.padding(padding)
            )
            else -> AboutScreen(Modifier.padding(padding))
        }
    }
}

@Composable
private fun LibraryScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            Text("Your library", style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold)
            Text("Discover content with clear licensing and source information.",
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Default.LibraryBooks, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary)
                    Text("Verified catalogue is being prepared",
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        "The catalogue will list public-domain, openly licensed, or otherwise authorized content. Each item will show its publisher, source and licence before any transfer can begin.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        item {
            Text("Nothing here yet", style = MaterialTheme.typography.titleSmall)
            Text("No catalogue publisher has been approved for this build. This is intentional: unverified sources are not accepted.",
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun EmptyScreen(title: String, detail: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Default.CloudDownload, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary)
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(detail, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AboutScreen(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("About TorrentFlow", style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold)
        Text("A companion app for discovering and managing authorized open-content transfers.")
        Text("Version 0.1.0", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("This preview does not yet download torrents. Native transfer support will be enabled only after the catalogue verification and licensing checks are complete.",
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
