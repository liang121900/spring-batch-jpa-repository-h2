package com.example.batch.job.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.example.batch.repository.PersonRepository;
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {
	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	@Autowired
	private PersonRepository personRepository;
	
	long previousCount=0;
	
	
	@Override
	public void beforeJob(JobExecution jobExecution) {
		previousCount=personRepository.count();
	}
	

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB FINISHED! Time to verify the results");
			long currentCount=personRepository.count();
			if(currentCount<=previousCount) {
				log.error("fail to save person in spring batch, previous count:"+previousCount+" post job count:"+currentCount);
				throw new RuntimeException("fail to save person in spring batch");
			}
		}
		
		log.info("all person in database: "+personRepository.findAll().toString());
	}
}
