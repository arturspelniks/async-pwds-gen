import asyncpasswordgenerator.GeneratorUtils._
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class GeneratorUtilsSpec extends FunSuite {
  val string0 = "" // 0
  val string1 = "a" // 1
  val string2 = "A&" // 2
  val string3 = "A&a" // 3
  val string4 = "A*u1" // 4
  val string5 = "Ar%ur1" // 5
  val string6 = "Ar%ur1+++++" // 5

  test("achieveMinLength should return string with length equal or greater than MinPasswordLength") {
    assert(achieveMinLength(string0).length >= MinPasswordLength)
    assert(achieveMinLength(string1).length >= MinPasswordLength)
    assert(achieveMinLength(string2).length >= MinPasswordLength)
    assert(achieveMinLength(string3).length >= MinPasswordLength)
    assert(achieveMinLength(string4).length >= MinPasswordLength)
    assert(achieveMinLength(string5).length >= MinPasswordLength)
    assert(achieveMinLength(string6).length >= MinPasswordLength)
  }

  test("passwordScore should return correct score") {
    assert(passwordScore(string0) == 0)
    assert(passwordScore(string1) == 1)
    assert(passwordScore(string2) == 2)
    assert(passwordScore(string3) == 3)
    assert(passwordScore(string4) == 4)
    assert(passwordScore(string5) == 5)
    assert(passwordScore(string6) == 5)
  }

  test("isPasswordSecure should return true/false when password achieves min score/not achieves min score") {
    assert(!isPasswordSecure(passwordScore(string0)))
    assert(!isPasswordSecure(passwordScore(string1)))
    assert(!isPasswordSecure(passwordScore(string2)))
    assert(!isPasswordSecure(passwordScore(string3)))
    assert(!isPasswordSecure(passwordScore(string4)))
    assert(isPasswordSecure(passwordScore(string5)))
    assert(isPasswordSecure(passwordScore(string6)))
  }

  test("generateSecurePassword should always return secure password") {
    assert(isPasswordSecure(passwordScore(generateSecurePassword(string0))))
    assert(isPasswordSecure(passwordScore(generateSecurePassword(string1))))
    assert(isPasswordSecure(passwordScore(generateSecurePassword(string2))))
    assert(isPasswordSecure(passwordScore(generateSecurePassword(string3))))
    assert(isPasswordSecure(passwordScore(generateSecurePassword(string4))))
    assert(isPasswordSecure(passwordScore(generateSecurePassword(string5))))
    assert(isPasswordSecure(passwordScore(generateSecurePassword(string6))))
  }

  test("replaceChar should return current char, digit, upper, lower char ir itself and not special char when it is already used") {
    val string = "Pass$b"
    val char = 'b'
    val newChar = replaceChar(string, char)
    assert(newChar.isLetterOrDigit)
  }

  test("replaceChar should return special char, current char, upper, lower char or itself and not digit when it is already used") {
    val string = "Pass5b"
    val char = 'b'
    val newChar = replaceChar(string, char)
    assert(!newChar.isDigit)
  }
}
