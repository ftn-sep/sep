package org.psp.repository;

import org.psp.model.LogItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LogObjectRepository extends MongoRepository<LogItem, String> {}
