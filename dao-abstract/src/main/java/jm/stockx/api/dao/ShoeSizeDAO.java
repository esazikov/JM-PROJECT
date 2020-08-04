package jm.stockx.api.dao;

import jm.stockx.entity.ShoeSize;
import jm.stockx.entity.User;

import java.util.Optional;

public interface ShoeSizeDAO extends GenericDao<ShoeSize, Long> {
    Optional<ShoeSize> getByName(String name);
    boolean doesItExistEntity(Long id);
}