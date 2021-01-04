package ru.skillbranch.skillarticles.extensions

import android.view.View
import android.view.ViewGroup
import androidx.core.view.*
import androidx.navigation.NavDestination
import com.google.android.material.bottomnavigation.BottomNavigationView


fun View.setMarginOptionally(
    left: Int = marginLeft,
    top: Int = marginTop,
    right: Int = marginRight,
    bottom: Int = marginBottom
) {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(left, top, right, bottom)
    this.requestLayout()
}

fun View.setPaddingOptionally(
    left: Int = paddingLeft,
    top: Int = paddingTop,
    right: Int = paddingRight,
    bottom: Int = paddingBottom
) {
    // ???
    setPadding(left, top, right, bottom)
}

fun View.setPadding(padding: Int) {
    setPadding(padding, padding, padding, padding)
}

fun View.selectDestination(destination: NavDestination) {
    if (destination.parent?.parent == null) {
        if (this is BottomNavigationView) {
            menu.children
                .filter { menuItem -> menuItem.itemId == destination.id }
                .forEach { menuItem -> menuItem.isChecked = true }
        }
    }
}

fun BottomNavigationView.selectItem(itemId: Int?){
    itemId?: return
    for (item in menu.iterator()) {
        if(item.itemId == itemId) {
            item.isChecked = true
            break
        }
    }
}