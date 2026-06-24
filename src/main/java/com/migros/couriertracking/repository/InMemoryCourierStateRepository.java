package com.migros.couriertracking.repository;

import com.migros.couriertracking.domain.CourierState;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;
import org.springframework.stereotype.Repository;

/**
 * <h1>Memory Kurye Durumu Deposu</h1>
 * <p>Aaynı kuryeye ait eşzamanlı konum bildirimlerinde toplam mesafe race condition olmadan birikir.</p>
 *
 * @author Bartug Sevindik
 * @since 24.06.2026
 */
@Repository
public class InMemoryCourierStateRepository implements CourierStateRepository {

    private final ConcurrentHashMap<String, CourierState> states = new ConcurrentHashMap<>();

    @Override
    public CourierState update(String courierId, UnaryOperator<CourierState> updater) {
        return states.compute(courierId, (id, current) -> updater.apply(current));
    }

    @Override
    public Optional<CourierState> findByCourierId(String courierId) {
        return Optional.ofNullable(states.get(courierId));
    }
}
