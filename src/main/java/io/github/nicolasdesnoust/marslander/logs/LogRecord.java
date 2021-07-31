package io.github.nicolasdesnoust.marslander.logs;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.github.nicolasdesnoust.marslander.core.Capsule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(indexName = "marslander-logs")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class LogRecord {
    @Id
    @JsonIgnore
    private String id;
    private String type;
    @JsonIgnore
    @Field(name = "@timestamp")
    private String timestamp;
    private Integer y;
    private Integer x;
    private Integer turn;
    @JsonIgnore
    private String message;
    private Integer generation;
    private Integer geneIndex;
    private Integer index;
    private Capsule capsule;
    private Double evaluation;
    private LoggableGene[] genes;
}
