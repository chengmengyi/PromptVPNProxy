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
    "prompt_show":5,
    "prompt_click":30,
    "prompt_open":[
        {
            "prompt_source":"admob",
            "prompt_id":"ca-app-pub-3940256099942544/3419835294x",
            "prompt_format":"open",
            "prompt_ppro":2
        },
        {
            "prompt_source":"admob",
            "prompt_id":"ca-app-pub-3940256099942544/3419835294x",
            "prompt_format":"open",
            "prompt_ppro":3
        },
       {
            "prompt_source":"admob",
            "prompt_id":"ca-app-pub-3940256099942544/3419835294",
            "prompt_format":"open",
            "prompt_ppro":1
        }
    ],
    "prompt_cil":[
        {
            "prompt_source":"admob",
            "prompt_id":"ca-app-pub-3940256099942544/2247696110",
            "prompt_format":"native",
            "prompt_ppro":2
        }
    ],
    "prompt_but":[
        {
            "prompt_source":"admob",
            "prompt_id":"ca-app-pub-3940256099942544/2247696110",
            "prompt_format":"native",
            "prompt_ppro":3
        }
    ],
    "prompt_intal":[
        {
            "prompt_source":"admob",
            "prompt_id":"ca-app-pub-3940256099942544/1033173712",
            "prompt_format":"interstitial",
            "prompt_ppro":2
        }
    ],
    "prompt_goback":[
        {
            "prompt_source":"admob",
            "prompt_id":"ca-app-pub-3940256099942544/1033173712",
            "prompt_format":"interstitial",
            "prompt_ppro":1
        }
    ]
}"""
}