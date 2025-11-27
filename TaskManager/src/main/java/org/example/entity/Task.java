package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.util.PriorityType;
import org.example.util.StatusType;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Getter
@Setter
@Table(name = "tasks")
public class Task{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", columnDefinition = "status_type")
    private StatusType status;
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "priority", columnDefinition = "priority")
    private PriorityType priority;
    private LocalDate deadline;
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "user_uuid", nullable = false)
    private User user;


}
