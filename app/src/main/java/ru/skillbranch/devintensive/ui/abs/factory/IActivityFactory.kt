package ru.skillbranch.devintensive.ui.abs.factory

import android.content.Context
import android.content.Intent
import ru.skillbranch.devintensive.ui.group.GroupActivity
import ru.skillbranch.devintensive.ui.main.MainActivity
import ru.skillbranch.devintensive.ui.profile.ProfileActivity

/**
 * @author Space
 * @date 07.09.2019
 */

interface IActivityFactory {

    fun startGroupScreen(context: Context) = context.startActivity(Intent(context, GroupActivity::class.java))

    fun startMainScreen(context: Context) = context.startActivity(Intent(context, MainActivity::class.java))

    fun startProfileScreen(context: Context) = context.startActivity(Intent(context, ProfileActivity::class.java))
}