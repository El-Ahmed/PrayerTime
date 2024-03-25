package nonetheless.prayertime.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nonetheless.prayertime.model.Prayer
import java.text.SimpleDateFormat


private val formatter = SimpleDateFormat("HH:mm")

@Composable
fun PrayersList(
    prayers: List<Prayer>, selectedPrayerIndex: Int, onSelectedPrayer: (Int) -> Unit
) {

    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            prayers.forEachIndexed { index, prayer ->
                ListItem(modifier = Modifier.selectable(selected = selectedPrayerIndex == index,
                    onClick = {
                        onSelectedPrayer(index)
                    }),
                    colors = if (selectedPrayerIndex == index) {
                        ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                    } else {
                        ListItemDefaults.colors()
                    },
                    headlineContent = {
                        Text(
                            prayer.name.name, fontSize = 16.sp, fontWeight = FontWeight.Medium
                        )
                    },
                    leadingContent = {
                        Image(
                            painter = painterResource(id = prayer.name.painter),
                            contentDescription = stringResource(id = prayer.name.painterDescription),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(24.dp))
                        )
                    },
                    trailingContent = {
                        Text(text = formatter.format(prayer.time.time), fontSize = 14.sp)
                    })
                if (index < prayers.lastIndex) {
                    Divider()
                }
            }
        }
    }

}
