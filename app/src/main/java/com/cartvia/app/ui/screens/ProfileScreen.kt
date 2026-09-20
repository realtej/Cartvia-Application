package com.cartvia.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cartvia.app.util.ApiResult
import com.cartvia.app.util.SecureScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import com.cartvia.app.data.model.UserPreferences

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    SecureScreen()
    val user by viewModel.user.collectAsState(null)
    val preferences by viewModel.preferences.collectAsState(UserPreferences())
    val updateState by viewModel.updateState.collectAsState()
    
    var isEditMode by remember { mutableStateOf(false) }
    var name by remember(user) { mutableStateOf(user?.name ?: "") }
    var email by remember(user) { mutableStateOf(user?.email ?: "") }
    var phone by remember(user) { mutableStateOf(user?.phone ?: "") }

    val availableTags = listOf("Vegan", "Gluten-Free", "Nut-Allergy", "Dairy-Free", "Halal")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Text("My Profile", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (isEditMode) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { isEditMode = false }) { Text("Cancel") }
                        Button(
                            onClick = { 
                                viewModel.updateProfile(name, email, phone)
                                isEditMode = false
                            },
                            enabled = updateState !is ApiResult.Loading
                        ) {
                            Text("Save")
                        }
                    }
                } else {
                    user?.let {
                        Text(text = it.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(text = "@${it.username}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(12.dp))
                        Text(text = it.email, style = MaterialTheme.typography.bodyMedium)
                        Text(text = it.phone, style = MaterialTheme.typography.bodyMedium)
                        
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { isEditMode = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("Edit Contact Info")
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        Text("Shopping Preferences", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Dietary & Allergy Tags", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(8.dp))
                
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableTags.forEach { tag ->
                        val isSelected = preferences.dietaryTags.contains(tag)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                val newTags = if (isSelected) {
                                    preferences.dietaryTags - tag
                                } else {
                                    preferences.dietaryTags + tag
                                }
                                viewModel.updatePreferences(preferences.copy(dietaryTags = newTags))
                            },
                            label = { Text(tag) }
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
                Text("Default Session Budget (₹)", style = MaterialTheme.typography.labelMedium)
                var budgetInput by remember(preferences.defaultBudget) { mutableStateOf(preferences.defaultBudget?.toString() ?: "") }
                
                OutlinedTextField(
                    value = budgetInput,
                    onValueChange = { 
                        budgetInput = it
                        viewModel.updatePreferences(preferences.copy(defaultBudget = it.toDoubleOrNull()))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("No limit set") },
                    singleLine = true
                )
            }
        }

        Spacer(Modifier.height(32.dp))
        
        OutlinedButton(
            onClick = onNavigateToHistory,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Order History & Receipts")
        }

        Spacer(Modifier.height(16.dp))
        
        Button(
            onClick = { 
                viewModel.logout()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Logout")
        }
        
        Spacer(Modifier.height(40.dp))
    }
}
