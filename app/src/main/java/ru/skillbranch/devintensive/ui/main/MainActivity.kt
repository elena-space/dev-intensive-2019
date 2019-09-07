package ru.skillbranch.devintensive.ui.main

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import kotlinx.android.synthetic.main.activity_main.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.ui.abs.BaseActivity
import ru.skillbranch.devintensive.ui.adapters.ChatAdapter
import ru.skillbranch.devintensive.ui.adapters.ChatItemTouchCallback
import ru.skillbranch.devintensive.viewmodels.MainViewModel

class MainActivity : BaseActivity() {

    override val coordinator: CoordinatorLayout by lazy { coordinatorLayout }

    private lateinit var chatAdapter: ChatAdapter

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()
        initViews()
        initViewModel()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchView = menu?.findItem(R.id.action_search)?.actionView as? SearchView ?: return super.onCreateOptionsMenu(menu)
        searchView.queryHint = getString(R.string.hint_enter_user_name)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = viewModel.handleSearchQuery(query).run { true }
            override fun onQueryTextChange(query: String?) = viewModel.handleSearchQuery(query).run { true }
        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun initViews() {
        chatAdapter = ChatAdapter { showSnackbar("Click on ${it.title}") }
        ItemTouchHelper(ChatItemTouchCallback(chatAdapter) { onItemSwipe(it) }).attachToRecyclerView(rv_chat_list)

        with(rv_chat_list) {
            adapter = chatAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

//        fab.setOnClickListener { startProfileScreen(this) }
        fab.setOnClickListener { startGroupScreen(this) }
    }

    private fun onItemSwipe(item: ChatItem) {
        viewModel.addToArchive(item.id)
        showSnackbar("Вы точно хотите добавить ${item.title} в архив?", R.string.snackbar_archive_cancel) { viewModel.restoreFromArchive(item.id) }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.getChatData().observe(this, Observer { chatAdapter.updateData(it) })
    }
}
