package ru.practicum.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommentDto {
    private long id;
    private String author;
    private String text;
    private String createdDate;
}
