package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.common.exception.NotFoundException;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.model.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.be.server.common.exception.ErrorMessage.USER_NOT_FOUND;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserReader userReader;

    public UserInfo.GetUser getUser(Long id) {
        User user = userReader.findById(id)
            .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));
        return UserInfo.GetUser.from(user);
    }

}
