/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package ie.equalit.ceno.home.topsites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ie.equalit.ceno.R
import ie.equalit.ceno.home.sessioncontrol.TopSiteInteractor
import mozilla.components.feature.top.sites.TopSite

class TopSitesAdapter(
    private val viewLifecycleOwner: LifecycleOwner,
    private val interactor: TopSiteInteractor
) : ListAdapter<TopSite, TopSiteItemViewHolder>(TopSitesDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopSiteItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.top_site_item, parent, false)
        return TopSiteItemViewHolder(view, viewLifecycleOwner, interactor)
    }

    override fun onBindViewHolder(holder: TopSiteItemViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    override fun onBindViewHolder(
        holder: TopSiteItemViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNullOrEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            when (payloads[0]) {
                is TopSite -> {
                    holder.bind((payloads[0] as TopSite), position)
                }
            }
        }
    }

    internal object TopSitesDiffCallback : DiffUtil.ItemCallback<TopSite>() {
        override fun areItemsTheSame(oldItem: TopSite, newItem: TopSite) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TopSite, newItem: TopSite) =
            oldItem.id == newItem.id && oldItem.title == newItem.title && oldItem.url == newItem.url

        override fun getChangePayload(oldItem: TopSite, newItem: TopSite): Any? {
            return if (oldItem.id == newItem.id && oldItem.url == newItem.url && oldItem.title != newItem.title) {
                newItem
            } else null
        }
    }
}
