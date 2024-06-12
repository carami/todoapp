package org.example.todoapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "files")
@Setter
@Getter
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;
    private String path;
    private String originalFilename;
    private Long size;
    private String mimeType;
    private LocalDateTime uploadDate = LocalDateTime.now();
}