package uk.co.yottr.model;

import uk.co.yottr.security.Role;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "user_roles")
public class UserRole {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 45)
    private Role role;

    public UserRole() {
        // required by hibernate
    }

    public UserRole(Role role) {
        this.role = role;
    }

    public Long getId() {
        return this.id;
    }

    public Role getRole() {
        return this.role;
    }

    @Override
    public String toString() {
        return role.name();
    }
}

