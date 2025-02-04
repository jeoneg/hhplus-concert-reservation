package kr.hhplus.be.server.domain.user.model;

import kr.hhplus.be.server.domain.user.entity.User;

public class UserInfo {

    public record GetUser(
        Long userId
    ) {
        public static GetUser from(User user) {
            return new GetUser(user.getId());
        }
    }

}
