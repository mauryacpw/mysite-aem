package com.mysite.core.service;



import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

import org.apache.commons.mail.SimpleEmail;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = EmailTestService.class, immediate = true)
@Designate(ocd = EmailTestConfig.class)
public class EmailTestService {

    private static final Logger log = LoggerFactory.getLogger(EmailTestService.class);

    private static final String TEST_EMAIL = "@gmail.com";

    @Reference
    private MessageGatewayService messageGatewayService;

    @Activate
    @Modified
    protected void activate(EmailTestConfig config) {

        if(config.sendEmail()) {
            sendTestEmail();
        }

    }

    private void sendTestEmail() {

        try {

            MessageGateway<SimpleEmail> gateway =
                    messageGatewayService.getGateway(SimpleEmail.class);

            if(gateway == null){
                log.error("Email gateway not available");
                return;
            }

            SimpleEmail email = new SimpleEmail();

            email.addTo(TEST_EMAIL);
            email.setSubject("AEM Email Test");
            email.setMsg("This is a test email sent from AEM.");

            gateway.send(email);

            log.info("Test email sent to {}", TEST_EMAIL);

        } catch (Exception e) {

            log.error("Error sending email", e);

        }

    }
}