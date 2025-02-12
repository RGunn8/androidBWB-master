package com.learning.leap.bwb.download

sealed class DownloadScreenState{

    object Starting : DownloadScreenState()
    data class Downloading(val progress:Int): DownloadScreenState()
    object Error : DownloadScreenState()

    object Success: DownloadScreenState()
}
