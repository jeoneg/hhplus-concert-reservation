package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.user.entity.User;

import java.util.Optional;

public interface UserReader {

    Optional<User> findById(Long id);

}
