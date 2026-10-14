package com.freightfox.dataset.controller;

import com.freightfox.dataset.entity.DatasetRecord;
import com.freightfox.dataset.service.DatasetRecordService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dataset")
public class DatasetRecordController {

    private final DatasetRecordService service;

    public DatasetRecordController(DatasetRecordService service) {
        this.service = service;
    }

    @PostMapping("/{datasetName}/record")
    public ResponseEntity<?> saveRecord(
            @PathVariable String datasetName,
            @RequestBody JsonNode record) throws JacksonException {

        validateDatasetName(datasetName);

        DatasetRecord savedRecord =
                service.saveRecord(datasetName, record);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", savedRecord.getId());
        response.put("datasetName", savedRecord.getDatasetName());
        response.put("record", record);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{datasetName}/query")
    public ResponseEntity<?> queryRecords(
            @PathVariable String datasetName,
            @RequestParam(required = false) String groupBy,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String order)
            throws JacksonException {

        validateDatasetName(datasetName);

        List<JsonNode> records =
                new ArrayList<>(service.getRecords(datasetName));

        // Group records by a JSON field.
        if (groupBy != null && !groupBy.isBlank()) {
            Map<String, List<JsonNode>> groupedRecords =
                    new LinkedHashMap<>();

            for (JsonNode record : records) {
                JsonNode field = record.get(groupBy);

                String key = field == null || field.isNull()
                        ? "null"
                        : field.asText();

                groupedRecords.computeIfAbsent(
                        key, ignored -> new ArrayList<>()
                ).add(record);
            }

            return ResponseEntity.ok(groupedRecords);
        }

        // Sort records by a JSON field.
        if (sortBy != null && !sortBy.isBlank()) {

            if (!order.equalsIgnoreCase("asc")
                    && !order.equalsIgnoreCase("desc")) {

                return ResponseEntity.badRequest().body(
                        Map.of(
                                "error",
                                "Order must be 'asc' or 'desc'"
                        )
                );
            }

            records.sort((first, second) -> {
                JsonNode a = first.get(sortBy);
                JsonNode b = second.get(sortBy);

                int result;

                // Handle missing and null fields consistently.
                if (a == null || a.isNull()) {
                    result = (b == null || b.isNull()) ? 0 : -1;
                } else if (b == null || b.isNull()) {
                    result = 1;
                } else if (a.isNumber() && b.isNumber()) {
                    // Compare numbers numerically.
                    result = a.decimalValue().compareTo(
                            b.decimalValue()
                    );
                } else {
                    // Compare other values as text.
                    result = a.asText().compareToIgnoreCase(
                            b.asText()
                    );
                }

                return order.equalsIgnoreCase("desc")
                        ? -result
                        : result;
            });
        }

        return ResponseEntity.ok(records);
    }

    private void validateDatasetName(String datasetName) {
        if (datasetName == null || datasetName.isBlank()) {
            throw new IllegalArgumentException(
                    "Dataset name cannot be empty"
            );
        }
    }

    // Return a clear 400 response for invalid input.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidArgument(
            IllegalArgumentException exception) {

        return ResponseEntity.badRequest().body(
                Map.of("error", exception.getMessage())
        );
    }

    // Return a clear 400 response when the request body is malformed JSON.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleInvalidJson(
            HttpMessageNotReadableException exception) {

        return ResponseEntity.badRequest().body(
                Map.of("error", "Request body must contain valid JSON")
        );
    }
}