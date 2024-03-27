package nonetheless.prayertime.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import java.util.Calendar
import java.util.concurrent.TimeUnit


private val formatter = SimpleDateFormat("HH:mm")

@Composable
fun PrayerPreview(prayer: Prayer?) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .height(170.dp)
            .fillMaxWidth()
    ) {
        if (prayer != null) {

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            )
            {
                Column {
                    Text(text = prayer.name.name, fontSize = 24.sp, fontWeight = FontWeight.Medium)
                    Text(text = timeDistance(prayer.time), fontSize = 12.sp)
                    Text(
                        text = formatter.format(prayer.time.time),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Image(
                    painter = painterResource(id = prayer.name.painter),
                    contentDescription = stringResource(id = prayer.name.painterDescription),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(128.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
    }
}

fun timeDistance(time: Calendar): String {
    val now = Calendar.getInstance()
    val difference = time.timeInMillis - now.timeInMillis
    val minutes = TimeUnit.MILLISECONDS.toMinutes(difference).toInt()
    if (minutes == 0) return "Now"
    if (minutes > 0) {
        if (minutes / 60 == 0) return "in ${minutes % 60} min"
        if (minutes % 60 == 0) return "in ${minutes / 60} hours"
        return "in ${minutes / 60} hours and ${minutes % 60} min"
    }
    if (-minutes / 60 == 0) return "${-minutes % 60} min ago"
    if (-minutes % 60 == 0) return "${-minutes / 60} hours ago"
    return "${-minutes / 60} hours and ${-minutes % 60} min ago"
}
