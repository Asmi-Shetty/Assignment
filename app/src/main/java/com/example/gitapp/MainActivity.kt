package com.example.gitapp


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.gitapp.ui.theme.GitAppTheme




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GitAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                }
            }
        }
    }
}

data class Repository(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val projectLink: String
)

data class Owner(
    val avatar_url: String,
    val login: String
)


data class Contributor(
    val id: String,
    val name: String,
    val repositories: List<Repository>
)



interface GitHubApiService {
    @GET("search/repositories")
    fun searchRepositories(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Single<SearchResponse>

    @GET("repos/{owner}/{repo}/contributors")
    fun getContributors(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Single<List<Contributor>>
}

data class SearchResponse(
    val totalCount: Int,
    val items: List<Repository>
)

//@Dao
//interface RepositoryDao {
//    @Query("SELECT * FROM repositories")
//    fun getRepositories(): PagingSource<Int, Repository>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertRepositories(repositories: List<Repository>)
//}
@Database(entities = [Repository::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repositoryDao(): RepositoryDao
}
class RepositoryRepository(private val apiService: GitHubApiService, private val database: AppDatabase) {
    fun searchRepositories(query: String): Flow<PagingData<Repository>> {
        val pagingSourceFactory = { database.repositoryDao().getRepositories() }

        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = pagingSourceFactory,
            remoteMediator = RepositoryRemoteMediator(query, apiService, database)
        ).flow
    }
}

class RepositoryViewModel(private val repository: RepositoryRepository) : ViewModel() {
    private val searchQuery = MutableStateFlow("")

    val repositories: Flow<PagingData<Repository>> = searchQuery
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            repository.searchRepositories(query)
        }
        .cachedIn(viewModelScope)

    fun searchRepositories(query: String) {
        searchQuery.value = query
    }
}
/
//@Composable
//fun SearchBar(modifier: Modifier = Modifier, onSearch: (String) -> Unit) {
//    var query by remember { mutableStateOf("") }
//
//    Row(modifier = modifier) {
//        TextField(
//            value = query,
//            onValueChange = { query = it },
//            label = { Text("Search") },
//            modifier = Modifier.weight(1f)
//        )
//
//        Button(
//            onClick = { onSearch(query) },
//            modifier = Modifier.padding(start = 8.dp)
//        ) {
//            Text(text = "Search")
//        }
//    }
class AppCompatActivity {
}
    class MainActivity : AppCompatActivity() {
        private val viewModel: RepositoryViewModel by viewModels()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                HomeScreen(viewModel)
            }
        }
    }
//
    class RepositoryDetailsActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                RepositoryDetailsScreen()
            }
        }
    }

    @Composable
    fun RepositoryDetailsScreen() {

    }









