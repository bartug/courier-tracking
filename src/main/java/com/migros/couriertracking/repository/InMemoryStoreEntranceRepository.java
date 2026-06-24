package com.migros.couriertracking.repository;

import com.migros.couriertracking.domain.StoreEntrance;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.stereotype.Repository;

/**
 * <h1>Memoryye Mağaza Giriş Deposu</h1>
 *
 * @author Bartug Sevindik
 * @since 24.06.2026
 */
@Repository
public class InMemoryStoreEntranceRepository implements StoreEntranceRepository {

    private final Queue<StoreEntrance> entrances = new ConcurrentLinkedQueue<>();

    @Override
    public void save(StoreEntrance entrance) {
        entrances.add(entrance);
    }

    @Override
    public List<StoreEntrance> findAll() {
        return List.copyOf(entrances);
    }

    @Override
    public List<StoreEntrance> findByCourierId(String courierId) {
        return entrances.stream()
                .filter(entrance -> entrance.getCourierId().equals(courierId))
                .toList();
    }
}
