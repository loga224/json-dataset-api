package com.freightfox.dataset;

import com.freightfox.dataset.entity.DatasetRecord;
import com.freightfox.dataset.repository.DatasetRecordRepository;
import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JsonDatasetApiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private DatasetRecordRepository repository;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void cleanDatabase() {
		repository.deleteAll();
	}

	@Test
	void shouldSaveEmployeeRecord() throws Exception {
		Map<String, Object> employee = Map.of(
				"name", "Arun",
				"age", 25,
				"department", "IT",
				"salary", 30000
		);

		mockMvc.perform(post("/api/dataset/employees/record")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(employee)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.datasetName").value("employees"))
				.andExpect(jsonPath("$.record.name").value("Arun"));
	}

	@Test
	void shouldRetrieveEmployeeRecords() throws Exception {
		repository.save(new DatasetRecord(
				"employees",
				"{\"name\":\"Arun\",\"age\":25,\"department\":\"IT\"}"
		));

		mockMvc.perform(get("/api/dataset/employees/query"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value("Arun"));
	}

	@Test
	void shouldGroupRecordsByDepartment() throws Exception {
		repository.save(new DatasetRecord(
				"employees",
				"{\"name\":\"Arun\",\"age\":25,\"department\":\"IT\"}"
		));

		mockMvc.perform(get(
						"/api/dataset/employees/query?groupBy=department"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.IT[0].name").value("Arun"));
	}

	@Test
	void shouldSortRecordsByAgeAscending() throws Exception {
		repository.save(new DatasetRecord(
				"employees",
				"{\"name\":\"Priya\",\"age\":30}"
		));

		repository.save(new DatasetRecord(
				"employees",
				"{\"name\":\"Arun\",\"age\":25}"
		));

		mockMvc.perform(get(
						"/api/dataset/employees/query?sortBy=age&order=asc"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value("Arun"))
				.andExpect(jsonPath("$[1].name").value("Priya"));
	}

	@Test
	void shouldRejectInvalidSortOrder() throws Exception {
		mockMvc.perform(get(
						"/api/dataset/employees/query?sortBy=age&order=wrong"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error")
						.value("Order must be 'asc' or 'desc'"));
	}
}