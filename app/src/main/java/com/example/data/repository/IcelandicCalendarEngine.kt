package com.example.data.repository

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class IcelandicMonthInfo(
    val name: String,
    val meaning: String,
    val halfYear: String,
    val dayOfMonth: Int,
    val weekNumberInSeason: Int,
    val seasonalLore: String
)

data class NorseObservance(
    val id: String,
    val name: String,
    val oldNorseName: String,
    val approxGregorian: String,
    val icelandicMonth: String,
    val attestationNote: String,
    val description: String,
    val linkedPassageId: String,
    val linkedWorkId: String
)

object IcelandicCalendarEngine {

    private data class MonthDef(
        val name: String,
        val meaning: String,
        val halfYear: String,
        val startMonth: Int,
        val startDay: Int,
        val lore: String
    )

    // Traditional Old Icelandic 30-day Misseri months mapped to modern astronomical/calendar anchors
    private val months = listOf(
        MonthDef("Gormánuður", "Slaughter month", "Vetrarmisseri (Winter)", 10, 21, "Begins with Winter Nights (Vetrnætr), marking the close of autumn harvest and start of the winter half-year."),
        MonthDef("Ýlir", "Yule-tide / howling month", "Vetrarmisseri (Winter)", 11, 20, "Darkening days leading toward midwinter solstice and the Jól season."),
        MonthDef("Mörsugur", "Fat-sucking / midwinter scarcity", "Vetrarmisseri (Winter)", 12, 20, "Deepest winter nights; includes the solstice and early Jól gatherings."),
        MonthDef("Þorri", "Frozen snow / Þorri month", "Vetrarmisseri (Winter)", 1, 19, "Mid-January to February; heralded by Þorrablót and hospitality feasts."),
        MonthDef("Góa", "Thin snow / late winter", "Vetrarmisseri (Winter)", 2, 18, "Named in Orkneyinga saga & Hversu Noregr byggðisk; honored on Konudagur."),
        MonthDef("Einmánuður", "Single / lone month", "Vetrarmisseri (Winter)", 3, 20, "The final month of winter before the summer half-year begins."),
        MonthDef("Harpa", "Spring / maiden of summer", "Sumarmisseri (Summer)", 4, 19, "Opens Sumardagurinn fyrsti (First Day of Summer) in the Old Icelandic reckoning."),
        MonthDef("Skerpla", "Dry / lean grazing month", "Sumarmisseri (Summer)", 5, 19, "Early pastures green as snow melts off highland valleys."),
        MonthDef("Sólmánuður", "Sun month", "Sumarmisseri (Summer)", 6, 18, "Bright northern nights of Náttleysi around Midsummer and the Alþingi assembly."),
        MonthDef("Heyannir", "Hay-making month", "Sumarmisseri (Summer)", 7, 18, "Vital summer hay harvest across Icelandic farmsteads like Hlíðarendi."),
        MonthDef("Tvímánuður", "Double / harvest month", "Sumarmisseri (Summer)", 8, 17, "Late summer reaping before autumn sheep roundups."),
        MonthDef("Haustmánuður", "Autumn month", "Sumarmisseri (Summer)", 9, 16, "Final month of summer leading to the autumn equinox and Winter Nights.")
    )

    val observances = listOf(
        NorseObservance(
            id = "obs_vetrnaetr",
            name = "Winter Nights (Vetrnætr)",
            oldNorseName = "Vetrnætr / Álfablót",
            approxGregorian = "Mid-October (Start of Gormánuður)",
            icelandicMonth = "Gormánuður",
            attestationNote = "Attested in Heimskringla, Gísla saga, and Austfirðinga sögur",
            description = "Marks the turning of the year into winter (Vetrarmisseri), welcoming the dísir and ancestors at household gatherings.",
            linkedPassageId = "havamal_1",
            linkedWorkId = "havamal"
        ),
        NorseObservance(
            id = "obs_yule",
            name = "Yule (Jól)",
            oldNorseName = "Jól / Hökunótt",
            approxGregorian = "Midwinter (Ýlir – Mörsugur)",
            icelandicMonth = "Mörsugur",
            attestationNote = "Attested in Hákonar saga góða (Heimskringla) & Haraldskvæði",
            description = "Midwinter feast ('at drekka jól') for peace, good harvest (ár ok friðr), and remembrance toasts across the darkening weeks.",
            linkedPassageId = "havamal_47",
            linkedWorkId = "havamal"
        ),
        NorseObservance(
            id = "obs_thorrablot",
            name = "Þorrablót",
            oldNorseName = "Þorrablót",
            approxGregorian = "Late January (Start of Þorri)",
            icelandicMonth = "Þorri",
            attestationNote = "Attested in Hversu Noregr byggðisk & Orkneyinga saga",
            description = "Midwinter endurance gathering in the heart of Þorri, celebrating preserved fare and storytelling by the hearth.",
            linkedPassageId = "njals_1",
            linkedWorkId = "njals_saga"
        ),
        NorseObservance(
            id = "obs_first_summer",
            name = "First Day of Summer",
            oldNorseName = "Sumardagurinn fyrsti",
            approxGregorian = "Late April (Start of Harpa)",
            icelandicMonth = "Harpa",
            attestationNote = "Attested in Ari Þorgilsson's Íslendingabók & medieval Icelandic calendars",
            description = "Begins the Summer Misseri when green shoots return—paired with Völuspá's vision of the evergreen ash and second earth.",
            linkedPassageId = "voluspa_59",
            linkedWorkId = "voluspa"
        ),
        NorseObservance(
            id = "obs_midsummer",
            name = "Midsummer & Alþingi",
            oldNorseName = "Miðsumar / Alþingi",
            approxGregorian = "Late June (Sólmánuður)",
            icelandicMonth = "Sólmánuður",
            attestationNote = "Attested in Njáls saga, Grágás, and Snorra Edda",
            description = "High summer under the midnight sun when Icelanders gathered at Þingvellir for law, poetry, and peaceful settlement.",
            linkedPassageId = "njals_75",
            linkedWorkId = "njals_saga"
        )
    )

    fun toIcelandicDate(date: LocalDate): IcelandicMonthInfo {
        val year = date.year
        // Build ordered anchor dates spanning previous year's Gormánuður to next year's
        val candidates = mutableListOf<Pair<LocalDate, MonthDef>>()
        for (yr in (year - 1)..(year + 1)) {
            for (m in months) {
                val anchor = LocalDate.of(yr, m.startMonth, m.startDay)
                candidates.add(anchor to m)
            }
        }
        val sorted = candidates.sortedBy { it.first }
        val active = sorted.lastOrNull { !it.first.isAfter(date) } ?: sorted.first()
        val dayOfMonth = (ChronoUnit.DAYS.between(active.first, date).toInt() + 1).coerceIn(1, 31)
        val seasonStartYear = if (date.monthValue >= 10 && date.dayOfMonth >= 21 || date.monthValue < 4 || (date.monthValue == 4 && date.dayOfMonth < 19)) {
            if (date.monthValue >= 10) date.year else date.year - 1
        } else {
            date.year
        }
        val seasonStart = if (active.second.halfYear.startsWith("Vetr")) {
            LocalDate.of(seasonStartYear, 10, 21)
        } else {
            LocalDate.of(date.year, 4, 19)
        }
        val weekInSeason = ((ChronoUnit.DAYS.between(seasonStart, date).toInt().coerceAtLeast(0)) / 7) + 1

        return IcelandicMonthInfo(
            name = active.second.name,
            meaning = active.second.meaning,
            halfYear = active.second.halfYear,
            dayOfMonth = dayOfMonth,
            weekNumberInSeason = weekInSeason,
            seasonalLore = active.second.lore
        )
    }
}
