package com.example.batch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.batch.model.Person;

public interface PersonRepository extends JpaRepository<Person, Integer>{

}
