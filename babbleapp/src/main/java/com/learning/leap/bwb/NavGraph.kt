package com.learning.leap.bwb

import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph

@RootNavGraph
@NavGraph
annotation class DownloadNavGraph(
    val start: Boolean = false
)

@RootNavGraph
@NavGraph
annotation class HomeNavGraph(
    val start: Boolean = false
)