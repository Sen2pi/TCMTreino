package com.treasury.kpstreasury.events;

import com.treasury.kpstreasury.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserEvent extends BaseEvent {
    
    public enum UserEventType {
        USER_CREATED,
        USER_UPDATED,
        USER_DELETED,
        USER_ENABLED,
        USER_DISABLED,
        PASSWORD_CHANGED,
        USER_LOGIN,
        USER_LOGOUT
    }
    
    private UserEventType userEventType;
    private Long userId;
    private String username;
    private String email;
    private Role role;
    private String performedBy;
    private String details;
    
    @Override
    public String getEventType() {
        return "USER_EVENT";
    }
    
    public static UserEvent userCreated(Long userId, String username, String email, Role role, String performedBy) {
        UserEvent event = new UserEvent();
        event.setUserEventType(UserEventType.USER_CREATED);
        event.setUserId(userId);
        event.setUsername(username);
        event.setEmail(email);
        event.setRole(role);
        event.setPerformedBy(performedBy);
        event.setDetails("New user account created");
        return event;
    }
    
    public static UserEvent userLogin(Long userId, String username) {
        UserEvent event = new UserEvent();
        event.setUserEventType(UserEventType.USER_LOGIN);
        event.setUserId(userId);
        event.setUsername(username);
        event.setPerformedBy(username);
        event.setDetails("User logged in successfully");
        return event;
    }
}