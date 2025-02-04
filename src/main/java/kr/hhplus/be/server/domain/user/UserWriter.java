package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.user.entity.User;

import java.util.List;

public interface UserWriter {

    User save(User user);

    List<User> saveAll(List<User> users);

}
