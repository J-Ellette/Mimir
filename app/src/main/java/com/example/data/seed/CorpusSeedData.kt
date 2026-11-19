package com.example.data.seed

import com.example.data.model.BookEntity
import com.example.data.model.ChapterEntity
import com.example.data.model.CommentaryPackEntity
import com.example.data.model.DiscussionPostEntity
import com.example.data.model.GlossaryCategory
import com.example.data.model.GlossaryEntryEntity
import com.example.data.model.RuneEntity
import com.example.data.model.RunestoneInscriptionEntity
import com.example.data.model.VerseEntity
import com.example.data.model.WorkCategory

object CorpusSeedData {

    val commentaryPacks = listOf(
        CommentaryPackEntity(
            id = "pack_core_pd",
            title = "Public Domain Primary Corpus",
            editionSubtitle = "Bellows (1923), Brodeur (1916), Morris & Magnússon (1870)",
            description = "Normalized Old West Norse texts paired with classic public-domain English translations. Stored 100% offline.",
            sizeLabel = "Bundled · 4.2 MB",
            isInstalled = true,
            isCoreRequired = true,
            isLicensedPreview = false
        ),
        CommentaryPackEntity(
            id = "pack_linguistic_meter",
            title = "Eddic Meter & Philology Pack",
            editionSubtitle = "Fornyrðislag, Ljóðaháttr & Málaháttr",
            description = "Alliterative stave analysis, vowel-length notes, and West Norse vs. East Norse dialectal glosses.",
            sizeLabel = "1.4 MB · Offline Module",
            isInstalled = true,
            isCoreRequired = false,
            isLicensedPreview = false
        ),
        CommentaryPackEntity(
            id = "pack_skaldic_kennings",
            title = "Skaldic Kennings & Poetics Pack",
            editionSubtitle = "Based on Snorri's Háttatal & Skáldskaparmál",
            description = "Unpacks multi-layered kennings, heiti, and mythological metaphors inline inside every stanza.",
            sizeLabel = "1.1 MB · Offline Module",
            isInstalled = true,
            isCoreRequired = false,
            isLicensedPreview = false
        ),
        CommentaryPackEntity(
            id = "pack_runology_epigraphy",
            title = "Runology & Epigraphic Parallels",
            editionSubtitle = "Viking Age Stone & Wood Inscriptions",
            description = "Cross-references poetic formulas with Younger Futhark runestones such as Rök, Jelling, and Karlevi.",
            sizeLabel = "920 KB · Offline Module",
            isInstalled = false,
            isCoreRequired = false,
            isLicensedPreview = false
        ),
        CommentaryPackEntity(
            id = "pack_modern_licensed",
            title = "Contemporary Scholarly Editions",
            editionSubtitle = "Optional Licensed Translation Add-On",
            description = "Placeholder slot for licensed contemporary translations (e.g., Larrington, Crawford, Pettit) alongside public-domain editions.",
            sizeLabel = "Optional Add-On",
            isInstalled = false,
            isCoreRequired = false,
            isLicensedPreview = true
        )
    )

    val books = listOf(
        BookEntity(
            id = "voluspa",
            title = "Völuspá",
            oldNorseTitle = "Vǫluspá",
            category = WorkCategory.POETIC_EDDA,
            translator = "Henry Adams Bellows (1923, Public Domain)",
            licenseNote = "Public Domain",
            summary = "The Prophecy of the Seeress: cosmogony, the building of Ásgarðr, the sacrifice at Mímir's well, Baldr's fall, Ragnarök, and the green earth rising anew.",
            manuscriptSource = "Codex Regius (GKS 2365 4to) & Hauksbók",
            sortOrder = 1
        ),
        BookEntity(
            id = "havamal",
            title = "Hávamál",
            oldNorseTitle = "Hávamál",
            category = WorkCategory.POETIC_EDDA,
            translator = "Henry Adams Bellows (1923, Public Domain)",
            licenseNote = "Public Domain",
            summary = "Sayings of the High One: hospitality, gnomic wisdom, friendship, the transience of wealth against reputation, and Odin's self-sacrifice on the windy tree.",
            manuscriptSource = "Codex Regius (GKS 2365 4to)",
            sortOrder = 2
        ),
        BookEntity(
            id = "lokasenna",
            title = "Lokasenna",
            oldNorseTitle = "Lokasenna",
            category = WorkCategory.POETIC_EDDA,
            translator = "Henry Adams Bellows (1923, Public Domain)",
            licenseNote = "Public Domain",
            summary = "Loki's Flyting at Ægir's hall: biting mythological invective revealing tensions across the Æsir and Vanir.",
            manuscriptSource = "Codex Regius (GKS 2365 4to)",
            sortOrder = 3
        ),
        BookEntity(
            id = "hero_lays",
            title = "Völundarkviða & Hero Lays",
            oldNorseTitle = "Vǫlundarkviða",
            category = WorkCategory.POETIC_EDDA,
            translator = "Henry Adams Bellows (1923, Public Domain)",
            licenseNote = "Public Domain",
            summary = "The Lay of Völundr the master smith and the heroic cycle of Helgi Hundingsbani.",
            manuscriptSource = "Codex Regius (GKS 2365 4to)",
            sortOrder = 4
        ),
        BookEntity(
            id = "gylfaginning",
            title = "Gylfaginning",
            oldNorseTitle = "Gylfaginning",
            category = WorkCategory.PROSE_EDDA,
            translator = "Arthur Gilchrist Brodeur (1916, Public Domain)",
            licenseNote = "Public Domain",
            summary = "Snorri Sturluson's narrative compendium of Norse cosmology, Mímir's well under Yggdrasil's root, and the twilight of the gods.",
            manuscriptSource = "Codex Upsaliensis (DG 11) & Codex Regius Snorra Eddu",
            sortOrder = 5
        ),
        BookEntity(
            id = "skaldskaparmal",
            title = "Skáldskaparmál",
            oldNorseTitle = "Skáldskaparmál",
            category = WorkCategory.PROSE_EDDA,
            translator = "Arthur Gilchrist Brodeur (1916, Public Domain)",
            licenseNote = "Public Domain",
            summary = "The Poetic Diction: Bragi explains the Mead of Poetry, kennings for gods and warriors, and skaldic craft.",
            manuscriptSource = "Codex Regius Snorra Eddu (GKS 2367 4to)",
            sortOrder = 6
        ),
        BookEntity(
            id = "njals_saga",
            title = "Njáls saga",
            oldNorseTitle = "Brennu-Njáls saga",
            category = WorkCategory.FAMILY_SAGA,
            translator = "George Webbe Dasent (1861, Public Domain)",
            licenseNote = "Public Domain",
            summary = "The great saga of law, friendship, feud, and fate in southern Iceland around Bergþórshváll and Hlíðarendi.",
            manuscriptSource = "Möðruvallabók (AM 132 fol.)",
            sortOrder = 7
        ),
        BookEntity(
            id = "egils_saga",
            title = "Egils saga",
            oldNorseTitle = "Egils saga Skalla-Grímssonar",
            category = WorkCategory.FAMILY_SAGA,
            translator = "W. C. Green (1893, Public Domain)",
            licenseNote = "Public Domain",
            summary = "Poet-warrior Egill Skalla-Grímsson from Borgarfjörður to York, featuring Höfuðlausn and Sonatorrek.",
            manuscriptSource = "Möðruvallabók (AM 132 fol.)",
            sortOrder = 8
        ),
        BookEntity(
            id = "laxdaela_saga",
            title = "Laxdæla saga",
            oldNorseTitle = "Laxdœla saga",
            category = WorkCategory.FAMILY_SAGA,
            translator = "Robert Proctor (1903, Public Domain)",
            licenseNote = "Public Domain",
            summary = "The tragic triangle of Guðrún Ósvífrsdóttir, Kjartan Óláfsson, and Bolli Þorleiksson in Breiðafjörður.",
            manuscriptSource = "Möðruvallabók (AM 132 fol.)",
            sortOrder = 9
        ),
        BookEntity(
            id = "grettis_saga",
            title = "Grettis saga",
            oldNorseTitle = "Grettis saga Ásmundarsonar",
            category = WorkCategory.FAMILY_SAGA,
            translator = "George Ainslie Hight (1914, Public Domain)",
            licenseNote = "Public Domain",
            summary = "The outlaw Grettir the Strong, his struggle with the draugr Glámr, and his last stand on Drangey.",
            manuscriptSource = "AM 551 a 4to",
            sortOrder = 10
        ),
        BookEntity(
            id = "volsunga_saga",
            title = "Völsunga saga",
            oldNorseTitle = "Vǫlsunga saga",
            category = WorkCategory.LEGENDARY_SAGA,
            translator = "William Morris & Eiríkr Magnússon (1870, Public Domain)",
            licenseNote = "Public Domain",
            summary = "The legendary cycle of Sigurðr Fáfnisbani, Reginn, Brynhildr, and the cursed hoard of Andvari.",
            manuscriptSource = "NKS 1824 b 4to",
            sortOrder = 11
        ),
        BookEntity(
            id = "heimskringla",
            title = "Heimskringla (Ynglinga)",
            oldNorseTitle = "Heimskringla · Ynglinga saga",
            category = WorkCategory.HEIMSKRINGLA,
            translator = "Samuel Laing (1844, Public Domain)",
            licenseNote = "Public Domain",
            summary = "Snorri Sturluson's euhemerized kings' history, recounting Mímir's counsel and Odin's preservation of Mímir's head.",
            manuscriptSource = "Kringla (Codex Frisianus)",
            sortOrder = 12
        )
    )

    val chapters = listOf(
        ChapterEntity("voluspa_ch1", "voluspa", 1, "Cosmogony & Yggdrasil", "Upphaf heims", "Creation from Ginnungagap and the World Ash.", 1),
        ChapterEntity("voluspa_ch2", "voluspa", 2, "Mímir's Well, Ragnarök & Rebirth", "Mímisbrunnr ok endrborin jǫrð", "Odin's pledged eye, the age of axes, and the green earth rising anew.", 2),
        ChapterEntity("havamal_ch1", "havamal", 1, "Gestaþáttr (Counsel for Guests)", "Gestaþáttr", "Hospitality, companionship, and enduring renown.", 1),
        ChapterEntity("havamal_ch2", "havamal", 2, "Rúnatal (Tale of Runes)", "Rúnatal", "Odin's nine nights on the windy tree winning the runes.", 2),
        ChapterEntity("lokasenna_ch1", "lokasenna", 1, "Flyting at Ægir's Hall", "At Ægis", "Loki challenges Odin and the Æsir.", 1),
        ChapterEntity("hero_lays_ch1", "hero_lays", 1, "Lay of Völundr", "Vǫlundarkviða", "The swan-maidens and the master smith.", 1),
        ChapterEntity("gylfaginning_ch1", "gylfaginning", 1, "Chapters IV & XV · Niflheimr & Mímir's Well", "Niflheimr ok Mímisbrunnr", "Ginnungagap, Hvergelmir, and Yggdrasil's three roots.", 1),
        ChapterEntity("skaldskaparmal_ch1", "skaldskaparmal", 1, "Chapter I · The Mead of Poetry", "Skáldskaparmjöðr", "Kvasir's wisdom brewed into poetic mead.", 1),
        ChapterEntity("njals_ch1", "njals_saga", 1, "Chapters XX & LXXV · Bergþórshváll & Hlíðarendi", "Njáll ok Gunnarr", "Njáll's counsel and Gunnarr's fateful return.", 1),
        ChapterEntity("egils_ch1", "egils_saga", 1, "Chapter LXXVIII · Sonatorrek", "Sonatorrek", "Egill's lament on poetry and loss.", 1),
        ChapterEntity("laxdaela_ch1", "laxdaela_saga", 1, "Chapter LXXVIII · Guðrún at Helgafell", "Helgafell", "Guðrún's twelve-word confession.", 1),
        ChapterEntity("grettis_ch1", "grettis_saga", 1, "Chapter XXXV · Glámr's Curse", "Glámr", "Grettir's struggle with the draugr.", 1),
        ChapterEntity("volsunga_ch1", "volsunga_saga", 1, "Chapter XIX · Fáfnir's Gold", "Gnitaheiðr", "Sigurðr and the dying dragon's warning.", 1),
        ChapterEntity("heimskringla_ch1", "heimskringla", 1, "Ynglinga saga Ch. IV · Mímir's Head", "Míms höfuð", "The Æsir–Vanir truce and Mímir's counsel.", 1)
    )

    val verses = listOf(
        // --- VÖLUSPÁ ---
        VerseEntity(
            id = "voluspa_1",
            bookId = "voluspa",
            chapterId = "voluspa_ch1",
            verseNumber = 1,
            numberLabel = "Stanza 1",
            oldNorseText = "Hljóðs bið ek allar\nhelgar kindir,\nmeiri ok minni\nmǫgu Heimdallar;\nviltu at ek, Valfǫðr,\nvel fyr telja\nforn spjǫll fira,\nþau er fremst of man.",
            englishText = "Hearing I ask from the holy races,\nFrom Heimdall's sons, both high and low;\nThou wilt, Valfather, that well I relate\nOld tales I remember of men long ago.",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Codex Regius (GKS 2365 4to) & Hauksbók",
            scholarlyNote = "The völva addresses both gods and humankind as 'sons of Heimdallr', establishing cosmic kinship before recounting creation.",
            linguisticPackNote = "Meter: Fornyrðislag (alliterative long-lines). Note 'hljóðs' (gen. sg. of hljóð, 'silence/hearing') governed by 'biðja'.",
            kenningPackNote = "'Valfǫðr' (Father of the Slain) is a poetic heiti for Óðinn; 'mǫgu Heimdallar' serves as an attested kenning-like periphrasis for humanity.",
            runologyPackNote = "Compare the invocation formula 'hljóðs bið ek' with oral-ritual openings on Younger Futhark memorial stones.",
            sortOrder = 1
        ),
        VerseEntity(
            id = "voluspa_3",
            bookId = "voluspa",
            chapterId = "voluspa_ch1",
            verseNumber = 3,
            numberLabel = "Stanza 3",
            oldNorseText = "Ár var alda\nþar er Ymir byggði,\nvara sandr né sær\nné svalar unnir;\njǫrð fannsk æva\nné upphiminn,\ngap var ginnunga,\nen gras hvergi.",
            englishText = "Of old was the age when Ymir lived;\nSea nor cool waves nor sand there were;\nEarth had not been, nor heaven above,\nBut a yawning gap, and grass nowhere.",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Codex Regius, Hauksbók, and quoted in Snorri's Gylfaginning",
            scholarlyNote = "The 'yawning void' (ginnunga gap) echoes across Old Germanic cosmology (cf. Old High German Wessobrunn Prayer).",
            linguisticPackNote = "Note the archaic suffixed negative verb 'vara' (var + -a, 'was not') and mediopassive 'fannsk' ('was found').",
            kenningPackNote = "Cosmological negation lists (sand, sea, earth, heaven, grass) heighten the primordial emptiness before creation.",
            runologyPackNote = "Alliterative pairs 'sandr / sær' and 'gap / gras' exemplify classic early Germanic poetic mnemonics.",
            sortOrder = 2
        ),
        VerseEntity(
            id = "voluspa_19",
            bookId = "voluspa",
            chapterId = "voluspa_ch1",
            verseNumber = 19,
            numberLabel = "Stanza 19",
            oldNorseText = "Ask veit ek standa,\nheitir Yggdrasill,\nhár baðmr, ausinn\nhvíta auri;\nþaðan koma dǫggvar\nþærs í dala falla,\nstendr æ yfir grœnn\nUrðarbrunni.",
            englishText = "An ash I know there stands, Yggdrasil is its name,\nA tall tree, showered with shining loam.\nFrom there come the dews that drop in the valleys;\nEver green it stands over Urðr's well.",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Codex Regius & Snorra Edda",
            scholarlyNote = "The World Ash stands evergreen above Urðarbrunnr, watered with white clay/loam (aurr) by the Norns.",
            linguisticPackNote = "'Yggdrasill' literally compounds 'Yggr' (The Terrifier = Óðinn) + 'drasill' (steed): 'Óðinn's gallows-steed'.",
            kenningPackNote = "'Hvíti aurr' (white clay/water) is later explained in Gylfaginning as the sacred preserving water of Urðr's well.",
            runologyPackNote = "Tree-pillar imagery parallels cosmic tree motifs on Gotland picture stones.",
            sortOrder = 3
        ),
        VerseEntity(
            id = "voluspa_28",
            bookId = "voluspa",
            chapterId = "voluspa_ch2",
            verseNumber = 28,
            numberLabel = "Stanza 28",
            oldNorseText = "Ein sat hon úti\nþá er inn aldni kom\nyggjungr ása\nok í augu leit:\nHvers fregnið mik?\nhví freistið mín?\nallt veit ek, Óðinn,\nhvar þú auga falt,\ní inum mæra\nMímisbrunni.",
            englishText = "Alone she sat outside when the Old One came,\nThe mighty among the gods, and looked into her eyes:\n'Why do you question me? Why do you test me?\nI know it all, Odin, where you hid your eye:\nIn that renowned well of Mímir.'",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Codex Regius (GKS 2365 4to)",
            scholarlyNote = "The core namesake passage of Mímir's Well: Odin pawned an eye for prophetic wisdom. 'Útiseta' (sitting out) refers to a medieval Icelandic term for nocturnal divination.",
            linguisticPackNote = "'Falt' is 2nd sg. past indicative of 'fela' (to hide/conceal). 'Mæra' is weak masc. dat. sg. ('famous/renowned').",
            kenningPackNote = "'Yggjungr ása' (The Terrible/Mighty One of the Æsir) is a solemn circumlocution for Óðinn.",
            runologyPackNote = "Knowledge of hidden secrets ('allt veit ek') echoes the boast of runemasters on Viking Age stones.",
            sortOrder = 4
        ),
        VerseEntity(
            id = "voluspa_45",
            bookId = "voluspa",
            chapterId = "voluspa_ch2",
            verseNumber = 45,
            numberLabel = "Stanza 45",
            oldNorseText = "Brœðr munu berjask\nok at bǫnum verðask,\nmunu systrungar\nsifjum spilla;\nhart er í heimi,\nhórdómr mikill,\nskeggǫld, skálmǫld,\nskildir ro klofnir,\nvindǫld, vargǫld,\náðr verǫld steypisk.",
            englishText = "Brothers shall fight and fell each other,\nAnd sisters' sons shall kinship stain;\nHard is it on earth, with mighty whoredom;\nAxe-time, sword-time, shields are sundered,\nWind-time, wolf-time, ere the world falls.",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Codex Regius & Gylfaginning",
            scholarlyNote = "The breakdown of kinship oaths precedes Ragnarök. Note that modern readings treating 'vargöld' as apocalyptic nationalism are ahistorical misappropriations; the text frames kin-strife as a moral tragedy.",
            linguisticPackNote = "Compound nouns with '-ǫld' (age/time: skegg-ǫld, skálm-ǫld, vind-ǫld, varg-ǫld) drive percussive Fornyrðislag rhythm.",
            kenningPackNote = "'Skildir ro klofnir' uses archaic 'ro' (< ero/eru, 'are').",
            runologyPackNote = "The rhythm of paired '-ǫld' compounds resembles cursed-formula warnings on the Björketorp and Stentoften stones.",
            sortOrder = 5
        ),
        VerseEntity(
            id = "voluspa_59",
            bookId = "voluspa",
            chapterId = "voluspa_ch2",
            verseNumber = 59,
            numberLabel = "Stanza 59",
            oldNorseText = "Sér hon upp koma\nǫðru sinni\njǫrð ór ægi\niðjagrœna;\nfalla forsar,\nflýgr ǫrn yfir,\nsá er á fjalli\nfiska veiðir.",
            englishText = "She sees come up a second time\nThe earth from the ocean, eternally green;\nThe waterfalls flow, the eagle flies over,\nWho hunts for fish upon the mountain.",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Codex Regius & Hauksbók",
            scholarlyNote = "After Ragnarök, an eternally green earth rises from the sea. Scholars debate Christian vs. pre-Christian cyclical influences in the rebirth stanzas.",
            linguisticPackNote = "'Iðjagrœna' intensifies 'grœnn' (green) with prefix 'iðja-' ('ever-, ceaselessly').",
            kenningPackNote = "Naturalistic renewal imagery replaces battle-kennings with waterfalls, eagles, and mountain streams.",
            runologyPackNote = "Cyclical cosmic imagery finds parallels in Viking Age hogback and cross-slab carvings.",
            sortOrder = 6
        ),

        // --- HÁVAMÁL ---
        VerseEntity(
            id = "havamal_1",
            bookId = "havamal",
            chapterId = "havamal_ch1",
            verseNumber = 1,
            numberLabel = "Stanza 1",
            oldNorseText = "Gáttir allar\náðr gangi fram,\num skoðask skyli,\num skyggnask skyli,\nþví at óvíst er at vita\nhvar óvinir\nsitja á fleti fyrir.",
            englishText = "At every doorway, before one enters,\nOne should look around, one should spy around,\nFor uncertain is it to know\nWhere enemies may sit upon the bench within.",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Codex Regius (GKS 2365 4to)",
            scholarlyNote = "The opening stanza of the Gestapáttr ('Guest's Section') counsels cautious vigilance in an unfamiliar hall.",
            linguisticPackNote = "Meter: Ljóðaháttr (stanza of two short lines + one full line). 'Skyli' is 3rd sg. pres. subjunctive of 'skulu' ('should').",
            kenningPackNote = "Direct gnomic speech; 'flet' refers to the raised wooden seating platform along the longhouse wall.",
            runologyPackNote = "Hospitality and caution motifs reflect Iron Age hall culture.",
            sortOrder = 1
        ),
        VerseEntity(
            id = "havamal_47",
            bookId = "havamal",
            chapterId = "havamal_ch1",
            verseNumber = 47,
            numberLabel = "Stanza 47",
            oldNorseText = "Ungr var ek forðum,\nfór ek einn saman,\nþá varð ek villr vega;\nauðigr þóttumk\ner ek annan fann:\nmaðr er manns gaman.",
            englishText = "Young was I once, and walked alone,\nAnd bewildered I seemed in the way;\nRich I thought myself when I met another:\nMan is the joy of man.",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Codex Regius (GKS 2365 4to)",
            scholarlyNote = "One of the most beloved ethical proverbs in the Edda: human companionship creates meaning and wealth on lonely roads.",
            linguisticPackNote = "'Villr vega' takes genitive plural 'vega' ('astray of paths'). 'Þóttumk' is 1st sg. past reflexive ('seemed to myself').",
            kenningPackNote = "Gnomic closing line 'maðr er manns gaman' balances alliteration on 'm'.",
            runologyPackNote = "Friendship and companionship values are echoed on Swedish 'félagi' (partner) runestones.",
            sortOrder = 2
        ),
        VerseEntity(
            id = "havamal_76",
            bookId = "havamal",
            chapterId = "havamal_ch1",
            verseNumber = 76,
            numberLabel = "Stanza 76",
            oldNorseText = "Deyr fé,\ndeyja frændr,\ndeyr sjálfr it sama;\nek veit einn\nat aldri deyr:\ndómr um dauðan hvern.",
            englishText = "Cattle die, kinsmen die,\nThe self must also die;\nI know one thing which never dies:\nThe judgment on each one dead.",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Codex Regius & quoted in Heimskringla (Hákonar saga góða)",
            scholarlyNote = "Paired with stanza 77 ('orðstírr deyr aldregi'), this stresses enduring moral reputation ('dómr') over material property ('fé').",
            linguisticPackNote = "Notice the structural parallel with Old English 'The Wanderer' and 'Beowulf'. 'It sama' = 'likewise'.",
            kenningPackNote = "Anaphora on 'deyr / deyja' drives home mortality before contrasting 'aldri deyr'.",
            runologyPackNote = "Memorial runestones literally enact 'dómr um dauðan hvern' by carving a person's good deeds in stone.",
            sortOrder = 3
        ),
        VerseEntity(
            id = "havamal_138",
            bookId = "havamal",
            chapterId = "havamal_ch2",
            verseNumber = 138,
            numberLabel = "Stanza 138",
            oldNorseText = "Veit ek at ek hekk\nvindgameiði á\nnætr allar níu,\ngeiri undaðr\nok gefinn Óðni,\nsjálfr sjálfum mér,\ná þeim meiði\ner manngi veit\nhvers hann af rótum renn.",
            englishText = "I know that I hung on a windy tree\nNine long nights,\nWounded with a spear, dedicated to Odin,\nMyself to myself,\nOn that tree of which no man knows\nFrom what roots it runs.",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Codex Regius (Rúnatal section)",
            scholarlyNote = "The Rúnatal myth of how Odin won the runes through ordeal. Scholars examine both shamanic-initiation parallels and medieval literary framing without projecting modern occult inventions.",
            linguisticPackNote = "'Manngi' (maðr + -gi) = 'no one'. 'Renn' is 3rd sg. pres. of 'renna' ('to run/spring').",
            kenningPackNote = "'Vindgameiðr' (windy tree) alludes to Yggdrasill without naming it directly.",
            runologyPackNote = "Stanza 139 immediately follows: 'nam ek upp rúnar, œpandi nam' ('I took up the runes, screaming I took them').",
            sortOrder = 4
        ),
        VerseEntity(
            id = "havamal_139",
            bookId = "havamal",
            chapterId = "havamal_ch2",
            verseNumber = 139,
            numberLabel = "Stanza 139",
            oldNorseText = "Við hleifi mik sældu\nné við hornigi,\nnýsta ek niðr,\nnam ek upp rúnar,\nœpandi nam,\nfell ek aptr þaðan.",
            englishText = "No bread did they give me nor a drink from a horn,\nDownwards I peered;\nI took up the runes, screaming I took them,\nThen I fell back from thence.",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Codex Regius (Rúnatal section)",
            scholarlyNote = "In Old Norse, 'rúnar' can mean both carved letters and secret lore/wisdom.",
            linguisticPackNote = "'Hornigi' = 'horn + -gi' (not even a horn). 'Nýsta' is 1st sg. past of 'nýsa' ('peered').",
            kenningPackNote = "Visceral physical verbs ('nýsta', 'nam', 'œpandi', 'fell') emphasize cost and effort.",
            runologyPackNote = "Directly links ritual endurance to literacy in runic script.",
            sortOrder = 5
        ),

        // --- LOKASENNA ---
        VerseEntity(
            id = "lokasenna_22",
            bookId = "lokasenna",
            chapterId = "lokasenna_ch1",
            verseNumber = 22,
            numberLabel = "Stanza 22",
            oldNorseText = "Þegi þú, Óðinn!\nþú kunnir aldregi\ndeila víg með verum;\noft þú gaft\nþeim er þú gefa skyldira,\ninum slævurum, sigr.",
            englishText = "Be silent, Odin! Thou couldst never\nDeal out war's fortune fairly among men;\nOft hast thou given victory to those\nTo whom thou shouldst not—to the faint-hearted.",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Codex Regius (GKS 2365 4to)",
            scholarlyNote = "Loki's taunt reflects a widespread Norse literary motif: Odin grants victory capriciously to gather the finest warriors into Valhöll.",
            linguisticPackNote = "'Skyldira' = 'skyldir + -a' ('shouldst not'). 'Slævurum' is dative plural comparative.",
            kenningPackNote = "'Deila víg' (to apportion battle) frames war as a judicial distribution.",
            runologyPackNote = "Flyting (senna) was a recognized courtly verbal duel genre.",
            sortOrder = 1
        ),

        // --- HERO LAYS ---
        VerseEntity(
            id = "volundarkvida_1",
            bookId = "hero_lays",
            chapterId = "hero_lays_ch1",
            verseNumber = 1,
            numberLabel = "Völundarkviða 1",
            oldNorseText = "Meyjar flugu sunnan\nmyrkvið í gǫgnum,\nalvitr ungar,\nørlǫg drýgja;\nþær á sævarstrǫnd\nsettusk at hvílask,\ndrósir suðrœnar,\ndýrt lín spunnu.",
            englishText = "Maidens flew from the south through Mirkwood,\nYoung swan-maidens, fate to fulfill;\nOn the shore of the sea they sat them to rest,\nSouthern maidens, and spun fine flax.",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Codex Regius (GKS 2365 4to)",
            scholarlyNote = "The opening of the Lay of Völundr (Wayland the Smith), whose legend also appears on the Anglo-Saxon Franks Casket.",
            linguisticPackNote = "'Alvitr' may mean 'all-wise' or 'swan-creature'. 'Drýgja ørlǫg' = 'to work out/endure fate'.",
            kenningPackNote = "'Drósir suðrœnar' introduces the southern swan-maidens who transform the smith's fate.",
            runologyPackNote = "Völundr's smithy scene is carved on the 8th-century Franks Casket in Anglo-Saxon Futhorc.",
            sortOrder = 1
        ),

        // --- PROSE EDDA ---
        VerseEntity(
            id = "gylfaginning_15",
            bookId = "gylfaginning",
            chapterId = "gylfaginning_ch1",
            verseNumber = 15,
            numberLabel = "Chapter XV · Mímir's Well",
            oldNorseText = "En undir þeiri rót, er til hrímþursa horfir, þar er Mímisbrunnr, er spekð ok mannvit er í fólgit, ok heitir sá Mímir, er á brunninn. Hann er fullr af vísindum, fyrir því at hann drekkr ór brunninum af horninu Gjallarhorni. Þar kom Alfǫðr ok beiddisk eins drykkjar af brunninum, en hann fekk eigi fyrr en hann lagði auga sitt at veði.",
            englishText = "But under that root which turns toward the frost-giants is Mímir's Well, wherein wisdom and understanding are stored; and he is called Mímir who owns the well. He is full of wisdom because he drinks of the well from the Gjallarhorn. Thither came Allfather and asked for one drink of the well, yet he got it not until he laid his eye in pledge.",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Snorri Sturluson's Prose Edda (c. 1220 CE)",
            scholarlyNote = "Snorri expands upon Völuspá 28, connecting Mímir's Well beneath Yggdrasil's second root with the Gjallarhorn and Odin's eye-pledge.",
            linguisticPackNote = "'Spekð ok mannvit' = 'wisdom and human-wit/intelligence'. 'At veði' = 'as a pledge/pawn'.",
            kenningPackNote = "'Alfǫðr' is Snorri's favored title for Odin in Gylfaginning.",
            runologyPackNote = "Illustrates how medieval Icelandic prose preserved and systematized older Eddic poetry.",
            sortOrder = 1
        ),
        VerseEntity(
            id = "gylfaginning_4",
            bookId = "gylfaginning",
            chapterId = "gylfaginning_ch1",
            verseNumber = 4,
            numberLabel = "Chapter IV · Ginnungagap",
            oldNorseText = "Þá mælti Jafnhár: 'Fyrr var þat mǫrgum ǫldum en jǫrð var skǫpuð, er Niflheimr var gjǫrr, ok í honum miðjum liggr brunnr sá er Hvergelmir heitir, ok þaðan af falla þær ár er svá heita: Svǫl, Gunnþrá, Fjǫrm, Fimbulþul, Slíðr ok Hríð.'",
            englishText = "Then said Just-as-High: 'It was many ages before the earth was created that Niflheimr was made, and in its midst lies the well called Hvergelmir, and from it flow the rivers called Svöl, Gunnþrá, Fjörm, Fimbulþul, Slíðr, and Hríð.'",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Snorri's Prose Edda (quoting Grímnismál)",
            scholarlyNote = "Snorri weaves Eddic poetry into a structured cosmology contrasting icy Niflheimr in the north with fiery Múspellsheimr in the south.",
            linguisticPackNote = "'Fyrr ... en' = 'earlier than / before'. 'Mǫrgum ǫldum' is dative of measure of time.",
            kenningPackNote = "'Hvergelmir' means 'Cauldron-Roarer', source of the primordial Élivágar rivers.",
            runologyPackNote = "Compare mythological river-lists in Grímnismál.",
            sortOrder = 2
        ),

        // --- SKÁLDSKAPARMÁL ---
        VerseEntity(
            id = "skaldskaparmal_1",
            bookId = "skaldskaparmal",
            chapterId = "skaldskaparmal_ch1",
            verseNumber = 1,
            numberLabel = "Chapter I · Mead of Poetry",
            oldNorseText = "Þeir blǫnduðu við hunangi þat er dreyri hans rann, ok varð þar af mjǫðr sá er hverr er af drekkr verðr skáld eða frœðamaðr.",
            englishText = "They mixed honey with the blood that flowed from Kvasir, and therefrom came that mead of which whoever drinks becomes a poet or a scholar.",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Snorri Sturluson's Skáldskaparmál",
            scholarlyNote = "Kvasir's blood brewed into Suttungr's mead explains why Norse skalds call poetry 'Odin's drink' or 'Kvasir's blood'.",
            linguisticPackNote = "'Frœðamaðr' literally means 'man of lore/knowledge' (from frœði, 'learning').",
            kenningPackNote = "Foundational origin story for poetic kennings like 'Kvasis dreyri' (poetry) and 'Óðins mjǫðr'.",
            runologyPackNote = "Poetic inspiration and wisdom drinks recur across Germanic myth.",
            sortOrder = 1
        ),

        // --- NJÁLS SAGA ---
        VerseEntity(
            id = "njals_1",
            bookId = "njals_saga",
            chapterId = "njals_ch1",
            verseNumber = 20,
            numberLabel = "Chapter XX · Njáll of Bergþórshváll",
            oldNorseText = "Njáll bjó at Bergþórshváli í Landeyjum; hann var vel auðigr at fé ok vænn at áliti, en sá hlutr var á ráði hans at honum óx eigi skegg. Hann var lǫgmaðr svá mikill at engi fannsk hans jafningi, vitr var hann ok forspár, heilráðr ok góðgjarn.",
            englishText = "Njáll dwelt at Bergþórshváll in the Landeyjar; he was well off for wealth and handsome to look on, though one thing was lacking in him: no beard grew on his chin. He was so great a lawyer that his equal was not to be found; wise he was and foreknowing, wholesome in counsel and kindly.",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Brennu-Njáls saga (Möðruvallabók)",
            scholarlyNote = "Introduces Njáll Þorgeirsson, whose legal wisdom and deep friendship with Gunnarr of Hlíðarendi anchor the saga.",
            linguisticPackNote = "'At fé' = 'in livestock/wealth'. 'Heilráðr' = 'sound-in-counsel' (heill + ráð).",
            kenningPackNote = "Saga prose favors understated characterization ('litotes') rather than elaborate skaldic kennings.",
            runologyPackNote = "Bergþórshváll in southern Iceland remains a key archaeological and literary site.",
            sortOrder = 1
        ),
        VerseEntity(
            id = "njals_75",
            bookId = "njals_saga",
            chapterId = "njals_ch1",
            verseNumber = 75,
            numberLabel = "Chapter LXXV · Fair is the Hillside",
            oldNorseText = "Þá leit Gunnarr til hlíðarinnar ok mælti: 'Fǫgr er hlíðin, svá at mér hefir hon aldri jafnfǫgr sýnzk, bleikir akrar ok slegin tún, ok mun ek ríða heim aptr ok fara hvergi.'",
            englishText = "Then Gunnar looked up toward the hillside and said: 'Fair is the slope, so that to me it has never seemed so fair, pale cornfields and mown home-meadows; and I will ride back home, and go nowhere.'",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Brennu-Njáls saga",
            scholarlyNote = "Gunnarr's fateful refusal to accept three-year outlawry abroad after his horse stumbles looking back at Hlíðarendi.",
            linguisticPackNote = "'Sýnzk' is past participle of 'sýnask' ('to seem/appear'). 'Slegin tún' = 'mown homefields'.",
            kenningPackNote = "Visual restraint—describing pale grainfields and mown meadows—heightens the tragedy of his return.",
            runologyPackNote = "Hlíðarendi in Fljótshlíð overlooks the Markarfljót plain in southern Iceland.",
            sortOrder = 2
        ),

        // --- EGILS SAGA ---
        VerseEntity(
            id = "egils_78",
            bookId = "egils_saga",
            chapterId = "egils_ch1",
            verseNumber = 78,
            numberLabel = "Chapter LXXVIII · Sonatorrek",
            oldNorseText = "Mjǫk erum tregt\ntungu at hrœra\nór loptvætt\nljóðpundara;\nerat nú vænt\nof Viðurs þýfi,\nné hógdrœgt\nór hugar fylgsni.",
            englishText = "Hard is it for me to stir the tongue\nOf the song-steelyard from the air-weight;\nNot easy now to draw forth Odin's theft,\nNor light to lift from the hiding-place of thought.",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Egils saga (Ketilsbók, AM 162 A)",
            scholarlyNote = "Opening stanza of Sonatorrek ('The Hard Loss of Sons'), Egill's lament after his son Böðvarr drowns.",
            linguisticPackNote = "Meter: Kviðuháttr (alternating 3- and 4-syllable lines). 'Erat' = 'is not'.",
            kenningPackNote = "'Viðurs þýfi' (Odin's theft) is a master kenning for poetry—referring to Odin stealing the Mead of Poetry.",
            runologyPackNote = "Egill's daughter Þorgerðr famously proposes carving the poem onto a rune-staff ('rísta á kefli').",
            sortOrder = 1
        ),

        // --- LAXDÆLA SAGA ---
        VerseEntity(
            id = "laxdaela_78",
            bookId = "laxdaela_saga",
            chapterId = "laxdaela_ch1",
            verseNumber = 78,
            numberLabel = "Chapter LXXVIII · Guðrún's Confession",
            oldNorseText = "Þá mælti Guðrún: 'Þeim var ek verst er ek unna mest.'",
            englishText = "Then Guðrún said: 'To him I was worst whom I loved the most.'",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Laxdæla saga (Möðruvallabók)",
            scholarlyNote = "Asked in old age by her son Bolli which of her four husbands she loved most, Guðrún delivers the most famous single line in Icelandic saga prose.",
            linguisticPackNote = "'Unna' is 1st sg. past indicative of 'unna' ('to love', taking the dative 'þeim').",
            kenningPackNote = "Classic saga understatement: twelve Old Norse syllables encapsulate an entire lifetime of tragedy.",
            runologyPackNote = "Set at Helgafell on the Snæfellsnes peninsula.",
            sortOrder = 1
        ),

        // --- GRETTIS SAGA ---
        VerseEntity(
            id = "grettis_35",
            bookId = "grettis_saga",
            chapterId = "grettis_ch1",
            verseNumber = 35,
            numberLabel = "Chapter XXXV · The Curse of Glámr",
            oldNorseText = "Þá mælti Glámr: 'Þeir munu verða þér augu mín jafnan fyrir sjónum, svá at þér mun erfitt þykkja einum at vera.'",
            englishText = "Then Glámr spoke: 'My eyes shall ever be before your sight, so that it will seem hard to you to be alone.'",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Grettis saga Ásmundarsonar",
            scholarlyNote = "Before Grettir beheads the undead Glámr, the draugr curses him with fear of the dark—sealing Grettir's fate as a hunted outlaw.",
            linguisticPackNote = "'Jafnan' = 'always/constantly'. 'Þykkja' = 'to seem/feel'.",
            kenningPackNote = "Contrast between Grettir's physical might and psychological dread of darkness.",
            runologyPackNote = "Compare supernatural draugr motifs in Vatnsdæla and Eyrbyggja sagas.",
            sortOrder = 1
        ),

        // --- VÖLSUNGA SAGA ---
        VerseEntity(
            id = "volsunga_19",
            bookId = "volsunga_saga",
            chapterId = "volsunga_ch1",
            verseNumber = 19,
            numberLabel = "Chapter XIX · Fáfnir's Counsel",
            oldNorseText = "Sigurðr svarar: 'Þat mun ek þó ráða at ríða til þess gulls er þú hefir átt, ok gef þú nú upp ǫndina.'",
            englishText = "Sigurd answers: 'Yet this counsel will I follow, to ride to that gold which thou hast owned; and now give thou up the ghost.'",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Völsunga saga (NKS 1824 b 4to)",
            scholarlyNote = "Echoes the Eddic Fáfnismál dialogue where the dying dragon warns Sigurðr that the ringing gold will be his bane.",
            linguisticPackNote = "'Átt' is past participle of 'eiga' ('to own/possess'). 'Ǫndina' = 'the spirit/breath' + suffixed definite article '-ina'.",
            kenningPackNote = "Ring-gold of Andvari is a central motif in Norse heroic poetry ('Andvara-nautr').",
            runologyPackNote = "Scenes of Sigurðr roasting Fáfnir's heart appear on the Ramsund carving in Sweden.",
            sortOrder = 1
        ),

        // --- HEIMSKRINGLA ---
        VerseEntity(
            id = "heimskringla_4",
            bookId = "heimskringla",
            chapterId = "heimskringla_ch1",
            verseNumber = 4,
            numberLabel = "Ynglinga saga Ch. IV · Mímir's Head",
            oldNorseText = "Þá tóku þeir Mími ok hjuggu af honum hǫfuðit ok sendu Ásum. Óðinn tók hǫfuðit ok smurði urtum þeim er eigi mátti fúna, ok kvað yfir galdra ok magnaði svá at þat mælti við hann ok sagði honum marga leynda hluti.",
            englishText = "Then they took Mímir and struck off his head and sent it to the Æsir. Odin took the head and smeared it with herbs so that it could not decay, and sang spells over it and charmed it so that it spoke with him and told him many hidden things.",
            isPrimaryAttested = true,
            attestationSummary = "Attested in Snorri Sturluson's Heimskringla (c. 1230 CE)",
            scholarlyNote = "After the Æsir–Vanir truce exchange, the Vanir behead Mímir; Odin preserves the head with herbs and galdr so Mímir continues to share hidden wisdom.",
            linguisticPackNote = "'Hjuggu' is 3rd pl. past of 'hǫggva' ('to hew/strike'). 'Leynda hluti' = 'hidden things'.",
            kenningPackNote = "In Eddic verse, Mímir's head ('Míms hǫfuð') is a recurring symbol of counsel before Ragnarök (Völuspá 46).",
            runologyPackNote = "Euhemeristic history by Snorri frames mythic figures as ancient sage rulers.",
            sortOrder = 1
        )
    )

    val glossary = listOf(
        GlossaryEntryEntity(
            id = "g_mimir",
            term = "Mímir",
            oldNorseForm = "Mímir / Mímr",
            category = GlossaryCategory.GOD,
            definition = "Sage figure associated with wisdom, memory, and the sacred well beneath Yggdrasil's root. In Heimskringla, his preserved head counsels Odin; in Völuspá, Odin pawns his eye in Mímir's well.",
            primarySource = "Völuspá 28, 46; Gylfaginning 15; Ynglinga saga 4",
            sourceDistinctionNote = "Attested as both owner of the wisdom-well and the talking severed head; later theories debating whether Mímir was Æsir, Jötunn, or water-spirit remain scholarly hypotheses."
        ),
        GlossaryEntryEntity(
            id = "g_odin",
            term = "Odin",
            oldNorseForm = "Óðinn",
            category = GlossaryCategory.GOD,
            definition = "Chief of the Æsir, seeker of wisdom, poetry, runic lore, and inspiration; called Alföðr, Valföðr, Viðurr, and Hár.",
            primarySource = "Völuspá, Hávamál, Grímnismál, Gylfaginning",
            sourceDistinctionNote = "Complex, ambivalent figure in primary sources—associated with ecstatic poetry and wisdom rather than simplistic modern superhero tropes."
        ),
        GlossaryEntryEntity(
            id = "g_freyja",
            term = "Freyja",
            oldNorseForm = "Freyja",
            category = GlossaryCategory.GOD,
            definition = "Vanir goddess of love, fertility, fate, and seiðr magic; receives half the slain at Fólkvangr and rides a boar or cat-drawn chariot.",
            primarySource = "Grímnismál 14, Lokasenna, Þrymskviða, Gylfaginning 24",
            sourceDistinctionNote = "Attested in both Eddas and place names across Scandinavia."
        ),
        GlossaryEntryEntity(
            id = "g_heimdallr",
            term = "Heimdallr",
            oldNorseForm = "Heimdallr",
            category = GlossaryCategory.GOD,
            definition = "Watchman of the Æsir at Bifröst, holder of the Gjallarhorn, and ancestor of human social orders in Rígsþula and Völuspá 1.",
            primarySource = "Völuspá 1, 46; Grímnismál 13; Gylfaginning 27",
            sourceDistinctionNote = "Primary sources link him to keen hearing and the Gjallarhorn."
        ),
        GlossaryEntryEntity(
            id = "g_ymir",
            term = "Ymir",
            oldNorseForm = "Ymir / Aurgelmir",
            category = GlossaryCategory.GIANT,
            definition = "Primordial frost-jötunn born in Ginnungagap from whose body Odin, Vili, and Vé fashioned earth, sea, sky, and clouds.",
            primarySource = "Völuspá 3, Vafþrúðnismál 21, Grímnismál 40–41, Gylfaginning 5–8",
            sourceDistinctionNote = "Attested across multiple Eddic poems and Snorri."
        ),
        GlossaryEntryEntity(
            id = "g_fafnir",
            term = "Fáfnir",
            oldNorseForm = "Fáfnir",
            category = GlossaryCategory.GIANT,
            definition = "Son of Hreiðmarr who slew his father for Andvari's gold and transformed into a dragon guarding Gnitaheiðr until slain by Sigurðr.",
            primarySource = "Reginsmál, Fáfnismál, Völsunga saga",
            sourceDistinctionNote = "Deeply attested in heroic poetry, prose saga, and Viking Age carvings."
        ),
        GlossaryEntryEntity(
            id = "g_yggdrasil",
            term = "Yggdrasil",
            oldNorseForm = "Yggdrasill",
            category = GlossaryCategory.PLACE,
            definition = "The colossal evergreen ash tree linking the Nine Worlds, watered at Urðarbrunnr, Mímisbrunnr, and Hvergelmir.",
            primarySource = "Völuspá 19, Grímnismál 29–35, Gylfaginning 15–16",
            sourceDistinctionNote = "Tree imagery is firmly attested; the exact list of 'Nine Worlds' varies between sources."
        ),
        GlossaryEntryEntity(
            id = "g_ginnungagap",
            term = "Ginnungagap",
            oldNorseForm = "Ginnunga gap",
            category = GlossaryCategory.PLACE,
            definition = "The primordial yawning void between icy Niflheimr and fiery Múspellsheimr before creation.",
            primarySource = "Völuspá 3, Gylfaginning 4–5",
            sourceDistinctionNote = "Literally 'gap of magical/yawning powers'."
        ),
        GlossaryEntryEntity(
            id = "g_kenning_poetry",
            term = "Viðurs þýfi / Kvasis dreyri",
            oldNorseForm = "Skáldskapar kenningar",
            category = GlossaryCategory.KENNING,
            definition = "Kennings for poetry ('Odin's theft' / 'Kvasir's blood'), referring to the myth of Odin stealing Suttungr's mead brewed from Kvasir.",
            primarySource = "Sonatorrek 1 (Egils saga), Skáldskaparmál 1–6",
            sourceDistinctionNote = "A kenning pairs a base word ('theft') with a determinant ('Odin') to signify a third concept ('poetry')."
        ),
        GlossaryEntryEntity(
            id = "g_kenning_ship",
            term = "Brim-dýr / Unn-hestr",
            oldNorseForm = "Brimdýr / Unnhestr",
            category = GlossaryCategory.KENNING,
            definition = "Kennings for a Viking ship: 'surf-beast' or 'wave-steed'. Classic maritime skaldic metaphors.",
            primarySource = "Skáldskaparmál 52, Helgakviða Hundingsbana I",
            sourceDistinctionNote = "Standard skaldic pattern: [sea-noun] + [land-animal]."
        )
    )

    val initialDiscussions = listOf(
        DiscussionPostEntity(
            passageId = "voluspa_28",
            authorName = "Dr. Sigrún (Philology Circle)",
            stanceTag = "SCHOLARLY",
            body = "Notice how Völuspá shifts from 3rd-person 'hon' to direct speech when the völva confronts Odin: she turns the tables on the questioner by revealing his own hidden pledge at Mímir's well.",
            createdAt = 1763400000000L
        ),
        DiscussionPostEntity(
            passageId = "havamal_76",
            authorName = "Einar · Reykjavík Reading Group",
            stanceTag = "DEVOTIONAL",
            body = "Paired with stanza 77, 'dómr' isn't about conquering others—it is the ethical integrity of how we treat guests, friends, and kin while alive.",
            createdAt = 1763410000000L
        ),
        DiscussionPostEntity(
            passageId = "laxdaela_78",
            authorName = "Ásta (Saga Study Circle)",
            stanceTag = "LINGUISTIC",
            body = "The dative 'þeim' + imperfect 'unna' makes Guðrún's understatement hit like a thunderclap. Less is always more in Möðruvallabók prose.",
            createdAt = 1763420000000L
        )
    )

    fun buildRuneEntities(): List<RuneEntity> =
        RunesData.allRunes.mapIndexed { index, r ->
            RuneEntity(
                id = r.id,
                alphabet = r.alphabet.name,
                glyph = r.glyph,
                name = r.name,
                soundValue = r.soundValue,
                meaning = r.meaning,
                aett = r.aett,
                lore = r.lore,
                sortOrder = index + 1
            )
        }

    fun buildInscriptionEntities(): List<RunestoneInscriptionEntity> =
        RunesData.inscriptions.mapIndexed { index, ins ->
            RunestoneInscriptionEntity(
                id = ins.id,
                title = ins.title,
                locationAndDate = ins.locationAndDate,
                alphabetUsed = ins.alphabetUsed,
                overview = ins.overview,
                sortOrder = index + 1
            )
        }
}
