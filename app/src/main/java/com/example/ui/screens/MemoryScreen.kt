package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AiMemoryEntity
import com.example.data.local.UserEntity
import com.example.model.Localization
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.DarkNavyCanvas
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceDeep
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MemoryScreen(
  memories: List<AiMemoryEntity>,
  currentUser: UserEntity?,
  strings: Localization.AppStrings,
  onBack: () -> Unit,
  onAddMemory: (title: String, content: String) -> Unit,
  onUpdateMemory: (AiMemoryEntity) -> Unit,
  onDeleteMemory: (Long) -> Unit,
  onDeleteAllMemories: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showAddDialog by remember { mutableStateOf(false) }
  var memoryToEdit by remember { mutableStateOf<AiMemoryEntity?>(null) }
  var showDeleteAllConfirm by remember { mutableStateOf(false) }
  var memoryToDelete by remember { mutableStateOf<AiMemoryEntity?>(null) }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(DarkNavyCanvas)
      .statusBarsPadding(),
    containerColor = DarkNavyCanvas,
    topBar = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(DarkNavyCanvas)
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier.testTag("memory_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = strings.back,
            tint = Color.White
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(ElectricViolet, NeonCyan))),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Psychology,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = strings.memoryManagementTitle,
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "${memories.size} ${if (memories.size == 1) "بیرەوەری" else "بیرەوەری تۆمارکراو"}",
            color = NeonCyan,
            fontSize = 12.sp
          )
        }

        IconButton(
          onClick = { showAddDialog = true },
          modifier = Modifier
            .testTag("add_memory_header_button")
            .clip(CircleShape)
            .background(DarkSurfaceDeep)
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = strings.addMemory,
            tint = NeonCyan
          )
        }
      }
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      // Security & Isolation Banner
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceDeep),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = CyberGreen,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = strings.explicitMemoryRule,
              color = Color.White.copy(alpha = 0.85f),
              fontSize = 12.sp,
              lineHeight = 17.sp
            )
            currentUser?.let { user ->
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "هەژماری خاوەندار: ${user.displayName} (${user.email})",
                color = CyberGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }

      // Memory List or Empty State
      if (memories.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(
              modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(DarkSurfaceDeep)
                .border(1.dp, NeonCyan.copy(alpha = 0.3f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(36.dp)
              )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = strings.emptyMemoriesNotice,
              color = Color.White.copy(alpha = 0.75f),
              fontSize = 14.sp,
              lineHeight = 22.sp,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center,
              modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
              onClick = { showAddDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.testTag("empty_state_add_memory_button")
            ) {
              Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = DarkNavyCanvas,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = strings.addMemory,
                color = DarkNavyCanvas,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .testTag("memories_list"),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(memories, key = { it.id }) { memory ->
            MemoryCardItem(
              memory = memory,
              strings = strings,
              onEdit = { memoryToEdit = memory },
              onDelete = { memoryToDelete = memory }
            )
          }

          item {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
              onClick = { showDeleteAllConfirm = true },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("delete_all_memories_button"),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonOrange),
              border = androidx.compose.foundation.BorderStroke(1.dp, NeonOrange.copy(alpha = 0.5f)),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = null,
                tint = NeonOrange,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = strings.deleteAllMemories,
                color = NeonOrange,
                fontWeight = FontWeight.SemiBold
              )
            }
            Spacer(modifier = Modifier.height(24.dp))
          }
        }
      }
    }
  }

  // --- Add Memory Dialog ---
  if (showAddDialog) {
    var titleInput by remember { mutableStateOf("") }
    var contentInput by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showAddDialog = false },
      containerColor = DarkSurfaceCard,
      title = {
        Text(
          text = strings.addMemory,
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp
        )
      },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = titleInput,
            onValueChange = { titleInput = it },
            label = { Text(strings.memoryTitleLabel, color = Color.White.copy(alpha = 0.6f)) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("memory_title_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = NeonCyan,
              unfocusedBorderColor = DarkSurfaceBorder
            )
          )
          Spacer(modifier = Modifier.height(12.dp))
          OutlinedTextField(
            value = contentInput,
            onValueChange = { contentInput = it },
            label = { Text(strings.memoryContentLabel, color = Color.White.copy(alpha = 0.6f)) },
            minLines = 3,
            maxLines = 6,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("memory_content_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = NeonCyan,
              unfocusedBorderColor = DarkSurfaceBorder
            )
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (contentInput.isNotBlank()) {
              onAddMemory(titleInput, contentInput)
              showAddDialog = false
            }
          },
          enabled = contentInput.isNotBlank(),
          colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
          modifier = Modifier.testTag("save_memory_button")
        ) {
          Text(strings.save, color = DarkNavyCanvas, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddDialog = false }) {
          Text(strings.cancel, color = Color.White.copy(alpha = 0.7f))
        }
      }
    )
  }

  // --- Edit Memory Dialog ---
  memoryToEdit?.let { memory ->
    var editTitle by remember { mutableStateOf(memory.title) }
    var editContent by remember { mutableStateOf(memory.content) }

    AlertDialog(
      onDismissRequest = { memoryToEdit = null },
      containerColor = DarkSurfaceCard,
      title = {
        Text(
          text = strings.editMemory,
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp
        )
      },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = editTitle,
            onValueChange = { editTitle = it },
            label = { Text(strings.memoryTitleLabel, color = Color.White.copy(alpha = 0.6f)) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("edit_memory_title_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = NeonCyan,
              unfocusedBorderColor = DarkSurfaceBorder
            )
          )
          Spacer(modifier = Modifier.height(12.dp))
          OutlinedTextField(
            value = editContent,
            onValueChange = { editContent = it },
            label = { Text(strings.memoryContentLabel, color = Color.White.copy(alpha = 0.6f)) },
            minLines = 3,
            maxLines = 6,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("edit_memory_content_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = NeonCyan,
              unfocusedBorderColor = DarkSurfaceBorder
            )
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (editContent.isNotBlank()) {
              onUpdateMemory(
                memory.copy(
                  title = editTitle.trim(),
                  content = editContent.trim(),
                  timestamp = System.currentTimeMillis()
                )
              )
              memoryToEdit = null
            }
          },
          enabled = editContent.isNotBlank(),
          colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
          modifier = Modifier.testTag("update_memory_button")
        ) {
          Text(strings.save, color = DarkNavyCanvas, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { memoryToEdit = null }) {
          Text(strings.cancel, color = Color.White.copy(alpha = 0.7f))
        }
      }
    )
  }

  // --- Confirm Delete Single Memory Dialog ---
  memoryToDelete?.let { mem ->
    AlertDialog(
      onDismissRequest = { memoryToDelete = null },
      containerColor = DarkSurfaceCard,
      title = {
        Text(
          text = strings.deleteMemory,
          color = Color.White,
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Text(
          text = mem.content.take(80) + if (mem.content.length > 80) "..." else "",
          color = Color.White.copy(alpha = 0.8f),
          fontSize = 14.sp
        )
      },
      confirmButton = {
        Button(
          onClick = {
            onDeleteMemory(mem.id)
            memoryToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = NeonOrange)
        ) {
          Text(strings.deleteMemory, color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { memoryToDelete = null }) {
          Text(strings.cancel, color = Color.White.copy(alpha = 0.7f))
        }
      }
    )
  }

  // --- Confirm Delete All Memories Dialog ---
  if (showDeleteAllConfirm) {
    AlertDialog(
      onDismissRequest = { showDeleteAllConfirm = false },
      containerColor = DarkSurfaceCard,
      icon = {
        Icon(
          imageVector = Icons.Default.Warning,
          contentDescription = null,
          tint = NeonOrange,
          modifier = Modifier.size(32.dp)
        )
      },
      title = {
        Text(
          text = strings.deleteAllMemories,
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp
        )
      },
      text = {
        Text(
          text = strings.deleteAllMemoriesConfirm,
          color = Color.White.copy(alpha = 0.8f),
          fontSize = 14.sp,
          lineHeight = 20.sp
        )
      },
      confirmButton = {
        Button(
          onClick = {
            onDeleteAllMemories()
            showDeleteAllConfirm = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = NeonOrange),
          modifier = Modifier.testTag("confirm_delete_all_memories_button")
        ) {
          Text(strings.deleteAllMemories, color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteAllConfirm = false }) {
          Text(strings.cancel, color = Color.White.copy(alpha = 0.7f))
        }
      }
    )
  }
}

@Composable
private fun MemoryCardItem(
  memory: AiMemoryEntity,
  strings: Localization.AppStrings,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd · HH:mm", Locale.getDefault()) }
  val formattedDate = remember(memory.timestamp) { dateFormat.format(Date(memory.timestamp)) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("memory_item_${memory.id}"),
    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
    shape = RoundedCornerShape(14.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(NeonCyan)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (memory.title.isNotBlank()) memory.title else "زانیاری لەبیرماو",
            color = NeonCyan,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onEdit,
            modifier = Modifier
              .size(32.dp)
              .testTag("edit_memory_${memory.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = strings.editMemory,
              tint = Color.White.copy(alpha = 0.75f),
              modifier = Modifier.size(17.dp)
            )
          }

          IconButton(
            onClick = onDelete,
            modifier = Modifier
              .size(32.dp)
              .testTag("delete_memory_${memory.id}")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = strings.deleteMemory,
              tint = NeonOrange.copy(alpha = 0.85f),
              modifier = Modifier.size(17.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = memory.content,
        color = Color.White,
        fontSize = 14.sp,
        lineHeight = 20.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = formattedDate,
          color = Color.White.copy(alpha = 0.4f),
          fontSize = 11.sp
        )

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(DarkSurfaceDeep)
            .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Text(
            text = "تایبەت بە هەژمار",
            color = CyberGreen,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }
  }
}
