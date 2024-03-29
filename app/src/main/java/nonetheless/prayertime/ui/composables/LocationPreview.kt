package nonetheless.prayertime.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import nonetheless.prayertime.R
import nonetheless.prayertime.model.City


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPreview(
    selectedCity: City,
    onSelectedCityChange: (city: City) -> Unit,
    showBottomSheet: Boolean,
    setBottomSheet: (state: Boolean) -> Unit,
    searchQuery: String,
    setSearchQuery: (query: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    TextButton(
        onClick = { setBottomSheet(true) },
    ) {
        Row {
            Icon(
                painter = painterResource(id = R.drawable.baseline_near_me_24),
                contentDescription = stringResource(
                    R.string.location_icon
                )
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(text = selectedCity.text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                setBottomSheet(false)
            },
            sheetState = sheetState,
            modifier = Modifier.fillMaxHeight()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { setSearchQuery(it) },
                singleLine = true,
                label = { Text(text = "City") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            val indexedCities =
                City.entries.filter { it.text.lowercase().contains(searchQuery.lowercase()) }
                    .withIndex().toList()
            LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                items(indexedCities) {
                    ListItem(
                        headlineContent = { Text(text = it.value.text) },
                        modifier = Modifier.clickable {
                            onSelectedCityChange(it.value)
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    setBottomSheet(false)
                                }
                            }
                        })
                    if (it.index != indexedCities.lastIndex) {
                        Divider()
                    }
                }
            }
        }
    }
}
