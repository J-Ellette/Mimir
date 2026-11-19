package com.example.data.seed

data class GrammarChallenge(
    val prompt: String,
    val sourceLine: String,
    val sourceTranslation: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class PronunciationPhrase(
    val id: String,
    val oldNorseText: String,
    val oldNorseIpa: String,
    val modernIcelandicIpa: String,
    val englishMeaning: String,
    val soundShiftNote: String
)

data class NorseGrammarLesson(
    val id: String,
    val unitNumber: Int,
    val title: String,
    val subtitle: String,
    val linkedPassageId: String,
    val grammarOverview: String,
    val challenges: List<GrammarChallenge>
)

object NorseLessonsData {

    val lessons = listOf(
        NorseGrammarLesson(
            id = "lesson_cases_1",
            unitNumber = 1,
            title = "Nominative '-r' & Genitive",
            subtitle = "From Völuspá 1 & Hávamál 47",
            linkedPassageId = "voluspa_1",
            grammarOverview = "In Old West Norse, strong masculine singular nouns usually end in '-r' in the subject (nominative) case: sandr, maðr, dómr. Genitive ('of') frequently ends in '-s' or '-ar'.",
            challenges = listOf(
                GrammarChallenge(
                    prompt = "In 'Hljóðs bið ek allar helgar kindir' (Völuspá 1), why does 'hljóðs' end in -s?",
                    sourceLine = "Hljóðs bið ek allar helgar kindir",
                    sourceTranslation = "Hearing/silence I ask from all holy races",
                    options = listOf(
                        "Genitive singular governed by 'biðja'",
                        "Plural subject ending",
                        "Past tense verb suffix"
                    ),
                    correctIndex = 0,
                    explanation = "The verb 'biðja' (to ask/entreat) takes its requested thing in the genitive case: hljóð -> hljóðs."
                ),
                GrammarChallenge(
                    prompt = "Identify the nominative subject in 'maðr er manns gaman' (Hávamál 47):",
                    sourceLine = "Maðr er manns gaman",
                    sourceTranslation = "Man is the joy of man",
                    options = listOf("manns", "maðr", "gaman (genitive)"),
                    correctIndex = 1,
                    explanation = "'Maðr' is nominative singular ('a person'), whereas 'manns' is genitive singular ('of a person')."
                )
            )
        ),
        NorseGrammarLesson(
            id = "lesson_articles_2",
            unitNumber = 2,
            title = "Suffixed Definite Article",
            subtitle = "From Heimskringla & Völsunga saga",
            linkedPassageId = "heimskringla_4",
            grammarOverview = "Unlike English 'the head', Old Norse attaches the definite article to the END of the noun: -inn (masc.), -in (fem.), -it (neut.). So hǫfuð ('head') becomes hǫfuðit ('the head').",
            challenges = listOf(
                GrammarChallenge(
                    prompt = "Translate 'brunninum' from Gylfaginning 15 ('drekkr ór brunninum'):",
                    sourceLine = "hann drekkr ór brunninum",
                    sourceTranslation = "he drinks out of the well",
                    options = listOf("from a well", "out of the well (dat. def.)", "many wells"),
                    correctIndex = 1,
                    explanation = "Brunnr + dative '-i' + definite dative '-num' yields 'brunninum' ('the well')."
                ),
                GrammarChallenge(
                    prompt = "What does '-it' mean in 'Óðinn tók hǫfuðit'?",
                    sourceLine = "Óðinn tók hǫfuðit",
                    sourceTranslation = "Odin took the head",
                    options = listOf("Neuter singular 'the'", "Negation 'not'", "Plural 'heads'"),
                    correctIndex = 0,
                    explanation = "'-it' is the neuter nominative/accusative singular definite suffix attached to 'hǫfuð'."
                )
            )
        ),
        NorseGrammarLesson(
            id = "lesson_negation_3",
            unitNumber = 3,
            title = "Poetic Negation (-a & -gi)",
            subtitle = "From Völuspá 3 & Hávamál 138",
            linkedPassageId = "voluspa_3",
            grammarOverview = "Alongside 'eigi / ekki', Eddic poetry frequently negates verbs or nouns directly with suffixes: '-a / -at' on verbs ('vara' = was not) and '-gi' on nouns ('manngi' = no one).",
            challenges = listOf(
                GrammarChallenge(
                    prompt = "In 'vara sandr né sær' (Völuspá 3), what is 'vara'?",
                    sourceLine = "vara sandr né sær",
                    sourceTranslation = "there was neither sand nor sea",
                    options = listOf("var + -a ('was not')", "vara ('to beware')", "plural 'were'"),
                    correctIndex = 0,
                    explanation = "Enclitic '-a' negates the 3rd person past 'var': 'was not'."
                ),
                GrammarChallenge(
                    prompt = "Decode 'manngi' in 'er manngi veit hvers hann af rótum renn':",
                    sourceLine = "er manngi veit",
                    sourceTranslation = "which no man knows",
                    options = listOf("every man", "no one / nobody", "some men"),
                    correctIndex = 1,
                    explanation = "'Maðr' (stem mann-) + negative particle '-gi' produces 'manngi' ('no one')."
                )
            )
        ),
        NorseGrammarLesson(
            id = "lesson_mediopassive_4",
            unitNumber = 4,
            title = "Mediopassive '-sk' Verbs",
            subtitle = "From Hávamál 1 & Njáls saga 75",
            linkedPassageId = "havamal_1",
            grammarOverview = "Attaching '-sk' (older '-mk' for 1st person) turns a verb reflexive, reciprocal, or passive: berja ('strike') -> berjask ('fight each other'), finna -> finnask ('be found').",
            challenges = listOf(
                GrammarChallenge(
                    prompt = "What does 'brœðr munu berjask' (Völuspá 45) express via '-sk'?",
                    sourceLine = "Brœðr munu berjask",
                    sourceTranslation = "Brothers shall fight one another",
                    options = listOf("Reciprocal action ('each other')", "Future command", "Past participle"),
                    correctIndex = 0,
                    explanation = "'-sk' derives from 'sik' (oneself/each other): 'strike one another'."
                ),
                GrammarChallenge(
                    prompt = "In Hávamál 1, 'um skoðask skyli' means:",
                    sourceLine = "um skoðask skyli",
                    sourceTranslation = "one should look around oneself",
                    options = listOf("should flee", "should look carefully around", "must sleep"),
                    correctIndex = 1,
                    explanation = "'Skoða' + '-sk' gives the reflexive 'look around / examine carefully for oneself'."
                )
            )
        )
    )

    val pronunciationBank = listOf(
        PronunciationPhrase(
            id = "p_voluspa",
            oldNorseText = "Vǫluspá · Ár var alda",
            oldNorseIpa = "[ˈwɔ.lu.spɑː · ˈɑːr wɑr ˈɑl.dɑ]",
            modernIcelandicIpa = "[ˈvœː.lʏ.spauː · ˈauːr vaːr ˈal.ta]",
            englishMeaning = "Prophecy of the Seeress · Of old was the age",
            soundShiftNote = "Old Norse long 'á' [ɑː] diphthongized to [auː] in Modern Icelandic; 'ǫ' merged with 'ö' [œ]."
        ),
        PronunciationPhrase(
            id = "p_havamal",
            oldNorseText = "Maðr er manns gaman",
            oldNorseIpa = "[ˈmɑðr er ˈmɑnːs ˈɡɑ.mɑn]",
            modernIcelandicIpa = "[ˈmaːðʏr ɛr ˈmans ˈkaː.man]",
            englishMeaning = "A person is the joy of another person",
            soundShiftNote = "Modern Icelandic inserts an epenthetic -u- [ʏ] before final -r ('maður') and deaspirates 'g' to [k]."
        ),
        PronunciationPhrase(
            id = "p_mimir",
            oldNorseText = "Mímisbrunnr · Spekð ok mannvit",
            oldNorseIpa = "[ˈmiː.mis.brunːr · spekθ ok ˈmɑnː.wit]",
            modernIcelandicIpa = "[ˈmiː.mɪs.prʏnːʏr · spɛxθ ɔx ˈman.vɪt]",
            englishMeaning = "Mímir's Well · Wisdom and understanding",
            soundShiftNote = "Old Norse 'u' [u] fronted to [ʏ] in Icelandic; initial 'v/w' shifted from [w] to labiodental [v]."
        ),
        PronunciationPhrase(
            id = "p_njals",
            oldNorseText = "Fǫgr er hlíðin",
            oldNorseIpa = "[ˈfɔɣr er ˈhliː.ðin]",
            modernIcelandicIpa = "[ˈfœɣʏr ɛr ˈl̥iː.ðɪn]",
            englishMeaning = "Fair is the hillside",
            soundShiftNote = "Initial 'hl-' was pronounced [hl] in Old Norse and became voiceless [l̥] in Modern Icelandic."
        )
    )
}
