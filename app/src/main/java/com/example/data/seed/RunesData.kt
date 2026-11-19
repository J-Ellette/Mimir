package com.example.data.seed

enum class RuneAlphabet(val title: String, val era: String, val summary: String) {
    ELDER_FUTHARK(
        "Elder Futhark",
        "c. 150–800 CE · Proto-Norse & Proto-Germanic",
        "24 runes arranged in three ættir (groups of eight). Used across early Germanic inscriptions like the Kylver Stone and Gallehus Horns."
    ),
    YOUNGER_FUTHARK(
        "Younger Futhark",
        "c. 800–1150 CE · Viking Age Old Norse",
        "16 streamlined runes used on thousands of Viking Age stones (Jelling, Rök). Each rune covers multiple related phonemes."
    ),
    ANGLO_SAXON_FUTHORC(
        "Anglo-Saxon Futhorc",
        "c. 5th–11th c. · Old English & Frisian",
        "Expanded to 29–33 runes to represent Old English vowel shifts, attested in the Old English Rune Poem and on the Franks Casket."
    )
}

data class RuneItem(
    val id: String,
    val alphabet: RuneAlphabet,
    val glyph: String,
    val name: String,
    val soundValue: String,
    val meaning: String,
    val aett: String,
    val lore: String
)

data class RunestoneStep(
    val runes: String,
    val transliteration: String,
    val normalizedOldNorse: String,
    val englishTranslation: String,
    val grammarNote: String
)

data class RunestoneInscription(
    val id: String,
    val title: String,
    val locationAndDate: String,
    val alphabetUsed: String,
    val overview: String,
    val steps: List<RunestoneStep>
)

object RunesData {

    val elderFuthark = listOf(
        RuneItem("ef_1", RuneAlphabet.ELDER_FUTHARK, "ᚠ", "Fehu", "/f/", "Cattle, mobile wealth", "Freyr's Ætt", "Attested in the Old Norwegian/Icelandic Rune Poems: wealth is a source of discord among kin yet sustains generosity."),
        RuneItem("ef_2", RuneAlphabet.ELDER_FUTHARK, "ᚢ", "Uruz", "/u, uː/", "Aurochs, primal strength", "Freyr's Ætt", "Named for the wild European bison; represents untamed vitality and rain/drizzle ('úr') in later Nordic rune poems."),
        RuneItem("ef_3", RuneAlphabet.ELDER_FUTHARK, "ᚦ", "Thurisaz", "/θ, ð/", "Jötunn (giant), thorn", "Freyr's Ætt", "In Old English called Þorn ('thorn'); in Scandinavian poems, Þurs ('giant'), source of strife."),
        RuneItem("ef_4", RuneAlphabet.ELDER_FUTHARK, "ᚨ", "Ansuz", "/a, aː/", "An Áss (god), breath/mouth", "Freyr's Ætt", "Linked to Odin as god of speech, poetry, and wise counsel ('Óss' in Younger Futhark)."),
        RuneItem("ef_5", RuneAlphabet.ELDER_FUTHARK, "ᚱ", "Raido", "/r/", "Ride, journey", "Freyr's Ætt", "Riding is hard on horses and swift on the road; connects travel with ordered movement."),
        RuneItem("ef_6", RuneAlphabet.ELDER_FUTHARK, "ᚲ", "Kaunan", "/k/", "Torch, ulcer/flame", "Freyr's Ætt", "Represents illumination inside the hall ('cen' = torch in Old English) or affliction ('kaun' = sore in Norse)."),
        RuneItem("ef_7", RuneAlphabet.ELDER_FUTHARK, "ᚷ", "Gebo", "/g/", "Gift, generosity", "Freyr's Ætt", "Reciprocal gift-exchange bound hosts and guests together in Germanic society (cf. Hávamál 41–44)."),
        RuneItem("ef_8", RuneAlphabet.ELDER_FUTHARK, "ᚹ", "Wunjo", "/w/", "Joy, bliss", "Freyr's Ætt", "Harmony within the kindred; dropped in Younger Futhark as initial /w/ shifted to /v/."),
        RuneItem("ef_9", RuneAlphabet.ELDER_FUTHARK, "ᚺ", "Hagalaz", "/h/", "Hailstone", "Heimdallr's Ætt", "Coldest of grains falling from sky—sudden disruption that melts into spring water."),
        RuneItem("ef_10", RuneAlphabet.ELDER_FUTHARK, "ᚾ", "Naudiz", "/n/", "Need, constraint", "Heimdallr's Ætt", "Tightness across the chest; survival through resourcefulness under hardship."),
        RuneItem("ef_11", RuneAlphabet.ELDER_FUTHARK, "ᛁ", "Isaz", "/i, iː/", "Ice", "Heimdallr's Ætt", "Bright as glass, slippery bridge over winter rivers."),
        RuneItem("ef_12", RuneAlphabet.ELDER_FUTHARK, "ᛃ", "Jera", "/j/", "Good year, harvest", "Heimdallr's Ætt", "Seasonal cycle of planting and autumn plenty."),
        RuneItem("ef_13", RuneAlphabet.ELDER_FUTHARK, "ᛇ", "Eihwaz", "/æː, iː/", "Yew tree", "Heimdallr's Ætt", "Evergreen yew used for bows—endurance between life and winter."),
        RuneItem("ef_14", RuneAlphabet.ELDER_FUTHARK, "ᛈ", "Pertho", "/p/", "Lot-cup / game-board", "Heimdallr's Ætt", "Laughter and chance at the mead-bench (OE Rune Poem)."),
        RuneItem("ef_15", RuneAlphabet.ELDER_FUTHARK, "ᛉ", "Algiz", "/z/ (final -R)", "Elk-sedge, protection", "Heimdallr's Ætt", "In Proto-Norse marked final voiced /z/ (> -r), later reversed as Yr in Younger Futhark. Historical sources link it to sedge grass that wounds when grasped rashly."),
        RuneItem("ef_16", RuneAlphabet.ELDER_FUTHARK, "ᛊ", "Sowilo", "/s/", "Sun", "Heimdallr's Ætt", "Light and guidance for seafarers; called Sól ('sun') in later Nordic poems."),
        RuneItem("ef_17", RuneAlphabet.ELDER_FUTHARK, "ᛏ", "Tiwaz", "/t/", "God Týr", "Týr's Ætt", "Named for Týr, god of sworn oaths, law assemblies, and courage."),
        RuneItem("ef_18", RuneAlphabet.ELDER_FUTHARK, "ᛒ", "Berkano", "/b/", "Birch twig", "Týr's Ætt", "Greenest of leaves in spring; symbol of renewal and shelter."),
        RuneItem("ef_19", RuneAlphabet.ELDER_FUTHARK, "ᛖ", "Ehwaz", "/e, eː/", "Horse, steed", "Týr's Ætt", "Companion of heroes and Sleipnir imagery."),
        RuneItem("ef_20", RuneAlphabet.ELDER_FUTHARK, "ᛗ", "Mannaz", "/m/", "Human being", "Týr's Ætt", "'Man is the joy of man' (Hávamál 47); mortality and shared humanity."),
        RuneItem("ef_21", RuneAlphabet.ELDER_FUTHARK, "ᛚ", "Laguz", "/l/", "Water, sea, leek", "Týr's Ætt", "Turbulent ocean waves or fertile flowing stream."),
        RuneItem("ef_22", RuneAlphabet.ELDER_FUTHARK, "ᛜ", "Ingwaz", "/ŋ/", "God Ing / Yngvi-Freyr", "Týr's Ætt", "Fertility and ancestral lineage of the Ingvaeones."),
        RuneItem("ef_23", RuneAlphabet.ELDER_FUTHARK, "ᛞ", "Dagaz", "/d/", "Day, dawn", "Týr's Ætt", "Bright light of day—blessing to rich and poor alike."),
        RuneItem("ef_24", RuneAlphabet.ELDER_FUTHARK, "ᛟ", "Othala", "/o, oː/", "Inherited homestead", "Týr's Ætt", "Ancestral estate and family inheritance. Mímir strictly rejects modern extremist co-option of historical runes.")
    )

    val youngerFuthark = listOf(
        RuneItem("yf_1", RuneAlphabet.YOUNGER_FUTHARK, "ᚠ", "Fé", "/f, v/", "Wealth, livestock", "First Ætt", "Wealth breeds strife among kinsmen; the wolf feeds in the forest (Norwegian Rune Poem)."),
        RuneItem("yf_2", RuneAlphabet.YOUNGER_FUTHARK, "ᚢ", "Úr", "/u, y, o, v/", "Dross, drizzle, aurochs", "First Ætt", "Multi-purpose vowel/consonant rune in Viking Age stones."),
        RuneItem("yf_3", RuneAlphabet.YOUNGER_FUTHARK, "ᚦ", "Þurs", "/θ, ð/", "Giant (þurs)", "First Ætt", "Retained in Icelandic script as letter Þ/þ ('thorn')."),
        RuneItem("yf_4", RuneAlphabet.YOUNGER_FUTHARK, "ᚬ", "Óss", "/o, ã/", "Estuary, Áss-god", "First Ætt", "Nasalized /a/ shifting to /o/ in Old Norse."),
        RuneItem("yf_5", RuneAlphabet.YOUNGER_FUTHARK, "ᚱ", "Reið", "/r/", "Riding", "First Ætt", "Reginn forged the finest sword; riding is hardest on horses."),
        RuneItem("yf_6", RuneAlphabet.YOUNGER_FUTHARK, "ᚴ", "Kaun", "/k, g/", "Ulcer, sore", "First Ætt", "Used for both voiceless /k/ and voiced /g/ in Younger Futhark."),
        RuneItem("yf_7", RuneAlphabet.YOUNGER_FUTHARK, "ᚼ", "Hagall", "/h/", "Hail", "Second Ætt", "Single-bar 'H' in Younger Futhark; coldest of seeds."),
        RuneItem("yf_8", RuneAlphabet.YOUNGER_FUTHARK, "ᚾ", "Nauðr", "/n/", "Need, hardship", "Second Ætt", "Constraint gives small choice; naked freezes in frost."),
        RuneItem("yf_9", RuneAlphabet.YOUNGER_FUTHARK, "ᛁ", "Íss", "/i, e, j/", "Ice", "Second Ætt", "Covers /i/, /e/, and /j/ on Viking Age runestones."),
        RuneItem("yf_10", RuneAlphabet.YOUNGER_FUTHARK, "ᛅ", "Ár", "/a, æ/", "Harvest, plenty", "Second Ætt", "Good harvest brings delight to all; Fróði's peace."),
        RuneItem("yf_11", RuneAlphabet.YOUNGER_FUTHARK, "ᛋ", "Sól", "/s/", "Sun", "Second Ætt", "Light of the lands; shield of the clouds."),
        RuneItem("yf_12", RuneAlphabet.YOUNGER_FUTHARK, "ᛏ", "Týr", "/t, d/", "Týr (one-handed god)", "Third Ætt", "Used for both /t/ and /d/ on the Jelling Stone."),
        RuneItem("yf_13", RuneAlphabet.YOUNGER_FUTHARK, "ᛒ", "Bjarkan", "/b, p/", "Birch", "Third Ætt", "Greenest of twigs; Loki was fortunate in deceit (Norwegian Poem)."),
        RuneItem("yf_14", RuneAlphabet.YOUNGER_FUTHARK, "ᛘ", "Maðr", "/m/", "Man, human", "Third Ætt", "Earth's dust-increase; great is the hawk's claw."),
        RuneItem("yf_15", RuneAlphabet.YOUNGER_FUTHARK, "ᛚ", "Lǫgr", "/l/", "Water, sea", "Third Ætt", "Waterfall plunging from the mountain crag."),
        RuneItem("yf_16", RuneAlphabet.YOUNGER_FUTHARK, "ᛦ", "Ýr", "/ʀ, y/", "Yew (palatal r)", "Third Ætt", "Represents palatalized /-ʀ/ distinct from alveolar /r/ in early Viking Age Norse.")
    )

    val angloSaxonFuthorc = listOf(
        RuneItem("as_1", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᚠ", "Feoh", "/f/", "Wealth", "Futhorc 1–8", "Wealth is a comfort to all; yet each must share it freely to gain honor before the Lord (OE Rune Poem)."),
        RuneItem("as_2", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᚢ", "Ur", "/u/", "Aurochs", "Futhorc 1–8", "Fierce horned beast dwelling on the moors."),
        RuneItem("as_3", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᚦ", "Þorn", "/θ, ð/", "Thorn", "Futhorc 1–8", "Exceedingly sharp; evil for any knight to grasp."),
        RuneItem("as_4", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᚩ", "Os", "/o/", "Mouth, god", "Futhorc 1–8", "Source of every speech, wisdom's pillar."),
        RuneItem("as_5", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᚱ", "Rad", "/r/", "Riding", "Futhorc 1–8", "Soft in the hall, strenuous for him who sits atop a stout horse."),
        RuneItem("as_6", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᚳ", "Cen", "/k, tʃ/", "Torch", "Futhorc 1–8", "Known by every living soul for its pale clear flame."),
        RuneItem("as_7", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᚷ", "Gyfu", "/g, j/", "Gift", "Futhorc 1–8", "Grace and honor to men, aid and sustenance."),
        RuneItem("as_8", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᚹ", "Wynn", "/w/", "Joy", "Futhorc 1–8", "Bliss of him who knows little sorrow and has abundance."),
        RuneItem("as_9", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᚻ", "Hægl", "/h/", "Hail", "Futhorc 9–16", "Whitest of grains, driven from sky's vault."),
        RuneItem("as_10", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᚾ", "Nyd", "/n/", "Need", "Futhorc 9–16", "Tight across the chest, yet often turns to help."),
        RuneItem("as_11", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᛁ", "Is", "/i/", "Ice", "Futhorc 9–16", "Very cold and slippery, glitters clear like glass."),
        RuneItem("as_12", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᛄ", "Ger", "/j/", "Year, harvest", "Futhorc 9–16", "Hope of men when earth yields bright fruits."),
        RuneItem("as_13", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᛇ", "Eoh", "/eo, x/", "Yew", "Futhorc 9–16", "Rough-barked tree, steadfast in the earth."),
        RuneItem("as_14", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᛈ", "Peorð", "/p/", "Game-piece", "Futhorc 9–16", "Source of mirth where proud warriors sit in the beer-hall."),
        RuneItem("as_15", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᛉ", "Eolhx", "/ks/", "Elk-sedge", "Futhorc 9–16", "Marsh grass that burns like fire with blood when gripped."),
        RuneItem("as_16", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᛋ", "Sigel", "/s/", "Sun", "Futhorc 9–16", "Ever a joy to seamen guiding over the gannet's bath."),
        RuneItem("as_17", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᛏ", "Tir", "/t/", "Star / Glory", "Futhorc 17–24", "Guiding sign keeping faith well with noblemen."),
        RuneItem("as_18", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᛒ", "Beorc", "/b/", "Birch", "Futhorc 17–24", "Bears no fruit, yet puts forth twigs without seed."),
        RuneItem("as_19", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᛖ", "Eh", "/e/", "Horse", "Futhorc 17–24", "Joy of princes over broad hooves."),
        RuneItem("as_20", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᛗ", "Man", "/m/", "Human", "Futhorc 17–24", "Dear to his kin in mirth, yet earth must receive him."),
        RuneItem("as_21", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᛚ", "Lagu", "/l/", "Sea", "Futhorc 17–24", "Seems endless to landsmen on a pitching sea-horse."),
        RuneItem("as_22", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᛝ", "Ing", "/ŋ/", "Ing (hero/god)", "Futhorc 17–24", "First seen among the East Danes before departing eastward over the wave."),
        RuneItem("as_23", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᛟ", "Eþel", "/ø, e/", "Homeland", "Futhorc 17–24", "Dear to every man if he may enjoy peace within his house."),
        RuneItem("as_24", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᛞ", "Dæg", "/d/", "Day", "Futhorc 17–24", "Glorious light of the Creator, comfort to rich and poor."),
        RuneItem("as_25", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᚪ", "Ac", "/ɑ/", "Oak", "Extended Futhorc", "Feeds swine on land and bears ships across the gannet's bath."),
        RuneItem("as_26", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᚫ", "Æsc", "/æ/", "Ash tree", "Extended Futhorc", "Exceedingly tall, dear to men, holds its ground against storm."),
        RuneItem("as_27", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᚣ", "Yr", "/y/", "Bow", "Extended Futhorc", "Honor and joy to noblemen; fair on a steed."),
        RuneItem("as_28", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᛡ", "Ior", "/io, j/", "River-fish / Eel", "Extended Futhorc", "River-creature taking food on land, encircled by water."),
        RuneItem("as_29", RuneAlphabet.ANGLO_SAXON_FUTHORC, "ᛠ", "Ear", "/ea/", "Grave-earth", "Extended Futhorc", "Grim to every knight when flesh begins to cool.")
    )

    val allRunes: List<RuneItem> = elderFuthark + youngerFuthark + angloSaxonFuthorc

    val inscriptions = listOf(
        RunestoneInscription(
            id = "jelling_stone",
            title = "Great Jelling Stone (DR 42)",
            locationAndDate = "Jelling, Denmark · c. 965 CE",
            alphabetUsed = "Younger Futhark (Long-Branch)",
            overview = "Erected by King Harald Bluetooth in memory of his parents Gorm and Thyra; often called Denmark's 'birth certificate'.",
            steps = listOf(
                RunestoneStep(
                    runes = "ᚼᛅᚱᛅᛚᛏᚱ ᛬ ᚴᚢᚾᚢᚴᛦ ᛬ ᛒᛅᚦ ᛬ ᚴᛅᚢᚱᚢᛅ ᛬ ᚴᚢᛒᛚ ᛬ ᚦᛅᚢᛋᛁ",
                    transliteration = "haraltr : kunukʀ : baþ : kaurua : kubl : þausi",
                    normalizedOldNorse = "Haraldr konungr bað gǫrva kuml þessi",
                    englishTranslation = "King Harald ordered these monuments to be made",
                    grammarNote = "Notice Younger Futhark 'ᚴ' serves for both /k/ ('konungr') and /g/ ('gǫrva'), and 'ᛒ' for /b/."
                ),
                RunestoneStep(
                    runes = "ᛅᚠᛏ ᛬ ᚴᚢᚱᛘ ᚠᛅᚦᚢᚱ ᛋᛁᚾ ᛬ ᛅᚢᚴ ᛅᚠᛏ ᛬ ᚦᚬᚢᚱᚢᛁ ᛬ ᛘᚢᚦᚢᚱ ᛋᛁᚾᛅ",
                    transliteration = "aft : kurm faþur sin : auk aft : þąurui : muþur sina",
                    normalizedOldNorse = "eft Gorm fǫður sinn ok eft Þyrvi móður sína",
                    englishTranslation = "in memory of Gorm his father, and in memory of Thyra his mother,",
                    grammarNote = "'Aft/eft' governs the accusative case ('fǫður sinn', 'móður sína')."
                ),
                RunestoneStep(
                    runes = "ᛋᛅ ᚼᛅᚱᛅᛚᛏᚱ ᛁᛅᛋ ᛬ ᛋᚬᛦ ᛫ ᚢᛅᚾ ᛫ ᛏᛅᚾᛘᛅᚢᚱᚴ ᛫ ᛅᛚᛅ ᛫ ᛅᚢᚴ ᚾᚢᚱᚢᛁᛅᚴ",
                    transliteration = "sa haraltr ias : sąʀ · uan · tanmaurk · ala · auk nuruiak",
                    normalizedOldNorse = "sá Haraldr es sér vann Danmǫrk alla ok Norveg",
                    englishTranslation = "that Harald who won for himself all of Denmark and Norway",
                    grammarNote = "'Sér' is the reflexive dative pronoun ('for himself'); 'ᛏ' writes /d/ in 'tanmaurk'."
                )
            )
        ),
        RunestoneInscription(
            id = "rok_stone",
            title = "Rök Runestone (Ög 136)",
            locationAndDate = "Östergötland, Sweden · c. 800 CE",
            alphabetUsed = "Younger Futhark (Short-Twig) + Elder & Cipher Runes",
            overview = "Famous for the longest runic inscription in the world (over 760 runes), raised by Varinn in memory of his fallen son Vámoðr, incorporating Eddic-style verse.",
            steps = listOf(
                RunestoneStep(
                    runes = "ᛆᚠᛏ ᚢᛆᛘᚢᚦ ᛋᛏᛆᚾᛏᛆ ᚱᚢᚿᛆᛦ ᚦᛆᛦ",
                    transliteration = "aft uamuþ stanta runaʀ þaʀ",
                    normalizedOldNorse = "Eft Vámoð standa rúnar þær",
                    englishTranslation = "In memory of Vámoðr stand these runes",
                    grammarNote = "Short-twig Younger Futhark uses single-sided branches (e.g., ᛆ for /a/, ᚿ for /n/)."
                ),
                RunestoneStep(
                    runes = "ᛁᚿ ᚢᛆᚱᛁᚿ ᚠᛆᚦᛁ ᚠᛆᚦᛁᚱ ᛆᚠᛏ ᚠᛆᛁᚴᛁᛆᚿ ᛋᚢᚿᚢ",
                    transliteration = "in uarin faþi faþir aft faikian sunu",
                    normalizedOldNorse = "en Varinn fáði, faðir, eft feigjan sunu",
                    englishTranslation = "and Varinn colored them, the father, for his doomed son",
                    grammarNote = "'Fáði' (< fá, 'to paint/color') proves runestones were originally brightly painted!"
                ),
                RunestoneStep(
                    runes = "ᚱᛆᛁᚦ ᚦᛁᛆᚢᚱᛁᚴᛦ ᚼᛁᚿ ᚦᚢᚱᛘᚢᚦᛁ ᛋᛏᛁᛚᛁᛦ ᚠᛚᚢᛏᚿᛆ ᛋᛏᚱᚆᚿᛏᚢ ᚼᚱᛆᛁᚦᛘᛆᚱᛆᛦ",
                    transliteration = "raiþ þiaurikʀ hin þurmuþi stiliʀ flutna strąntu hraiþmaraʀ",
                    normalizedOldNorse = "Réð Þjóðríkr hinn þormóði, stillir flotna, strǫndu Hreiðmarar",
                    englishTranslation = "Þjóðríkr the bold, lord of sea-warriors, ruled the shore of the Hreið-sea",
                    grammarNote = "One of the earliest surviving stanzas in Fornyrðislag meter anywhere in Scandinavia."
                )
            )
        ),
        RunestoneInscription(
            id = "kylver_stone",
            title = "Kylver Stone (G 88)",
            locationAndDate = "Gotland, Sweden · c. 400 CE",
            alphabetUsed = "Elder Futhark",
            overview = "A limestone grave slab bearing the oldest complete 24-rune Elder Futhark row (futhark order), followed by a tree-like bind-rune and the formula 'sueus'.",
            steps = listOf(
                RunestoneStep(
                    runes = "ᚠᚢᚦᚨᚱᚲᚷᚹ ᚺᚾᛁᛃᛇᛈᛉᛊ ᛏᛒᛖᛗᛚᛜᛞᛟ",
                    transliteration = "fuþarkgw hnijïpzs tbemlŋdo",
                    normalizedOldNorse = "Futhark row (24 signs)",
                    englishTranslation = "Complete Elder Futhark alphabet in traditional three-ætt order",
                    grammarNote = "Shows the canonical order differing from Latin/Greek ABC; note ᛈ (pertho) and ᛇ (eihwaz)."
                ),
                RunestoneStep(
                    runes = "ᛊᚢᛖᚢᛊ",
                    transliteration = "sueus",
                    normalizedOldNorse = "Palindrome formula",
                    englishTranslation = "Symmetric 'sueus' carving beside a pine-branch bind-rune",
                    grammarNote = "Its exact lexical meaning remains debated among epigraphers—a classic case of attested artifact vs. speculative interpretation."
                )
            )
        )
    )
}

object RuneTransliterator {

    fun transliterate(input: String, alphabet: RuneAlphabet): String {
        val text = input.lowercase()
        val sb = StringBuilder()
        var i = 0
        while (i < text.length) {
            val two = if (i + 1 < text.length) text.substring(i, i + 2) else ""
            when (alphabet) {
                RuneAlphabet.ELDER_FUTHARK -> {
                    when (two) {
                        "th" -> { sb.append("ᚦ"); i += 2; continue }
                        "ng" -> { sb.append("ᛜ"); i += 2; continue }
                    }
                    sb.append(
                        when (text[i]) {
                            'f', 'v' -> "ᚠ"
                            'u', 'y' -> "ᚢ"
                            'þ', 'ð' -> "ᚦ"
                            'a', 'á', 'æ', 'ǫ' -> "ᚨ"
                            'r' -> "ᚱ"
                            'k', 'c', 'q' -> "ᚲ"
                            'g' -> "ᚷ"
                            'w' -> "ᚹ"
                            'h' -> "ᚺ"
                            'n' -> "ᚾ"
                            'i', 'í' -> "ᛁ"
                            'j' -> "ᛃ"
                            'p' -> "ᛈ"
                            'z', 'x' -> "ᛉ"
                            's' -> "ᛊ"
                            't' -> "ᛏ"
                            'b' -> "ᛒ"
                            'e', 'é' -> "ᛖ"
                            'm' -> "ᛗ"
                            'l' -> "ᛚ"
                            'd' -> "ᛞ"
                            'o', 'ó', 'ö', 'ø' -> "ᛟ"
                            ' ' -> " ᛫ "
                            else -> text[i].toString()
                        }
                    )
                }
                RuneAlphabet.YOUNGER_FUTHARK -> {
                    when (two) {
                        "th" -> { sb.append("ᚦ"); i += 2; continue }
                    }
                    sb.append(
                        when (text[i]) {
                            'f', 'v' -> "ᚠ"
                            'u', 'o', 'ó', 'y', 'w', 'ö', 'ø' -> "ᚢ"
                            'þ', 'ð' -> "ᚦ"
                            'ą', 'ǫ' -> "ᚬ"
                            'r' -> "ᚱ"
                            'k', 'g', 'c', 'q' -> "ᚴ"
                            'h' -> "ᚼ"
                            'n' -> "ᚾ"
                            'i', 'í', 'e', 'é', 'j' -> "ᛁ"
                            'a', 'á', 'æ' -> "ᛅ"
                            's', 'z' -> "ᛋ"
                            't', 'd' -> "ᛏ"
                            'b', 'p' -> "ᛒ"
                            'm' -> "ᛘ"
                            'l' -> "ᛚ"
                            'ʀ' -> "ᛦ"
                            ' ' -> " ᛬ "
                            else -> text[i].toString()
                        }
                    )
                }
                RuneAlphabet.ANGLO_SAXON_FUTHORC -> {
                    when (two) {
                        "th" -> { sb.append("ᚦ"); i += 2; continue }
                        "ng" -> { sb.append("ᛝ"); i += 2; continue }
                        "ae" -> { sb.append("ᚫ"); i += 2; continue }
                        "ea" -> { sb.append("ᛠ"); i += 2; continue }
                        "eo" -> { sb.append("ᛇ"); i += 2; continue }
                    }
                    sb.append(
                        when (text[i]) {
                            'f', 'v' -> "ᚠ"
                            'u' -> "ᚢ"
                            'þ', 'ð' -> "ᚦ"
                            'o' -> "ᚩ"
                            'r' -> "ᚱ"
                            'c', 'k', 'q' -> "ᚳ"
                            'g' -> "ᚷ"
                            'w' -> "ᚹ"
                            'h' -> "ᚻ"
                            'n' -> "ᚾ"
                            'i' -> "ᛁ"
                            'j' -> "ᛄ"
                            'p' -> "ᛈ"
                            'x' -> "ᛉ"
                            's' -> "ᛋ"
                            't' -> "ᛏ"
                            'b' -> "ᛒ"
                            'e' -> "ᛖ"
                            'm' -> "ᛗ"
                            'l' -> "ᛚ"
                            'd' -> "ᛞ"
                            'a' -> "ᚪ"
                            'æ' -> "ᚫ"
                            'y' -> "ᚣ"
                            ' ' -> " ᛫ "
                            else -> text[i].toString()
                        }
                    )
                }
            }
            i++
        }
        return sb.toString()
    }

    data class TransliteratedRuneToken(
        val sourceChars: String,
        val glyph: String,
        val runeName: String,
        val soundValue: String
    )

    fun transliterateElderFutharkDetailed(input: String): List<TransliteratedRuneToken> {
        val runeByGlyph = RunesData.elderFuthark.associateBy { it.glyph }
        val text = input.lowercase()
        val result = mutableListOf<TransliteratedRuneToken>()
        var i = 0
        while (i < text.length) {
            val two = if (i + 1 < text.length) text.substring(i, i + 2) else ""
            if (two == "th" || two == "ng") {
                val glyph = if (two == "th") "ᚦ" else "ᛜ"
                val info = runeByGlyph[glyph]
                result += TransliteratedRuneToken(
                    sourceChars = input.substring(i, i + 2),
                    glyph = glyph,
                    runeName = info?.name ?: glyph,
                    soundValue = info?.soundValue ?: ""
                )
                i += 2
                continue
            }
            val ch = text[i]
            if (ch == ' ') {
                result += TransliteratedRuneToken("space", "᛫", "Word Divider", "pause")
                i++
                continue
            }
            val glyph = when (ch) {
                'f', 'v' -> "ᚠ"
                'u', 'y' -> "ᚢ"
                'þ', 'ð' -> "ᚦ"
                'a', 'á', 'æ', 'ǫ' -> "ᚨ"
                'r' -> "ᚱ"
                'k', 'c', 'q' -> "ᚲ"
                'g' -> "ᚷ"
                'w' -> "ᚹ"
                'h' -> "ᚺ"
                'n' -> "ᚾ"
                'i', 'í' -> "ᛁ"
                'j' -> "ᛃ"
                'p' -> "ᛈ"
                'z', 'x' -> "ᛉ"
                's' -> "ᛊ"
                't' -> "ᛏ"
                'b' -> "ᛒ"
                'e', 'é' -> "ᛖ"
                'm' -> "ᛗ"
                'l' -> "ᛚ"
                'd' -> "ᛞ"
                'o', 'ó', 'ö', 'ø' -> "ᛟ"
                else -> ch.toString()
            }
            val info = runeByGlyph[glyph]
            result += TransliteratedRuneToken(
                sourceChars = input[i].toString(),
                glyph = glyph,
                runeName = info?.name ?: "Unmapped character",
                soundValue = info?.soundValue ?: ch.toString()
            )
            i++
        }
        return result
    }
}
