package ru.skillbranch.devintensive.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_chat_group.*
import kotlinx.android.synthetic.main.item_chat_single.*
import kotlinx.android.synthetic.main.item_chat_single.sv_indicator
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.utils.ChatType

class ChatAdapter(private val listener: (ChatItem) -> Unit) : RecyclerView.Adapter<ChatAdapter.ChatItemViewHolder>() {

    var items: List<ChatItem> = listOf()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            SINGLE_TYPE -> SingleViewHolder(inflater.inflate(R.layout.item_chat_single, parent, false))
            GROUP_TYPE -> GroupViewHolder(inflater.inflate(R.layout.item_chat_group, parent, false))
            else -> GroupViewHolder(inflater.inflate(R.layout.item_chat_group, parent, false))
        }
    }

    override fun onBindViewHolder(holder: ChatItemViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    override fun getItemViewType(position: Int) = when (items[position].chatType) {
        ChatType.ARCHIVE -> ARCHIVE_TYPE
        ChatType.SINGLE -> SINGLE_TYPE
        ChatType.GROUP -> GROUP_TYPE
    }

    fun updateData(data: List<ChatItem>) {
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

    abstract inner class ChatItemViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        abstract fun bind(item: ChatItem, listener: (ChatItem) -> Unit)
    }

    inner class SingleViewHolder(override val containerView: View) : ChatItemViewHolder(containerView), ItemTouchViewHolder {

        override fun onItemSelected() = itemView.setBackgroundColor(Color.LTGRAY)

        override fun onItemCleared() = itemView.setBackgroundColor(Color.WHITE)

        override fun bind(item: ChatItem, listener: (ChatItem) -> Unit) {
            if (item.avatar == null) {
                Glide.with(itemView).clear(iv_avatar_single)
                iv_avatar_single.initials = item.initials
            } else Glide.with(itemView)
                    .load(item.avatar)
                    .into(iv_avatar_single)

            sv_indicator.isVisible = item.isOnline
            with(tv_date_single) {
                isVisible = item.lastMessageDate != null
                text = item.lastMessageDate
            }

            with(tv_counter_single) {
                isVisible = item.messageCount > 0
                text = item.messageCount.toString()
            }

            tv_title_single.text = item.title
            tv_message_single.text = item.shortDescription

            itemView.setOnClickListener {
                listener.invoke(item)
            }
        }
    }

    inner class GroupViewHolder(override val containerView: View) : ChatItemViewHolder(containerView), ItemTouchViewHolder {

        override fun onItemSelected() = itemView.setBackgroundColor(Color.LTGRAY)

        override fun onItemCleared() = itemView.setBackgroundColor(Color.WHITE)

        override fun bind(item: ChatItem, listener: (ChatItem) -> Unit) {
            iv_avatar_group.initials = item.initials

            with(tv_date_group) {
                isVisible = item.lastMessageDate != null
                text = item.lastMessageDate
            }

            with(tv_counter_group) {
                isVisible = item.messageCount > 0
                text = item.messageCount.toString()
            }

            tv_title_group.text = item.title
            tv_message_group.text = item.shortDescription

            with(tv_message_author) {
                isVisible = item.messageCount > 0
                text = item.author
            }

            itemView.setOnClickListener {
                listener.invoke(item)
            }
        }
    }

    companion object {
        private const val ARCHIVE_TYPE = 0
        private const val SINGLE_TYPE = 1
        private const val GROUP_TYPE = 2
    }
}