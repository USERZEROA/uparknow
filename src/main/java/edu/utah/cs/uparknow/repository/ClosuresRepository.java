package edu.utah.cs.uparknow.repository;

import java.util.Date;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import edu.utah.cs.uparknow.model.Closures;
import edu.utah.cs.uparknow.model.ClosuresId;

@Repository
public interface ClosuresRepository extends JpaRepository<Closures, ClosuresId> {

    @Transactional
    @Modifying
    void deleteAllByManaId(int manaId);

    void deleteAllBySpaceIdAndManaId(int spaceId, int manaId);

    Optional<Closures> findBySpaceIdAndManaIdAndModStart(Integer spaceId, Integer manaId, Date modStart);
}
