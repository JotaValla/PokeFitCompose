package com.jimmy.valladares.pokefitcompose.presentation.strength_training.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun ExperienceGainedDialog(
    expGained: Int,
    leveledUp: Boolean,
    newLevel: Int?,
    breakdown: Map<String, Int>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A2E)
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título principal
                Text(
                    text = if (leveledUp) "🎉 ¡SUBISTE DE NIVEL!" else "⭐ EXPERIENCIA GANADA",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Experiencia total ganada
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = if (leveledUp) {
                                    listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                                } else {
                                    listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))
                                }
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "+$expGained EXP",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        
                        if (leveledUp && newLevel != null) {
                            Text(
                                text = "Nuevo Nivel: $newLevel",
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Desglose de experiencia
                if (breakdown.isNotEmpty()) {
                    Text(
                        text = "Desglose:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp)
                    ) {
                        items(breakdown.entries.toList()) { (reason, exp) ->
                            if (exp > 0) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .background(
                                            Color(0xFF2A2A40),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = getExperienceReasonText(reason),
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    Text(
                                        text = "+$exp",
                                        color = Color(0xFF4CAF50),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Botón para cerrar
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6200EA)
                    )
                ) {
                    Text(
                        text = "¡Genial!",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun getExperienceReasonText(reason: String): String {
    return when (reason) {
        "completion" -> "🏁 Entrenamiento completado"
        "completionRate" -> "📊 Ejercicios completados"
        "improvement" -> "📈 Mejora en rendimiento"
        "duration" -> "⏱️ Duración del entrenamiento"
        "consistency" -> "🔥 Consistencia"
        "perfectWorkout" -> "💯 Entrenamiento perfecto"
        else -> reason
    }
}
