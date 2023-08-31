package com.gagi.repository;

import org.springframework.data.repository.CrudRepository;
import com.gagi.domain.User;

public interface UserRepository extends CrudRepository<User, Long> {

}
