package de.repeatuntil.yata.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.repeatuntil.yata.ui.theme.YATATheme
import androidx.lifecycle.viewmodel.compose.viewModel
import de.repeatuntil.yata.domain.todolist.entities.Todo

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YATATheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when (uiState) {
            UIState.Loading -> {
                Greeting(
                    message = "Loading",
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is UIState.ShowTodoList -> {
                val todoList = (uiState as UIState.ShowTodoList).todoList
                Column {
                    Button(
                        modifier = Modifier.padding(innerPadding),
                        onClick = {
                            viewModel.addTodo(
                                todoList,
                                Todo(
                                    title = "New todo ${todoList.todos.size + 1}",
                                    description = "New todo description"
                                )
                            )
                        }) {
                        Text("Add to-do")
                    }

                    if (todoList.todos.isEmpty()) {
                        Greeting(
                            message = "No to-dos"
                        )
                    } else {
                        todoList.todos.forEach { todo ->
                            Greeting(
                                message = todo.title
                            )
                        }
                    }
                }
            }

            is UIState.ShowError -> {
                Greeting(
                    message = (uiState as UIState.ShowError).message,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

    }
}

@Composable
fun Greeting(message: String, modifier: Modifier = Modifier) {
    Text(
        text = "$message!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    YATATheme {
        Greeting("Android")
    }
}