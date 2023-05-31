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


    const val localAd234="""{
    "prompt_show":30,
    "prompt_click":5,
    "prompt_open":[
        {
            "prompt_source":"admob",
            "prompt_id":"ca-app-pub-2618006020231520/2088781481",
            "prompt_format":"open",
            "prompt_ppro":2
        }
    ],
    "prompt_cil":[
        {
            "prompt_source":"admob",
            "prompt_id":"ca-app-pub-2618006020231520/9341457682",
            "prompt_format":"native",
            "prompt_ppro":2
        }
    ],
    "prompt_but":[
        {
            "prompt_source":"admob",
            "prompt_id":"ca-app-pub-2618006020231520/9775699815",
            "prompt_format":"native",
            "prompt_ppro":3
        }
    ],
    "prompt_intal":[
        {
            "prompt_source":"admob",
            "prompt_id":"ca-app-pub-2618006020231520/9439344811",
            "prompt_format":"interstitial",
            "prompt_ppro":2
        }
    ],
    "prompt_goback":[
        {
            "prompt_source":"admob",
            "prompt_id":"ca-app-pub-2618006020231520/2962879450",
            "prompt_format":"interstitial",
            "prompt_ppro":1
        }
    ]
}
"""
}