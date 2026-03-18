package com.example.api.repository;

import com.example.api.model.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE i.id > :cursor ORDER BY i.id ASC")
    List<Item> findItemsAfterCursor(@Param("cursor") Long cursor, Pageable pageable);

}
