package poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import poker.model.GameTable;

import java.util.List;

@Repository
public interface GameTableRepository extends JpaRepository<GameTable, Long> {
    @Transactional(readOnly = true)
    List<GameTable> findAllGamesByOrderByIdAsc();

    @Modifying
    @Query(value = "UPDATE GameTable AS gt SET gt.name = :name WHERE gt.id = :id")
    @Transactional
    void updateGameTableNameById(@Param("id") Long id, @Param("name") String name);
}
