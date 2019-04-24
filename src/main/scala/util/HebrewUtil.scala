package util


object HebrewUtil {

  private val gibberishToHebrew = Seq(
    ("à", "א"),
    ("á", "ב"),
    ("â", "ג"),
    ("ã", "ד"),
    ("ä", "ה"),
    ("å", "ו"),
    ("æ", "ז"),
    ("ç", "ח"),
    ("è", "ט"),
    ("é", "י"),
    ("ë", "כ"),
    ("ê", "ך"),
    ("ì", "ל"),
    ("î", "מ"),
    ("í", "ם"),
    ("ð", "נ"),
    ("ï", "ן"),
    ("ñ", "ס"),
    ("ò", "ע"),
    ("ô", "פ"),
    ("ó", "ף"),
    ("ö", "צ"),
    ("õ", "ץ"),
    ("÷", "ק"),
    ("ø", "ר"),
    ("ù", "ש"),
    ("ú", "ת"))

  def fixText(gibberishText: String): String = {
    val hebrewText = replaceGibberishWithHebrew(gibberishText)
    fixReversedHebrewText(hebrewText)
  }

  private def fixReversedHebrewText(hebrewText: String) = {
    val reversedLines = hebrewText.split("\n").toSeq
    val fixedLines = reversedLines.map(line => flipHebrewNotNumbers(line))
    fixedLines.mkString("\n")
  }

  private def replaceGibberishWithHebrew(gibberishText: String): String = {
    gibberishToHebrew.foldLeft(gibberishText) {
      case (str, (gibberish, hebrew)) => str.replace(gibberish, hebrew)
    }
  }

  private def flipHebrewNotNumbers(line: String): String = {
    val nonNumbers = "([^\\d.,/%])"
    val split = line.split(s"(?<=$nonNumbers+)(?!$nonNumbers)|(?<!$nonNumbers)(?=$nonNumbers+)")
    val splitRevertHebrew = split.map(s => revertHebrew(s))
    splitRevertHebrew.reverse.mkString
  }

  private def revertHebrew(s: String) = {
    if (s.matches(".*([\\p{InHebrew}\\-])+.*")) reverseWithoutEnglish(s)
    else s
  }

  private def reverseWithoutEnglish(str: String) = {
    val english = "([a-zA-Z ])"
    val split = str.split(s"(?<=$english+)(?!$english)|(?<!$english)(?=$english+)")
    val splitRevertNonEnglish = split.map(s => if (s.matches(s"$english+")) s else s.reverse)
    splitRevertNonEnglish.reverse.mkString
  }


  private val hebrewToEnglishMonth = Map(
    "ינואר" -> "January",
    "פברואר" -> "February",
    "מרץ" -> "March",
    "אפריל" -> "April",
    "מאי" -> "May",
    "יוני" -> "June",
    "יולי" -> "July",
    "אוגוסט" -> "August",
    "ספטמבר" -> "September",
    "אוקטובר" -> "October",
    "נובמבר" -> "November",
    "דצמבר" -> "December")

  def dateConvert(date: String): String = {
    val split = date.split("[ ]+")
    split.map(s => if (!s.matches("\\d+")) hebrewToEnglishMonth(s) else s).mkString(" ")
  }
}
