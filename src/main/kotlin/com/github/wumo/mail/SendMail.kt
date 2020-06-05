package com.github.wumo.mail

import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object SendMail {
  private val prop = Properties()
  private var username = ""
  private var password = ""
  private var auth = object : Authenticator() {
    override fun getPasswordAuthentication(): PasswordAuthentication {
      return PasswordAuthentication(
        username,
        password
      )
    }
  }
  
  fun configure(username: String, password: String) {
    val host = "smtp.${username.split('@')[1]}"
    prop.apply {
      put("mail.smtp.host", host)
      put("mail.smtp.port", "465")
      put("mail.smtp.auth", "true")
      put("mail.smtp.socketFactory.port", "465")
      put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
    }
    SendMail.username = username
    SendMail.password = password
  }
  
  fun sendMail(recipient: String, subject: String, content: String) {
    val session = Session.getInstance(
      prop,
      auth
    )
    val message: Message = MimeMessage(session)
    message.setFrom(InternetAddress(username))
    message.setRecipients(
      Message.RecipientType.TO,
      InternetAddress.parse(recipient)
    )
    message.subject = subject
    message.setText(content)
    Transport.send(message)
  }
  
  fun sendMail(
    username: String, password: String,
    recipient: String, subject: String, content: String
  ) {
    configure(username, password)
    sendMail(recipient, subject, content)
  }
}