package com.example.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.example.batch.model.Person;
import com.example.batch.processor.PersonProcessor;
import com.example.batch.repository.PersonRepository;
import com.example.batch.task.TaskTwo;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	@Autowired
	private JobBuilderFactory jobFactory;

	@Autowired
	private StepBuilderFactory stepFactory;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private PersonProcessor personProcessor;
	
	@Bean
	public FlatFileItemReader<Person> personReader() {
		return new FlatFileItemReaderBuilder<Person>().name("personItemReader")
				.resource(new ClassPathResource("personData.csv")).delimited().names(new String[] { "ID", "Name" })
				.fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {
					{
						setTargetType(Person.class);
					}
				}).build();
	}

	@Bean
	public RepositoryItemWriter<Person> personWriter() {
		return new RepositoryItemWriterBuilder<Person>().repository(personRepository).methodName("save").build();
	}

	@Bean
	public Step stepTwo() {
		return stepFactory.get("stepTwo").tasklet(new TaskTwo()).build();
	}
	
	//define job 
	@Bean
	public Job importUserJob(JobExecutionListener listener, Step personImportStep) {
		return jobFactory.get("importPersonJob").incrementer(new RunIdIncrementer()).listener(listener).flow(personImportStep)
				.end().build();
	}

	//define a single step, with reader, processor and writer
    @Bean
    public Step personImportStep(RepositoryItemWriter<Person> writer) {
        return stepFactory.get("personImportStep")
            .<Person, Person> chunk(10)
            .reader(personReader())
            .processor(personProcessor)
            .writer(writer)
            .build();
    }
	
}
