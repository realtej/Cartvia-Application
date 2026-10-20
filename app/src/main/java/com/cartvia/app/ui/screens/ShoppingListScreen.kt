package com.cartvia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cartvia.app.data.model.ShoppingListItemDto
import com.cartvia.app.ui.components.CartviaInputField
import com.cartvia.app.ui.components.SwipeDismissWrapper
import com.cartvia.app.ui.theme.BackgroundGray
import com.cartvia.app.ui.theme.BorderGray
import com.cartvia.app.ui.theme.InputGray
import com.cartvia.app.ui.theme.NeutralDark
import com.cartvia.app.ui.theme.NeutralGray
import com.cartvia.app.ui.theme.PrimaryBlue
import com.cartvia.app.ui.theme.SuccessGreen
import com.cartvia.app.util.ApiResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    viewModel: ShoppingListViewModel = hiltViewModel(),
    listId: String = "",
    onBack: () -> Unit = {}
) {
    val listState by viewModel.listState.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var tempListName by remember { mutableStateOf("") }

    var showAddItemSheet by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<ShoppingListItemDto?>(null) }
    var showShareSheet by remember { mutableStateOf(false) }
    var showMenuDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(listId) {
        viewModel.loadList(listId)
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create New List") },
            text = {
                OutlinedTextField(
                    value = tempListName,
                    onValueChange = { tempListName = it },
                    placeholder = { Text("list name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempListName.isNotBlank()) {
                            viewModel.createNewListWithName(tempListName)
                            showCreateDialog = false
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    val activeList = (listState as? ApiResult.Success)?.data
    val isListCreated = activeList != null && activeList.name.isNotBlank() && activeList.id.isNotBlank()

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isListCreated) activeList.name else "My List",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.saveCurrentState()
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeutralDark)
                    }
                },
                actions = {
                    if (isListCreated) {
                        IconButton(onClick = { showShareSheet = true }) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Share", tint = NeutralDark, modifier = Modifier.size(18.dp))
                            }
                        }
                        Box {
                            IconButton(onClick = { showMenuDropdown = true }) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = NeutralDark, modifier = Modifier.size(18.dp))
                                }
                            }
                            DropdownMenu(
                                expanded = showMenuDropdown,
                                onDismissRequest = { showMenuDropdown = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Delete List", color = MaterialTheme.colorScheme.error) },
                                    onClick = {
                                        showMenuDropdown = false
                                        viewModel.deleteCurrentList()
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundGray)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                !isListCreated -> {
                    EmptyListDesign(
                        onCreateList = { 
                            tempListName = ""
                            showCreateDialog = true 
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "${activeList.items.size} items · ${activeList.items.count { it.purchased }} purchased",
                            fontSize = 14.sp,
                            color = NeutralGray,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // "+ Add new item" button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .clickable { 
                                    editingItem = null
                                    showAddItemSheet = true 
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEFF6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Add new item",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (activeList.items.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No items in this list yet. Tap '+ Add new item' to start.", color = NeutralGray, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(32.dp))
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                            ) {
                                items(activeList.items, key = { it.id }) { item ->
                                    SwipeDismissWrapper(
                                        onDismiss = { viewModel.removeItem(item.id) }
                                    ) {
                                        ShoppingListItemCard(
                                            item = item,
                                            onToggle = { purchased -> viewModel.toggleItem(item.id, purchased) },
                                            onEdit = {
                                                editingItem = item
                                                showAddItemSheet = true
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Add/Edit Item Bottom Sheet
            if (showAddItemSheet) {
                AddItemBottomSheet(
                    listName = activeList?.name ?: "My List",
                    editingItem = editingItem,
                    onDismiss = { 
                        showAddItemSheet = false
                        editingItem = null
                    },
                    onConfirm = { name, qty, unit, subAllowed, urgent ->
                        if (editingItem != null) {
                            viewModel.updateItemCustom(editingItem!!.id, name, qty, unit, subAllowed, urgent)
                        } else {
                            viewModel.addItemCustom(name, qty, unit, subAllowed, urgent)
                        }
                        showAddItemSheet = false
                        editingItem = null
                    }
                )
            }

            if (showShareSheet) {
                ShareWithFamilyBottomSheet(
                    onDismiss = { showShareSheet = false }
                )
            }
        }
    }
}

@Composable
fun EmptyListDesign(
    onCreateList: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = NeutralGray.copy(alpha = 0.5f),
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No List yet - Create one to get Started",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = NeutralGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onCreateList,
            modifier = Modifier
                .width(160.dp)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text(
                text = "Create List",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun ShoppingListItemCard(
    item: ShoppingListItemDto,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onToggle(!item.purchased) },
                modifier = Modifier.size(28.dp)
            ) {
                if (item.purchased) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Purchased",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Not purchased",
                        tint = BorderGray,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (item.purchased) NeutralDark.copy(alpha = 0.6f) else NeutralDark,
                    textDecoration = if (item.purchased) TextDecoration.LineThrough else null
                )
                Text(
                    text = "${item.aisleNumber ?: "1 Pcs"} / note",
                    fontSize = 13.sp,
                    color = NeutralGray
                )
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Item", tint = NeutralGray, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemBottomSheet(
    listName: String,
    editingItem: ShoppingListItemDto?,
    onDismiss: () -> Unit,
    onConfirm: (String, Int, String, Boolean, Boolean) -> Unit
) {
    var itemName by remember { mutableStateOf(editingItem?.name ?: "") }
    
    val initialQty = editingItem?.aisleNumber?.split(" ")?.firstOrNull()?.toIntOrNull() ?: 1
    val initialUnit = editingItem?.aisleNumber?.split(" ")?.getOrNull(1) ?: "Pcs"

    var quantity by remember { mutableIntStateOf(initialQty) }
    var selectedUnit by remember { mutableStateOf(initialUnit) }
    var substituteAllowed by remember { mutableStateOf(false) }
    var markAsUrgent by remember { mutableStateOf(editingItem?.sectionName == "Urgent") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFCBD5E1))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp, bottom = 32.dp, top = 4.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = if (editingItem != null) "Edit Item" else "Add Item",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = NeutralDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                placeholder = { Text("Item name", color = NeutralGray.copy(alpha = 0.6f)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = InputGray,
                    unfocusedContainerColor = InputGray,
                    disabledContainerColor = InputGray,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = BorderGray,
                    focusedTextColor = NeutralDark,
                    unfocusedTextColor = NeutralDark
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stepper and Unit selector side-by-side in one clean horizontal row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Quantity numeric stepper [ - 1 + ]
                Row(
                    modifier = Modifier
                        .background(InputGray, RoundedCornerShape(12.dp))
                        .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "–",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralDark,
                        modifier = Modifier
                            .clickable { if (quantity > 1) quantity-- }
                            .padding(horizontal = 6.dp)
                    )
                    Text(
                        text = "$quantity",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralDark,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(
                        text = "+",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralDark,
                        modifier = Modifier
                            .clickable { quantity++ }
                            .padding(horizontal = 6.dp)
                    )
                }

                // Segmented pill selector container (without "Unit" word)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .background(InputGray, RoundedCornerShape(12.dp))
                        .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val units = listOf("Pcs", "Packs", "Grams", "Kg")
                    units.forEach { unit ->
                        val isSelected = selectedUnit == unit
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFFEFF6FF) else Color.White,
                            border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, BorderGray) else androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.3f)),
                            shadowElevation = if (isSelected) 1.dp else 0.dp,
                            modifier = Modifier.clickable { selectedUnit = unit }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = unit,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) PrimaryBlue else NeutralDark
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        modifier = Modifier.size(13.dp),
                                        tint = PrimaryBlue
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Two toggle switches side-by-side with compact scale
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Substitute allowed", fontSize = 13.sp, color = NeutralDark)
                    Switch(
                        checked = substituteAllowed,
                        onCheckedChange = { substituteAllowed = it },
                        modifier = Modifier.scale(0.75f),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryBlue,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFE2E8F0),
                            uncheckedBorderColor = Color(0xFFCBD5E1)
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Mark as Urgent", fontSize = 13.sp, color = NeutralDark)
                    Switch(
                        checked = markAsUrgent,
                        onCheckedChange = { markAsUrgent = it },
                        modifier = Modifier.scale(0.75f),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = SuccessGreen,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFE2E8F0),
                            uncheckedBorderColor = Color(0xFFCBD5E1)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Adding to : [$listName]",
                fontSize = 13.sp,
                color = NeutralGray
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (itemName.isNotBlank()) {
                        onConfirm(itemName, quantity, selectedUnit, substituteAllowed, markAsUrgent)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(
                    text = if (editingItem != null) "Save Changes" else "Add to List",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareWithFamilyBottomSheet(
    onDismiss: () -> Unit
) {
    var inviteInput by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Share with Family",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = NeutralDark,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Invite others to view and edit this list",
                fontSize = 14.sp,
                color = NeutralGray,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(24.dp))

            CartviaInputField(
                value = inviteInput,
                onValueChange = { inviteInput = it },
                label = "",
                placeholder = "Enter email or username",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(
                    text = "Send Invite",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = InputGray
            ) {
                Text(
                    text = "⏱ Coming soon",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeutralGray,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
