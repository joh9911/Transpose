package com.example.transpose.data.repository

class MusicCategoryRepository {

    private val youtubePlaylistBaseUrl = "https://www.youtube.com/playlist?list="

    private val thisYearMusicId = arrayOf(
        "RDCLAK5uy_kFoxHZo_PEVrqVnwKkeucGn4ldSyKHD8A",
        "RDCLAK5uy_lZZ5gBJBR1AYziS_onNkE2m18Peg042fI",
        "RDCLAK5uy_nwmWfUayX21xwr1VB72jjBqDPDxGm9zc0",
        "RDCLAK5uy_mOWD5gzPMEI5Ip97H4ivxpcxE7uglskHo",
        "RDCLAK5uy_kaV_BsACArm2vCjj74y871plTk9F8RIDA",
        "RDCLAK5uy_kvnBKwOS6J1tBqsNmdISJEhJ2a-RhNliI",
        "RDCLAK5uy_keUi-XamVp1RPNUoJ-BPIQy3_DusjC5Mg",
        "RDCLAK5uy_kV8LcjsV6_R-y5ncz_SOyoo1BfCRs79wM",
        "RDCLAK5uy_l5AiprVpfhWBQvu-76GJFd1_T2HZoYZWs",
        "RDCLAK5uy_ln2vAJIQILond713LQLKfxSruLhFB1lIM",
        "RDCLAK5uy_kcIyRk6eceGdn0Zjijw0ARVbHCubof1zM",
        "RDCLAK5uy_k4vBNV3dzeyoMlSagQydjsZ-uyo1x1fIU")

    private val todayHotMusicId = arrayOf(
        "RDCLAK5uy_l7wbVbkC-dG5fyEQQsBfjm_z3dLAhYyvo",
        "RDCLAK5uy_m9ty3WvAucm7-5KsKdro9_HnocE8LSS9o",
        "RDCLAK5uy_kRRqnSpfrRZ9OJyTB2IE5WsAqYluG0uYo",
        "RDCLAK5uy_lYPvoz4gPFnKMw_BFojpMk7xRSIqVBkEE",
        "RDCLAK5uy_l6DCR35xfT9bfeUqP7-uw6kWApcfYeDPY",
        "RDCLAK5uy_k6pZ82Gha0sopanWffXo4iMBVaGR7jQaE",
        "RDCLAK5uy_mMRkzfvFXzNQbSl3K-hE_FJ7g8TqMtSlo",
        "RDCLAK5uy_mjCKq8hnUQJqul0W6YW6x2Ep4P67jQ5Po",
        "RDCLAK5uy_l0nFcbRh2kbs27gleqzu364A9rN-D8Ib8",
        "RDCLAK5uy_ky-kXJCA_i0Gf0k6iNxsRHBhAgugAN8-g",
        "RDCLAK5uy_lBfTSdVdgXeN399Mxt2L3C6hLarC1aaN0")

    private val latestMusicId = arrayOf(
        "RDCLAK5uy_lS4dqGRHszluFAbLsV-sHJCqULtBm2Gfw",
        "RDCLAK5uy_mVBAam6Saaqa_DeJRxGkawqqxwPTBrGXM",
        "RDCLAK5uy_nkjcHIQK0Hgf7ihU25uJc0CEokeGkSNxA",
        "RDCLAK5uy_mWqhoadUUp9crhEkmZZkdExj7YpBuFBEQ",
        "RDCLAK5uy_n0f4tLAkNM233wO0yiTEI7467ovnaGbR8",
        "RDCLAK5uy_lN9xj1RQGmBltmvrzTVHMg-vyVt594KYU",
        "RDCLAK5uy_kITLp-IuXw_winp1mnN9PSNatPBiAK52A",
        "RDCLAK5uy_mn7OLm9QvyB230t7RtLWt0BvUmFVlQ-Hc",
        "RDCLAK5uy_lz175mC_wAtZHK0hbDqLrxb5J28QbUznQ",
        "RDCLAK5uy_nppUVicPb1PRbUZmVEMhqgvyFz33Il4pE"
    )

    private val bestAtmosphereMusicId = arrayOf(
        "RDCLAK5uy_mA88hxo-cmI0-WaaRH8Bb2k0x2NptOPqM",
        "RDCLAK5uy_meEBX-iIBwtXBhkeWzwX6njohWnpMijP8",
        "RDCLAK5uy_kT-sIJz2O-hpkxwjosN2hMt9Y5xevcPYI",
        "RDCLAK5uy_lNJA7PB9DAQEdtTnfuKaC2XEOAE1OoX50",
        "RDCLAK5uy_lv6V83HLaJMQDx8YFtfSAaZ6GGvSqI6PE",
        "RDCLAK5uy_mL0lDqxdKwRtBbzva3yVjVy-BZ9L7KX5I",
        "RDCLAK5uy_nXDnxSmhez06eAnjfT2pWjSpp-p2VBv54",
        "RDCLAK5uy_ksGphJr7YduIL-vDvJBUJQ2_JCYnCkaYI",
        "RDCLAK5uy_kDBL_tFOUos7q3SOifZrMHXKwuebdzf7I"
    )

    private val bestSituationMusicId = arrayOf(
        "RDCLAK5uy_msV9Vc8q_guumIXgLkzYs58uBZHVVBPtE",
        "RDCLAK5uy_lFgjDM5dWvoq0_wkEqx4_M43Nk6wXviaM",
        "RDCLAK5uy_nEcCeflWNpzQNRExtAKjKkkX96wjom9Nc",
        "RDCLAK5uy_kskrFUGb5Tnz3-x4wyK9Q5j8RgfwQvq4k",
        "RDCLAK5uy_kjKtb_RC7LRbxiEmSIzZqJRVcYm8U9KMc",
        "RDCLAK5uy_mS7UhvWzUZdjauupjE5JO6VCn-CCwaRoI",
        "RDCLAK5uy_krjFmKbzWzkGvhqkYvvNnUbdrHy0QN1S8",
        "RDCLAK5uy_kQ09S7a68znbjr7h26ur1RJb2tCXDlruY"
    )

    private val popularTop100MusicId = "PL4fGSI1pDJn6jXS_Tv_N9B8Z0HTRVJE0m"


    private val koreanPopularSongsId = "PL4fGSI1pDJn6jXS_Tv_N9B8Z0HTRVJE0m"

    private val americanPopularSongsId = "PL4fGSI1pDJn6O1LS0XSdF3RyO0Rq_LDeI"

    private val indonesiaPopularSongsId = "PL4fGSI1pDJn5ObxTlEPlkkornHXUiKX1z"

    private val globalPopularSongsId = "PL4fGSI1pDJn6puJdseH2Rt9sMvt9E2M4i"

    private val unitedKingdomPopularSongsId = "PL4fGSI1pDJn6_f5P3MnzXg9l3GDfnSlXa"

    private val japanesePopularSongsId = "PL4fGSI1pDJn4-UIb6RKHdxam-oAUULIGB"

    private val australiaPopularSongsId= "PL4fGSI1pDJn7xvYy-bP6UFeG5tITQgScd"

    private val spainPopularSongsId = "PL4fGSI1pDJn4jhQB4kb9M36dvVmJQPt4T"

    val nationalPlaylistIds = arrayOf(koreanPopularSongsId, americanPopularSongsId,
        indonesiaPopularSongsId, unitedKingdomPopularSongsId, japanesePopularSongsId, globalPopularSongsId,
        australiaPopularSongsId, spainPopularSongsId
    )

    val nationalPlaylistUrls = nationalPlaylistIds.map { id ->
        youtubePlaylistBaseUrl + id
    }.toTypedArray()

    val recommendPlaylistChannelId = "UCvt5p3A11M8zd8iJPCC5XvQ"

    val typedPlaylistChannelId = "UCSGC87iX0QhnIfUOI_B_Rdg"



    fun getThisYearMusicIdArray(): Array<String> {
        return this.thisYearMusicId
    }

    fun getTodayHotMusicIdArray(): Array<String>{
        return this.todayHotMusicId
    }

    fun getLatestMusicIdArray(): Array<String>{
        return this.latestMusicId
    }

    fun getBestAtmosphereMusicIdArray(): Array<String>{
        return this.bestAtmosphereMusicId
    }

    fun getBestSituationMusicIdArray(): Array<String>{
        return this.bestSituationMusicId
    }

    fun getPopularTop100MusicId(): String{
        return this.popularTop100MusicId
    }

    fun getMusicCategorySequence(musicId: String): Int{
        return if (todayHotMusicId.contains(musicId)) 0
        else if (latestMusicId.contains(musicId)) 1
        else if (bestAtmosphereMusicId.contains(musicId)) 2
        else 3
    }

}