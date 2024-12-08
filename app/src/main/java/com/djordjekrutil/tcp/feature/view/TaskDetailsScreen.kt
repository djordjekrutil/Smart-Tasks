package com.djordjekrutil.tcp.feature.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.djordjekrutil.tcp.R
import com.djordjekrutil.tcp.core.extension.toLocalDate
import com.djordjekrutil.tcp.feature.model.TaskEntity
import com.djordjekrutil.tcp.feature.utils.calculateDaysLeft
import com.djordjekrutil.tcp.feature.viewmodel.TaskDetailsState
import com.djordjekrutil.tcp.feature.viewmodel.TaskDetailsViewModel
import com.djordjekrutil.tcp.ui.theme.green
import com.djordjekrutil.tcp.ui.theme.lightYellow
import com.djordjekrutil.tcp.ui.theme.orange
import com.djordjekrutil.tcp.ui.theme.secondary
import com.djordjekrutil.tcp.ui.utils.ErrorView
import com.djordjekrutil.tcp.ui.utils.LoadingView
import com.djordjekrutil.tcp.ui.utils.CustomButton
import java.time.format.DateTimeFormatter

@Composable
fun TaskDetailsScreen(
    viewModel: TaskDetailsViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val state by viewModel.state.collectAsState()

    when (state) {
        is TaskDetailsState.Loading -> LoadingView()
        is TaskDetailsState.Error -> ErrorView((state as TaskDetailsState.Error).message)
        is TaskDetailsState.Content -> {
            Column {
                val task by (state as TaskDetailsState.Content).task.collectAsState(initial = null)
                HeaderView(navController)
                task?.let {
                    TaskItem(
                        it,
                        { viewModel.resolveTask(true) },
                        { viewModel.resolveTask(false) },
                        { comment -> viewModel.addComment(comment) })
                }
            }
        }
    }
}

@Composable
fun HeaderView(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_left),
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }
        Text(
            text = "Task Details",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(48.dp))

    }
}

@Composable
fun TaskItem(
    task: TaskEntity,
    onResolveClick: () -> Unit,
    onCantResolveClick: () -> Unit,
    commentTask: (String) -> Unit
) {
    Column {
        TaskDetailsCard(task = task)
        when (task.isResolved) {
            true -> TaskStatus(true)
            false -> TaskStatus(false)
            null -> TaskButtons(onResolveClick, onCantResolveClick, commentTask)
        }
    }

}

@Composable
fun TaskDetailsCard(task: TaskEntity) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy")
    val titlesColor = when (task.isResolved) {
        true -> green
        false, null -> secondary
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.task_details), // Replace with your image resource
            contentDescription = "Overlay Image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()

        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = task.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = titlesColor
                ),
                modifier = Modifier.padding(bottom = 8.dp, top = 28.dp)
            )

            task.dueDate?.takeIf { it.isNotEmpty() }?.let {

                HorizontalDivider(
                    thickness = 1.dp,
                    color = lightYellow
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Due date",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Color.Gray
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = it.toLocalDate().format(dateFormatter),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = titlesColor
                            )
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Days left",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Color.Gray
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        val daysLeft = calculateDaysLeft(it)
                        Text(
                            text = daysLeft.coerceAtLeast(0).toString(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = titlesColor
                            )
                        )
                    }
                }
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = lightYellow
            )
            Text(
                text = task.description,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = Color.Gray,
                ),
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = lightYellow
            )

            val textStatus = when (task.isResolved) {
                true -> "Resolved"
                false, null -> "Unresolved"
            }

            Text(
                text = textStatus,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = if (task.isResolved == null) orange else titlesColor
                ),
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

@Composable
fun TaskButtons(
    onResolveClick: () -> Unit,
    onCantResolveClick: () -> Unit,
    commentTask: (String) -> Unit
) {
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showCommentDialog by remember { mutableStateOf(false) }
    var selectedAction by remember { mutableStateOf({}) }
    var commentText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CustomButton(
            buttonName = "Resolve",
            buttonColor = green,
            onClick = {
                selectedAction = onResolveClick
                showConfirmationDialog = true
            }, modifier = Modifier.weight(1f)
        )
        CustomButton(
            buttonName = "Can't resolve",
            buttonColor = secondary,
            onClick = {
                selectedAction = onCantResolveClick
                showConfirmationDialog = true
            },
            modifier = Modifier.weight(1f)
        )
    }

    if (showConfirmationDialog) {
        TaskConfirmationDialog(
            onDismiss = { showConfirmationDialog = false },
            onConfirm = {
                showConfirmationDialog = false
                showCommentDialog = true
            },
            onSkip = {
                showConfirmationDialog = false
                selectedAction()
            }
        )
    }

    if (showCommentDialog) {
        TaskCommentDialog(
            commentText = commentText,
            onTextChange = { commentText = it },
            onDismiss = {
                showCommentDialog = false
                commentText = ""
            },
            onSubmit = {
                showCommentDialog = false
                commentTask(commentText)
                commentText = ""
                selectedAction()
            }
        )
    }
}

@Composable
fun TaskStatus(isResolved: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val image = if (isResolved) R.drawable.sign_resolved else R.drawable.unresolved_sign

        val configuration = LocalConfiguration.current
        val maxImageSize = configuration.screenWidthDp.dp * 0.4f

        Image(
            painter = painterResource(id = image),
            contentDescription = "Task status",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(maxImageSize)
                .aspectRatio(1f)
        )
    }
}

@Composable
fun TaskConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onSkip: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add a comment?",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = "Do you want to leave a comment?",
                style = MaterialTheme.typography.titleMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = green
                )
            ) {
                Text(
                    text = "Yes",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onSkip,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = secondary
                )
            ) {
                Text(
                    text = "No",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCommentDialog(
    commentText: String,
    onTextChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Enter Comment",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            TextField(
                value = commentText,
                onValueChange = onTextChange,
                label = {
                    Text(
                        "Comment",
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = green,
                    focusedIndicatorColor = green,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedLabelColor = green,
                    unfocusedLabelColor = Color.Gray,
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = onSubmit,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = green
                )
            ) {
                Text(
                    text = "Submit",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = secondary
                )
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    )
}
