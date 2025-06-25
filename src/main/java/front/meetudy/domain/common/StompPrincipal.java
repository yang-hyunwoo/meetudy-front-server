package front.meetudy.domain.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.Objects;

@RequiredArgsConstructor
@Getter
public class StompPrincipal implements Principal {

    private final Long userId;
    private final String username;
    private final Long studyGroupId;

    public StompPrincipal(Long userId,
                          String username
    ) {
        this.userId = userId;
        this.username = username;
        this.studyGroupId = null;
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StompPrincipal that = (StompPrincipal) o;
        return Objects.equals(userId, that.userId) && Objects.equals(studyGroupId, that.studyGroupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, studyGroupId);
    }

    @Override
    public String toString() {
        return "StompPrincipal{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", studyGroupId=" + studyGroupId +
                '}';
    }

}
