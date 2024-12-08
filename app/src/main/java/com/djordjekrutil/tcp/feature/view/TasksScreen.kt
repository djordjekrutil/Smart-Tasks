import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.djordjekrutil.tcp.feature.viewmodel.TasksScreenState
import com.djordjekrutil.tcp.feature.viewmodel.TasksViewModel
import java.time.format.DateTimeFormatter
import com.djordjekrutil.tcp.R
import com.djordjekrutil.tcp.feature.model.TaskEntity
import com.djordjekrutil.tcp.feature.navigation.NavigationItem
import com.djordjekrutil.tcp.feature.utils.calculateDaysLeft
import com.djordjekrutil.tcp.ui.theme.lightYellow
import com.djordjekrutil.tcp.ui.theme.secondary
import com.djordjekrutil.tcp.ui.utils.ErrorView
import com.djordjekrutil.tcp.ui.utils.LoadingView
import com.djordjekrutil.tcp.ui.utils.fractionToDp

@Composable
fun TasksScreen(
    viewModel: TasksViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val screenState by viewModel.screenState.collectAsState()

    when (screenState) {
        is TasksScreenState.Loading -> LoadingView()
        is TasksScreenState.Error -> ErrorView((screenState as TasksScreenState.Error).message)
        is TasksScreenState.Content -> {
            val tasks by (screenState as TasksScreenState.Content).tasks.collectAsState(initial = null)
            val contentState = screenState as TasksScreenState.Content
            Column(modifier = Modifier.fillMaxSize()) {
                HeaderView(
                    state = contentState,
                    onPreviousDay = { viewModel.changeDate(false) },
                    onNextDay = { viewModel.changeDate(true) },
                    isToday = viewModel.isSelectedDateToday()
                )
                if (tasks?.isEmpty() == true) {
                    EmptyStateView(
                        isToday = viewModel.isSelectedDateToday()
                    )
                } else {
                    TasksContent(
                        tasks = tasks ?: emptyList(),
                        onTaskClick = { task -> navController.navigate(NavigationItem.Task(task.id).route) }
                    )
                }
            }

        }

        is TasksScreenState.Refreshing -> LoadingView() // Optional: Different UI for refreshing
    }
}

@Composable
fun HeaderView(
    state: TasksScreenState.Content,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    isToday: Boolean,
) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPreviousDay) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_left),
                contentDescription = "Previous Day",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }

        Text(
            text = if (isToday) "Today" else state.date.format(dateFormatter),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        IconButton(onClick = onNextDay) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_right),
                contentDescription = "Next Day",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun EmptyStateView(isToday: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(y = (-0.05f).fractionToDp())
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.empty_screen),
                contentDescription = "Empty smile",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f)
            )
            Text(
                text = if (isToday) "No tasks for today!" else "No tasks for the selected date!",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(top = 40.dp)
            )
        }
    }
}


@Composable
fun TasksContent(
    tasks: List<TaskEntity>,
    onTaskClick: (task: TaskEntity) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(tasks) { task ->
            TaskItem(task = task, onClick = { onTaskClick(task) })
        }
    }
}

@Composable
fun TaskItem(task: TaskEntity, onClick: () -> Unit) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = secondary
                    ),
                    modifier = Modifier.weight(1f)
                )
                if (task.isResolved != null) {
                    val icon =
                        if (task.isResolved) R.drawable.btn_resolved else R.drawable.btn_unresolved
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = "Task status",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            task.dueDate?.takeIf { it.isNotEmpty() }?.let {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = lightYellow,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
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
                            text = it.format(dateFormatter),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = secondary
                            )
                        )

                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Days left",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Color.Gray,
                                fontWeight = FontWeight.Normal
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        val daysLeft = calculateDaysLeft(it)
                        Text(
                            text = daysLeft.coerceAtLeast(0).toString(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = secondary
                            )
                        )
                    }
                }
            }
        }
    }
}