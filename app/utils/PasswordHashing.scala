package utils

import java.security.MessageDigest

import play.Logger

object PasswordHashing {

  /**
    * Password Hashing Using Message Digest Algo
    */
  def encryptPassword(password: String): String = {
    val algorithm: MessageDigest = MessageDigest.getInstance("SHA-256")
    val defaultBytes: Array[Byte] = password.getBytes
    algorithm.reset()
    algorithm.update(defaultBytes)
    val messageDigest: Array[Byte] = algorithm.digest
    getHexString(messageDigest)
  }

  /**
    * Generate HexString For Password & userId Encryption
    */
  def getHexString(messageDigest: Array[Byte]): String = {
    val hexString: StringBuffer = new StringBuffer
    messageDigest foreach { digest =>
      val hex = Integer.toHexString(0xFF & digest)
      if (hex.length == 1) hexString.append('0') else hexString.append(hex)
    }
    Logger.info("encrypt Data" + hexString.toString)
    hexString.toString
  }

}
