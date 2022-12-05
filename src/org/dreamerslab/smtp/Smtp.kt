package org.dreamerslab.smtp

import com.google.appinventor.components.annotations.SimpleEvent
import com.google.appinventor.components.annotations.SimpleFunction
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent
import com.google.appinventor.components.runtime.ComponentContainer
import com.google.appinventor.components.runtime.EventDispatcher
import com.google.appinventor.components.runtime.util.AsynchUtil
import com.google.appinventor.components.runtime.util.YailList
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@Suppress("FunctionName")
class Smtp(container: ComponentContainer) : AndroidNonvisibleComponent(container.`$form`()) {

    private val activity = container.`$context`()
    private var session: Session? = null

    @SimpleFunction(description = "Create session.")
    fun CreateSession(
        smtpHost: String,
        smtpPort: String,
        user: String?,
        password: String
    ) {
        val properties = Properties().apply {
            getOrPut("mail.smtp.host") { smtpHost }
            getOrPut("mail.smtp.port") { smtpPort }
            getOrPut("mail.smtp.auth") { "true" }
            getOrPut("mail.smtp.starttls.enable") { "true" }
        }
        val authenticator = object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(user, password)
            }
        }
        session = Session.getInstance(properties, authenticator)
    }

    @SimpleFunction(description = "Send message using smtp.")
    fun SendMessage(
        senderEmail: String,
        receiverEmails: YailList,
        emailCcs: YailList,
        subject: String,
        content: String
    ) {
        if (session == null) {
            OnError("Session not initialized.")
            return
        }
        try {
            val mimeMessage = MimeMessage(session).apply {
                setFrom(senderEmail)
                receiverEmails.toStringArray()
                    .map { InternetAddress(it) }
                    .forEach {
                        addRecipient(Message.RecipientType.TO, it)
                    }

                emailCcs.toStringArray()
                    .map { InternetAddress(it) }
                    .forEach {
                        addRecipient(Message.RecipientType.CC, it)
                    }

                this.subject = subject
                setText(content)
                sentDate = Date()
            }

            AsynchUtil.runAsynchronously {
                try {
                    session?.getTransport("smtp")?.apply {
                        connect()
                        sendMessage(mimeMessage, mimeMessage.allRecipients)
                        close()
                    }
                    activity.runOnUiThread { OnSuccess() }
                } catch (e: MessagingException) {
                    OnError(e.message)
                }
            }
        } catch (e: MessagingException) {
            OnError(e.toString())
        }
    }

    @SimpleEvent(description = "Event raised when message sending fails.")
    fun OnError(msg: String?) {
        EventDispatcher.dispatchEvent(this, "OnError", msg)
    }

    @SimpleEvent(description = "Email sent successfully")
    fun OnSuccess() {
        EventDispatcher.dispatchEvent(this, "OnSuccess")
    }
}