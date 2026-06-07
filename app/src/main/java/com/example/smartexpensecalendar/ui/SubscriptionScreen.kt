package com.example.smartexpensecalendar.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartexpensecalendar.data.model.PlanFeatureInfo
import com.example.smartexpensecalendar.data.model.SubscriptionData
import com.example.smartexpensecalendar.data.model.SubscriptionPlan
import com.example.smartexpensecalendar.core.designsystem.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(navController: NavController) {
    val features = SubscriptionData.allFeatures
    val plans = SubscriptionData.plans

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose Your Plan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundStart,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = BackgroundStart
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Plan Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                plans.forEach { plan ->
                    PlanCard(
                        plan = plan,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Feature Comparison",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Comparison Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Features", Modifier.weight(1.5f), color = TextSecondary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                Text("Free", Modifier.weight(1f), textAlign = TextAlign.Center, color = TextSecondary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                Text("Pro", Modifier.weight(1f), textAlign = TextAlign.Center, color = PrimaryAccent, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                Text("Pro+", Modifier.weight(1f), textAlign = TextAlign.Center, color = ColorShopping, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
            }

            HorizontalDivider(color = SurfaceGlassBright)

            features.forEach { feature ->
                FeatureRow(feature)
                HorizontalDivider(color = SurfaceGlassBright.copy(alpha = 0.5f))
            }

            Spacer(modifier = Modifier.height(40.dp))
            
            Button(
                onClick = { /* Handle Upgrade */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Get Started with PRO", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PlanCard(
    plan: SubscriptionPlan,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (plan.isPopular) plan.color.copy(alpha = 0.1f) else SurfaceGlass)
            .border(
                1.dp,
                if (plan.isPopular) plan.color else SurfaceGlassBright,
                RoundedCornerShape(20.dp)
            )
            .padding(12.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        if (plan.isPopular) {
            Text(
                text = "POPULAR",
                color = BackgroundStart,
                modifier = Modifier
                    .offset(y = (-20).dp)
                    .background(plan.color, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold
            )
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(plan.name, color = plan.color, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(plan.price, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Text(plan.period, color = TextSecondary, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun FeatureRow(feature: PlanFeatureInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = feature.name,
            modifier = Modifier.weight(1.5f),
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodySmall
        )
        
        FeatureIcon(feature.includedInFree, Modifier.weight(1f))
        FeatureIcon(feature.includedInPro, Modifier.weight(1f))
        FeatureIcon(feature.includedInProPlus, Modifier.weight(1f))
    }
}

@Composable
fun FeatureIcon(included: Boolean, modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (included) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Included",
                tint = PrimaryAccent,
                modifier = Modifier.size(18.dp)
            )
        } else {
            Icon(
                Icons.Default.Close,
                contentDescription = "Not Included",
                tint = Color.White.copy(alpha = 0.1f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
