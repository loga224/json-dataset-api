package com.freightfox.dataset.service;

import com.freightfox.dataset.entity.DatasetRecord;
import com.freightfox.dataset.repository.DatasetRecordRepository;

import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class DatasetRecordService {

    private final DatasetRecordRepository repository;
    private final ObjectMapper objectMapper;

    public DatasetRecordService(
            DatasetRecordRepository repository,
            ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public DatasetRecord saveRecord(
            String datasetName,
            JsonNode record) throws JacksonException {

        validateDatasetName(datasetName);

        if (record == null || !record.isObject()) {
            throw new IllegalArgumentException(
                    "Record must be a valid JSON object");
        }

        String jsonData = objectMapper.writeValueAsString(record);

        DatasetRecord datasetRecord =
                new DatasetRecord(datasetName, jsonData);

        return repository.save(datasetRecord);
    }

    public List<JsonNode> getRecords(
            String datasetName) throws JacksonException {

        validateDatasetName(datasetName);

        List<DatasetRecord> records =
                repository.findByDatasetNameOrderByIdAsc(datasetName);

        List<JsonNode> result = new ArrayList<>();

        for (DatasetRecord record : records) {
            JsonNode jsonRecord =
                    objectMapper.readTree(record.getRecordData());

            result.add(jsonRecord);
        }

        return result;
    }

    private void validateDatasetName(String datasetName) {
        if (datasetName == null || datasetName.isBlank()) {
            throw new IllegalArgumentException(
                    "Dataset name cannot be empty");
        }
    }
}