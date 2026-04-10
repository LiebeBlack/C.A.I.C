package com.LBs.EEDA.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.LBs.EEDA.ui.theme.EedaAdaptiveTheme

/**
 * Barra de navegación inferior adaptativa para E.E.D.A.
 * Cambia su estética según la fase del usuario.
 */
data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
)

@Composable
fun AdaptiveBottomBar(
    items: List<BottomNavItem>,
    currentRoute: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = EedaAdaptiveTheme.colors
    val config = EedaAdaptiveTheme.typoConfig

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.navBarBackground,
        shadowElevation = config.elevation.dp,
        shape = RoundedCornerShape(
            topStart = config.borderRadius.dp,
            topEnd = config.borderRadius.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = item.route == currentRoute
                val tint by animateColorAsState(
                    targetValue = if (isSelected) colors.navBarSelected else colors.navBarUnselected,
                    animationSpec = tween(300),
                    label = "navTint"
                )

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(config.borderRadius.dp))
                        .clickable { onItemSelected(item.route) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 40.dp else 36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) colors.navBarSelected.copy(alpha = 0.12f)
                                else Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.icon,
                            contentDescription = item.label,
                            tint = tint,
                            modifier = Modifier.size(config.iconSize.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = tint,
                            fontSize = if (config.borderRadius > 20f) 12.sp else 11.sp
                        )
                    )
                }
            }
        }
    }
}
