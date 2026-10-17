package com.cinecompare.aggregator.base.models

enum class Platform(
    val displayName: String,
    val packageName: String,
    val baseUrl: String,
    val primaryColor: Long
) {
    BOOK_MY_SHOW(
        displayName = "BookMyShow",
        packageName = "com.bt.bms",
        baseUrl = "https://in.bookmyshow.com",
        primaryColor = 0xFFCC0A2B
    ),
    DISTRICT(
        displayName = "District",
        packageName = "com.district.app",
        baseUrl = "https://www.district.in",
        primaryColor = 0xFF3D1D8E
    ),
    TICKET_NEW(
        displayName = "TicketNew",
        packageName = "com.ticketnew.moviebooking",
        baseUrl = "https://www.ticketnew.com",
        primaryColor = 0xFF0D47A1
    ),
    JUSTICKETS(
        displayName = "JustTickets",
        packageName = "com.justickets.client",
        baseUrl = "https://www.justickets.in",
        primaryColor = 0xFF1B5E20
    ),
    MAKEMYTRIP(
        displayName = "MakeMyTrip",
        packageName = "com.makemytrip",
        baseUrl = "https://www.makemytrip.com/movies",
        primaryColor = 0xFFD32F2F
    )
}
