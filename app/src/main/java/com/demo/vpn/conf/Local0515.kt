package com.demo.vpn.conf

import com.demo.vpn.bean.Server0515Bean

object Local0515 {

    val localServerList= arrayListOf(
        Server0515Bean(
            ip = "100.223.52.78",
            port = 100,
            pwd = "123456",
            account = "chacha20-ietf-poly1305",
            country = "Japan",
            city = "LocalTokyo",
        ),
        Server0515Bean(
            ip = "100.223.52.77",
            port = 100,
            pwd = "123456",
            account = "chacha20-ietf-poly1305",
            country = "UnitedStates",
            city = "LocalNewYork",
        ),
    )

}