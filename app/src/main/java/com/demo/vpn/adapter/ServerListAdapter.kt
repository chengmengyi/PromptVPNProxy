package com.demo.vpn.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.demo.vpn.R
import com.demo.vpn.bean.Server0515Bean
import com.demo.vpn.server.Connect0515Util
import com.demo.vpn.server.Server0515Util
import com.demo.vpn.util.get0515ServerLogo
import kotlinx.android.synthetic.main.item_server.view.*

class ServerListAdapter(
    private val context: Context,
    private val clickCallback:(bean:Server0515Bean)->Unit
):Adapter<ServerListAdapter.ServerView>() {
    private val list= arrayListOf<Server0515Bean>()

    init {
        list.add(Server0515Bean())
        list.addAll(Server0515Util.allServerList)
    }

    inner class ServerView(view:View):ViewHolder(view){
        init {
            view.setOnClickListener {
                clickCallback.invoke(list[layoutPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerView {
        return ServerView(LayoutInflater.from(context).inflate(R.layout.item_server,parent,false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ServerView, position: Int) {
        with(holder.itemView){
            val server0515Bean = list[position]
            tv_name.text=if (server0515Bean.isFast()){
                server0515Bean.country
            }else{
                "${server0515Bean.country} - ${server0515Bean.city}"
            }
            iv_logo.setImageResource(get0515ServerLogo(server0515Bean.country))

            val select = server0515Bean.ip == Connect0515Util.currentServer.ip
            iv_select.isSelected=select
            item_layout.isSelected=select
        }
    }
}