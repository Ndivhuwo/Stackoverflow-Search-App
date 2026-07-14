package za.co.ndivhuwo.stackoverflow_search_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TagChip(tag: String) {
    Box(
        modifier = Modifier
            .background(
                color = Color(0xFFE1ECF4), // Stack Overflow light blue tag background
                shape = RoundedCornerShape(3.dp)
            )
            .padding(horizontal = 6.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = tag.lowercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF39739D), // Stack Overflow dark blue tag text
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp
        )
    }
}
