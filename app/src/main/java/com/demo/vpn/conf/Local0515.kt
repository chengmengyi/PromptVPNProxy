package com.demo.vpn.conf

import com.demo.vpn.bean.Server0515Bean

object Local0515 {

    val localServerList= arrayListOf(
        Server0515Bean(
            ip = "69.50.92.37",
            port = 1740,
            pwd = "oA8bw0qkNaiSR5eO",
            account = "chacha20-ietf-poly1305",
            country = "United States",
            city = "denver",
        ),
        Server0515Bean(
            ip = "162.251.61.100",
            port = 1740,
            pwd = "oA8bw0qkNaiSR5eO",
            account = "chacha20-ietf-poly1305",
            country = "United States",
            city = "chicago",
        ),
    )

}