package com.mysite.core.service;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.commons.mail.SimpleEmail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailTestServiceTest {

    private final AemContext context = new AemContext();

    @Mock
    private MessageGatewayService messageGatewayService;

    @Mock
    private MessageGateway<SimpleEmail> messageGateway;

    @Mock
    private EmailTestConfig config;

    private EmailTestService service;

    @BeforeEach
    void setUp() {
        context.registerService(MessageGatewayService.class, messageGatewayService);
        service = context.registerInjectActivateService(new EmailTestService());
    }

    @Test
    void testActivate_sendEmailTrue_gatewayAvailable() throws Exception {
        when(config.sendEmail()).thenReturn(true);
        when(messageGatewayService.getGateway(SimpleEmail.class)).thenReturn(messageGateway);

        service.activate(config);

        verify(messageGatewayService).getGateway(SimpleEmail.class);
    }

    @Test
    void testActivate_sendEmailTrue_gatewayNull() {
        when(config.sendEmail()).thenReturn(true);
        when(messageGatewayService.getGateway(SimpleEmail.class)).thenReturn(null);

        service.activate(config);

        verify(messageGatewayService).getGateway(SimpleEmail.class);
        verifyNoInteractions(messageGateway);
    }

    @Test
    void testActivate_sendEmailFalse() {
        when(config.sendEmail()).thenReturn(false);

        service.activate(config);

        verifyNoInteractions(messageGatewayService);
    }
}