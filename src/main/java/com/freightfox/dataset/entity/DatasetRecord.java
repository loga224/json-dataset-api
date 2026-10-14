
package com.freightfox.dataset.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dataset_records")
public class DatasetRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dataset_name", nullable = false)
    private String datasetName;

    @Column(name = "record_data", nullable = false, columnDefinition = "TEXT")
    private String recordData;

    public DatasetRecord() {
    }

    public DatasetRecord(String datasetName, String recordData) {
        this.datasetName = datasetName;
        this.recordData = recordData;
    }

    public Long getId() {
        return id;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public String getRecordData() {
        return recordData;
    }
}
