package asyncpasswordgenerator

import scala.util.Random
import scala.concurrent.duration._

object GeneratorUtils {
  final val MinPasswordLength = 6
  final val MinScore = 5
  final val MaxGenerationDuration = 5 seconds
  final val SpecialChars = "~`!@#%^&*()-_=+[{]}\\|;:\'\",<.>/?"
  final val NrOfDigits = 9
  final val UpperAlpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
  final val DefaultPositions = List(1, 3, 4)

  def achieveMinLength (password: String): String = {
    if (password.length >= MinPasswordLength) password
    else {
      password + Random.alphanumeric.take(MinPasswordLength - password.length).mkString("")
    }
  }

  /*
  considered properties:
    - password length
    - password contains 1 or more upper case letters
    - password contains 1 or more lower case letters
    - password contains 1 or more special characters
    - password contains 1 or more digits
   */
  def passwordScore (password: String): Int = {
    implicit def bool2int(b: Boolean) = if (b) 1 else 0
    (password.length >= MinPasswordLength).toInt + (password.filter(_.isUpper).length > 0) + (password.filter(_.isLower).length > 0) + (password.filter(_.isDigit).length > 0) + (password.filter(!_.isLetterOrDigit).length > 0)
  }

  def isPasswordSecure(score: Int) = score >= MinScore

  def generateSecurePassword(passwordHintText: String): String = {
    val passwordHintTextMin = achieveMinLength(passwordHintText)
    var securePassword: String = ""

    val deadline = MaxGenerationDuration.fromNow
    def loop(password: String): String = {
      if (password.filter(!_.isLetterOrDigit).length == 1 &&
        password.filter(_.isDigit).length == 1 &&
        isPasswordSecure(passwordScore(password))
      ) password
      else if (!deadline.hasTimeLeft) defaultSecurePassword(passwordHintTextMin)
      else loop(passwordHintTextMin.foldLeft("")((str, c) => str + replaceChar(str, c)))
    }
    loop("")
  }

  def defaultSecurePassword(passwordHintText: String): String = {
    val defaultPart = "" + randomLetterUpper + randomLetterLower + randomSpecialChar + randomDigit
    passwordHintText + defaultPart
  }

  /*
  function replaces current character with one of the following, taking into account that similar replacement should not be done yet:
    - current character
    - upper case character
    - lower case character
    - digit
    - special character
   */
  def replaceChar(string: String, char: Char): Char = {
    val random = new Random
    val letterNewCase =
      if (string.filter(_.isUpper).length > 0) char
      else if (char.isLetter && char.isLower) char.toUpper
      else if (char.isLetter && char.isLower) char.toLower
      else char

    val digit =
      if (string.filter(_.isDigit).length > 0) char
      else randomDigit
    val specialChar =
      if (string.filter(!_.isLetterOrDigit).length > 0) char
      else randomSpecialChar

    val possibleChars = Seq(
      char,
      letterNewCase,
      digit,
      specialChar
    )
    possibleChars(random.nextInt(possibleChars.length))
  }

  def randomDigit: Char = {
    val random = new Random
    random.nextInt(NrOfDigits).toString.charAt(0)
  }

  def randomSpecialChar: Char = {
    val random = new Random
    SpecialChars(random.nextInt(SpecialChars.length))
  }

  def randomLetterUpper: Char = {
    val random = new Random
    UpperAlpha(random.nextInt(UpperAlpha.length))
  }

  def randomLetterLower: Char = {
    val random = new Random
    UpperAlpha(random.nextInt(UpperAlpha.length)).toLower
  }
}
