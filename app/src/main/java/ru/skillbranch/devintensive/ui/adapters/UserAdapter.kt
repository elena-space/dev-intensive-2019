package ru.skillbranch.devintensive.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_user_list.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.data.UserItem

class UserAdapter(private val listener: (UserItem) -> Unit) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    var items = listOf<UserItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
            UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_user_list, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    fun updateData(data: List<UserItem>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldPos: Int, newPos: Int) = items[oldPos].id == data[newPos].id
            override fun areContentsTheSame(oldPos: Int, newPos: Int) = items[oldPos] == data[newPos]
            override fun getOldListSize() = items.size
            override fun getNewListSize() = data.size
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = data
        diffResult.dispatchUpdatesTo(this)
    }

    class UserViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(user: UserItem, listener: (UserItem) -> Unit) {
            user.avatar?.let { Glide.with(itemView).load(it).into(iv_avatar_user) }
                    ?: Glide.with(itemView).clear(iv_avatar_user).run { iv_avatar_user.initials = user.initials }
            sv_indicator.isVisible = user.isOnline
            tv_user_name.text = user.fullName
            tv_last_activity.text = user.lastActivity
            iv_selected.isVisible = user.isSelected
            itemView.setOnClickListener { listener.invoke(user) }
        }
    }
}
