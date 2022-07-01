package com.example.demo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface KanbanProjectRepoInterface extends MongoRepository<Item, String> {

    Collection<Item> findAllByUserId(String id);
}
