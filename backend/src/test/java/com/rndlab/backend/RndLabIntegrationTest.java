package com.rndlab.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rndlab.backend.dto.ExperimentSearchDTO;
import com.rndlab.backend.mapper.ExperimentRecordMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class RndLabIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("rnd_lab_management")
            .withUsername("root")
            .withPassword("test")
            .withInitScript("init-schema.sql");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> String.valueOf(redis.getMappedPort(6379)));
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ExperimentRecordMapper experimentRecordMapper;

    @Test
    void searchRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/experiments/search"))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isIn(401, 403));
    }

    @Test
    void mapperSearchCountsSeedData() {
        ExperimentSearchDTO search = new ExperimentSearchDTO();
        search.setPage(1);
        search.setPageSize(20);
        assertThat(experimentRecordMapper.countSearch(search)).isPositive();
    }

    @Test
    void searchWithJwtUsesRedisCache() throws Exception {
        String loginJson = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(loginJson);
        assertThat(root.hasNonNull("token")).isTrue();
        String token = root.get("token").asText();

        mockMvc.perform(get("/experiments/search")
                        .param("page", "1")
                        .param("pageSize", "20")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        assertThat(stringRedisTemplate.keys("experimentSearch:v1:*")).isNotEmpty();

        mockMvc.perform(get("/experiments/search")
                        .param("page", "1")
                        .param("pageSize", "20")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
