package com.example.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.example.batch.model.Person;
@Component
public class PersonProcessor implements ItemProcessor<Person, Person>{

	@Override
	public Person process(Person person) throws Exception {
		System.out.println("person processor running");
		return new Person (person.getId()*10,person.getName().toUpperCase());		
	}
	
}
