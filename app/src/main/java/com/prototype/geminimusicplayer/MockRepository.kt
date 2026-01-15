package com.prototype.geminimusicplayer

class MockRepository {
  fun getTracks(): List<Track> {
    return listOf(
      Track(
        id = "0",
        title = "Black Friday (pretty like the sun)",
        artist = "Lost Frequencies, Tom Odell, Poppy Baskcomb",
        coverResId = R.drawable.black_friday_cover_art,
        audioResId = 0,
        duration = "5:11"
      ),
      Track(
        id = "1",
        title = "Chilled to Zero",
        artist = "Chill Cow",
        coverResId = R.drawable.ic_cover_chilled_to_zero,
        audioResId = R.raw.chilled_to_zero,
        duration = "1:38"
      ),
      Track(
        id = "2",
        title = "Future Worlds",
        artist = "The Martians",
        coverResId = R.drawable.ic_cover_future_worlds,
        audioResId = R.raw.future_worlds,
        duration = "1:58"
      ),
      Track(
        id = "3",
        title = "The Coffee Lounge",
        artist = "Barista Beats",
        coverResId = R.drawable.ic_cover_coffee_lounge,
        audioResId = R.raw.the_coffee_lounge,
        duration = "1:56"
      )
    )
  }
}