package jm.stockx.api.dao;

import jm.stockx.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RoleDaoImpl extends AbstractDAO<Role, Long> implements RoleDAO {
    @Override
    public Optional<Role> getByName(String name) {
        Role role = (Role) entityManager.createQuery("FROM Role AS r WHERE r.roleName = :roleName")
                .setParameter("roleName", name)
                .getSingleResult();
        return Optional.of(role);

    }
}
