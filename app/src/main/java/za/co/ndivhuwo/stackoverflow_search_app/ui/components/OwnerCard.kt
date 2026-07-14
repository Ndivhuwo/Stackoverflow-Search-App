package za.co.ndivhuwo.stackoverflow_search_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import za.co.ndivhuwo.stackoverflow_search_app.data.models.Owner
import java.text.NumberFormat
import java.util.Locale

@Composable
fun OwnerCard(owner: Owner,) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Avatar Circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    color = Color(0xFF0074CC),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (!owner.profileImage.isNullOrEmpty()) {
                AsyncImage(
                    model = owner.profileImage,
                    contentDescription = "Profile image of ${owner.displayName}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            } else {
                Text(
                    text = owner.displayName.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Owner Info Column
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = owner.displayName,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF0074CC),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = NumberFormat.getNumberInstance(Locale.getDefault())
                    .format(owner.reputation),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}