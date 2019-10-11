package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.RecordHairstylisToUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordHairstylisToUserRepository extends JpaRepository<RecordHairstylisToUser, Integer>{

    RecordHairstylisToUser findById(int id);

    void deleteById(int id);

}