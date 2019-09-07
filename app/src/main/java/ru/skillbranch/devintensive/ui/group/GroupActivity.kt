package ru.skillbranch.devintensive.ui.group

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_group.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.themedColorAccent
import ru.skillbranch.devintensive.models.data.UserItem
import ru.skillbranch.devintensive.ui.adapters.UserAdapter
import ru.skillbranch.devintensive.ui.custom.TextDrawable
import ru.skillbranch.devintensive.viewmodels.GroupViewModel

class GroupActivity : AppCompatActivity() {

    private lateinit var usersAdapter: UserAdapter
    private lateinit var viewModel: GroupViewModel

    private val avatarPixelSize by lazy { (resources.getDimensionPixelSize(R.dimen.avatar_item_size)/1.65).toInt() }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        initToolbar()
        initViews()
        initViewModel()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> finish().run { true }
        else -> super.onOptionsItemSelected(item)
    }

    private fun initViews() {
        title = getString(R.string.title_create_group)
        usersAdapter = UserAdapter { viewModel.handleSelectedItem(it.id) }

        with(rv_user_list) {
            adapter = usersAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        fab.setOnClickListener {
            viewModel.handleCreateGroup()
            finish()
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)
        viewModel.getUsersData().observe(this, Observer { usersAdapter.updateData(it) })
        viewModel.getSelectedData().observe(this, Observer {
            updateChips(it)
            toggleFab(it.size > 1)
        })
    }

    private fun toggleFab(isShow: Boolean) = if (isShow) fab.show() else fab.hide()

    private fun addChipToGroup(user: UserItem) {
        val chip = Chip(this).apply {
            Glide.with(this).load(user.avatar)
                    .circleCrop()
                    .placeholder(R.drawable.avatar_default)
                    .into(object : CustomTarget<Drawable>() {
                        override fun onLoadCleared(placeholder: Drawable?) {
                            chipIcon = placeholder
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            chipIcon = drawAvatarForChip(user.initials ?: "??")
                        }

                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            chipIcon = resource
                        }
                    })
            text = user.fullName
            isCloseIconVisible = true
            tag = user.id
            isClickable = true
            closeIconTint = ColorStateList.valueOf(Color.WHITE)
            chipBackgroundColor = ColorStateList.valueOf(getColor(R.color.color_primary_light))
            setTextColor(Color.WHITE)
        }
        chip.setOnCloseIconClickListener { viewModel.handleRemoveChip(it.tag.toString()) }
        chip_group.addView(chip)
    }

    private fun updateChips(listUsers: List<UserItem>) {
        chip_group.isVisible = listUsers.isNotEmpty()

        val users = listUsers
                .associateBy { user -> user.id }
                .toMutableMap()

        val views = chip_group.children.associateBy { view -> view.tag }

        for ((key, value) in views) {
            if (!users.containsKey(key)) chip_group.removeView(value)
            else users.remove(key)
        }

        users.forEach { (_, v) -> addChipToGroup(v) }
    }

    private fun drawAvatarForChip(initials: String): TextDrawable {
        return TextDrawable.Builder()
                .text(initials)
                .textColor(Color.WHITE)
                .backgroundColor(themedColorAccent)
                .fontSize(avatarPixelSize/2)
                .width(avatarPixelSize)
                .height(avatarPixelSize)
                .bold()
                .round()
                .build()
    }
}
