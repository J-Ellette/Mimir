package com.example.data.seed

data class ReadingPlanDay(
    val dayNumber: Int,
    val title: String,
    val passageId: String,
    val workId: String,
    val reflectionPrompt: String
)

data class ReadingPlan(
    val id: String,
    val title: String,
    val subtitle: String,
    val categoryBadge: String,
    val totalDaysLabel: String,
    val days: List<ReadingPlanDay>
)

data class NineWorldNode(
    val id: String,
    val name: String,
    val oldNorseName: String,
    val tier: String,
    val xFraction: Float,
    val yFraction: Float,
    val description: String,
    val inhabitants: String,
    val linkedPassageId: String
)

data class FamilyMemberNode(
    val id: String,
    val name: String,
    val role: String,
    val parents: String,
    val children: String,
    val sourceCitation: String
)

data class FamilyTreeGroup(
    val id: String,
    val title: String,
    val subtitle: String,
    val members: List<FamilyMemberNode>
)

object StudyAtlasData {

    val readingPlans = listOf(
        ReadingPlan(
            id = "plan_voluspa_7",
            title = "Völuspá in 7 Days",
            subtitle = "From primordial Ginnungagap to the green earth rising anew",
            categoryBadge = "Edduc Classic",
            totalDaysLabel = "7-Day Journey",
            days = listOf(
                ReadingPlanDay(1, "The Seeress Calls for Silence", "voluspa_1", "voluspa", "What ancient story or lineage feels foundational in your own life?"),
                ReadingPlanDay(2, "Ymir & the Yawning Void", "voluspa_3", "voluspa", "How does naming what is absent ('no sand, no sea') help imagine beginnings?"),
                ReadingPlanDay(3, "The Evergreen Ash Yggdrasil", "voluspa_19", "voluspa", "Reflect on what anchors your sense of order above changing seasons."),
                ReadingPlanDay(4, "The Pledge at Mímir's Well", "voluspa_28", "voluspa", "What have you been willing to give up in pursuit of genuine wisdom?"),
                ReadingPlanDay(5, "The Age of Axe and Wind", "voluspa_45", "voluspa", "Why do the Eddas treat broken kinship and oath-breaking as the true ruin?"),
                ReadingPlanDay(6, "The Second Earth Rises", "voluspa_59", "voluspa", "Where do you see quiet renewal emerging after difficult upheaval?"),
                ReadingPlanDay(7, "Snorri's Echo in Gylfaginning", "gylfaginning_15", "gylfaginning", "Compare poetic vision in Völuspá with Snorri's prose synthesis.")
            )
        ),
        ReadingPlan(
            id = "plan_gods_30",
            title = "The Gods: A 30-Day Intro",
            subtitle = "Curated milestones introducing Æsir, Vanir, Jötnar & Yggdrasil",
            categoryBadge = "Foundations",
            totalDaysLabel = "30-Day Path · 6 Key Milestones",
            days = listOf(
                ReadingPlanDay(1, "Ginnungagap & Niflheimr", "gylfaginning_4", "gylfaginning", "How do opposing forces of ice and fire shape creation?"),
                ReadingPlanDay(2, "Mímir's Well & Gjallarhorn", "gylfaginning_15", "gylfaginning", "Why must even the Allfather pay a price for counsel?"),
                ReadingPlanDay(3, "Kvasir & the Mead of Poetry", "skaldskaparmal_1", "skaldskaparmal", "What distinguishes technical skill from true poetic inspiration?"),
                ReadingPlanDay(4, "Loki's Challenge at Ægir's Hall", "lokasenna_22", "lokasenna", "What role does truth-telling mockery play inside a pantheon?"),
                ReadingPlanDay(5, "Odin's Ordeal on the Windy Tree", "havamal_138", "havamal", "Reflect on patience and endurance in learning difficult craft."),
                ReadingPlanDay(6, "Mímir's Head in Heimskringla", "heimskringla_4", "heimskringla", "How does Snorri transform myth into legendary history?")
            )
        ),
        ReadingPlan(
            id = "plan_njals_month",
            title = "Njáls saga in a Month",
            subtitle = "Law, loyalty, and fate across southern Iceland",
            categoryBadge = "Family Saga",
            totalDaysLabel = "30-Day Saga Study",
            days = listOf(
                ReadingPlanDay(1, "Njáll of Bergþórshváll", "njals_1", "njals_saga", "What makes quiet legal wisdom compelling alongside martial prowess?"),
                ReadingPlanDay(2, "Gunnarr Looks Back at Hlíðarendi", "njals_75", "njals_saga", "Have you ever loved a place or commitment so deeply you chose it despite the cost?")
            )
        ),
        ReadingPlan(
            id = "plan_ragnarok",
            title = "Themed Path: Ragnarök",
            subtitle = "Cosmic dissolution, oath-strife, and rebirth",
            categoryBadge = "Themed Path",
            totalDaysLabel = "4 Readings",
            days = listOf(
                ReadingPlanDay(1, "Primordial Beginning", "voluspa_3", "voluspa", "Trace the symmetry between opening void and ending sea."),
                ReadingPlanDay(2, "Axe-Time & Wolf-Time", "voluspa_45", "voluspa", "Consider why social ethics precede cosmic collapse."),
                ReadingPlanDay(3, "Eternally Green Earth", "voluspa_59", "voluspa", "What endures when old worlds pass away?")
            )
        ),
        ReadingPlan(
            id = "plan_odins_wisdom",
            title = "Themed Path: Odin's Wisdom",
            subtitle = "Guest-counsel, memory, and the price of runes",
            categoryBadge = "Themed Path",
            totalDaysLabel = "4 Readings",
            days = listOf(
                ReadingPlanDay(1, "At Every Doorway", "havamal_1", "havamal", "Practice attentive awareness before stepping into a new room."),
                ReadingPlanDay(2, "Man is the Joy of Man", "havamal_47", "havamal", "Reach out to a companion on your own road today."),
                ReadingPlanDay(3, "What Never Dies", "havamal_76", "havamal", "What reputation or kindness do you hope outlasts possessions?"),
                ReadingPlanDay(4, "Taking Up the Runes", "havamal_139", "havamal", "Note one hard-won skill that changed how you read the world.")
            )
        ),
        ReadingPlan(
            id = "plan_women_sagas",
            title = "Themed Path: Women of the Sagas",
            subtitle = "Agency, prophecy, and memory across Edda and Saga",
            categoryBadge = "Themed Path",
            totalDaysLabel = "3 Readings",
            days = listOf(
                ReadingPlanDay(1, "The Völva Commands the Gods", "voluspa_28", "voluspa", "Observe how the seeress holds moral authority even over Odin."),
                ReadingPlanDay(2, "Guðrún's Confession at Helgafell", "laxdaela_78", "laxdaela_saga", "Reflect on grief, pride, and honesty in Guðrún's twelve words."),
                ReadingPlanDay(3, "Swan-Maidens of Völundarkviða", "volundarkvida_1", "hero_lays", "Explore agency and fate in the opening of the heroic lay.")
            )
        )
    )

    val nineWorlds = listOf(
        NineWorldNode("w_asgard", "Ásgarðr", "Ásgarðr", "Upper Canopy", 0.50f, 0.14f, "Citadel of the Æsir gods, housing Valhöll, Gladsheimr, and Hliðskjálf.", "Óðinn, Frigg, Þórr, Týr, Heimdallr", "voluspa_1"),
        NineWorldNode("w_alfheim", "Álfheimr", "Álfheimr", "Upper Canopy", 0.24f, 0.24f, "Bright realm given as a tooth-gift to Freyr, home of the light elves (Ljósálfar).", "Freyr, Ljósálfar", "gylfaginning_15"),
        NineWorldNode("w_vanaheim", "Vanaheimr", "Vanaheimr", "Upper Canopy", 0.76f, 0.24f, "Homeland of the Vanir deities of fertility, sea-craft, and prophecy.", "Njörðr, Freyr, Freyja", "heimskringla_4"),
        NineWorldNode("w_midgard", "Miðgarðr", "Miðgarðr", "Middle Trunk", 0.50f, 0.44f, "The middle enclosure of humankind, ringed by the ocean and Jörmungandr.", "Humankind, Saga heroes", "voluspa_19"),
        NineWorldNode("w_jotunheim", "Jötunheimr", "Jǫtunheimar", "Eastern Outlands", 0.80f, 0.48f, "Wild mountains and frost-halls beyond the eastern rivers, home of the Jötnar.", "Mímir's root, Skrýmir, Skaði", "gylfaginning_15"),
        NineWorldNode("w_svartalfheim", "Svartálfaheimr", "Svartálfaheimr / Niðavellir", "Subterranean Roots", 0.22f, 0.52f, "Deep caverns where master dwarf-smiths forged Mjölnir, Gungnir, and Gleipnir.", "Brokkr, Sindri, Ívaldi's sons", "skaldskaparmal_1"),
        NineWorldNode("w_muspell", "Múspellsheimr", "Múspell", "Primordial South", 0.76f, 0.72f, "Land of primordial fire guarded by Surtr with his flaming sword.", "Surtr, Sons of Múspell", "gylfaginning_4"),
        NineWorldNode("w_niflheim", "Niflheimr", "Niflheimr", "Primordial North", 0.24f, 0.72f, "Region of mist and frost housing the seething cauldron-spring Hvergelmir.", "Níðhöggr, Hvergelmir", "gylfaginning_4"),
        NineWorldNode("w_hel", "Hel", "Helheimr", "Deepest Root", 0.50f, 0.86f, "Cold subterranean hall beyond Gjöll bridge ruled by Hel, daughter of Loki.", "Hel, Baldr (until Ragnarök)", "voluspa_45")
    )

    val familyTrees = listOf(
        FamilyTreeGroup(
            id = "tree_aesir_vanir",
            title = "Æsir & Vanir Lineage",
            subtitle = "Cosmological kinships attested in Prose & Poetic Edda",
            members = listOf(
                FamilyMemberNode("m_borr", "Búri -> Borr & Bestla", "Primordial Ancestors", "Licked from salty rime-stones by Auðumbla", "Óðinn, Vili, Vé", "Gylfaginning 6"),
                FamilyMemberNode("m_odin", "Óðinn & Frigg", "Sovereigns of Ásgarðr", "Borr & Bestla (Óðinn); Fjörgynn (Frigg)", "Baldr, Höðr; (Thor by Jörð)", "Gylfaginning 9, 20"),
                FamilyMemberNode("m_njord", "Njörðr & Skaði / Sister", "Vanir Bridge to Ásgarðr", "Vanaheimr lineage", "Freyr & Freyja", "Lokasenna 36; Ynglinga saga 4")
            )
        ),
        FamilyTreeGroup(
            id = "tree_jotnar",
            title = "Ymir, Loki & the Jötnar",
            subtitle = "Primordial giants and Ragnarök adversaries",
            members = listOf(
                FamilyMemberNode("m_ymir", "Ymir (Aurgelmir)", "First Frost-Jötunn", "Born from Élivágar venom & Múspell sparks", "Thrúðgelmir -> Bergelmir", "Vafþrúðnismál 29–35"),
                FamilyMemberNode("m_loki", "Loki Laufeyjarson", "Trickster of mixed parentage", "Fárbauti (Jötunn) & Laufey", "By Angrboða: Fenrir, Jörmungandr, Hel; By Sigyn: Narfi & Váli", "Gylfaginning 33–34")
            )
        ),
        FamilyTreeGroup(
            id = "tree_volsungs",
            title = "The Völsung Heroic Line",
            subtitle = "Sigi through Sigurðr Fáfnisbani",
            members = listOf(
                FamilyMemberNode("m_sigmund", "Völsungr -> Sigmundr & Hjördís", "Heroic Kings", "Rerir (Völsungr); Eylimi (Hjördís)", "Sigurðr, Helgi Hundingsbani, Sinfjötli", "Völsunga saga 1–12"),
                FamilyMemberNode("m_sigurd", "Sigurðr & Guðrún Gjúkadóttir", "Dragon-Slayer & Burgundian Queen", "Sigmundr & Hjördís", "Svanhildr (by Guðrún), Áslaug (by Brynhildr)", "Völsunga saga 28–40")
            )
        )
    )
}
