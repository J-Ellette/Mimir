package com.example.data.seed

data class NorseLexiconEntry(
    val surfaceForm: String,
    val lemma: String,
    val partOfSpeech: String,
    val grammarTag: String,
    val englishGloss: String,
    val note: String
)

object OldNorseLexicon {

    private val exactEntries: Map<String, NorseLexiconEntry> = listOf(
        NorseLexiconEntry("hljóðs", "hljóð", "Noun (neuter a-stem)", "Genitive Singular", "hearing, silence, attention", "Governed by 'biðja' (to ask for)."),
        NorseLexiconEntry("bið", "biðja", "Verb (strong cl. 5)", "1st Sg. Present Indicative", "I ask, entreat", "Governs genitive of the thing requested."),
        NorseLexiconEntry("ek", "ek", "Personal Pronoun", "1st Sg. Nominative", "I", "Proto-Germanic *ek; often enclitic '-k' in verse."),
        NorseLexiconEntry("allar", "allr", "Adjective / Quantifier", "Accusative Pl. Feminine", "all", "Agrees with 'helgar kindir'."),
        NorseLexiconEntry("helgar", "heilagr", "Adjective", "Accusative Pl. Feminine", "holy, sacred", "Related to 'heill' (whole, auspicious)."),
        NorseLexiconEntry("kindir", "kind", "Noun (feminine i-stem)", "Accusative Plural", "kin, races, offspring", "Cognate with English 'kind'."),
        NorseLexiconEntry("heimdallar", "Heimdallr", "Proper Noun (masc.)", "Genitive Singular", "of Heimdallr", "Watchman god of the Æsir."),
        NorseLexiconEntry("valfǫðr", "Valfǫðr", "Proper Noun (masc.)", "Vocative / Nominative", "Father of the Slain (Odin)", "Compound of valr (slain) + fǫðr (father)."),
        NorseLexiconEntry("forn", "forn", "Adjective", "Accusative Pl. Neuter", "ancient, old", "Describes lore from time out of mind."),
        NorseLexiconEntry("spjǫll", "spjall", "Noun (neuter a-stem)", "Accusative Plural", "tales, tidings, sayings", "U-umlaut changes 'a' to 'ǫ' in neuter plural."),
        NorseLexiconEntry("ymir", "Ymir", "Proper Noun (masc.)", "Nominative Singular", "Ymir", "Primordial frost-jötunn."),
        NorseLexiconEntry("sandr", "sandr", "Noun (masc. a-stem)", "Nominative Singular", "sand, shore", "Preserves nominative '-r' ending."),
        NorseLexiconEntry("sær", "sær", "Noun (masc. i-stem)", "Nominative Singular", "sea", "Cognate with English 'sea'."),
        NorseLexiconEntry("jǫrð", "jǫrð", "Noun (fem. ō-stem)", "Nominative Singular", "earth, land", "Fractured from *erþu; also personified as Thor's mother."),
        NorseLexiconEntry("ginnunga", "ginnung", "Noun / Prefix", "Genitive Plural", "of mighty/yawning powers", "Forms 'ginnunga gap', primordial void."),
        NorseLexiconEntry("ask", "askr", "Noun (masc. a-stem)", "Accusative Singular", "ash tree", "Object of 'veit ek standa'."),
        NorseLexiconEntry("yggdrasill", "Yggdrasill", "Proper Noun (masc.)", "Nominative Singular", "Yggdrasil (Odin's steed)", "World Ash tree uniting the cosmos."),
        NorseLexiconEntry("urðarbrunni", "Urðarbrunnr", "Proper Noun (masc.)", "Dative Singular", "Urðr's well", "Sacred well of the Norn Urðr (Fate)."),
        NorseLexiconEntry("hon", "hon", "Personal Pronoun", "3rd Sg. Feminine Nom.", "she", "Refers to the völva (seeress)."),
        NorseLexiconEntry("óðinn", "Óðinn", "Proper Noun (masc.)", "Nom. / Vocative Sg.", "Odin", "From *Wōðanaz (lord of poetic/ecstatic fury)."),
        NorseLexiconEntry("óðni", "Óðinn", "Proper Noun (masc.)", "Dative Singular", "to Odin", "Dative object of 'gefinn' (dedicated)."),
        NorseLexiconEntry("auga", "auga", "Noun (neuter n-stem)", "Accusative Singular", "eye", "Odin's pledged eye in Mímir's well."),
        NorseLexiconEntry("mímisbrunni", "Mímisbrunnr", "Proper Noun (masc.)", "Dative Singular", "in Mímir's well", "Governed by preposition 'í' (in)."),
        NorseLexiconEntry("mímir", "Mímir", "Proper Noun (masc.)", "Nominative Singular", "Mímir", "Guardian of wisdom beneath Yggdrasil."),
        NorseLexiconEntry("mími", "Mímir", "Proper Noun (masc.)", "Accusative Singular", "Mímir", "Direct object in Ynglinga saga."),
        NorseLexiconEntry("brœðr", "bróðir", "Noun (masc. r-stem)", "Nominative Plural", "brothers", "I-umlaut plural of 'bróðir'."),
        NorseLexiconEntry("ragnarǫk", "ragnarǫk", "Noun (neuter pl.)", "Nom. / Acc. Plural", "fate/doom of the powers", "From regin (ruling powers) + rǫk (fate)."),
        NorseLexiconEntry("gáttir", "gátt", "Noun (fem. i-stem)", "Accusative Plural", "doorways, door-frames", "Object of 'skoðask um'."),
        NorseLexiconEntry("óvinir", "óvinr", "Noun (masc. i-stem)", "Nominative Plural", "enemies (un-friends)", "Negative prefix ó- + vinr (friend)."),
        NorseLexiconEntry("ungr", "ungr", "Adjective", "Nominative Sg. Masc.", "young", "Strong masculine nominative '-r'."),
        NorseLexiconEntry("maðr", "maðr", "Noun (masc. root-stem)", "Nominative Singular", "person, human, man", "Assimilates *mannr -> maðr."),
        NorseLexiconEntry("manns", "maðr", "Noun (masc. root-stem)", "Genitive Singular", "of a person/man", "Stem 'mann-' reappears in oblique cases."),
        NorseLexiconEntry("gaman", "gaman", "Noun (neuter a-stem)", "Nominative Singular", "joy, delight, amusement", "Cognate with English 'game'."),
        NorseLexiconEntry("deyr", "deyja", "Verb (strong cl. 6)", "3rd Sg. Present Ind.", "dies", "I-umlaut in singular present indicative."),
        NorseLexiconEntry("deyja", "deyja", "Verb (strong cl. 6)", "3rd Pl. Present Ind.", "die", "Agrees with plural 'frændr' (kinsmen)."),
        NorseLexiconEntry("fé", "fé", "Noun (neuter a-stem)", "Nominative Singular", "cattle, livestock, wealth", "Same root as Elder Futhark rune Fehu (ᚠ)."),
        NorseLexiconEntry("frændr", "frændi", "Noun (masc. nd-stem)", "Nominative Plural", "kinsmen, relatives", "Related to 'friend' (originally loving/kin)."),
        NorseLexiconEntry("dómr", "dómr", "Noun (masc. a-stem)", "Nominative Singular", "judgment, reputation, renown", "Cognate with English 'doom'."),
        NorseLexiconEntry("veit", "vita", "Verb (preterite-present)", "1st/3rd Sg. Present", "know / knows", "Cognate with German 'weiß', English 'wit'."),
        NorseLexiconEntry("hekk", "hanga", "Verb (strong cl. 7)", "1st Sg. Past Ind.", "hung", "Odin hanging on the windy tree."),
        NorseLexiconEntry("nætr", "nótt", "Noun (fem. root-stem)", "Accusative Plural", "nights", "Duration of time in accusative."),
        NorseLexiconEntry("níu", "níu", "Numeral", "Indeclinable", "nine", "Sacred ritual number across Germanic myth."),
        NorseLexiconEntry("geiri", "geirr", "Noun (masc. a-stem)", "Dative Singular", "with a spear", "Instrumental dative."),
        NorseLexiconEntry("rúnar", "rún", "Noun (fem. ō-stem)", "Accusative Plural", "runes, secret wisdom", "Both carved letters and hidden counsel."),
        NorseLexiconEntry("spekð", "spekð", "Noun (fem. i-stem)", "Nominative Singular", "wisdom, insight", "From 'spakr' (wise)."),
        NorseLexiconEntry("mannvit", "mannvit", "Noun (neuter a-stem)", "Nominative Singular", "human intelligence, wit", "Compound of maðr + vit."),
        NorseLexiconEntry("gjallarhorni", "Gjallarhorn", "Proper Noun (neuter)", "Dative Singular", "the Resounding Horn", "Horn from which Mímir drinks and Heimdallr blows."),
        NorseLexiconEntry("alfǫðr", "Alfǫðr", "Proper Noun (masc.)", "Nominative Singular", "Allfather", "Title of Odin in Eddic & Prose Edda."),
        NorseLexiconEntry("skáld", "skáld", "Noun (neuter a-stem)", "Nominative Singular", "poet, skald", "Courtly and Eddic composer of verse."),
        NorseLexiconEntry("mjǫðr", "mjǫðr", "Noun (masc. u-stem)", "Nominative Singular", "mead", "Metaphor for poetic inspiration."),
        NorseLexiconEntry("njáll", "Njáll", "Proper Noun (masc.)", "Nominative Singular", "Njáll", "Wise lawman of Bergþórshváll; borrowed from Old Irish Niall."),
        NorseLexiconEntry("lǫgmaðr", "lǫgmaðr", "Noun (masc.)", "Nominative Singular", "lawman, jurist", "Compound of lǫg (laws) + maðr (person)."),
        NorseLexiconEntry("skegg", "skegg", "Noun (neuter ja-stem)", "Nominative Singular", "beard", "Njáll's famous lack of a beard."),
        NorseLexiconEntry("gunnarr", "Gunnarr", "Proper Noun (masc.)", "Nominative Singular", "Gunnarr", "Hero of Hlíðarendi; from *Gunþiharjaz."),
        NorseLexiconEntry("hlíð", "hlíð", "Noun (fem. ō-stem)", "Nominative Singular", "hillside, slope", "Refers to Fljótshlíð around Hlíðarendi."),
        NorseLexiconEntry("fǫgr", "fagr", "Adjective", "Nominative Sg. Fem.", "fair, beautiful", "U-umlaut changes 'a' to 'ǫ' in fem. sg."),
        NorseLexiconEntry("guðrún", "Guðrún", "Proper Noun (fem.)", "Nominative Singular", "Guðrún", "Central heroine of Laxdæla saga (guð + rún)."),
        NorseLexiconEntry("unna", "unna", "Verb (preterite-present)", "1st Sg. Past Ind.", "loved, favored", "Governs dative object ('þeim')."),
        NorseLexiconEntry("sigurðr", "Sigurðr", "Proper Noun (masc.)", "Nominative Singular", "Sigurd", "Dragon-slayer of Völsunga saga."),
        NorseLexiconEntry("hǫfuðit", "hǫfuð", "Noun (neuter a-stem)", "Accusative Sg. + Def.", "the head", "Suffixed definite article '-it'."),
        NorseLexiconEntry("galdra", "galdr", "Noun (masc. a-stem)", "Accusative Plural", "incantations, chants", "From 'gala' (to chant/sing).")
    ).associateBy { it.surfaceForm }

    fun lookupWord(rawToken: String): NorseLexiconEntry {
        val cleaned = rawToken
            .lowercase()
            .trim { !it.isLetter() && it != 'þ' && it != 'ð' && it != 'ǫ' && it != 'æ' && it != 'œ' && it != 'ø' }
        if (cleaned.isEmpty()) {
            return NorseLexiconEntry(rawToken, rawToken, "Punctuation / Particle", "—", "Non-lexical token", "")
        }
        exactEntries[cleaned]?.let { return it }

        // Morphological inflectional analyzer for any corpus token not in exactEntries
        return when {
            cleaned.endsWith("sk") && cleaned.length > 4 ->
                NorseLexiconEntry(cleaned, cleaned.removeSuffix("sk") + "a", "Verb (Mediopassive)", "Reflexive / Passive (-sk)", "self / reciprocal action of '${cleaned.removeSuffix("sk")}'", "Old Norse '-sk' (< sik) creates reflexive or passive verbs.")
            cleaned.endsWith("um") && cleaned.length > 4 ->
                NorseLexiconEntry(cleaned, cleaned.removeSuffix("um"), "Noun / Adj. / Verb", "Dative Plural (-um) or 1st Pl.", "to/for/with '${cleaned.removeSuffix("um")}'", "'-um' is the universal Old Norse dative plural ending.")
            cleaned.endsWith("inn") || cleaned.endsWith("in") || cleaned.endsWith("it") ->
                NorseLexiconEntry(cleaned, cleaned.dropLast(2), "Noun + Definite Article", "Suffixed Def. Article", "the ${cleaned.dropLast(2)}", "Old Norse attaches the definite article (-inn/-in/-it) to the end of the noun.")
            cleaned.endsWith("ar") && cleaned.length > 3 ->
                NorseLexiconEntry(cleaned, cleaned.removeSuffix("ar"), "Noun / Adj.", "Gen. Sg. or Nom./Acc. Pl. (-ar)", "plural / 'of' ${cleaned.removeSuffix("ar")}", "Common feminine & masculine strong inflection '-ar'.")
            cleaned.endsWith("ir") && cleaned.length > 3 ->
                NorseLexiconEntry(cleaned, cleaned.removeSuffix("ir"), "Noun / Verb", "Nom./Acc. Pl. or 2nd/3rd Sg.", "plural / present form of '${cleaned.removeSuffix("ir")}'", "I-stem plural or weak ia-verb present ending.")
            cleaned.endsWith("ði") || cleaned.endsWith("ti") ->
                NorseLexiconEntry(cleaned, cleaned.dropLast(2) + "a", "Weak Verb", "Past Indicative Sg.", "past tense of '${cleaned.dropLast(2)}a'", "Dental suffix (-ði / -ti) marks weak preterite verbs.")
            cleaned.endsWith("r") && cleaned.length > 3 ->
                NorseLexiconEntry(cleaned, cleaned, "Noun / Adj.", "Nominative Sg. Masc. (-r)", "subject form of '${cleaned.dropLast(1)}'", "Final '-r' preserves Proto-Norse *-az nominative marker.")
            else ->
                NorseLexiconEntry(cleaned, cleaned, "Old West Norse Lexeme", "Attested Corpus Form", "Contextual form in parallel translation", "Tap surrounding words or inspect the parallel English clause beside this stanza.")
        }
    }
}
