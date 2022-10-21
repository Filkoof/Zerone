package ru.example.group.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.mail.MailHealthContributorAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.example.group.main.config.KafkaProducerConfig;
import ru.example.group.main.service.KafkaService;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractAllTestH2ContextLoad {
    @MockBean
    private JavaMailSender mailSender;

    @MockBean
    private MailHealthContributorAutoConfiguration mailHealthContributorAutoConfiguration;

    @Autowired
    private MockMvc mockMvc;
}
