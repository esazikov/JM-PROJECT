package jm.stockx.api.dao;

import jm.stockx.dto.user.UserDto;
import jm.stockx.entity.User;

public interface UserDAO extends GenericDao<User, Long> {
    UserDto getUserDtoByUserName(String name);

    UserDto getUserDtoByUserEmail(String name);

    UserDto getUserDtoByUserAppleId(String appleId);

    UserDto getUserDtoByUserId(Long id);

    User getUserByName(String name);

    User getUserById(Long id);

    User getUserByEmail(String email);
}
