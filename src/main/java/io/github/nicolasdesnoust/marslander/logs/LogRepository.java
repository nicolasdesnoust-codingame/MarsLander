package io.github.nicolasdesnoust.marslander.logs;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface LogRepository extends ElasticsearchRepository<LogRecord, String> {
    List<LogRecord> findByTypeOrderByTimestampAsc(String type);
    List<LogRecord> findByTypeAndGenerationOrderByTimestampAsc(String type, int generation, Pageable pageable);
    long countByTypeAndGeneration(String type, int generation);
    List<LogRecord> findByTypeAndGenerationOrderByTimestampAsc(String type, int generation);
    List<LogRecord> findByGenerationOrderByTimestampAsc(int generation, Pageable pageable);
//    List<LogRecord> findByGenerationAndTypeStartsWithOrderByIndexAsc(int generation, String type);
    List<LogRecord> findByGenerationAndTypeStartsWith(int generation, String type);
    long countByGeneration(int generation);
    List<LogRecord> findByTypeOrderByGenerationAsc(String type);

    @Query("Select count(distinct generation) from Table logRecord")
    long countDistinctGeneration();
	List<LogRecord> findByTypeOrderByIndexAsc(String string);
	
    List<LogRecord> findByGenerationAndType(int generation, String type);
}
